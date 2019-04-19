package visitor;
import java.util.*;
import symbol.*;
import syntaxtree.*;

public class MakeSymbolTableVisitor extends GJDepthFirst<MType, MType> {
	/**
	 * 
	 * @param argu
	 *            MClass/MMethod/MVar
	 *            Insert it into owner.
	 * @param owner
	 *            MClasses/MClass/MMethod
	 * @return success or not
	 */
	public boolean insert(MType argu, MType owner) {
		if (argu instanceof MClass) {
			if (((MClasses) owner).insertClass((MClass) argu))
				return true;
			else {
				ErrorMsg.addMsg("Class " + argu.name + " has been declared before.");
				return false;
			}
		} else if (argu instanceof MMethod) {
			if (((MClass) owner).insertMethod((MMethod) argu))
				return true;
			else {
				ErrorMsg.addMsg("Method " + argu.name + " has been declared before.");
				return false;
			}
		} else if (argu instanceof MVar) {
			if (((MVar) argu).init) {
				if (((MMethod) owner).insertParam((MVar) argu))
					return true;
				else {
					ErrorMsg.addMsg(
							"Parameter " + argu.name + " has been declared before.");
					return false;
				}
			} else {
				if (owner instanceof MMethod) {
					if (((MMethod) owner).insertVar((MVar) argu))
						return true;
					else {
						ErrorMsg.addMsg(
								"Variable " + argu.name + " has been declared before.");
						return false;
					}
				} else if (owner instanceof MClass) {
					if (((MClass) owner).insertVar((MVar) argu))
						return true;
					else {
						ErrorMsg.addMsg(
								"Variable " + argu.name + " has been declared before.");
						return false;
					}
				}
			}
		}
		return false;
	}

	/**
	 * f0 -> "class"
	 * f1 -> Identifier()
	 * f2 -> "{"
	 * f3 -> "public"
	 * f4 -> "static"
	 * f5 -> "void"
	 * f6 -> "main"
	 * f7 -> "("
	 * f8 -> "String"
	 * f9 -> "["
	 * f10 -> "]"
	 * f11 -> Identifier()
	 * f12 -> ")"
	 * f13 -> "{"
	 * f14 -> ( VarDeclaration() )*
	 * f15 -> ( Statement() )*
	 * f16 -> "}"
	 * f17 -> "}"
	 */
	// insert main class, main method, string[] args
	public MType visit(MainClass n, MType argu) {
		MType _ret = null;
		MType ID = n.f1.accept(this, argu);
		MClass Class = new MClass(ID, (MClasses) argu, null);
		if (insert(Class, argu) == false)
			return _ret;
		MType mainID = new MType("main");
		MMethod method = new MMethod("void", mainID, Class);
		if (insert(method, Class) == false)
			return _ret;
		MType stringID = n.f11.accept(this, argu);
		MVar param = new MVar("String[]", stringID, method, true);
		if (insert(param, method) == false)
			return _ret;
		n.f14.accept(this, method);
		n.f15.accept(this, method);
		return _ret;
	}

	/**
	 * f0 -> "class"
	 * f1 -> Identifier()
	 * f2 -> "{"
	 * f3 -> ( VarDeclaration() )*
	 * f4 -> ( MethodDeclaration() )*
	 * f5 -> "}"
	 */
	// insert class
	public MType visit(ClassDeclaration n, MType argu) {
		MType _ret = null;
		MType ID = n.f1.accept(this, argu);
		MClass Class = new MClass(ID, (MClasses) argu, null);
		if (insert(Class, argu) == false)
			return _ret;
		n.f3.accept(this, Class);
		n.f4.accept(this, Class);
		return _ret;
	}

	/**
	 * f0 -> "class"
	 * f1 -> Identifier()
	 * f2 -> "extends"
	 * f3 -> Identifier()
	 * f4 -> "{"
	 * f5 -> ( VarDeclaration() )*
	 * f6 -> ( MethodDeclaration() )*
	 * f7 -> "}"
	 */
	// insert class with a superClass
	public MType visit(ClassExtendsDeclaration n, MType argu) {
		MType _ret = null;
		MType ID = n.f1.accept(this, argu);
		String superClassName = n.f3.accept(this, argu).name;
		MClass Class = new MClass(ID, (MClasses) argu, superClassName);
		if (insert(Class, argu) == false)
			return _ret;
		n.f5.accept(this, Class);
		n.f6.accept(this, Class);
		return _ret;
	}

	/**
	 * f0 -> Type()
	 * f1 -> Identifier()
	 * f2 -> ";"
	 */
	// insert variable
	public MType visit(VarDeclaration n, MType argu) {
		MType _ret = null;
		String varType = n.f0.accept(this, argu).name;
		MType ID = n.f1.accept(this, argu);
		MVar var = new MVar(varType, ID, argu, false);
		insert(var, argu);
		return _ret;
	}

	/**
	 * f0 -> "public"
	 * f1 -> Type()
	 * f2 -> Identifier()
	 * f3 -> "("
	 * f4 -> ( FormalParameterList() )?
	 * f5 -> ")"
	 * f6 -> "{"
	 * f7 -> ( VarDeclaration() )*
	 * f8 -> ( Statement() )*
	 * f9 -> "return"
	 * f10 -> Expression()
	 * f11 -> ";"
	 * f12 -> "}"
	 */
	// insert method
	public MType visit(MethodDeclaration n, MType argu) {
		MType _ret = null;
		String returnType = n.f1.accept(this, argu).name;
		MType ID = n.f2.accept(this, argu);
		MMethod method = new MMethod(returnType, ID, (MClass) argu);
		if (insert(method, argu) == false)
			return _ret;
		n.f4.accept(this, method);
		n.f7.accept(this, method);
		n.f8.accept(this, method);
		n.f10.accept(this, method);
		return _ret;
	}

	/**
	 * f0 -> Type()
	 * f1 -> Identifier()
	 */
	// insert parameter
	public MType visit(FormalParameter n, MType argu) {
		MType _ret = null;
		String paramType = n.f0.accept(this, argu).name;
		MType ID = n.f1.accept(this, argu);
		MVar param = new MVar(paramType, ID, (MMethod) argu, true);
		insert(param, argu);
		return _ret;
	}

	/**
	 * f0 -> ArrayType()
	 * | BooleanType()
	 * | IntegerType()
	 * | Identifier()
	 */
	// return a type
	public MType visit(Type n, MType argu) {
		MType _ret = n.f0.accept(this, argu);
		return _ret;
	}

	/**
	 * f0 -> "int"
	 * f1 -> "["
	 * f2 -> "]"
	 */
	// return "int[]"
	public MType visit(ArrayType n, MType argu) {
		MType _ret = new MType("int[]");
		return _ret;
	}

	/**
	 * f0 -> "boolean"
	 */
	// return "boolean"
	public MType visit(BooleanType n, MType argu) {
		MType _ret = new MType("boolean");
		return _ret;
	}

	/**
	 * f0 -> "int"
	 */
	// return "int"
	public MType visit(IntegerType n, MType argu) {
		MType _ret = new MType("int");
		return _ret;
	}

	/**
	 * f0 -> <IDENTIFIER>
	 */
	// return Identifier
	public MType visit(Identifier n, MType argu) {
		MType _ret = new MType(n.f0.toString());
		return _ret;
	}
}
