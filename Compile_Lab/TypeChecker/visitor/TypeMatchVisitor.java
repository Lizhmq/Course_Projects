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
	// ArrayLookup.f2(PrimaryExpression) must be int
	public String visit(ArrayLookup n, MType table) {
		String _ret = null;
		_ret = n.f0.accept(this, table);
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
	/*
	 * Deal with Expression
	 */
	////
	public String visit(Identifier n, MType table) {
		String _ret = null;
		MType a = null, b = null;
		if (table.members != null)
			a = table.membersHasThis(n.f0.toString());
		if (table.vars != null)
			b = table.varsHasThis(n.f0.toString());
		if (a == null && b == null) {
			// System.out.println(String.format(
			// 	"Identifier \"%s\" not found.",
			// 	n.f0.toString()
			// ));
			return null;
		}
		// a is Class or MemberFunction
		if (a != null) {
			a = (MClass)a;
			if (a.members == null) {
				a = (MMethod)a;
				_ret = a.type;
			} else {
				_ret = a.name;
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

	public String visit(MessageSend n, MType table) {
		// return n.f0.accept(this, table);
		return "int";
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