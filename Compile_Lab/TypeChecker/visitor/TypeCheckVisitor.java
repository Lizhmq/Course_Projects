package visitor;
import syntaxtree.*;
import java.util.*;
import typecheck.*;
import symbol.*;

public class TypeCheckVisitor extends DepthFirstVisitor {
	public MType visit(MethodDeclaration n, MType argu) {
		MType _ret = null;
		n.f0.accept(this, argu);
		
		MType type = n.f1.accept(this, argu);
		checkTypeDeclared(type);

		MIdentifier id = (MIdentifier)n.f2.accept(this, argu);
		MMethod newMethod = ((MClass)argu).getMethod(id.getName());

		n.f3.accept(this, newMethod);
		n.f4.accept(this, newMethod);
		n.f5.accept(this, newMethod);
		n.f6.accept(this, newMethod);
		n.f7.accept(this, newMethod);
		n.f8.accept(this, newMethod);
		n.f9.accept(this, newMethod);
		
		MType exp = n.f10.accept(this, newMethod);
		checkExpEqual(exp, type.getType(), "Return expression doesn't match return type");

		n.f11.accept(this, newMethod);
		n.f12.accept(this, newMethod);

		return _ret;
	}

	public void checkTypeDeclared(MType type) {
		String typename = "";
		if (type instanceof MIdentifier) {
			typename = ((MIdentifier)type).getName();
			if (allClassList.containClass(typename)) {
				return;
			}
			else {
				
			}
		}
		else {
			typename = type.getType();
			if (typename.equals("int") || typename.equals("int[]") || typename.equals("boolean")) {
				return;
			}
		}
		ErrorPrinter.print("Undefined type:\'" + typename + "\'", type.getRow(), type.getCol());
	}
}