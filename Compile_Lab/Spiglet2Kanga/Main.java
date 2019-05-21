import java.io.*;

import visitor.*;
import syntaxtree.*;
import java.util.*;
import utils.*;

// Usage:	java Main sample.pg out.spg

public class Main {
	public static void main(String args[]) {
		try {
			InputStream in = new FileInputStream(args[0]);
			Node root = new PigletParser(in).Goal();
			
			GraphVertexVisitor VVis = new GetGraphVertex();
			root.accept(VVis);


			new SpigletParser(input);
			Node AST = SpigletParser.Goal();
			// visit 1: Get Flow Graph Vertex
			AST.accept(new GetFlowGraphVertex());
			// visit 2: Get Flow Graph
			AST.accept(new GetFlowGraph());
			// Linear Scan Algorithm on Flow Graph
			new Temp2Reg().LinearScan();
			// visit 3: Spiglet->Kanga
			AST.accept(new Spiglet2Kanga());


			String outputfile = null;
			if (args.length > 1) {
				outputfile = args[1];
			} else {
				outputfile = "Spiglet.txt";
			}
			OutputStream out = new FileOutputStream(outputfile);
			out.write(spig.getBytes());
			System.out.println(String.format("Spiglet code generated -- \"%s\".", outputfile));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}