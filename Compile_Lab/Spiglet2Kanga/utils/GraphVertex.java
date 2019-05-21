package utils;
import java.util.*;

import com.sun.tools.jdeps.Graph;

class GraphVertex {
	public int vid;
	public HashSet<GraphVertex> Pred = new HashSet<GraphVertex>();
	public HashSet<GraphVertex> Succ = new HashSet<GraphVertex>();

	// Type var = new var();
	public HashSet<Integer> Def = new HashSet<Integer>();
	// var = 2333;
	public HashSet<Integer> Use = new HashSet<Integer>();
	public HashSet<Integer> In = new HashSet<Integer>();
	public HashSet<Integer> Out = new HashSet<Integer>();

	public GraphVertex(int vid) {
		this.vid = vid;
	}
}