package symbol;
import java.util.*;

public class MClass extends MIdentifier {
	// MClass has a List of MMethod objects
	// 		  and a List of MVar objects
	// their names are "members" and "vars"
	// its "name" is the name of class like "MClass" or "Main"
	// its "father" is its subclass name if it exists.
	//
	public MClass(String classname, MType parent, String fatherName) {
		super(classname, parent);
		members = new ArrayList<MType>();
		vars = new ArrayList<MType>();
		father = fatherName;
	}
}