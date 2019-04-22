package visitor;
import java.util.*;
import syntaxtree.*;
import symbol.*;

public class Java2PigletVisitor extends GJDepthFirst<String, MType> {

    int UsedTemp = 20;

    int UsedLabel = 0;

    int base = 20;

    MClasses global;

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
        return base + i;
    }

    public String visit(NodeList n, MType env) {
        StringBuilder b = new StringBuilder("");
        for (Enumeration<Node> e = n.elements(); e.hasMoreElements(); ) {
            b.append(e.nextElement().accept(this, env));
            if (e.hasMoreElements())
                b.append("\n");
        }
        return b.toString();
    }

    public String visit(NodeListOptional n, MType env) {
        if (n.present()) {
            StringBuilder b = new StringBuilder("");
            for (Enumeration<Node> e = n.elements(); e.hasMoreElements(); ) {
                b.append(e.nextElement().accept(this, env));
                if (e.hasMoreElements())
                    b.append("\n");
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
            if (e.hasMoreElements())
                b.append("\n");
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
        String mainClassName = n.f1.f0.toString();
        this.global = (MClasses) env;
        MClass classEnv = global.queryClass(mainClassName);
        MMethod methodEnv = classEnv.queryMethod("main");
        base = UsedTemp;
        UsedTemp += methodEnv.otherLocalVars.size();
        String Code = n.f15.accept(this, methodEnv);
        String head = "MAIN\n", tail = "END\n\n";
        //UsedTemp -= methodEnv.otherLocalVars.size();
        UsedTemp = base;
        return head + Code + '\n' + tail;
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
        String className = n.f1.f0.toString();
        this.global = (MClasses) env;
        MClass classEnv = global.queryClass(className);
        String Code = n.f4.accept(this, classEnv);
        return Code + "\n\n";
    }

    public String visit(ClassExtendsDeclaration n, MType env) {
        String className = n.f1.f0.toString();
        this.global = (MClasses) env;
        MClass classEnv = global.queryClass(className);
        String Code = n.f6.accept(this, classEnv);
        return Code + "\n\n";
    }

    public String visit(MethodDeclaration n, MType env) {
        String methodName = n.f2.f0.toString();
        MClass classEnv = (MClass) env;
        String className = classEnv.name;
        MMethod methodEnv = classEnv.queryMethod(methodName);
        int param = methodEnv.params.size();
        String head = String.format("%s_%s [%d]\nBEGIN\n", className, methodName, param + 1);
        String tail = "END";
        UsedTemp += methodEnv.otherLocalVars.size();
        String code = n.f8.accept(this, methodEnv);
        String ret = "\nRETURN " + n.f10.accept(this, methodEnv);
        UsedTemp -= methodEnv.otherLocalVars.size();
        return head + code + ret + "\n" + tail;
    }

    public String visit(Statement n, MType env) {
        return n.f0.accept(this, env);
    }

    public String visit(Block n, MType env) {
        return n.f1.accept(this, env);
    }

    // **** queryVar
    public String visit(AssignmentStatement n, MType env) {
        // MMethod methodEnv = (MMethod) env;
        // MVar var = methodEnv.queryVar(n.f0.f0.toString());
        // String id = n.f0.accept(this, env);
        // int offSet = getOffSet(methodEnv, var);
        MMethod method = (MMethod)env;
        String id = n.f0.f0.toString();
        for (int i = 0; i < method.params.size(); ++i) {
            if (method.params.get(i).name.equals(id)) {
                // return String.format("MOVE TEMP %d %s", i + 1, n.f2.accept(this, env);
                return String.format("MOVE %s %s", n.f0.accept(this, env), n.f2.accept(this, env));
            }
        }
        if (method.varsTable.containsKey(id))
            return String.format("MOVE %s %s", n.f0.accept(this, env), n.f2.accept(this, env));
        MClass classEnv = method.owner;
        if (classEnv.queryVar(id) != null) {
            // get class variable address by this pointer in TEMP 0
            // return TEMP 0 + certain offset
            MClass c = ((MMethod)env).owner;
            Enumeration vars = c.memberVars.keys();
            int i = 0;
            while (c.memberVars.get(vars.nextElement()).name != id)
                i = i + 1;
            return String.format("HSTORE TEMP 0 %d %s", 4 * (i + 1), n.f2.accept(this, env));
        }
        return "\n\n??? in visit Assignment\n\n";
    }

    // **** queryVar
    public String visit(ArrayAssignmentStatement n, MType env) {
        // MMethod methodEnv = (MMethod) env;
        // MVar var = methodEnv.queryVar(n.f0.f0.toString());
        String id = n.f0.accept(this, env);
        // int offSet = getOffSet(methodEnv, var);
        int desid = UsedTemp;
        ++UsedTemp;
        String getAddr = String.format("MOVE TEMP %d PLUS %s TIMES 4 PLUS %s 1\n", desid, id, n.f2.accept(this, env));
        String ret = String.format("HSTORE TEMP %d 0 %s", desid, n.f5.accept(this, env));
        --UsedTemp;
        return getAddr + ret;
    }
    
    // **** test
    public String visit(IfStatement n, MType env) {
        String C1 = String.format("L%d", UsedLabel++);
        String C2 = String.format("L%d", UsedLabel++);
        String Comp = String.format("CJUMP LT 0 %s %s", n.f2.accept(this, env), C2);
        return String.format("%s\n%s\nJUMP %s\n%s\n%s\n%s NOOP\n", Comp, n.f4.accept(this, env), C1, C2, n.f6.accept(this, env), C1);
    }

    // **** test
    public String visit(WhileStatement n, MType env) {
        String in = String.format("L%d", UsedLabel++);
        String out = String.format("L%d", UsedLabel++);
        String Comp = String.format("CJUMP LT 0 %s %s", n.f2.accept(this, env), out);
        return String.format("%s\n%s\n%s\nJUMP %s\n%s NOOP\n", in, Comp, n.f4.accept(this, env), in, out);
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
        String tail = "END";
        ++UsedTemp;
        String getAddr = String.format("MOVE TEMP %d PLUS %s TIMES 4 PLUS %s 1\n", UsedTemp - 1, n.f0.accept(this, env), n.f2.accept(this, env));
        String getValue = String.format("HLOAD TEMP %d TEMP %d 0\n", UsedTemp, UsedTemp - 1);
        String retValue = String.format("RETURN TEMP %d\n", UsedTemp--);
        return head + getAddr + getValue + retValue + tail;
    }

    public String visit(ArrayLength n, MType env) {
        String getValue = String.format("HLOAD TEMP %d %s 0\n", UsedTemp++, n.f0.accept(this, env));
        String retValue = String.format("RETURN TEMP %d\n", --UsedTemp);
        return String.format("BEGIN\n%s%sEND", getValue, retValue);
    }

    public String visit(MessageSend n, MType env) {
        // get addr of primary expression
        // find method entry of this object
        // calculate parameters
        // call the func
        String ret = "";
        int ObjecttableTemp = UsedTemp++;
        int MethodtableTemp = UsedTemp++;
        int MethodTemp = UsedTemp++;
        ret += "CALL\n";
        ret += String.format("BEGIN\nMOVE TEMP %d %s\n", ObjecttableTemp, n.f0.accept(this, env));
        ret += String.format("HLOAD TEMP %d TEMP %d 0\n", MethodtableTemp, ObjecttableTemp);
        TypeVisitor v = new TypeVisitor(global);
        String className = n.f0.accept(v, env);
        MClass c = this.global.queryClass(className);
        // System.out.println(String.format("ClassName: %s", className));
        String methodName = n.f2.f0.toString();
        int i = 0;
        Enumeration method = c.memberMethods.keys();
        while (c.memberMethods.get(method.nextElement()).name != methodName)
            i += 1;
        ret += String.format("HLOAD TEMP %d TEMP %d %d\n", MethodTemp, MethodtableTemp, i * 4);
        ret += String.format("RETURN TEMP %d\n", MethodTemp);
        String args = n.f4.accept(this, env);
        ret += String.format("END\n(TEMP %d%s)", ObjecttableTemp, args == "" ? "" : " " + args);
        UsedTemp -= 3;
        return ret;
    }

    public String visit(ExpressionList n, MType env) {
        return n.f0.accept(this, env) + n.f1.accept(this, env);
    }

    public String visit(ExpressionRest n, MType env) {
        return " " + n.f1.accept(this, env);
    }

    public String visit(PrimaryExpression n, MType env) {
        return n.f0.accept(this, env);    
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
        String id = n.f0.toString();
        MMethod method = (MMethod)env;
        int i = 0;
        for (; i < method.params.size(); ++i) {
            if (method.params.get(i).name.equals(id)) {
                return String.format("TEMP %d", i + 1);
            }
        }
        MVar var = method.queryVar(id);
        if (method.varsTable.containsKey(id)) {
            return String.format("TEMP %d", getOffSet(method, var));
        }
        MClass classEnv = method.owner;
        if (classEnv.queryVar(id) != null) {
            // get class variable address by this pointer in TEMP 0
            // return TEMP 0 + certain offset
            MClass c = ((MMethod)env).owner;
            Enumeration vars = c.memberVars.keys();
            i = 0;
            while (c.memberVars.get(vars.nextElement()).name != id)
                i = i + 1;
            int returnTemp = UsedTemp;
            return String.format("BEGIN\nHLOAD TEMP %d TEMP 0 %d\nRETURN TEMP %d\nEND", returnTemp, 4 * (i + 1), returnTemp);
        }
        return "\n\n??? in visit Identifier\n\n";
    }

    public String visit(ThisExpression n, MType env) {
        return "TEMP 0";
    }

    public String visit(ArrayAllocationExpression n, MType env) {
        String head = "BEGIN\n";
        String tail = "END";
        int sizeTemp = UsedTemp++;
        String getSize = String.format("MOVE TEMP %d TIMES 4 %s\n", sizeTemp, n.f3.accept(this, env));
        int addrTemp = UsedTemp++;
        String getAddr = String.format("MOVE TEMP %d HALLOCATE PLUS 4 TEMP %d\n", addrTemp, sizeTemp);
        String setLen = String.format("HSTORE TEMP %d 0 TEMP %d\n", addrTemp, sizeTemp);
        String ret = String.format("RETURN TEMP %d\n", addrTemp);
        UsedTemp -= 2;
        return head + getSize + getAddr + setLen + ret + tail;
    }

    public String visit(AllocationExpression n, MType env) {
        //  check what class type
        //  construct DTable and VTable
        //  return addr of DTable
        String ret = "";
        int ObjecttableTemp = UsedTemp++;
        int MethodtableTemp = UsedTemp++;
        // ret += String.format("MOVE TEMP %d\n", ObjecttableTemp);
        ret += "BEGIN\n";
        String className = n.f1.f0.toString();
        MClass c = this.global.queryClass(className);
        Enumeration methods = c.memberMethods.keys();
        int methodSize = c.memberMethods.size();
        ret += String.format("MOVE TEMP %d HALLOCATE %d\n", MethodtableTemp, methodSize * 4);
        for (int i = 0; i < methodSize; ++i) {
            MMethod method = c.memberMethods.get(methods.nextElement());
            ret += String.format("HSTORE TEMP %d %d %s_%s\n",
                                 MethodtableTemp, 4 * i, c.name, method.name);
        }
        Enumeration vars = c.memberVars.keys();
        int varSize = c.memberVars.size();
        ret += String.format("MOVE TEMP %d HALLOCATE %d\n", ObjecttableTemp, varSize * 4 + 4);
        for (int i = 1; i <= varSize; ++i) {
            ret += String.format("HSTORE TEMP %d %d 0\n", ObjecttableTemp, 4 * i);
        }
        ret += String.format("HSTORE TEMP %d 0 TEMP %d\n", ObjecttableTemp, MethodtableTemp);
        ret += String.format("RETURN TEMP %d\nEND", ObjecttableTemp);
        UsedTemp -= 2;
        return ret;
    }

    public String visit(NotExpression n, MType env) {
        return "MINUS 1 " + n.f1.accept(this, env);
    }

    public String visit(BracketExpression n, MType env) {
        return n.f1.accept(this, env);
    }
}