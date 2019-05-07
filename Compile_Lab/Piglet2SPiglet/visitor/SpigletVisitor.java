package visitor;
import java.util.*;

import syntaxtree.*;
import utils.*;

public class SpigletVisitor extends GJDepthFirst<SpigletResult, Boolean> {

        public StringBuilder result = new StringBuilder("");
        int UsedTemp;

        public SpigletVisitor(int UsedTemp) {
                this.UsedTemp = UsedTemp;
        }

        void put(String cur) {
                result.append(cur);
                return;
        }

        public SpigletResult visit(NodeList n, Boolean isProc) {
                StringBuilder b = new StringBuilder("");
                for (Enumeration<Node> e = n.elements(); e.hasMoreElements(); ) {
                        SpigletResult tmp = e.nextElement().accept(this, isProc);
                        if (tmp != null) {
                                b.append(tmp.toString());
                                b.append("\n");
                        }
                }
                return new SpigletResult(b.toString(), false);
        }
        
        public SpigletResult visit(NodeListOptional n, Boolean isProc) {
                if (n.present()) {
                        StringBuilder b = new StringBuilder("");
                        for (Enumeration<Node> e = n.elements(); e.hasMoreElements(); ) {
                                SpigletResult tmp = e.nextElement().accept(this, isProc);
                                if (tmp != null) {
                                        b.append(tmp.toString());
                                        b.append("\n");
                                }
                        }
                        return new SpigletResult(b.toString(), false);
                } else {
                        return null;
                }
        }

        public SpigletResult visit(NodeSequence n, Boolean isProc) {
                StringBuilder b = new StringBuilder("");
                for (Enumeration<Node> e = n.elements(); e.hasMoreElements(); ) {
                        SpigletResult tmp = e.nextElement().accept(this, isProc);
                        if (tmp != null) {
                                b.append(tmp.toString());
                                b.append("\n");
                        }
                }
                return new SpigletResult(b.toString(), false);
        }

        public SpigletResult visit(Goal n, Boolean isProc) {
                put("MAIN\n");
                n.f1.accept(this, false);
                put("END\n");
                n.f3.accept(this, false);
                return null;
        }

        public SpigletResult visit(Procedure n, Boolean isProc) {
                put(n.f0.f0.tokenImage + " [" + n.f2.f0.tokenImage + "]\n");
                n.f4.accept(this, true);
                return null;
        }

        public SpigletResult visit(NoOpStmt n, Boolean isProc) {
                String res = "NOOP\n";
                put(res);
                return new SpigletResult(res, false);
        }

        public SpigletResult visit(ErrorStmt n, Boolean isProc) {
                String res = "ERROR\n";
                put(res);
                return new SpigletResult(res, false);
        }

        public SpigletResult visit(JumpStmt n, Boolean isProc) {
                String res = "JUMP " + n.f1.f0.tokenImage + "\n";
                put(res);
                return new SpigletResult(res, false);
        }

        public SpigletResult visit(HStoreStmt n, Boolean isProc) {
                String res= "";
                int savedTemp = UsedTemp;
                int temp1 = UsedTemp++;
                int temp2 = UsedTemp++;
                SpigletResult exp1 = n.f1.accept(this, false);
                if (!exp1.isTemp()) {
                        put("MOVE TEMP " + temp1 + " " + exp1 + "\n");
                        exp1 = new SpigletResult("TEMP " + temp1, true);
                }
                SpigletResult exp2 = n.f3.accept(this, false);
                if (!exp2.isTemp()) {
                        put("MOVE TEMP " + temp2 + " " + exp2 + "\n");
                        exp2 = new SpigletResult("TEMP " + temp2, true);
                }
                res = "HSTORE " + exp1 + " " + n.f2.f0.tokenImage + " " + exp2 + "\n";
                put(res);
                UsedTemp = savedTemp;
                return new SpigletResult(res, false);
        }

        public SpigletResult visit(CJumpStmt n, Boolean isProc) {
                String res = "";
                int savedTemp = UsedTemp;
                int temp = UsedTemp++;
                SpigletResult exp = n.f1.accept(this, false);
                if (!exp.isTemp()) {
                        put("MOVE TEMP " + temp + " " + exp + "\n");
                        exp = new SpigletResult("TEMP " + temp, true);
                }
                res = "CJUMP " + exp + " " + n.f2.f0.tokenImage + "\n";
                put(res);
                UsedTemp = savedTemp;
                return new SpigletResult(res, false);
        }

        public SpigletResult visit(HLoadStmt n, Boolean isProc) {
                String res = "";
                int savedTemp = UsedTemp;
                int temp = UsedTemp++;
                SpigletResult Temp = n.f1.accept(this, false);
                SpigletResult exp = n.f2.accept(this, false);
                if (!exp.isTemp()) {
                        put("MOVE TEMP " + temp + " " + exp + "\n");
                        exp = new SpigletResult("TEMP " + temp, true);
                }
                res = "HLOAD " + Temp + " " + exp + " " + n.f3.f0.tokenImage + "\n";
                put(res);
                UsedTemp = savedTemp;
                return new SpigletResult(res, false);
        }

        public SpigletResult visit(PrintStmt n, Boolean isProc) {
                String res = "";
                int savedTemp = UsedTemp;
                int temp = UsedTemp++;
                SpigletResult exp = n.f1.accept(this, false);
                if (!exp.isSimple()) {
                        put("MOVE TEMP " + temp + " " + exp + "\n");
                        exp = new SpigletResult("TEMP " + temp, true);
                }
                res = "PRINT " + exp + "\n";
                put(res);
                UsedTemp = savedTemp;
                return new SpigletResult(res, false);
        }

