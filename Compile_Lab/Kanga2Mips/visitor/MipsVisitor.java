package visitor;
import syntaxtree.*;
import java.util.*;


// Concat, CallStack, PASSARG
public class MipsVisitor extends GJNoArguDepthFirst<String> {

	public String alloc = "";
	Integer argnum, stacknum, totalnum;
	/*
	 * 	.text
		.globl _halloc
	_halloc: li $v0, 9
		syscall
		j $ra
		
		.text
		.globl _print
	_print: li $v0, 1
		syscall
		la $a0, newl
		li $v0, 4
		syscall
		j $ra

		.data
		.align 0
	newl: .asciiz "\n"
		.data
		.align 0
	str_er: .asciiz " ERROR: abnormal termination\n" 
	 */
	void genalloc() {
		alloc += "\t.text\n\t.globl _halloc\n";
		alloc += "_halloc:\n\tli $v0, 9\n\tsyscall\n\tj $ra\n\n";
		alloc += "\t.text\n\t.globl _print\n";
		alloc += "_print:\n\tli $v0, 1\n\tsyscall\n\tla $a0, newl\n\tli $v0, 4\n\tsyscall\n\tj $ra\n\n";
		alloc += "\t.data\n\t.align 0\n";
		alloc += "newl:\n\t.asciiz \"\\n\"\n\t.data\n\t.align 0\n";
		alloc += "str_er:\n\t.asciiz \" ERROR: abnormal termination\\n\"";
	}
	

	/*
	 * Represent a optional grammar list, e.g. (A)*
	 * 
	 */
	public String visit(NodeListOptional n) {
		if (n.present()) {
			String ret = "";
			for (Enumeration<Node> e = n.elements(); e.hasMoreElements();)
				ret += e.nextElement().accept(this) + "\n";
				return ret;
			}
		else
			return "";
	}
	

	public String visit(NodeSequence n) {
		String ret = "";
		int k = 0;
		for ( Enumeration<Node> e = n.elements(); e.hasMoreElements(); ) {
			String tmp = e.nextElement().accept(this);
			if (k == 0) {		// (Label)?
				if (!tmp.equals(""))
					ret += tmp + ": nop\n";
			} else {		// Stmt
				ret += "\t" + tmp;
			}
			k += 1;
		}
		return ret;
	}

	/*
	 * Represent a grammar optional node, e.g. (A)?
	 * 
	 */
	public String visit(NodeOptional n) {
		if (n.present())
			return n.node.accept(this);
		else
			return "";
	}


	public String visit(NodeToken n) { return n.tokenImage; }
	
	//
	// User-generated visitor methods below
	//
	
	/**
	 * f0 -> "MAIN"
	* f1 -> "["
	* f2 -> IntegerLiteral()
	* f3 -> "]"
	* f4 -> "["
	* f5 -> IntegerLiteral()
	* f6 -> "]"
	* f7 -> "["
	* f8 -> IntegerLiteral()
	* f9 -> "]"
	* f10 -> StmtList()
	* f11 -> "END"
	* f12 -> ( Procedure() )*
	* f13 -> <EOF>
	*/
	public String visit(Goal n) {
		argnum = Integer.parseInt(n.f2.accept(this));
		argnum = argnum > 4 ? argnum - 4 : 0;
		stacknum = Integer.parseInt(n.f5.accept(this));
		totalnum = Integer.parseInt(n.f8.accept(this));
		totalnum = totalnum > 4 ? totalnum - 4 : 0;
		Integer stack = stacknum - argnum + totalnum + 2;
		String ret = "";
		ret += "\t.text\n";
		ret += "\t.globl main\n";
		ret += "main:\n";
		ret += "\tsw $fp, -8($sp)\n";
		ret += "\tsw $ra, -4($sp)\n";
		ret += "\tmove $fp, $sp\n";
		ret += "\tsubu $sp, $sp, " + (stack * 4) + "\n";
		//ret += "\tsw $ra, -4($fp)\n";
		ret += n.f10.accept(this);
		ret += "\tlw $ra, -4($fp)\n";
		ret += "\tlw $fp, -8($fp)\n";
		ret += "\taddu $sp, $sp, " + (stack * 4) + "\n";
		ret += "\tj $ra\n";
		ret += n.f12.accept(this);

		genalloc();
		ret += "\n" + alloc + "\n";
		return ret;
	}
	
	/**
	 * f0 -> ( ( Label() )? Stmt() )*
	*/
	public String visit(StmtList n) {
		return n.f0.accept(this);
	}
	
