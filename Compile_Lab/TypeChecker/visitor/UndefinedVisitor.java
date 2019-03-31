package visitor;
import syntaxtree.*;
import java.util.*;
import symbol.*;

public class UndefinedVisitor extends GJDepthFirst<String, MType> {
	public String visit (MainClass n, MType argu) {
		if (argu == null) {
			System.out.println("table = null! in mainclass");
			System.exit(0);
		}
		n.f0.accept(this, argu);
		String mainclass = n.f1.accept(this, argu);
		n.f2.accept(this, argu);
		n.f3.accept(this, argu);
		n.f4.accept(this, argu);
		n.f5.accept(this, argu);
		n.f6.accept(this, argu);
		n.f7.accept(this, argu);
		n.f8.accept(this, argu);
		n.f9.accept(this, argu);
		n.f10.accept(this, argu);
		n.f11.accept(this, argu);
		n.f12.accept(this, argu);
		n.f13.accept(this, argu);
		MType mainfunc = argu.membersHasThis(mainclass).membersHasThis("main");
		n.f14.accept(this, mainfunc);
		n.f15.accept(this, mainfunc);
		n.f16.accept(this, argu);
		n.f17.accept(this, argu);
		return "";
	}
	public String visit(ClassDeclaration n, MType table) {
		if (table == null) {
			System.out.println("table = null! in classdec");
			System.exit(0);
		}
		n.f0.accept(this, table);
		String nameOfClass = n.f1.accept(this, table);
		MType newClass = table.membersHasThis(nameOfClass);
		n.f2.accept(this, table);
		n.f3.accept(this, newClass);
		n.f4.accept(this, newClass);
		n.f5.accept(this, table);
		return "";
	}
	public String visit(ClassExtendsDeclaration n, MType table) {
		if (table == null) {
			System.out.println("table = null! in classextdec");
			System.exit(0);
		}
		n.f0.accept(this, table);
		String nameOfClass = n.f1.accept(this, table);
		n.f2.accept(this, table);
		n.f3.accept(this, table);
		MType newClass = table.membersHasThis(nameOfClass);
		n.f4.accept(this, table);
		n.f5.accept(this, newClass);
		n.f6.accept(this, newClass);
		n.f7.accept(this, table);
		return "";
	}
	public String visit(MethodDeclaration n, MType table) {
		if (table == null) {
			System.out.println("table = null! in methoddec");
			System.exit(0);
		}
		n.f0.accept(this, table);
		n.f1.accept(this, table);
		String methodName = n.f2.accept(this, table);
		MType method = table.membersHasThis(methodName);
		if (method == null) {
			System.out.println(String.format("No method of name: %s in class %s", methodName, table.name));
		}
		MClass classenv = (MClass) table;
		MClassList global = (MClassList) table.parent;
		while (classenv.father != null) {
			//check override in subclass
			classenv = (MClass)(global.membersHasThis(classenv.father));
			MMethod same, me = (MMethod) method;
			if ((same = (MMethod)(classenv.membersHasThis(method.name))) != null) {
				// subclass has a method of same name
				// check its parameter and return type
				if (same.type != me.type) {
					System.out.println(String.format("Method %s override error: return type mismatch.", me.name));
					System.exit(0);
				}
				if (same.params.size() != me.params.size()) {
					System.out.println(String.format("Method %s override error: parameter list length mismatch.", me.name));
					System.exit(0);
				}
				for (int i = 0; i < me.params.size(); ++i) {
					if (!me.params.get(i).equals(same.params.get(i))) {
						System.out.println(String.format("Method %s override error: parameter type mismatch.", me.name));
						System.exit(0);
					}
				}
			}
		}
		n.f3.accept(this, table);
		n.f4.accept(this, method);
		n.f5.accept(this, table);
		n.f6.accept(this, table);
		n.f7.accept(this, method);
		if (method == null) {
			System.out.println(String.format("method = null!"));
		}
		n.f8.accept(this, method);
		n.f9.accept(this, table);
		n.f10.accept(this, method);
		n.f11.accept(this, table);
		n.f12.accept(this, table);
		return "";
	}
	public String visit(Statement n, MType argu) {
		if (argu == null) {
			System.out.println("argu  = null! in statement");
		}
		n.f0.accept(this, argu);
		return "";
	}
	public String visit(AssignmentStatement n, MType argu) {
		if (argu == null) {
			System.out.println("table = null! in assign");
			System.exit(0);
		}
		//System.out.println("Assign");
		String name = n.f0.accept(this, argu);
		if (argu.varsHasThis(name) == null) {
			MType parent = argu.parent;
			if (parent.varsHasThis(name) == null) {
				MClass ClassEnv = (MClass) parent;
				MClassList global = (MClassList) ClassEnv.parent;
				//System.out.println(global.name);
				Boolean a = false;
				while ((ClassEnv = (MClass)(global.membersHasThis(ClassEnv.father))) != null) {
					if (ClassEnv.varsHasThis(name) != null) {
						a = true;
						break;
					}
				}
				if (!a) {
					System.out.println(String.format("Undefined variable: %s in %s", name, argu.name));
					System.exit(0);
				}
			}
		}
		n.f1.accept(this, argu);
		n.f2.accept(this, argu);
		n.f3.accept(this, argu);
		//System.out.println("Assign succeed");
		return "";
	}
	public String visit(ArrayAssignmentStatement n, MType argu) {
		if (argu == null) {
			System.out.println("table = null! in arrayassign");
			System.exit(0);
		}
		//System.out.println("ArrayAssign");
		String name = n.f0.accept(this, argu);
		if (argu.varsHasThis(name) == null) {
			MType parent = argu.parent;
			if (parent.varsHasThis(name) == null) {
				MClass ClassEnv = (MClass) parent;
				MClassList global = (MClassList) ClassEnv.parent;
				Boolean a = false;
				while ((ClassEnv = (MClass)(global.membersHasThis(ClassEnv.father))) != null) {
					if (ClassEnv.varsHasThis(name) != null) {
						a = true;
						break;
					}
				}
				if (!a) {
					System.out.println(String.format("Undefined variable: %s in %s", name, argu.name));
					System.exit(0);
				}
			}
		}
		n.f1.accept(this, argu);
		n.f2.accept(this, argu);
		n.f3.accept(this, argu);
		n.f4.accept(this, argu);
		n.f5.accept(this, argu);
		n.f6.accept(this, argu);
		//System.out.println("ArrayAssign succeed");
		return "";
	}
	//
	// is it enough to just do this?
	// seem not!
	public String visit(ArrayType n, MType argu) {
		n.f0.accept(this, argu);
		n.f1.accept(this, argu);
		n.f2.accept(this, argu);
		return "int[]";
	}
	public String visit(BooleanType n, MType argu) {
		n.f0.accept(this, argu);
		return "boolean";
	}
	public String visit(IntegerType n, MType argu) {
		n.f0.accept(this, argu);
		return "int";
	}
	public String visit(Type n, MType argu) {
		String name = n.f0.accept(this, argu);
		if(name.equals("int") && name.equals("int[]") && name.equals("boolean")) {
			while (argu.parent != null) argu = argu.parent;
			if (argu.membersHasThis(name) == null) {
				System.out.println(String.format("Undefined Type %s in %s.", name, argu.name));
				System.exit(0);
			}
		}
		return name;
	}
	public String visit(Expression n, MType table) {
		if (table == null) {
			System.out.println("table = null! in expression");
			//System.exit(0);
		}
		return n.f0.accept(this, table);
	}
	public String visit(AndExpression n, MType table) {
		n.f0.accept(this, table);
		n.f1.accept(this, table);
		n.f2.accept(this, table);
		return "boolean";
	}
	public String visit(CompareExpression n, MType table) {
		n.f0.accept(this, table);
		n.f1.accept(this, table);
		n.f2.accept(this, table);
		return "boolean";
	}
	public String visit(PlusExpression n, MType table) {
		n.f0.accept(this, table);
		n.f1.accept(this, table);
		n.f2.accept(this, table);
		return "int";
	}
	public String visit(MinusExpression n, MType table) {
		n.f0.accept(this, table);
		n.f1.accept(this, table);
		n.f2.accept(this, table);
		return "int";
	}
	public String visit(TimesExpression n, MType table) {
		n.f0.accept(this, table);
		n.f1.accept(this, table);
		n.f2.accept(this, table);
		return "int";
	}
	public String visit(ArrayLookup n, MType table) {
		n.f0.accept(this, table);
		n.f1.accept(this, table);
		n.f2.accept(this, table);
		n.f3.accept(this, table);
		return "int";
	}
	public String visit(ArrayLength n, MType table) {
		n.f0.accept(this, table);
		n.f1.accept(this, table);
		n.f2.accept(this, table);
		return "int";
	}
	public String visit(MessageSend n, MType argu) {
		String name = n.f0.accept(this, argu);
		if (name.equals("boolean") || name.equals("int") || name.equals("int[]")) {
			System.out.println(String.format("Inavalid usage of \'.\' in %s", argu.name));
			System.exit(0);
		}
		//System.out.println(name);
		MType classOfname = argu, root = argu;
		MType r = argu;
		while (root.parent != null) {
			root = root.parent;
		} 
		while (r.varsHasThis(name) == null && r.parent.parent != null) {
			// find MVar from method to class
			r = r.parent;
			//if (r.parent == null) break;
		}
		//System.out.println("r complete");
		Boolean c = false;
		if (r.varsHasThis(name) == null) {
			// method and class don't have this var
			// search it in subclass
			MClass sym = (MClass) r;
			MClassList Root = (MClassList) root;
			Boolean found = false;
			//System.out.println(Root.name);
			while (sym.father != null) {
				sym = (MClass)(Root.membersHasThis(sym.father));
				if (sym.varsHasThis(name) != null) {
					found = true;
					break;
				}
			}
			if (!found) {
				c = true;
				//System.out.println(String.format("Unknown var: %s in %s", name, argu.name));
			} else {
				classOfname = root.membersHasThis(sym.varsHasThis(name).type);
			}
		} else {
			String vartype = r.varsHasThis(name).type;
			if (vartype.equals("int") || vartype.equals("int[]") || vartype.equals("boolean")) {
				System.out.println(String.format("Inavalid usage of \'.\' for %s in %s", name, argu.name));
				System.exit(0);
			}
			classOfname = root.membersHasThis(r.varsHasThis(name).type);
		}
		if (c) {
			classOfname = root.membersHasThis(name);
			if (classOfname == null) {
				System.out.println(String.format("Unknown class name: %s in %s", name, argu.name));
				System.exit(0);
			}
		}
		//System.out.println("former found");
		//System.out.println(classOfname.name);
		n.f1.accept(this, argu);
		String method = n.f2.accept(this, argu);
		MType Initialclass = classOfname;
		while (classOfname.membersHasThis(method) == null) {
			if (classOfname.father == null) {
				System.out.println(String.format("Object %s of type %s doesn\'t has method %s", name, Initialclass.name, method));
				System.exit(0);
			}
			classOfname = root.membersHasThis(classOfname.father);
		}
		n.f3.accept(this, argu);
		n.f4.accept(this, argu);
		n.f5.accept(this, argu);
		return classOfname.membersHasThis(method).type;
	}
	public String visit(PrimaryExpression n, MType argu) {
		if (argu == null) {
			System.out.println("table = null! in primaryexpression");
			//System.exit(0);
		}
		return n.f0.accept(this, argu);
	}
	public String visit(IntegerLiteral n, MType table) {
		n.f0.accept(this, table);
		return "int";
	}
	public String visit(TrueLiteral n, MType table) {
		n.f0.accept(this, table);
		return "boolean";
	}
	public String visit(FalseLiteral n, MType table) {
		n.f0.accept(this, table);
		return "boolean";
	}
	public String visit(Identifier n, MType argu) {
		String name = n.f0.toString();
		n.f0.accept(this, argu);
		return name;
	}
	public String visit(ThisExpression n, MType argu) {
		if (argu == null) {
			System.out.println("table = null! in thisexpression");
			System.exit(0);
		}
		n.f0.accept(this, argu);
		// check argu is MMethod or MClass
		if (argu.parent.name.equals("sys")) {
			return argu.name;
		} else {
			return argu.parent.name;
		}
	}
	public String visit(ArrayAllocationExpression n, MType argu) {
		if (argu == null) {
			System.out.println("table = null! in arrayallocationexpression");
			System.exit(0);
		}
		n.f0.accept(this, argu);
		n.f1.accept(this, argu);
		n.f2.accept(this, argu);
		n.f3.accept(this, argu);
		n.f4.accept(this, argu);
		return "int[]";
	}
	public String visit(AllocationExpression n, MType argu) {
		n.f0.accept(this, argu);
		String idn = n.f1.accept(this, argu);
		MType root = argu;
		while (root.parent != null) root = root.parent;
		if (root.membersHasThis(idn) == null) {
			System.out.println(String.format("Invalid usage of new: %s is not a class.", idn));
			System.exit(0);
		}
		n.f2.accept(this, argu);
		n.f3.accept(this, argu);
		return idn;
	}
	public String visit(NotExpression n, MType table) {
		n.f0.accept(this, table);
		n.f1.accept(this, table);
		return "boolean";
	}
	public String visit(BracketExpression n, MType table) {
		if (table == null) {
			System.out.println("table = null! in Bracketexpression");
			System.exit(0);
		}
		n.f0.accept(this, table);
		String type = n.f1.accept(this, table);
		n.f2.accept(this, table);
		return type;
	}
}
