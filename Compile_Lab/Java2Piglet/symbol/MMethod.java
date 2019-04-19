package symbol;
import java.util.*;


public class MMethod extends MType {
	public MClass owner;
	public String returnType;
	public Vector<MVar> params = new Vector<MVar>();
	public Vector<MVar> otherLocalVars = new Vector<MVar>();
	public Hashtable<String, MVar> varsTable = new Hashtable<String, MVar>();

	public boolean insertParam(MVar param) {
		if (varsTable.containsKey(param.name)) {
			return false;
		} else {
			varsTable.put(param.name, param);
			params.addElement(param);
			return true;
		}
	}

	public boolean insertVar(MVar var) {
		if (varsTable.containsKey(var.name)) {
			return false;
		} else {
			varsTable.put(var.name, var);
			otherLocalVars.addElement(var);
			return true;
		}
	}

	public MMethod(String returnType, MType methodm, MClass owner) {
		super(methodm);
		this.owner = owner;
		this.returnType = returnType;
	}

	public MVar queryVar(String varName) {
		if (varsTable.containsKey(varName))
			return varsTable.get(varName);
		else
			return owner.queryVar(varName);
	}
}