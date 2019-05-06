import java.io.*;
import symbol.*;
import visitor.*;
import syntaxtree.*;
import java.util.*;

public class Main {
	public static void main(String args[]) {
		try {
			InputStream in = new FileInputStream(args[0]);
			Node root = new SpigletParser(in).Goal();
			root.accept(new BuildSymbolTableVisitor(), classListTable);
			// root.accept(new UndefinedVisitor(), classListTable);
			System.out.println(String.format("Program exits normally. No type error found in %s.", args[0]));

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}