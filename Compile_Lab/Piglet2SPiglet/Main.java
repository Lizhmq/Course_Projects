import java.io.*;

import visitor.*;
import syntaxtree.*;
import java.util.*;
import utils.*;

public class Main {
	public static void main(String args[]) {
		try {
			InputStream in = new FileInputStream(args[0]);
			Node root = new PigletParser(in).Goal();
			MaxTempVisitor tmpVis = new MaxTempVisitor();
			root.accept(tmpVis, 0);
			SpigletVisitor vis = new SpigletVisitor(tmpVis.MaxTemp + 1);
			SpigletResult res = root.accept(vis, true);
			String spig = vis.result.toString();
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