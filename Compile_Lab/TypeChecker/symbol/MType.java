package symbol;
import java.util.*;

public class MType {
	public String name, type, father;
	public ArrayList<MType> members, vars;
	public MType parent;
	public MType(String newName, MType itsParent) {
		name = newName;
		parent = itsParent;
	}
	public MType membersHasThis(String candidate) {
		if (members == null) {
			System.err.println(String.format("MType instance %s doesn't has members.",
				name));
			System.exit(1);
		}
		for (MType member : members) {
			if (member.name.equals(candidate)) {
				return member;
			}
		}
		return null;
	}
	public MType varsHasThis(String candidate) {
		if (vars == null) {
			System.err.println(String.format("MType instance %s doesn't has vars.",
				name));
			System.exit(1);
		}
		for (MType var : vars) {
			if (var.name.equals(candidate)) {
				return var;
			}
		}
		return null;
	}
	public MType addMember(String TYPE, String memberName, MType parent, String rtTypeOrFather) {
		if (members == null) {
			System.err.println(String.format("MType instance %s want to add member %s,\n" +
			"but its members is null", name, memberName));
			System.exit(1);
		}
		MType newMember;
		if (TYPE.equals("MClass")){
			newMember = new MClass(memberName, parent, rtTypeOrFather);
		} else {
			// TYPE.equals("MMethod")
			if (!TYPE.equals("MMethod")) {
				System.out.println(String.format("Invalid TYPE: %s", TYPE));
				System.exit(0);
			}
			newMember = new MMethod(memberName, parent, rtTypeOrFather);
		}
		members.add(newMember);
		return newMember;
	}
	public MType addVar(String varName, MType parent, String type) {
		if (vars == null) {
			System.err.println(String.format("MType instance %s want to add var %s,\n" +
			"but its vars is null", name, varName));
			System.exit(1);
		}
		MType newVar = new MVar(varName, parent, type);
		vars.add(newVar);
		return newVar;
	}
	@Override
	public String toString() {
		return name;
	}
}