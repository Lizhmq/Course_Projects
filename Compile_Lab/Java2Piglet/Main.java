import java.io.*;
import symbol.*;
import visitor.*;
import syntaxtree.*;
import java.util.*;

public class Main {
	public static void main(String[] args) {
		try {
			InputStream in = new FileInputStream(args[0]);
			Node root = new MiniJavaParser(in).Goal();
			MClasses allClasses = new MClasses();
			// Build our global symbol table
			root.accept(new MakeSymbolTableVisitor(), allClasses);
			System.out.println(String.format("Program exits normally. No type error found in \"%s\".", args[0]));
			String pigletCode = root.accept(new Java2PigletVisitor(), allClasses);
			String outputfile = null;
			if (args.length > 1) {
				outputfile = args[1];
			} else {
				outputfile = "piglet.txt";
			}
			OutputStream out = new FileOutputStream(outputfile);
			out.write(pigletCode.getBytes());
			System.out.println(String.format("Piglet code generated -- \"%s\".", outputfile));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}