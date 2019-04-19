package symbol;
import java.util.*;

public class MClass extends MType {
	// MClass has a List of MMethod objects
	// 		  and a List of MVar objects
	// their names are "memberMethods" and "memberVars"
	// its "name" is the name of class such as "MClass" or "Main"
	// its "superClassName" is its subclass name if it exists.
	//
	public MClasses classesTable;
	public String superClassName;
	public Hashtable<String, MVar> memberVars = new Hashtable<String, MVar>();
	public Hashtable<String, MMethod> memberMethods = new Hashtable<String, MMethod>();

	public MClass(MType name, MClasses classesTable, String superClassName) {
		super(name);
		this.classesTable = classesTable;
		this.superClassName = superClassName;
	}
	public MClass(String name) {
		super(name);
	}

	public boolean insertMethod(MMethod method) {
		if (memberMethods.containsKey(method.name)) {
			return false;
		} else {
			memberMethods.put(method.name, method);
			return true;
		}
	}
	public boolean insertVar(MVar var) {
		if (memberVars.containsKey(var.name)) {
			return false;
		} else {
			memberVars.put(var.name, var);
			return true;
		}
	}

	public MMethod queryMethod(String methodName) {
		if (memberMethods.containsKey(methodName))
			return memberMethods.get(methodName);
		else if (superClassName != null) {
			MClass superClass = classesTable.queryClass(superClassName);
			if (superClass == null)
				return null;
			return superClass.queryMethod(methodName);
		} else
			return null;
	}
	public MVar queryVar(String varName) {
		if (memberVars.containsKey(varName))
			return memberVars.get(varName);
		else if (superClassName != null) {
			MClass superClass = classesTable.queryClass(superClassName);
			if (superClass == null)
				return null;
			return superClass.queryVar(varName);
		} else
			return null;
	}
}