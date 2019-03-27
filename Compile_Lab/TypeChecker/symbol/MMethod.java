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
	public MMethod(MMethod mth) {
		super((MIdentifier)mth);
		vars = new ArrayList<MType>(mth.vars);
		params = new ArrayList<String>(mth.params);
		type = mth.type;
	}
	public String ConcatParams() {
		String _ret = "";
		boolean findany = false;
		for (String param : params) {
			if (findany)
				_ret = _ret + "," + param;
			else
				_ret = param;
			findany = true;
		}
		return _ret;
	}
	public boolean CheckParamMatch(String s) {
		String params = ConcatParams();
		// if (params == null)
		// 	System.out.println(String.format("%s", this.name));
		if (s == null)
			return params == null;
		return params.equals(s);
	}
	// method parameter types setup must be done by user
}