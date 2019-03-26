package visitor;
import syntaxtree.*;
import java.util.*;
import symbol.*;

public class BuildSymbolTableVisitor extends GJVoidDepthFirst<MType> {
	public void visit(VarDeclaration n, MType table) {
		// Identifier id = (Identifier)n.f1;
		// System.out.println("VarName:" + id.f0.toString());
		if (table.varsHasThis(((Identifier)(n.f1)).f0.toString()) != null) {
			System.out.println(String.format(
				"var \"%s\" is declared for more than one times", 
				((Identifier)(n.f1)).f0.toString()));
			System.exit(0);
		}
		// table.addVar(((Identifier)(n.f1)).f0.toString(), 
		// 			table,
		// 			((Identifier)(n.f1)).f0.toString());
		table.addVar(((Identifier)(n.f1)).f0.toString(), table, 
				((Type)(n.f0)).f0.toString());

		n.f0.accept(this, table);
		n.f1.accept(this, table);
		n.f2.accept(this, table);
	}
	/*
		Goal seems to keep its form
	*/
	public void visit(MainClass n, MType table) {
		n.f0.accept(this, table);
		n.f1.accept(this, table);
		// MType mainclass = table.addMember(
			// "MClass", "Main", table, null);
		MType mainclass = table.addMember(
			"MClass", ((Identifier)(n.f1)).f0.toString(), table, null
		);
		n.f2.accept(this, table); 
		n.f3.accept(this, table);
		n.f4.accept(this, table);
		n.f5.accept(this, table);
		n.f6.accept(this, table);
		MMethod mainfunc = (MMethod)mainclass.addMember(
			"MMethod", "main", mainclass, "void");
		n.f7.accept(this, table);
		n.f8.accept(this, table);
		n.f9.accept(this, table);
		n.f10.accept(this, table);
		n.f11.accept(this, table);
		mainfunc.addVar(((Identifier)(n.f11)).f0.toString(), 
			mainfunc, "String[]");
		mainfunc.params.add("String[]");
		n.f12.accept(this, table);
		n.f13.accept(this, table);
		n.f14.accept(this, mainfunc);
		n.f15.accept(this, mainfunc);
		n.f16.accept(this, table);
		n.f17.accept(this, table);
	}
	public void visit(ClassDeclaration n, MType table) {
		n.f0.accept(this, table);
		n.f1.accept(this, table);
		if(table.membersHasThis(((Identifier)(n.f1)).f0.toString()) != null){
			System.out.println(String.format(
				"class \"%s\" is declared for more than one times", 
				((Identifier)(n.f1)).f0.toString()));
			System.exit(0);
		}
		table = (MClassList) table;
		MType newClass = table.addMember("MClass", 
			((Identifier)(n.f1)).f0.toString(), table, null);
		//newClass.parent = null;
		n.f2.accept(this, table);
		n.f3.accept(this, newClass);
		n.f4.accept(this, newClass);
		n.f5.accept(this, table);
	}
	public void visit(ClassExtendsDeclaration n, MType table) {
		n.f0.accept(this, table);
		n.f1.accept(this, table);
		if (table.membersHasThis(((Identifier)(n.f1)).f0.toString()) != null) {
			System.out.println(String.format(
				"class \"%s\" is declared for more than one times", 
				((Identifier)(n.f1)).f0.toString()));
			System.exit(0);
		}
		table = (MClassList) table;
		n.f2.accept(this, table);
		n.f3.accept(this, table);
		MType newClass = table.addMember( "MClass", 
			((Identifier)(n.f1)).f0.toString(), table, 
			((Identifier)(n.f3)).f0.toString());
		n.f4.accept(this, table);
		n.f5.accept(this, newClass);
		n.f6.accept(this, newClass);
		n.f7.accept(this, table);
	}
	public void visit(MethodDeclaration n, MType table) {
		n.f0.accept(this, table);
		n.f1.accept(this, table);
		n.f2.accept(this, table);
		String type = ((Type)n.f1).f0.toString();
		String name = ((Identifier)n.f2).f0.toString();
		if (table.membersHasThis(name) != null) {
			System.out.println(String.format(
				"method \"%s\" is declared for more than one times", 
				name));
			System.exit(0);
		}
		MType method = table.addMember("MMethod", name, table, type);
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
	}
	public void visit(FormalParameter n, MType table) {
		n.f0.accept(this, table);
		n.f1.accept(this, table);
		String type = ((Type)n.f0).f0.toString();
		String name = ((Identifier)n.f1).f0.toString();
		MMethod method = (MMethod) table;
		for (String varName : method.params) {
			if (varName.equals(name)) {
				System.out.println(String.format(
					"%s in parameters of %s is declared for more than once.", 
					name, method.name));
				System.exit(0);
			}
		}
		method.addVar(name, method, type);
		method.params.add(type);
	}
}
