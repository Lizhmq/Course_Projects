package visitor;
import java.util.*;
import syntaxtree.*;
import utils.*;
/* 
 * GetVertexVisitor:
 * 		Build vertex for graph
 * 		record call position in every method	
 * 		initialize intervals of Temp 
 */
public class GetVertexVisitor extends GJNoArguDepthFirst<String> {
    HashMap<String, Method> mMethod;
    HashMap<String, Integer> mLabel;
    Method current;
    int vtid;
    public GetVertexVisitor(HashMap<String, Method>m, 
    					HashMap<String, Integer>l) {
    	mMethod = m;
    	mLabel = l;
    }
    public String visit(NodeOptional n) {
		if (n.present()) {
			mLabel.put(n.node.accept(this), vtid);
		}
		return null;
	}
	/*
	 * f0 -> "MAIN"
	 * f1 -> StmtList()
	 * f2 -> "END"
	 * f3 -> ( Procedure() )*
	 * f4 -> <EOF>
	 */
    public String visit(Goal n) {
		current = new Method("MAIN", 0);
		mMethod.put("MAIN", current);
		vtid = 0;
		current.graph.addVertex(vtid);
		vtid++;
		n.f1.accept(this);
		current.graph.addVertex(vtid);
		n.f3.accept(this);
		return null;
	}
	/*
	 * f0 -> Label()
	 * f1 -> "["
	 * f2 -> IntegerLiteral()
	 * f3 -> "]"
	 * f4 -> StmtExp()
	 */
    public String visit(Procedure n) {
		vtid = 0;
		String methodName = n.f0.f0.toString();
		int paramNum = Integer.parseInt(n.f2.accept(this));
		current = new Method(methodName, paramNum);
		mMethod.put(methodName, current);
		n.f4.accept(this);
		return null;
	}
	/*
	 * f0 -> NoOpStmt()
	 * | ErrorStmt()
	 * | CJumpStmt()
	 * | JumpStmt()
	 * | HStoreStmt()
	 * | HLoadStmt()
	 * | MoveStmt()
	 * | PrintStmt()
	 */
    public String visit(Stmt n) {
		current.graph.addVertex(vtid);
		n.f0.accept(this);
		vtid++;
		return null;
	}
	/*
	 * f0 -> "CALL"
	 * f1 -> SimpleExp()
	 * f2 -> "("
	 * f3 -> ( Temp() )*
	 * f4 -> ")"
	 */
    public String visit(Call n) {
		n.f1.accept(this);
		n.f3.accept(this);
		current.graph.CallPos.add(vtid);
		if (current.callParamNum < n.f3.size())
			current.callParamNum = n.f3.size();
		return null;
	}
	/*
	 * f0 -> "BEGIN"
	 * f1 -> StmtList()
	 * f2 -> "RETURN"
	 * f3 -> SimpleExp()
	 * f4 -> "END"
	 */
    public String visit(StmtExp n) {
		current.graph.addVertex(vtid);
		vtid++;
		n.f1.accept(this);
		current.graph.addVertex(vtid);
		vtid++;
		n.f3.accept(this);
		current.graph.addVertex(vtid);
		return null;
	}
	/*
	 * f0 -> "TEMP"
	 * f1 -> IntegerLiteral()
	 */
    public String visit(Temp n) {
		Integer tempid = Integer.parseInt(n.f1.accept(this));
		if (!current.mTemp.containsKey(tempid)) {
			if (tempid < current.paramNum)
				// func parameter
				current.mTemp.put(tempid, new Interval(tempid, 0, vtid));
			else
				// local Temp
				current.mTemp.put(tempid, new Interval(tempid, vtid, vtid));
		}
		return (tempid).toString();
	}
	/*
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
