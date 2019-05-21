package visitor;
import java.util.*;
import syntaxtree.*;
import symbol.*;

public class Java2PigletVisitor extends GJDepthFirst<String, MType> {

    int UsedTemp = 20;

    int UsedLabel = 0;

    int base = 20;

    MClasses global;

    HashMap<String, Integer> varNum = new HashMap<String, Integer>();

    HashMap<String, Integer> methodNum = new HashMap<String, Integer>();

    HashMap<String, HashMap<String, Integer> > VarOffset = new HashMap<String, HashMap<String, Integer> >();

    HashMap<String, HashMap<String, Integer> > MethodOffset = new HashMap<String, HashMap<String, Integer> >();

    void createEntry(String str) {

        if (VarOffset.get(str) != null) {
            return;
        }

        HashMap<String, Integer> varOfs;
        HashMap<String, Integer> methodOfs;

        MClass classEnv = global.queryClass(str);

        int vars = 0, methods = 0;

        if (classEnv.superClassName != null) {
            createEntry(classEnv.superClassName);
            HashMap<String, Integer> superVarOfs = VarOffset.get(classEnv.superClassName);
            HashMap<String, Integer> superMethodOfs = MethodOffset.get(classEnv.superClassName);
            varOfs = new HashMap<String, Integer>(superVarOfs);
            methodOfs = new HashMap<String, Integer>(superMethodOfs);
            vars = (int)varNum.get(classEnv.superClassName);
            methods = (int)methodNum.get(classEnv.superClassName);
        } else {
            varOfs = new HashMap<String, Integer>();
            methodOfs = new HashMap<String, Integer>();
        }

        for (String varName : classEnv.memberVars.keySet()) {
            ++vars;
            varOfs.put(varName, 4 * vars);
        }
        for (String methodName : classEnv.memberMethods.keySet()) {
            if (methodOfs.containsKey(methodName)) {
                continue;
            }
            methodOfs.put(methodName, 4 * methods);
            ++methods;
        }

        VarOffset.put(str, varOfs);
        MethodOffset.put(str, methodOfs);
        varNum.put(str, vars);
        methodNum.put(str, methodOfs.size());

    }

    int getVarOffset(String className, String varName) {
        HashMap varMap = (HashMap) (VarOffset.get(className));
        if (varMap.get(varName) != null) {
            return (int)varMap.get(varName);
        } else {
            System.out.println(String.format("Class: %s, var: %s  not found.", className, varName));
            System.exit(0);
            return -1;
        }
    }

    int getMethodOffset(String className, String methodName) {
        HashMap<String, Integer> methodMap = (MethodOffset.get(className));
        if (methodMap.get(methodName) != null) {
            return (int)methodMap.get(methodName);
        } else {
            System.out.println(String.format("Class: %s, method: %s  not found.", className, methodName));
            System.exit(0);
            return -1;
        }
    }

    void SetHashMap() {
        global.classesTable.keySet().forEach(
            str -> {createEntry(str);}
        );
        // global.classesTable.keySet().forEach(
        //     str -> {
        //         HashMap vMap = (HashMap)VarOffset.get(str);
        //         HashMap mMap = (HashMap)MethodOffset.get(str);
        //         System.out.println(String.format("Class: %s", str));
        //         for (Object o : vMap.keySet()) {
        //             System.out.println(String.format("\t\t%s", (String)o));
        //         }
        //         for (Object o : mMap.keySet()) {
        //             System.out.println(String.format("\t%s", (String)o));
        //         }
        //     }
        // );
    }

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
        SetHashMap();
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

