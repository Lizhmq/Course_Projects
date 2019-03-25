package symbol;
import java.util.*;

public class MVar extends MIdentifier {
	// MVar has no List member
	// it only has its own name and type
	//
	public MVar(String varName, MType parent, String Type) {
		super(varName, parent);
		type = Type;
	}
}