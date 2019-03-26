package visitor;
import syntaxtree.*;
import java.util.*;
import symbol.*;

public class UndefinedVisitor extends GJDepthFirst<String, MType> {
	public String visit (MainClass n, MType argu) {
		n.f0.accept(this, argu);
		//n.f1.accept(this, argu);
		n.f2.accept(this, argu);
		n.f3.accept(this, argu);
		n.f4.accept(this, argu);
		n.f5.accept(this, argu);
		n.f6.accept(this, argu);
		n.f7.accept(this, argu);
		n.f8.accept(this, argu);
		n.f9.accept(this, argu);
		n.f10.accept(this, argu);
		//n.f11.accept(this, argu);
		n.f12.accept(this, argu);
		n.f13.accept(this, argu);
		MType mainfunc = argu.membersHasThis(
			((Identifier)(n.f1)).f0.toString()).membersHasThis("main");
		n.f14.accept(this, mainfunc);
		n.f15.accept(this, mainfunc);
		n.f16.accept(this, argu);
		n.f17.accept(this, argu);
		return "";
	}
	public String visit(ClassDeclaration n, MType table) {
		n.f0.accept(this, table);
		//n.f1.accept(this, table);
		MType newClass = table.membersHasThis(((Identifier)(n.f1)).f0.toString());
		n.f2.accept(this, table);
		n.f3.accept(this, newClass);
		n.f4.accept(this, newClass);
		n.f5.accept(this, table);
		return "";
	}
	public String visit(ClassExtendsDeclaration n, MType table) {
		n.f0.accept(this, table);
		//n.f1.accept(this, table);
		n.f2.accept(this, table);
		//n.f3.accept(this, table);
		MType newClass = table.membersHasThis(((Identifier)(n.f1)).f0.toString());
		n.f4.accept(this, table);
		n.f5.accept(this, newClass);
		n.f6.accept(this, newClass);
		n.f7.accept(this, table);
		return "";
	}
	public String visit(MethodDeclaration n, MType table) {
		n.f0.accept(this, table);
		n.f1.accept(this, table);
		//n.f2.accept(this, table);
		String name = ((Identifier)n.f2).f0.toString();
		MType method = table.membersHasThis(name);
		n.f3.accept(this, table);
		//n.f4.accept(this, method);
		n.f5.accept(this, table);
		n.f6.accept(this, table);
		n.f7.accept(this, method);
		n.f8.accept(this, method);
		n.f9.accept(this, table);
		n.f10.accept(this, method);
		n.f11.accept(this, table);
		n.f12.accept(this, table);
		return "";
	}
	public String visit(AssignmentStatement n, MType argu) {
		String name = ((Identifier)(n.f0)).f0.toString();
		if (argu.varsHasThis(name) == null) {
			MType parent = argu.parent;
			if (parent.varsHasThis(name) == null) {
				System.out.println(String.format("Undefined variable: %s", name));
				System.exit(0);
			}
		}
		n.f0.accept(this, argu);
		n.f1.accept(this, argu);
		n.f2.accept(this, argu);
		n.f3.accept(this, argu);
		return "";
	}
	public String visit(ArrayAssignmentStatement n, MType argu) {
		String name = ((Identifier)(n.f0)).f0.toString();
		if (argu.varsHasThis(name) == null) {
			MType parent = argu.parent;
			if (parent.varsHasThis(name) == null) {
				System.out.println(String.format("Undefined variable: %s", name));
				System.exit(0);
			}
		}
		n.f0.accept(this, argu);
		n.f1.accept(this, argu);
		n.f2.accept(this, argu);
		n.f3.accept(this, argu);
		n.f4.accept(this, argu);
		n.f5.accept(this, argu);
		n.f6.accept(this, argu);
		return "";
	}
	//
	// is it enough to just do this?
	// seem not!
	public String visit(Identifier n, MType argu) {
		String name = n.f0.toString();
		n.f0.accept(this, argu);
		return name;
	}
	public String visit(ArrayType n, MType argu) {
		n.f0.accept(this, argu);
		n.f1.accept(this, argu);
		n.f2.accept(this, argu);
		return "";
	}
	public String visit(BooleanType n, MType argu) {
		n.f0.accept(this, argu);
		return "";
	}
	public String visit(IntegerType n, MType argu) {
		n.f0.accept(this, argu);
		return "";
	}
	public String visit(Type n, MType argu) {
		String name = n.f0.accept(this, argu);
		if(name != "") {
			while (argu.parent != null) argu = argu.parent;
			if (argu.membersHasThis(name) == null) {
				System.out.println(String.format("Undefined Type %s in %s.", name, argu.name));
				System.exit(0);
			}
		}
		return "";
	}
	public String visit(ThisExpression n, MType argu) {
		n.f0.accept(this, argu);
		return "this";
	}
	public String visit(AllocationExpression n, MType argu) {
		n.f0.accept(this, argu);
		String idn = n.f1.accept(this, argu);
		n.f2.accept(this, argu);
		n.f3.accept(this, argu);
		return "new " + idn;
	}
	public String visit(PrimaryExpression n, MType argu) {
		return n.f0.accept(this, argu);
	}
	public String visit(MessageSend n, MType argu) {
		String name = n.f0.accept(this, argu);
		if (name == null) {
			System.out.println(String.format("Inavalid usage of \'.\' in %s", argu.name));
			System.exit(0);
		}
		MType sym, classOfname;
		// sym = MType Onject of sender
		// classOfname = MClass Object of sym
		MType r = argu;
		while (r.parent != null) r = r.parent;
		if (name == "this") {
			sym = argu.parent;
			//System.out.println(sym.name + " " + sym.type);
			classOfname = r.membersHasThis(sym.name);
		} else if(name.startsWith("new ")) {
			name = name.substring(4);
			classOfname = r.membersHasThis(name);
		} else {
			sym = argu.varsHasThis(name);
			if (sym == null) {
				argu = argu.parent;
				sym = argu.varsHasThis(name);
			}
			if (sym == null) {
				System.out.println(String.format("%s is not found.", name));
				System.exit(0);
			}
			classOfname = r.membersHasThis(sym.type);
		}
		if (classOfname == null) {
			System.out.println(String.format("Invalid usage of \'.\': %s in %s.", name, argu.name));
			System.exit(0);
		}
		n.f1.accept(this, argu);
		String method = n.f2.accept(this, argu);
		if (classOfname.membersHasThis(method) == null) {
			System.out.println(String.format("Object %s doesn\'t has method %s", name, method));
			System.exit(0);
		}
		n.f3.accept(this, argu);
		n.f4.accept(this, argu);
		n.f5.accept(this, argu);
		return "";
	}
}