    // params: TEMP 0 for THIS, TEMP 1-18 for params 0-17, TEMP 19 for more params
    public String visit(MethodDeclaration n, MType env) {
        String methodName = n.f2.f0.toString();
        MClass classEnv = (MClass) env;
        String className = classEnv.name;
        MMethod methodEnv = classEnv.queryMethod(methodName);
        int param = methodEnv.params.size();
        String head = String.format("%s_%s [%d]\nBEGIN\n", className, methodName, param + 1 < 20 ? param + 1 : 20);
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
                if (i < 18) {
                    return String.format("MOVE %s %s", n.f0.accept(this, env), n.f2.accept(this, env));
                }
                else {
                    return String.format("HSTORE TEMP 19 %d %s", (i - 18) * 4, n.f2.accept(this, env));
                }
            }
        }
        if (method.varsTable.containsKey(id))
            return String.format("MOVE %s %s", n.f0.accept(this, env), n.f2.accept(this, env));
        MClass classEnv = method.owner;
        // if (classEnv.queryVar(id) != null) {
        int offset = getVarOffset(classEnv.name, id);
        if (offset > 0) {
            // get class variable address by this pointer in TEMP 0
            // return TEMP 0 + certain offset
            // System.out.println("Found class var: " + id);
            // Enumeration vars = classEnv.memberVars.keys();
            // int i = 0;
            // String ccc;
            // while (true) {
            //     if (!vars.hasMoreElements()) {
            //         System.out.println("class var set of " + classEnv.name + " didn't find " + id);
            //         System.exit(0);
            //     }
            //     ccc = vars.nextElement().toString();
            //     if (ccc.equals(id)) {
            //         break;
            //     }
            //     ++i;
            // }
            return String.format("HSTORE TEMP 0 %d %s", offset, n.f2.accept(this, env));
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
        // MMethod method = c.queryMethod(methodName);
        // int i = 0;
        // Enumeration method = c.memberMethods.keys();
        // while (c.memberMethods.get(method.nextElement()).name != methodName)
        //     i += 1;
        int offset = getMethodOffset(className, methodName);
        ret += String.format("HLOAD TEMP %d TEMP %d %d\n", MethodTemp, MethodtableTemp, offset);
        ret += String.format("RETURN TEMP %d\n", MethodTemp);
        String args = n.f4.accept(this, env);
        ret += String.format("END\n(TEMP %d%s)", ObjecttableTemp, args == null ? "" : " " + args);
        UsedTemp -= 3;
        return ret;
    }

    public String visit(ExpressionList n, MType env) {
        String ret = n.f0.accept(this, env) + n.f1.accept(this, env);
        String[] ss = ret.split(",");
        if (ss.length <= 18)
            return ret.replace(',', ' ');
        String sret = ss[0];
        for (int i = 1; i < 18; ++i)
            sret = sret + " " + ss[i];
        int allocTemp = UsedTemp++;
        int allocSize = (ss.length - 18) * 4;
        String allocStmt = String.format("MOVE TEMP %d HALLOCATE %d", allocTemp, allocSize);
        String storeStmt = "";
        for (int i = 18; i < ss.length; ++i) {
            storeStmt += String.format("HSTORE TEMP %d %d %s\n", allocTemp, (i - 18) * 4, ss[i]);
        }
        UsedTemp--;
        return String.format("%s BEGIN\n%s\n%s RETURN TEMP %d END", sret, allocStmt, storeStmt, allocTemp);
    }

    public String visit(ExpressionRest n, MType env) {
        return "," + n.f1.accept(this, env);
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
                if (i < 18) {
                    return String.format("TEMP %d", i + 1);
                }
                else {
                    return String.format("BEGIN HLOAD TEMP %d TEMP 19 %d RETURN TEMP %d END", UsedTemp, (i - 18) * 4, UsedTemp);
                }
            }
        }
        MVar var = method.queryVar(id);
        if (method.varsTable.containsKey(id)) {
            return String.format("TEMP %d", getOffSet(method, var));
        }
        MClass classEnv = method.owner;
        int offset = getVarOffset(classEnv.name, id);
        //if (classEnv.queryVar(id) != null) {
        if (offset > 0) {
            // get class variable address by this pointer in TEMP 0
            // return TEMP 0 + certain offset
            // MClass c = ((MMethod)env).owner;
            // Enumeration vars = c.memberVars.keys();
            // i = 0;
            // while (c.memberVars.get(vars.nextElement()).name != id)
            //     i = i + 1;
            int returnTemp = UsedTemp;
            return String.format("BEGIN\nHLOAD TEMP %d TEMP 0 %d\nRETURN TEMP %d\nEND", returnTemp, offset, returnTemp);
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
        String getSize = String.format("MOVE TEMP %d %s\n", sizeTemp, n.f3.accept(this, env));
        int addrTemp = UsedTemp++;
        String getAddr = String.format("MOVE TEMP %d HALLOCATE PLUS 4 TIMES 4 TEMP %d\n", addrTemp, sizeTemp);
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
        // MClass c = this.global.queryClass(className);
        // Enumeration methods = c.memberMethods.keys();
        // int methodSize = c.memberMethods.size();
        int methodSize = (int)methodNum.get(className);
        ret += String.format("MOVE TEMP %d HALLOCATE %d\n", MethodtableTemp, methodSize * 4);
        HashMap<String, Integer> methods = MethodOffset.get(className);
        // for (int i = 0; i < methodSize; ++i) {
        //     MMethod method = c.memberMethods.get(methods.nextElement());
        //     ret += String.format("HSTORE TEMP %d %d %s_%s\n",
        //                          MethodtableTemp, 4 * i, c.name, method.name);
        // }
        for (Object o : methods.keySet()) {
            String methodName = (String) o;
            int offset = getMethodOffset(className, methodName);
            String cstr = className;
            while (!global.queryClass(cstr).memberMethods.keySet().contains(methodName)) {
                cstr = global.queryClass(cstr).superClassName;
            }
            ret += String.format("HSTORE TEMP %d %d %s_%s\n",
                                MethodtableTemp, offset, cstr, methodName);
        }
        // Enumeration vars = c.memberVars.keys();
        // int varSize = c.memberVars.size();
        int varSize = (int)varNum.get(className);
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