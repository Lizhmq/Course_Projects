package symbol;
import java.util.*;

public class MMethod extends MIdentifier {
	// MMethod has a List of MVar objects indicating name of variables
	// besides it has a List of params types indicating type of parameters
	// MVar list use the name "vars"
	// params restore parameter type list
	// finally it has a returnType as a String
	//
	public ArrayList<String> params;
	public MMethod(String methodName, MType parent, String returnType) {
		super(methodName, parent);
		//members = new ArrayList<MType>();
		vars = new ArrayList<MType>();
		// vars = new ArrayList<MVar>();
		params = new ArrayList<String>();
		type = returnType;
	}
	// method parameter types setup must be done by user
}