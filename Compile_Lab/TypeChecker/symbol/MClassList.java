package symbol;
import java.util.*;

public class MClassList extends MType {
	// MClassList has a List containing MClass objects
	// which is named "member"
	// name of itself is useless here
	//
	public MClassList(String classListName, MType parent) {
		super(classListName, parent);
		// members = new ArrayList<MType>();
		members = new ArrayList<MClass>();
	}
}