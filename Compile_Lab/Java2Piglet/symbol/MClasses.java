package symbol;
import java.util.*;

public class MClasses extends MType {
	// MClasses has a Hashtable containing MClass objects
	// which is named "classesTable"
	// name of itself is useless here

	public Hashtable<String, MClass> classesTable = new Hashtable<String, MClass>();

	public boolean insertClass(MClass someClass) {
		if (classesTable.containsKey(someClass.name)) {
			return false;
		} else {
			classesTable.put(someClass.name, someClass);
			return true;
		}
	}

	public MClass queryClass(String className) {
		if (classesTable.containsKey(className))
			return classesTable.get(className);
		return null;
	}
}