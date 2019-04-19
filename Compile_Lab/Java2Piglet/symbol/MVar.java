package symbol;
import java.util.*;


public class MVar extends MType {
	public String varType;
	public MType owner;
	public boolean init;

	public MVar(String varType, MType varID, MType owner, boolean init) {
		super(varID);
		this.varType = varType;
		this.owner = owner;
		this.init = init;
	}
}
