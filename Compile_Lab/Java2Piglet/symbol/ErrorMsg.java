package symbol;
import java.util.*;


public class ErrorMsg {
	public static Vector<String> errorMsgs = new Vector<String>();
	public static int verbose = 0; // different output level

	public static void addMsg(String msg) {
                // String errorMsg = "Error at Line " + line + ", Column " + col + ": " + msg;
                String errorMsg = msg;
		errorMsgs.addElement(errorMsg);
		if (ErrorMsg.verbose == 0) {
			System.out.println("Type error");
			System.exit(1);
		}
	}

	public static void print(MClasses allClasses) {
		for (String className : allClasses.classesTable.keySet()) {
			MClass Class = allClasses.classesTable.get(className);
			if (Class.superClassName != null)
				System.out.println("Class \"" + className + "\" : extends \"" + Class.superClassName);
			else
				System.out.println("Class \"" + className);
			for (String varName : Class.memberVars.keySet()) {
				MVar var = Class.memberVars.get(varName);
				System.out.println("  Member variable \"" + varName + "\" of type \"" + var.varType);
			}
			for (String methodName : Class.memberMethods.keySet()) {
				MMethod method = Class.memberMethods.get(methodName);
				System.out.println("  Member method " + methodName + " of return type \"" + method.returnType);
				for (MVar param : method.params) {
					System.out.println("    Parameter of type \"" + param.varType + "\"");
				}
				for (String varName2 : method.varsTable.keySet()) {
					MVar var2 = method.varsTable.get(varName2);
					System.out.println("    Method variable \"" + varName2 + "\" of type \"" + var2.varType);
				}
			}
		}
	}
}
