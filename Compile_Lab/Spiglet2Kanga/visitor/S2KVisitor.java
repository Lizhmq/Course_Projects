package visitor;
import java.util.*;
import utils.*;
import syntaxtree.*;

public class S2KVisitor extends GJNoArguDepthFirst<String> {
    HashMap<String, Method> mMethod;
    Method curMethod;
    public StringBuilder code;
    public S2KVisitor(HashMap<String, Method> m) {
        mMethod = m;
        code = new StringBuilder();
	}
	
    String getNewLabel(String labelName) {
		return labelName + "_" + curMethod.methodName;
	}

	// tempName->regName
	// if spilled, load tempName in regName
	String temp2Reg(String regName, String tempName) {
		if (curMethod.regT.containsKey(tempName)) {
			return curMethod.regT.get(tempName);
		} else if (curMethod.regS.containsKey(tempName)) {
			return curMethod.regS.get(tempName);
		} else {
			code.append(String.format("\t\tALOAD %s %s\n", regName, curMethod.regSpilled.get(tempName)));
			return regName;
		}
	}

	// MOVE tempName exp
	// if spilled, store in regSpilled
	void moveToTemp(String tempName, String exp) {
		if (curMethod.regSpilled.containsKey(tempName)) {
			code.append(String.format("\t\tMOVE v0 %s\n", exp));
			code.append(String.format("\t\tASTORE %s v0\n", curMethod.regSpilled.get(tempName)));
		} else {
			tempName = temp2Reg("", tempName);
			if (!tempName.equals(exp))
				code.append(String.format("\t\tMOVE %s %s\n", tempName, exp));
		}
	}

	// StmtList ::= ( (Label)?Stmt)*
	// get Labels
	public String visit(NodeOptional n) {
		if (n.present()) // get new label
			code.append(getNewLabel(n.node.accept(this)));
		return null;
	}

	/**
	 * f0 -> "MAIN"
	 * f1 -> StmtList()
	 * f2 -> "END"
	 * f3 -> ( Procedure() )*
	 * f4 -> <EOF>
	 */
	public String visit(Goal n) {
		curMethod = mMethod.get("MAIN");
		code.append(String.format("MAIN [%d][%d][%d]\n", curMethod.paramNum, curMethod.stackNum, curMethod.callParamNum));
		n.f1.accept(this);
		code.append("END\n");
		n.f3.accept(this);
		return null;
	}

	/**
	 * f0 -> Label()
	 * f1 -> "["
	 * f2 -> IntegerLiteral()
	 * f3 -> "]"
	 * f4 -> StmtExp()
	 */
	public String visit(Procedure n) {
		String methodName = n.f0.accept(this);
		curMethod = mMethod.get(methodName);
		code.append(String.format("\n%s [%d][%d][%d]\n", methodName, curMethod.paramNum, curMethod.stackNum,
				curMethod.callParamNum));
		n.f4.accept(this);
		return null;
	}

	/**
	 * f0 -> "NOOP"
	 */
	public String visit(NoOpStmt n) {
		code.append("\t\tNOOP\n");
		return null;
	}

	/**
	 * f0 -> "ERROR"
	 */
	public String visit(ErrorStmt n) {
		code.append("\t\tERROR\n");
		return null;
	}

	/**
	 * f0 -> "CJUMP"
	 * f1 -> Temp()
	 * f2 -> Label()
	 */
	public String visit(CJumpStmt n) {
		code.append(String.format("\t\tCJUMP %s %s\n", temp2Reg("v0", n.f1.accept(this)), getNewLabel(n.f2.accept(this))));
		return null;
	}

	/**
	 * f0 -> "JUMP"
	 * f1 -> Label()
	 */
	public String visit(JumpStmt n) {
		code.append(String.format("\t\tJUMP %s\n", getNewLabel(n.f1.accept(this))));
		return null;
	}

	/**
	 * f0 -> "HSTORE"
	 * f1 -> Temp()
	 * f2 -> IntegerLiteral()
	 * f3 -> Temp()
	 */
	public String visit(HStoreStmt n) {
		code.append(String.format("\t\tHSTORE %s %s %s\n", temp2Reg("v0", n.f1.accept(this)), n.f2.accept(this),
				temp2Reg("v1", n.f3.accept(this))));
		return null;
	}

	/**
	 * f0 -> "HLOAD"
	 * f1 -> Temp()
	 * f2 -> Temp()
	 * f3 -> IntegerLiteral()
	 */
	public String visit(HLoadStmt n) {
		String tempTo = n.f1.accept(this);
		String regFrom = temp2Reg("v1", n.f2.accept(this));
		String offset = n.f3.accept(this);
		if (curMethod.regSpilled.containsKey(tempTo)) {
			code.append(String.format("\t\tHLOAD v1 %s %s\n", regFrom, offset));
			moveToTemp(tempTo, "v1");
		} else {
			code.append(String.format("\t\tHLOAD %s %s %s\n", temp2Reg("", tempTo), regFrom, offset));
		}
		return null;
	}

	/**
	 * f0 -> "MOVE"
	 * f1 -> Temp()
	 * f2 -> Exp()
	 */
	public String visit(MoveStmt n) {
		moveToTemp(n.f1.accept(this), n.f2.accept(this));
		return null;
	}

