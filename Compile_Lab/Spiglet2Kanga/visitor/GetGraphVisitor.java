package visitor;
import syntaxtree.*;
import utils.*;
import java.util.*;

public class GetGraphVisitor extends  GJNoArguDepthFirst<String> {

	public int vid;
	public HashMap<String, Method> mMethod;
	public HashMap<String, Integer> mLabel;
        public Method curMethod;
        public GraphVertex curVertex;

        public GetGraphVisitor(HashMap<String, Method> m1, HashMap<String, Integer> m2) {
                mMethod = m1;
                mLabel = m2;
        }

	/**
	 * f0 -> "MAIN"
	* f1 -> StmtList()
	* f2 -> "END"
	* f3 -> ( Procedure() )*
	* f4 -> <EOF>
	*/
	public String visit(Goal n) {
                vid = 0;
                curMethod = mMethod.get("Main");
                curMethod.graph.addEdge(0, 1);
                vid = 1;
                n.f1.accept(this);
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
                curMethod = mMethod.get(methodName);
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
                curVertex = curMethod.graph.getVertex(vid);
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
                curMethod.graph.addEdge(vid, vid + 1);
                vid++;
                n.f1.accept(this);
                curMethod.graph.addEdge(vid, vid + 1);
                vid++;
                n.f3.accept(this);
                vid++;
                return null;
	}

        /**
         * f0 -> "NOOP"
        */
        public String visit(NoOpStmt n) {
                curMethod.graph.addEdge(vid, vid + 1);
                return null;
        }
        
        /**
         * f0 -> "ERROR"
        */
        public String visit(ErrorStmt n) {
                curMethod.graph.addEdge(vid, vid + 1);
                return null;
        }
        
        /**
         * f0 -> "CJUMP"
        * f1 -> Temp()
        * f2 -> Label()
        */
        public STring visit(CJumpStmt n) {
                curMethod.graph.addEdge(vid, vid + 1);
                String label = n.f2.f0.tokenImage;
                int target = mLabel.get(label);
                curMethod.graph.addEdge(vid, target);
                n.f1.accept(this);
                return null;
        }
        
        /**
         * f0 -> "JUMP"
        * f1 -> Label()
        */
        public String visit(JumpStmt n) {
                String label = n.f2.f0.tokenImage;
                int target = mLabel.get(label);
                curMethod.graph.addEdge(vid, target);
                return null;
        }
        
        /**
         * f0 -> "HSTORE"
        * f1 -> Temp()
        * f2 -> IntegerLiteral()
        * f3 -> Temp()
        */
        public String visit(HStoreStmt n) {
                curMethod.graph.addEdge(vid, vid + 1);
                n.f1.accept(this);
                n.f3.accept(this);
                return null;
        }
        
        /**
         * f0 -> "HLOAD"
        * f1 -> Temp()
        * f2 -> Temp()
        * f3 -> IntegerLiteral()
        */
        public String visit(HLoadStmt n) {
                curMethod.graph.addEdge(vid, vid + 1);
                n.f1.accept(this);
                n.f3.accept(this);
                return null;
        }
        
        /**
         * f0 -> "MOVE"
        * f1 -> Temp()
        * f2 -> Exp()
        */
        public String visit(MoveStmt n) {
                curMethod.graph.addEdge(vid, vid + 1);
                n.f1.accept(this);
                n.f3.accept(this);
                return null;
        }
        
        /**
         * f0 -> "PRINT"
        * f1 -> SimpleExp()
        */
        public String visit(PrintStmt n) {
                curMethod.graph.addEdge(vid, vid + 1);
                n.f1.accept(this);
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
                
        }
        
        /**
         * f0 -> "CALL"
        * f1 -> SimpleExp()
        * f2 -> "("
        * f3 -> ( Temp() )*
        * f4 -> ")"
        */
        public String visit(Call n) {
                
        }

        
        /**
         * f0 -> "TEMP"
        * f1 -> IntegerLiteral()
        */
        public String visit(Temp n) {
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
        public String visit(Label n) {
                return n.f0.tokenImage;
        }

}