package visitor;
import syntaxtree.*;
import utils.*;
import java.util.*;

public class GetGraphVertex extends  GJNoArguDepthFirst<String> {

	public int vid;
	public HashMap<String, Method> mMethod;
	public HashMap<String, Integer> mLabel;
	public Method curMethod;

	public String visit(NodeOptional n) {
		if (n.present())
			mLabel.put(n.node.accept(this), vid);
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
		Method main = new Method("Main", 0);
		curMethod = main;
		mMethod.put("Main", curMethod);
		vid = 0;
		curMethod.graph.addVertex(vid);
		vid++;
		n.f1.accept(this);
		curMethod.graph.addVertex(vid);
		vid++;
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
		vid = 0;
		String methodName = n.f0.f0.tokenImage;
		int paramNum = Integer.parseInt(n.f2.f0.tokenImage);
		curMethod = new Method(methodName, paramNum);
		mMethod.put(methodName, curMethod);
		n.f4.accept(this);
		return null;
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
	*/
	public String visit(Stmt n) {
		curMethod.graph.addVertex(vid);
		n.f0.accept(this);
		vid++;
		return null;
	}

	/**
	 * f0 -> "BEGIN"
	* f1 -> StmtList()
	* f2 -> "RETURN"
	* f3 -> SimpleExp()
	* f4 -> "END"
	*/
	public String visit(StmtExp n) {
		curMethod.graph.addVertex(vid);
		vid++;
		n.f1.accept(this);
		curMethod.graph.addVertex(vid);
		vid++;
		n.f3.accept(this);
		curMethod.graph.addVertex(vid);
		vid++;
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
		curMethod.graph.CallPos.add(vid);
		n.f1.accept(this);
		n.f3.accept(this);
		// // callParamNum uses the MAX
		// if (currMethod.callParamNum < n.f3.size())
		// 	currMethod.callParamNum = n.f3.size();
		// return null;
	}

	/**
	 * f0 -> "TEMP"
	 * f1 -> IntegerLiteral()
	 */
	public String visit(Temp n) {
		Integer tempNo = Integer.parseInt(n.f1.f0.tokenImage);
		if (!currMethod.mTemp.containsKey(tempNo)) {	// ?????
			if (tempNo < currMethod.paramNum)
				// parameter
				currMethod.mTemp.put(tempNo, new Interval(tempNo, 0, vid));
			else
				// local Temp (first shows up at vid)
				currMethod.mTemp.put(tempNo, new Interval(tempNo, vid, vid));
		}
		return null;
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
	public Stirng visit(Label n) {
		return n.f0.tokenImage;
	}

}