	/**
	 * f0 -> Label()
	* f1 -> "["
	* f2 -> IntegerLiteral()
	* f3 -> "]"
	* f4 -> "["
	* f5 -> IntegerLiteral()
	* f6 -> "]"
	* f7 -> "["
	* f8 -> IntegerLiteral()
	* f9 -> "]"
	* f10 -> StmtList()
	* f11 -> "END"
	*/
	public String visit(Procedure n) {
		String ret = "";
		String procname = n.f0.accept(this);
		ret += "\t.text\n\t.globl " + procname + "\n";
		ret += procname + ":\n";
		argnum = Integer.parseInt(n.f2.accept(this));
		argnum = argnum > 4 ? argnum - 4 : 0;
		stacknum = Integer.parseInt(n.f5.accept(this));
		totalnum = Integer.parseInt(n.f8.accept(this));
		totalnum = totalnum > 4 ? totalnum - 4 : 0;
		Integer stack = stacknum - argnum + totalnum + 2;
		// build call frame
		ret += "\tsw $fp, -8($sp)\n";
		ret += "\tmove $fp, $sp\n";
		ret += String.format("\tsubu $sp, $sp, %d\n", stack * 4);
		ret += "\tsw $ra, -4($fp)\n";
		
		ret += n.f10.accept(this);

		ret += "\tlw $ra, -4($fp)\n";
		ret += String.format("\tlw $fp, -8($fp)\n");
		ret += String.format("\taddu $sp, $sp, %d\n", stack * 4);
		ret += "\tj $ra\n";
		return ret;
	}
	
	/**
	 * f0 -> NoOpStmt()
	*       | ErrorStmt()
	*       | CJumpStmt()
	*       | JumpStmt()
	*       | HStoreStmt()
	*       | HLoadStmt()
	*       | MoveStmt()
	*       | PrintStmt()
	*       | ALoadStmt()
	*       | AStoreStmt()
	*       | PassArgStmt()
	*       | CallStmt()
	*/
	public String visit(Stmt n) {
		return n.f0.accept(this);
	}
	
	/**
	 * f0 -> "NOOP"
	*/
	public String visit(NoOpStmt n) {
		return "nop";
	}
	
	// /**
	//  * f0 -> "ERROR"
	// */
	// public R visit(ErrorStmt n) {
	// 	R _ret=null;
	// 	n.f0.accept(this);
	// 	return _ret;
	// }
	
	/**
	 * f0 -> "CJUMP"
	* f1 -> Reg()
	* f2 -> Label()
	*/
	public String visit(CJumpStmt n) {
		return "beqz " + n.f1.accept(this) + " " + n.f2.accept(this);
	}
	
	/**
	 * f0 -> "JUMP"
	* f1 -> Label()
	*/
	public String visit(JumpStmt n) {
		return "b " + n.f1.accept(this);
	}
	
	/**
	 * f0 -> "HSTORE"
	* f1 -> Reg()
	* f2 -> IntegerLiteral()
	* f3 -> Reg()
	*/
	public String visit(HStoreStmt n) {
		String base = n.f1.accept(this);
		String offset = n.f2.accept(this);
		String reg2 = n.f3.accept(this);
		return "sw " + reg2 + ", " + offset + "(" + base + ")";
	}
	
	/**
	 * f0 -> "HLOAD"
	* f1 -> Reg()
	* f2 -> Reg()
	* f3 -> IntegerLiteral()
	*/
	public String visit(HLoadStmt n) {
		String reg1 = n.f1.accept(this);
		String base = n.f2.accept(this);
		String offset = n.f3.accept(this);
		return "lw " + reg1 + " " + offset + "(" + base + ")";
	}
	
	/**
	 * f0 -> "MOVE"
	* f1 -> Reg()
	* f2 -> Exp()
	*/
	public String visit(MoveStmt n) {
		int c = n.f2.f0.which;
		if (c == 1) {	// Exp -> Binop
			String []buf = n.f2.accept(this).split("!");
			return buf[0] + " " + n.f1.accept(this) + buf[1];
		} else if (c == 0) {	// Exp -> Halloc
			String ret = n.f2.accept(this);
			return ret + "\n\t" + "move " + n.f1.accept(this) + " $v0";	
		} else {	// SimpleExp
			char[] simp = n.f2.accept(this).toCharArray();
			if (simp[0] >= '0' && simp[0] <= '9') {		// IntergerLiteral
				return "li " + n.f1.accept(this) + " " + n.f2.accept(this);
			} else {
				if (simp[0] == '$') {
					return "move " + n.f1.accept(this) + " " + n.f2.accept(this);
				}
				return "la " + n.f1.accept(this) + " " + n.f2.accept(this);
			}
		}
	}
	