	/**
	 * f0 -> "PRINT"
	 * f1 -> SimpleExp()
	 */
	public String visit(PrintStmt n) {
		code.append(String.format("\t\tPRINT %s\n", n.f1.accept(this)));
		return null;
	}

	/**
	 * f0 -> Call() | HAllocate() | BinOp() | SimpleExp()
	 */
	public String visit(Exp n) {
		return n.f0.accept(this);
	}

	/**
	 * f0 -> "BEGIN"
	 * f1 -> StmtList()
	 * f2 -> "RETURN"
	 * f3 -> SimpleExp()
	 * f4 -> "END"
	 */
	public String visit(StmtExp n) {
		int stackIdx = curMethod.paramNum > 4 ? curMethod.paramNum - 4 : 0;
		// store callee-saved S
		if (curMethod.regS.size() != 0) {
			for (int idx = stackIdx; idx < stackIdx + curMethod.regS.size(); idx++) {
				if (idx - stackIdx > 7)
					break;
				code.append(String.format("\t\tASTORE SPILLEDARG " + idx + " s" + (idx - stackIdx) + "\n"));
			}
		}
		// move params regA to TEMP
		for (stackIdx = 0; stackIdx < curMethod.paramNum && stackIdx < 4; stackIdx++)
			if (curMethod.mTemp.containsKey(stackIdx))
				moveToTemp("TEMP " + stackIdx, "a" + stackIdx);
		// load params(>4)
		for (; stackIdx < curMethod.paramNum; stackIdx++) {
			String tempName = "TEMP " + stackIdx;
			if (curMethod.mTemp.containsKey(stackIdx)) {
				if (curMethod.regSpilled.containsKey(tempName)) {
					code.append(String.format("\t\tALOAD v0 SPILLEDARG %d\n", stackIdx - 4));
					moveToTemp(tempName, "v0");
				} else {
					code.append(String.format("\t\tALOAD %s SPILLEDARG %d\n", temp2Reg("", tempName), stackIdx - 4));
				}
			}
		}

		n.f1.accept(this);
		// v0 stores returnValue
		code.append(String.format("\t\tMOVE v0 " + n.f3.accept(this) + "\n"));

		// restore callee-saved S
		stackIdx = curMethod.paramNum > 4 ? curMethod.paramNum - 4 : 0;
		if (curMethod.regS.size() != 0) {
			for (int j = stackIdx; j < stackIdx + curMethod.regS.size(); j++) {
				if (j - stackIdx > 7)
					break;
				code.append(String.format("\t\tALOAD s" + (j - stackIdx) + " SPILLEDARG " + j + "\n"));
			}
		}

		code.append("END\n");
		return null;
	}

	/**
	 * f0 -> "CALL"
	 * f1 -> SimpleExp()
	 * f2 -> "("
	 * f3 -> ( Temp() )*
	 * f4 -> ")"
	 */
	public String visit(Call n) {
		Vector<Node> vTemp = n.f3.nodes;
		int nParam = vTemp.size();
		int paramIdx;
		// pass params
		for (paramIdx = 0; paramIdx < nParam && paramIdx < 4; paramIdx++)
			code.append(String.format("\t\tMOVE a%d %s\n", paramIdx, temp2Reg("v0", vTemp.get(paramIdx).accept(this))));
		for (; paramIdx < nParam; paramIdx++)
			code.append(String.format("\t\tPASSARG %d %s\n", paramIdx - 3, temp2Reg("v0", vTemp.get(paramIdx).accept(this))));
		// call
		code.append(String.format("\t\tCALL %s\n", n.f1.accept(this)));
		return "v0";
	}

	/**
	 * f0 -> "HALLOCATE"
	 * f1 -> SimpleExp()
	 */
	public String visit(HAllocate n) {
		return "HALLOCATE " + n.f1.accept(this);
	}

	/**
	 * f0 -> Operator()
	 * f1 -> Temp()
	 * f2 -> SimpleExp()
	 */
	public String visit(BinOp n) {
		return n.f0.accept(this) + temp2Reg("v0", n.f1.accept(this)) + " " + n.f2.accept(this);
	}

	/**
	 * f0 -> "LT" | "PLUS" | "MINUS" | "TIMES"
	 */
	public String visit(Operator n) {
		String[] ops = { "LT ", "PLUS ", "MINUS ", "TIMES " };
		return ops[n.f0.which];
	}

	/**
	 * f0 -> Temp() | IntegerLiteral() | Label()
	 */
	public String visit(SimpleExp n) {
		String out = n.f0.accept(this);
		if (n.f0.which == 0)
			out = temp2Reg("v1", out);
		return out;
	}

	/**
	 * f0 -> "TEMP"
	 * f1 -> IntegerLiteral()
	 */
	public String visit(Temp n) {
		return "TEMP " + n.f1.accept(this);
	}

	/**
	 * f0 -> <INTEGER_LITERAL>
	 */
	public String visit(IntegerLiteral n) {
		return n.f0.toString();
	}

	/**
	 * f0 -> <IDENTIFIER>
	 */
	public String visit(Label n) {
		return n.f0.toString();
	}
}