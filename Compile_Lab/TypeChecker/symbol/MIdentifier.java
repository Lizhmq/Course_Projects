package symbol;
import java.util.*;

public class MIdentifier extends MType {
	public MIdentifier(String newName, MType parent) {
		super(newName, parent);
	}
	public MIdentifier(MIdentifier mi) {
		super((MType)mi);
	}
}