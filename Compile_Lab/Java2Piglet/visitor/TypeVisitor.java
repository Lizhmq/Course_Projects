package visitor;
import syntaxtree.*;
import java.util.*;
import symbol.*;


// return value: String is the type of TreeNode
public class TypeVisitor extends GJDepthFirst<String, MType> {
	
	/* 
	 * 
	 */
        MClasses global;
        TypeVisitor(MClasses g) { global = g; }
        public String visit(PrimaryExpression n, MType env) {
                return n.f0.accept(this, env);
        }
        public String visit(IntegerLiteral n, MType env) {
                return "int";
        }
        public String visit(TrueLiteral n, MType env) {
                return "boolean";
        }
        public String visit(FalseLiteral n, MType env) {
                return "boolean";
        }
        public String visit(NotExpression n, MType env) {
                return "boolean";
        }
        public String visit(Identifier n, MType env) {
                String id = n.f0.toString();
                if (global.queryClass(id) != null)
                        return id;
                MMethod method = (MMethod)env;
                MVar var = method.queryVar(id);
                if (var != null)
                        return var.varType;
                var = method.owner.queryVar(id);
                return var.varType;
        }
        public String visit(ThisExpression n, MType env) {
                MMethod method = (MMethod)env;
                MClass c = method.owner;
                return c.name;
        }
        public String visit(BracketExpression n, MType env) {
                return n.f1.accept(this, env);
        }
        public String visit(ArrayAllocationExpression n, MType env) {
                return "int[]";
        }
        public String visit(AllocationExpression n, MType env) {
                return n.f1.accept(this, env);
        }
        public String visit(Expression n, MType env) {
                return n.f0.accept(this, env);
        }
        public String visit(AndExpression n, MType env) {
                return "boolean";
        }
        public String visit(CompareExpression n, MType env) {
                return "boolean";
        }
        public String visit(PlusExpression n, MType env) {
                return "int";
        }
        public String visit(MinusExpression n, MType env) {
                return "int";
        }
        public String visit(TimesExpression n, MType env) {
                return "int";
        }
        public String visit(ArrayLookup n, MType env) {
                return "int";
        }
        public String visit(ArrayLength n, MType env) {
                return "int";
        }
        public String visit(MessageSend n, MType env) {
                String className = n.f0.accept(this, env);
                //System.out.println("class type: " + className);
                MClass c = global.queryClass(className);
                if (c == null) {
                        System.out.println("Unknown class type.");
                        System.exit(0);
                }
                MMethod method = c.queryMethod(((Identifier)n.f2).f0.toString());
                //System.out.println("method name: " + ((Identifier)n.f2).f0.toString());
                if (method == null) {
                        System.out.println("Unknown method name.");
                        System.exit(0);
                }
                return method.returnType;
        }
}