        public SpigletResult visit(MoveStmt n, Boolean isProc) {
                SpigletResult exp = n.f2.accept(this, false);
                String res = "MOVE TEMP " + n.f1.f1.f0.tokenImage + " " + exp + "\n";
                put(res);
                return new SpigletResult(res, false);
        }

        public SpigletResult visit(Exp n, Boolean isProc) {
                return n.f0.accept(this, false);
        }

        public SpigletResult visit(Stmt n, Boolean isProc) {
                return n.f0.accept(this, isProc);
        }

        public SpigletResult visit(StmtExp n, Boolean isProc) {
                int savedTemp = UsedTemp;
                int temp = UsedTemp++;
                if (isProc) {
                        String tmp = "";
                        put("BEGIN\n");
                        SpigletResult stmtlist = n.f1.accept(this, false);
                        SpigletResult exp = n.f3.accept(this, false);
                        if (!exp.simple) {
                                put("MOVE TEMP " + temp + " " + exp + "\n");
                                exp = new SpigletResult("TEMP " + temp, true);
                        }
                        tmp = "RETURN ";
                        tmp += exp + "\nEND\n";
                        put(tmp);
                        UsedTemp = savedTemp;
                        return new SpigletResult(tmp, false);
                } else {
                        String tmp = "";
                        SpigletResult stmtlist = n.f1.accept(this, false);
                        SpigletResult exp = n.f3.accept(this, false);
                        if (!exp.isTemp()) {
                                put("MOVE TEMP " + temp + " " + exp + "\n");
                                exp = new SpigletResult("TEMP " + temp, true);
                        }
                        return exp;
                }
        }

        public SpigletResult visit(StmtList n, Boolean isProc) {
                return n.f0.accept(this, isProc);
        }

        public SpigletResult visit(Call n, Boolean isProc) {
                int savedTemp = UsedTemp;
                int temp = UsedTemp;
                UsedTemp += 25;
                String tmp = "";
                SpigletResult exp = n.f1.accept(this, false);
                SpigletResult explist = n.f3.accept(this, false);
                if (!exp.isSimple()) {
                        tmp = "MOVE TEMP " + temp + " " + exp + "\n";
                        put(tmp);
                        exp = new SpigletResult("TEMP " + temp, true);
                }
                String []args = explist.toString().split("\n");
                String templist = " ";
                for (int i = 0; i < args.length; ++i) {
                        if (args[i] == "" || args[i] == null || args[i].length() == 0)
                                continue;
                        if (!args[i].startsWith("TEMP")) {
                                tmp = "MOVE TEMP " + (temp + i + 1) + " " + args[i] + "\n";
                                put(tmp);
                                templist += "TEMP " + (temp + i + 1) + " ";
                        } else {
                                templist += args[i] + " ";
                        }
                }
                tmp = "CALL ";
                tmp += exp + "(";
                tmp += templist + ")\n";
                UsedTemp = savedTemp;
                return new SpigletResult(tmp, false);
        }

        public SpigletResult visit(HAllocate n, Boolean isProc) {
                int savedTemp = UsedTemp;
                int temp = UsedTemp++;
                String tmp = "";
                SpigletResult exp = n.f1.accept(this, false);
                if (!exp.isSimple()) {
                        put("MOVE TEMP " + temp + " " + exp + "\n");
                        exp = new SpigletResult("TEMP " + temp, true);
                }
                tmp = "HALLOCATE " + exp + "\n";
                UsedTemp = savedTemp;
                return new SpigletResult(tmp, false);
        }

        public SpigletResult visit(BinOp n, Boolean isProc) {
                int savedTemp = UsedTemp;
                int temp1 = UsedTemp++;
                int temp2 = UsedTemp++;
                SpigletResult op = n.f0.accept(this, false);
                SpigletResult exp1 = n.f1.accept(this, false);
                SpigletResult exp2 = n.f2.accept(this, false);
                String tmp = "";
                if (!exp1.isTemp()) {
                        tmp = "MOVE TEMP " + temp1 + " " + exp1 + "\n";
                        put(tmp);
                        exp1 = new SpigletResult("TEMP " + temp1, true);
                }
                if (!exp2.simple) {
                        tmp = "MOVE TEMP " + temp2 + " " + exp2 + "\n";
                        put(tmp);
                        exp2 = new SpigletResult("TEMP " + temp2, true);
                }
                tmp = op + " " + exp1 + " " + exp2 + "\n";
                UsedTemp = savedTemp;
                return new SpigletResult(tmp, false);
        }

        public SpigletResult visit(Operator n, Boolean isProc) {
                String res = "";
                switch (n.f0.which) {
                        case 0: res = "LT"; break;
                        case 1: res = "PLUS"; break;
                        case 2: res = "MINUS"; break;
                        case 3: res = "TIMES"; break;
                        default: res = "??????? in Operator"; break;
                }
                return new SpigletResult(res, false);
        }

        public SpigletResult visit(Temp n, Boolean isProc) {
                return new SpigletResult("TEMP " + n.f1.f0.tokenImage, true);
        }

        public SpigletResult visit(IntegerLiteral n, Boolean isProc) {
                return new SpigletResult(n.f0.tokenImage, true);
        }

        public SpigletResult visit(Label n, Boolean isProc) {
                put(n.f0.tokenImage + " ");
                return new SpigletResult(n.f0.tokenImage, true);
        }

}