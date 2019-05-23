package utils;
import java.util.*;

public class Method {
	public String methodName;
	public int paramNum, stackNum = 0, callParamNum = 0;

	// t0-t9
	public HashMap<String, String> regT = new HashMap<String, String>();
	// s0-s7
	public HashMap<String, String> regS = new HashMap<String, String>();
	// SPILLEDARG *
	public HashMap<String, String> regSpilled = new HashMap<String, String>();
	// tempNo -> Interval
	public HashMap<Integer, Interval> mTemp = new HashMap<Integer, Interval>();

	public Graph graph = new Graph();

	public Method(String methodName, int paramNum) {
		this.methodName = methodName;
		this.paramNum = paramNum;
	}
}
