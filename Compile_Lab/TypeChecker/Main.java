import java.io.*;
import symbol.*;
import visitor.*;
import syntaxtree.*;
import java.util.*;

public class Main {
	public static void main(String args[]) {
		try {
			InputStream in = new FileInputStream(args[0]);
			Node root = new MiniJavaParser(in).Goal();
			MType classListTable = new MClassList("sys", null);

			// Build our global symbol table
			root.accept(new BuildSymbolTableVisitor(), classListTable);
			if (!Main.circle((MClassList)classListTable)) {
				System.out.println(String.format("Syntax error: circle in succession."));
				return;
			}
			root.accept(new UndefinedVisitor(), classListTable);
			root.accept(new TypeMatchVisitor(), classListTable);
			// Check circle in succession
			
			System.out.println(String.format("Program exits normally. No type error found in %s.", args[0]));

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public static boolean circle(MClassList c) {
		HashSet<String> s = new HashSet<String>();
		for (MType v : c.members) {
			if(s.contains(v.name)) {
				continue;
			} else {
				String root = v.name;
				MType p = v;
				while (p.father != null && !s.contains(p.father)) {
					MType f = c.membersHasThis(p.father);
					if (f == null) {
						System.out.println(String.format("Unknown class: %s", p.father));
						System.exit(0);
					}
					p = f;
					if (p.name.equals(root)) {
						System.out.println("Circle in succession!");
						System.out.println(String.format("Referenced class :%s", root));
						return false;
					}
				}
				p = v;
				s.add(p.name);
				while (p.father != null && !s.contains(p.father)) {
					p = c.membersHasThis(p.father);
					s.add(p.name);
				}
			}
		}
		return true;
	}
}