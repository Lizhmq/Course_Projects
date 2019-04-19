package symbol;
import java.util.*;

public class MType {
	public String name;
	public MType() {}
	public MType(String _name) {
		this.name = _name;
	}
	public MType(MType mt) {
		this.name = mt.name;
	}
}
