package visitor;
import syntaxtree.*;
import java.util.*;
import symbol.*;


// return value: String is the type of TreeNode
// check if Type matches
public class TypeMatchVisitor extends GJDepthFirst<String, MType> {
	
	/* 
	 * 
	 */
	// for ExpressionList
	public String visit(NodeListOptional n, MType table) {
		if ( n.present() ) {
			String _ret = "";
			// int _count = 0;
			for ( Enumeration<Node> e = n.elements(); e.hasMoreElements(); ) {
				_ret = _ret + "," + e.nextElement().accept(this, table);
				// _count++;
			}
			return _ret;
		}
		else
			return null;
	}

	public String visit(MainClass n, MType table) {
		String _ret = null;
		n.f0.accept(this, table);
		n.f1.accept(this, table);
		MType mainfunc = table.membersHasThis(
			((Identifier)(n.f1)).f0.toString()).membersHasThis("main");
		n.f2.accept(this, table);
		n.f3.accept(this, table);
		n.f4.accept(this, table);
		n.f5.accept(this, table);
		n.f6.accept(this, table);
		n.f7.accept(this, table);
		n.f8.accept(this, table);
		n.f9.accept(this, table);
		n.f10.accept(this, table);
		n.f11.accept(this, mainfunc);
		n.f12.accept(this, table);
		n.f13.accept(this, table);
		n.f14.accept(this, mainfunc);
		n.f15.accept(this, mainfunc);
		n.f16.accept(this, table);
		n.f17.accept(this, table);
		return _ret;
	}
	public String visit(ClassDeclaration n, MType table) {
		String _ret = null;
		n.f0.accept(this, table);
		n.f1.accept(this, table);
		MType newClass = table.membersHasThis(((Identifier)(n.f1)).f0.toString());
		n.f2.accept(this, table);
		n.f3.accept(this, newClass);
		n.f4.accept(this, newClass);
		n.f5.accept(this, table);
		return _ret;
	}
	public String visit(ClassExtendsDeclaration n, MType table) {
		String _ret = null;
		n.f0.accept(this, table);
		n.f1.accept(this, table);
		MType newClass = table.membersHasThis(((Identifier)(n.f1)).f0.toString());
		n.f2.accept(this, table);
		n.f3.accept(this, table);
		n.f4.accept(this, table);
		n.f5.accept(this, newClass);
		n.f6.accept(this, newClass);
		n.f7.accept(this, table);
		return _ret;
	}
	// no need to override VarDeclaration
	public String visit(MethodDeclaration n, MType table) {
		String _ret = null;
		n.f0.accept(this, table);
		n.f1.accept(this, table);
		n.f2.accept(this, table);
		String name = ((Identifier)n.f2).f0.toString();
		MType method = table.membersHasThis(name);
		n.f3.accept(this, table);
		n.f4.accept(this, method);
		n.f5.accept(this, table);
		n.f6.accept(this, table);
		n.f7.accept(this, method);
		n.f8.accept(this, method);
		n.f9.accept(this, table);
		n.f10.accept(this, method);
		n.f11.accept(this, table);
		n.f12.accept(this, table);
		return _ret;
	}	
	// no need to override Type -> ...
	public String visit(Statement n, MType table) {
		return n.f0.accept(this, table);
	}
	// Type equals Assignment.f0(Identifier) & Assignment.f2(Expression)
	public String visit(AssignmentStatement n, MType table) {
		String _ret = null;
		String type1 = n.f0.accept(this, table);
		n.f1.accept(this, table);
		String type2 = n.f2.accept(this, table);
		if (type1 == null || !type1.equals(type2)) {
			System.out.println(String.format(
				"Type doesn't match: \"%s\", \"%s\" in Assignment",
				n.f0.toString(), n.f2.toString()
			));
			System.exit(0);
		}
		n.f3.accept(this, table);
		return _ret;
	}
	// ArrayAssign type -- Array type can only be "int" in minijava
	public String visit(ArrayAssignmentStatement n, MType table) {
		String _ret = null;
		// String type1 = n.f0.accept(this, table);
		String type1 = "int";
		String type2 = n.f5.accept(this, table);
		if (type1 == null || !type1.equals(type2)) {
			System.out.println(String.format(
				"Type doesn't match: \"%s\", \"%s\" in ArrayAssignment",
				n.f0.toString(), n.f5.toString()
			));
			System.exit(0);
		}
		String type3 = n.f2.accept(this, table);
		if (type3 == null || !type3.equals("int")) {
			System.out.println(String.format(
				"Expression \"%s\" in ArrayAssignment is not of type \"int\"",
				n.f2.toString(), n.f5.toString()
			));
			System.exit(0);
		}
		return _ret;
	}
	// IfStatement.f2(Expression) must be bool
	public String visit(IfStatement n, MType table) {
		String _ret = null;
		n.f0.accept(this, table);
		n.f1.accept(this, table);
		String type = n.f2.accept(this, table);
		if (type == null || !type.equals("boolean")) {
			System.out.println(String.format(
				"Expression \"%s\" in IfStatement is not of type \"boolean\"",
				((Expression)(n.f2)).toString()
			));
			System.exit(0);
		}
		n.f3.accept(this, table);
		n.f4.accept(this, table);
		n.f5.accept(this, table);
		n.f6.accept(this, table);
		return _ret;
	}
	// WhileStatement.f2(Expression) must be bool
	public String visit(WhileStatement n, MType table) {
		String _ret = null;
		n.f0.accept(this, table);
		n.f1.accept(this, table);
		String type = n.f2.accept(this, table);
		if (type == null || !type.equals("boolean")) {
			System.out.println(String.format(
				"Expression \"%s\" in WhileStatement is not of type \"boolean\"",
				((Expression)(n.f2)).toString()
			));
			System.exit(0);
		}
		n.f3.accept(this, table);
		n.f4.accept(this, table);
		return _ret;
	}
	// PrintStatement.f2(Expression) must be int
	public String visit(PrintStatement n, MType table) {
		String _ret = null;
		n.f0.accept(this, table);
		n.f1.accept(this, table);
		String type = n.f2.accept(this, table);
		System.out.println(type);
		if (type == null || !type.equals("int")) {
			System.out.println(String.format(
				"Expression \"%s\" in PrintStatement is not of type \"int\"",
				((Expression)(n.f2)).toString()
			));
			System.exit(0);
		}
		n.f3.accept(this, table);
		n.f4.accept(this, table);
		return _ret;
	}
	public String visit(Expression n, MType table) {
		return n.f0.accept(this, table);
	}
	// ArrayLookup.f2(PrimaryExpression) must be int
	// Array type can only be "int" in minijava
	public String visit(ArrayLookup n, MType table) {
		String _ret = null;
		// _ret = n.f0.accept(this, table);
		_ret = "int";
		n.f1.accept(this, table);
		String type = n.f2.accept(this, table);
		if (type == null || !type.equals("int")) {
			System.out.println(String.format(
				"PrimaryExpression \"%s\" in ArrayLookup is not of type \"int\"",
				n.f2.toString()
			));
			System.exit(0);
		}
		n.f3.accept(this, table);
		return _ret;
	}
	/*
	 * Deal with Identifier
	 */
	public String visit(Identifier n, MType table) {
		String _ret = null;
		MType a = null, b = null;
		MType curtable;
		curtable = table;
		while (a == null && curtable != null) {
			if (curtable.members != null)
				a = curtable.membersHasThis(n.f0.toString());
			curtable = curtable.parent;
		}
		curtable = table;
		while (b == null && curtable != null) {
			if (curtable.vars != null)
				b = curtable.varsHasThis(n.f0.toString());
			curtable = curtable.parent;
		}
		if (a == null && b == null) {
			System.out.println(String.format(
				"Identifier \"%s\" not found in \"%s\".",
				n.f0.toString(), table.name
			));
			System.out.println(String.format("Warning: This shouldn't happen.	-- TypeMatchVisitor"));
			return null;
		}
		// a is Class or MemberFunction
		if (a != null) {
			try {
				a = (MClass)a;
				_ret = a.name;
			}
			catch (Exception e) {
				a = (MMethod) a;
				_ret = a.type;
			}
		}
		if (b != null) {
			_ret = b.type;
		}
		return _ret;
	}
	public String visit(AndExpression n, MType table) {
		return "boolean";
	}
	// CompareExpression args should be "int"
	public String visit(CompareExpression n, MType table) {
		String type1 = n.f0.accept(this, table);
		n.f1.accept(this, table);
		String type2 = n.f2.accept(this, table);
		if (type1 == null || !type1.equals("int")) {
			System.out.println(String.format(
				"Type of Expression \"%s\" in Compare should be int.",
				n.f0.toString()
			));
			System.exit(0);
		}
		if (type2 == null || !type2.equals("int")) {
			System.out.println(String.format(
				"Type of Expression \"%s\" in Compare should be int.",
				n.f2.toString()
			));
			System.exit(0);
		}
		return "boolean";
	}
	// PlusExpression args should be "int"
	public String visit(PlusExpression n, MType table) {
		String type1 = n.f0.accept(this, table);
		n.f1.accept(this, table);
		String type2 = n.f2.accept(this, table);
		if (type1 == null || !type1.equals("int")) {
			System.out.println(String.format(
				"Type of Expression \"%s\" in Plus should be int.",
				n.f0.toString()
			));
			System.exit(0);
		}
		if (type2 == null || !type2.equals("int")) {
			System.out.println(String.format(
				"Type of Expression \"%s\" in Plus should be int.",
				n.f2.toString()
			));
			System.exit(0);
		}
		return "int";
	}
	// MinusExpression args should be "int"
	public String visit(MinusExpression n, MType table) {
		String type1 = n.f0.accept(this, table);
		n.f1.accept(this, table);
		String type2 = n.f2.accept(this, table);
		if (type1 == null || !type1.equals("int")) {
			System.out.println(String.format(
				"Type of Expression \"%s\" in Minus should be int.",
				n.f0.toString()
			));
			System.exit(0);
		}
		if (type2 == null || !type2.equals("int")) {
			System.out.println(String.format(
				"Type of Expression \"%s\" in Minus should be int.",
				n.f2.toString()
			));
			System.exit(0);
		}
		return "int";
	}
	// TimesExpression args should be "int"
	public String visit(TimesExpression n, MType table) {
		String type1 = n.f0.accept(this, table);
		n.f1.accept(this, table);
		String type2 = n.f2.accept(this, table);
		if (type1 == null || !type1.equals("int")) {
			System.out.println(String.format(
				"Type of Expression \"%s\" in Times should be int.",
				n.f0.toString()
			));
			System.exit(0);
		}
		if (type2 == null || !type2.equals("int")) {
			System.out.println(String.format(
				"Type of Expression \"%s\" in Times should be int.",
				n.f2.toString()
			));
			System.exit(0);
		}
		return "int";
	}
	public String visit(ArrayLength n, MType table) {
		return "int";
	}
	// function calls only in MessageSend, check if type Matches
	// so is ExpressionList
	public String visit(MessageSend n, MType table) {
		String classname = n.f0.accept(this, table);
		MType cls = null;
		MType curtable;
		curtable = table;
		while (cls == null && curtable != null) {
			if (curtable.members != null)
				cls = curtable.membersHasThis(classname);
			curtable = curtable.parent;
		}
		if (cls == null) {
			System.out.println(String.format(
				"Class \"%s\" not found in \"%s\" and its parents.",
				classname, table.name
			));
			System.exit(0);
		}
		n.f1.accept(this, table);
		String method_rettype = n.f2.accept(this, cls);
		String method_name = ((Identifier)n.f2).f0.toString();
		MType method = cls.membersHasThis(method_name);
		n.f3.accept(this, table);
		String params = n.f4.accept(this, table);
		if (!((MMethod)method).CheckParamMatch(params)) {
			System.out.println(String.format("Wrong Parameters %s for Method %s.",
				 params, method_name));
			System.exit(0);
		}
		n.f5.accept(this, table);
		return method_rettype;
	}
	//// NodeListOptional
	public String visit(ExpressionList n, MType table) {
		String _ret = null;
		String type0 = n.f0.accept(this, table);
		String rest = n.f1.accept(this, table);
		if (rest != null)
			_ret = type0 + "," + n.f1.accept(this, table);
		else
			_ret = type0;
		return _ret;
	}
	public String visit(ExpressionRest n, MType table) {
		return n.f1.accept(this, table);
	}
	public String visit(PrimaryExpression n, MType table) {
		return n.f0.accept(this, table);
	}
	/*
	 * Deal with PrimaryExpression
	 */
	public String visit(IntegerLiteral n, MType table) {
		return "int";
	}
	public String visit(TrueLiteral n, MType table) {
		return "boolean";
	}
	public String visit(FalseLiteral n, MType table) {
		return "boolean";
	}
	public String visit(ThisExpression n, MType table) {
		return ((MClass)table).name;
	}
	public String visit(AllocationExpression n, MType table) {
		return n.f1.accept(this, table);
	}
	public String visit(ArrayAllocationExpression n, MType table) {
		return "int[]";
	}
	public String visit(NotExpression n, MType table) {
		return "boolean";
	}
	public String visit(BracketExpression n, MType table) {
		return n.f1.accept(this, table);
	}
}