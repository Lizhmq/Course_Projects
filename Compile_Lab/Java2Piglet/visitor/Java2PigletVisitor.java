package visitor;
import java.util.*;
import syntaxtree.*;
import symbol.*;

public class Java2PigletVisitor extends GJDepthFirst<String, MType> {

    int UsedTemp = 20;

    int UsedLabel = 0;

    int getOffSet(MMethod method, MVar var) {
        int i;
        boolean found = false;
        for (i = 0; i < method.otherLocalVars.size(); ++i) {
            if (method.otherLocalVars.get(i).equals(var)) {
                found = true;
                break;
            }
        }
        if (!found) {
            System.out.println(String.format("method %s doesn't have %s", method.name, var.name));
            System.exit(0);
        }
        return UsedTemp + i - method.otherLocalVars.size();
    }

    public String visit(NodeList n, MType env) {
        StringBuilder b = new StringBuilder("");
        for (Enumeration<Node> e = n.elements(); e.hasMoreElements(); ) {
            b.append(e.nextElement().accept(this, env));
        }
        return b.toString();
    }

    public String visit(NodeListOptional n, MType env) {
        if (n.present()) {
            StringBuilder b = new StringBuilder("");
            for (Enumeration<Node> e = n.elements(); e.hasMoreElements(); ) {
                b.append(e.nextElement().accept(this, env));
            }
            return b.toString();
        } else {
            return "";
        }
    }

    public String visit(NodeSequence n, MType env) {
        StringBuilder b = new StringBuilder("");
        for (Enumeration<Node> e = n.elements(); e.hasMoreElements(); ) {
            b.append(e.nextElement().accept(this, env));
        }
        return b.toString();
    }

    public String visit(Goal n, MType env) {
        String s1 = n.f0.accept(this, env);
        String s2 = n.f1.accept(this, env);
        return s1 + s2;
    }

    public String visit(MainClass n, MType env) {
        //
        // we only need to deal with Statement
        //
        String mainClassName = n.f1.accept(this, env);
        MClasses global = (MClasses) env;
        MClass classEnv = global.queryClass(mainClassName);
        MMethod methodEnv = classEnv.queryMethod("main");
        UsedTemp += methodEnv.otherLocalVars.size();
        String Code = n.f15.accept(this, methodEnv);
        String head = "MAIN\n", tail = "END\n";
        UsedTemp -= methodEnv.otherLocalVars.size();
        return head + Code + tail;
        //
        //  MAIN
        //      ***
        //  END
        // 
    }

    public String visit(TypeDeclaration n, MType env) {
        return n.f0.accept(this, env);
    }

    public String visit(ClassDeclaration n, MType env) {
        String className = n.f1.accept(this, env);
        MClasses global = (MClasses) env;
        MClass classEnv = global.queryClass(className);
        String Code = n.f4.accept(this, classEnv);
        return Code;
    }

    public String visit(ClassExtendsDeclaration n, MType env) {
        String className = n.f1.accept(this, env);
        MClasses global = (MClasses) env;
        MClass classEnv = global.queryClass(className);
        String Code = n.f6.accept(this, classEnv);
        return Code;
    }

    public String visit(MethodDeclaration n, MType env) {
        String methodName = n.f2.accept(this, env);
        MClass classEnv = (MClass) env;
        String className = classEnv.name;
        MMethod methodEnv = classEnv.queryMethod(methodName);
        int param = methodEnv.params.size();
        String head = String.format("%s_%s [%d]\nBEGIN\n", className, methodName, param);
        String tail = "END";
        UsedTemp += methodEnv.otherLocalVars.size();
        String code = n.f8.accept(this, methodEnv);
        String ret = "\nRETURN " + n.f10.accept(this, methodEnv);
        UsedTemp -= methodEnv.otherLocalVars.size();
        return head + code + ret + "\n" + tail;
    }

    public String visit(Statement n, MType env) {
        return n.f0.accept(this, env) + "\n";
    }

    public String visit(Block n, MType env) {
        return n.f1.accept(this, env);
    }

    public String visit(AssignmentStatement n, MType env) {
        MMethod methodEnv = (MMethod) env;
        MVar var = methodEnv.queryVar(n.f0.accept(this, env));
        int offSet = getOffSet(methodEnv, var);
        return String.format("MOVE TEMP %d ", offSet) + n.f2.accept(this, env) + "\n";
    }

    public String visit(ArrayAssignmentStatement n, MType env) {
        MMethod methodEnv = (MMethod) env;
        MVar var = methodEnv.queryVar(n.f0.accept(this, env));
        int offSet = getOffSet(methodEnv, var);
        ++UsedTemp;
        String getAddr = String.format("MOVE TEMP %d PLUS TEMP %d TIMES 4 %s\n", UsedTemp - 1, offSet, n.f2.accept(this, env));
        String ret = String.format("HSTORE %s 0 %s\n", getAddr, n.f5.accept(this, env));
        --UsedTemp;
        return ret;
    }
    