	/**
	 * f0 -> "PRINT"
	* f1 -> SimpleExp()
	*/
	public String visit(PrintStmt n) {
		String ret = "";
		ret += "move $a0 " + n.f1.accept(this) + "\n";
		ret += "\tjal _print";
		return ret;
	}
	
	/**
	 * f0 -> "ERROR"
	*/
	public String visit(ErrorStmt n) {
		String ret = "";
		ret += "la $a0 str_er\n";
		ret += "\tjal _print";
		return ret;
	}
	/**
	 * f0 -> "ALOAD"
	* f1 -> Reg()
	* f2 -> SpilledArg()
	*/
	public String visit(ALoadStmt n) {
		String reg = n.f1.accept(this);
		String spilledarg = n.f2.accept(this);
		return String.format("lw %s, %s", reg, spilledarg);
	}
	
	/**
	 * f0 -> "ASTORE"
	* f1 -> SpilledArg()
	* f2 -> Reg()
	*/
	public String visit(AStoreStmt n) {
		String spilledarg = n.f1.accept(this);
		String reg = n.f2.accept(this);
		return String.format("sw %s, %s", reg, spilledarg);
	}
	
	/**
	 * f0 -> "PASSARG"
	* f1 -> IntegerLiteral()
	* f2 -> Reg()
	*/
	public String visit(PassArgStmt n) {
		// PASSARG starts from 1
		int offset = Integer.parseInt(n.f1.accept(this)) - 1;
		String regFrom = n.f2.accept(this);
		return String.format("sw %s, %d($sp)", regFrom, offset * 4);
	}
	
	/**
	 * f0 -> "CALL"
	* f1 -> SimpleExp()
	*/
	public String visit(CallStmt n) {
		return "jalr " + n.f1.accept(this);
	}
	
	/**
	 * f0 -> HAllocate()
	*       | BinOp()
	*       | SimpleExp()
	*/
	public String visit(Exp n) {
		return n.f0.accept(this);
	}
	
	/**
	 * f0 -> "HALLOCATE"
	* f1 -> SimpleExp()
	*/
	public String visit(HAllocate n) {
		String ret = "";
		String simp = n.f1.accept(this);
		if (simp.charAt(0) == '$')
			ret += "move $a0 " + n.f1.accept(this) + "\n";
		else if (simp.charAt(0) >= '0' && simp.charAt(0) <= '9') {
			ret += "li $a0 " + n.f1.accept(this) + "\n";
		} else {
			ret += "la $a0 " + n.f1.accept(this) + "\n";
		}
		ret += "\tjal _halloc";
		return ret;
	}
	
	/**
	 * f0 -> Operator()
	* f1 -> Reg()
	* f2 -> SimpleExp()
	*/
	// "slt (),$s1,$t0"
	public String visit(BinOp n) {
		return n.f0.accept(this) + "!, " + n.f1.accept(this) + ", " + n.f2.accept(this);
	}
	
	/**
	 * f0 -> "LT"
	*       | "PLUS"
	*       | "MINUS"
	*       | "TIMES"
	*/
	public String visit(Operator n) {
		int c = n.f0.which;
		String []buf = { "slt", "add", "sub", "mul" };
		return buf[c];
	}
	
	/**
	 * f0 -> "SPILLEDARG"
	* f1 -> IntegerLiteral()
	*/
	public String visit(SpilledArg n) {
		Integer id = Integer.parseInt(n.f1.f0.tokenImage);
		//System.out.println(String.format("%d %d", id, argnum));
		if (id >= argnum) {
			id = argnum - id - 3;
		} 
		return 4 * id + "($fp)";
	}
	
	/**
	 * f0 -> Reg()
	*       | IntegerLiteral()
	*       | Label()
	*/
	public String visit(SimpleExp n) {
		return n.f0.accept(this);
	}
	
	/**
	 * f0 -> "a0"
	*       | "a1"
	*       | "a2"
	*       | "a3"
	*       | "t0"
	*       | "t1"
	*       | "t2"
	*       | "t3"
	*       | "t4"
	*       | "t5"
	*       | "t6"
	*       | "t7"
	*       | "s0"
	*       | "s1"
	*       | "s2"
	*       | "s3"
	*       | "s4"
	*       | "s5"
	*       | "s6"
	*       | "s7"
	*       | "t8"
	*       | "t9"
	*       | "v0"
	*       | "v1"
	*/
	public String visit(Reg n) {
		return "$" + n.f0.accept(this);
	}
	
	/**
	 * f0 -> <INTEGER_LITERAL>
	*/
	public String visit(IntegerLiteral n) {
		return n.f0.tokenImage;
	}
	
	/**
	 * f0 -> <IDENTIFIER>
	*/
	public String visit(Label n) {
		return n.f0.tokenImage;
	}
	
}
