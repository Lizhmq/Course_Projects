package utils;
import java.util.*;

//import spiglet.visitor.GetFlowGraphVertex;

public class Graph {
	public HashSet<GraphVertex> Vertexs = new HashSet<GraphVertex>();
	public HashMap<Integer, GraphVertex> VertexMap = new HashMap<Integer, GraphVertex>();
	public HashSet<Integer> CallPos = new HashSet<Integer>();

	public GraphVertex getVertex(int vid) {
		return VertexMap.get(vid);
	}

	public void addVertex(int vid) {
		GraphVertex v = new GraphVertex(vid);
		this.Vertexs.add(v);
		this.VertexMap.put(vid, v);
	}

	public void addEdge(int src, int dst) {
		GraphVertex s = this.VertexMap.get(src);
		GraphVertex d = this.VertexMap.get(dst);
		s.Succ.add(d);
		d.Pred.add(s);
	}
}