    public String visit(IfStatement n, MType env) {
        String C1 = String.format("L%d", UsedLabel++);
        String C2 = String.format("L%d", UsedLabel++);
        String Comp = String.format("CJUMP LT %s 1 %s", n.f2.accept(this, env), C2);
        return String.format("%s\n%s\nJUMP %s\n%s\n%s\n%s\n", Comp, n.f4.accept(this, env), C1, C2, n.f6.accept(this, env), C1);
    }

    public String visit(WhileStatement n, MType env) {
        String in = String.format("L%d", UsedLabel++);
        String out = String.format("L%d", UsedLabel++);
        String Comp = String.format("CJUMP LT %s 1 %s", n.f2.accept(this, env), out);
        return String.format("%s\n%s\n%s\nJUMP %s\n%s\n", in, Comp, n.f4.accept(this, env), in, out);
    }

    public String visit(PrintStatement n, MType env) {
        return "PRINT " + n.f2.accept(this, env);
    }

    public String visit(Expression n, MType env) {
        return n.f0.accept(this, env);
    }

    public String visit(AndExpression n, MType env) {
        return String.format("TIMES %s %s", n.f0.accept(this, env), n.f2.accept(this, env));
    }

    public String visit(CompareExpression n, MType env) {
        return String.format("LT %s %s", n.f0.accept(this, env), n.f2.accept(this, env));

    }

    public String visit(PlusExpression n, MType env) {
        return String.format("PLUS %s %s", n.f0.accept(this, env), n.f2.accept(this, env));
    }

    public String visit(MinusExpression n, MType env) {
        return String.format("MINUS %s %s", n.f0.accept(this, env), n.f2.accept(this, env));
    }

    public String visit(TimesExpression n, MType env) {
        return String.format("TIMES %s %s", n.f0.accept(this, env), n.f2.accept(this, env));
    }

    public String visit(ArrayLookup n, MType env) {
        String head = "BEGIN\n";
        String tail = "END\n";
        String getAddr = String.format("PLUS %s TIMES 4 %s\n", n.f0.accept(this, env), n.f2.accept(this, env));
        ++UsedTemp;
        String getValue = String.format("HLOAD TEMP %d %s 0\n", UsedTemp - 1, getAddr);
        String retValue = String.format("RETURN TEMP %d\n", --UsedTemp);
        return head + getValue + retValue + tail;
    }

    public String visit(ArrayLength n, MType env) {
        ++UsedTemp;
        String getValue = String.format("HLOAD TEMP %d %s 0\n", UsedTemp - 1, n.f0.accept(this, env));
        String retValue = String.format("RETURN TEMP %d\n", --UsedTemp);
        return String.format("BEGIN\n%s%sEND\n", getValue, retValue);
    }

    public String visit(MessageSend n, MType env) {
        // get addr of primary expression
        // find method entry of this object
        // calculate parameters
        // call the func
        return "";
    }

    public String visit(ExpressionList n, MType env) {
        return n.f0.accept(this, env) + n.f1.accept(this, env);
    }

    public String visit(ExpressionRest n, MType env) {
        return " " + n.f1.accept(this, env);
    }

    public String visit(PrimaryExpression n, MType env) {
        String ret = n.f0.accept(this, env);
        MMethod method = (MMethod) env;
        int i = 0;
        for (; i < method.params.size(); ++i) {
            if (method.params.get(i).name.equals(ret)) {
                return String.format("TEMP %d", i + 1);
            }
        }
        MVar var = method.queryVar(ret);
        if (var != null) {
            return String.format("TEMP %d", getOffSet(method, var));
        }
        return ret;
    }

    public String visit(IntegerLiteral n, MType env) {
        return n.f0.toString();
    }

    public String visit(TrueLiteral n, MType env) {
        return "1";
    }

    public String visit(FalseLiteral n, MType env) {
        return "0";
    }

    public String visit(Identifier n, MType env) {
        return n.f0.toString();
    }

    public String visit(ThisExpression n, MType env) {
        return "TEMP 0";
    }

    public String visit(ArrayAllocationExpression n, MType env) {
        String getSize = String.format("PLUS 4 TIMES 4 %s", n.f3.accept(this, env));
        String getAddr = String.format("HALLOCATE %s", getSize);
        return getAddr;
    }

    public String visit(AllocationExpression n, MType env) {
        //  check what class type
        //  construct DTable and VTable
        //  return addr of DTable
        return "";
    }

    public String visit(NotExpression n, MType env) {
        return "MINUS 1 " + n.f1.accept(this, env);
    }

    public String visit(BracketExpression n, MType env) {
        return n.f1.accept(this, env);
    }
}