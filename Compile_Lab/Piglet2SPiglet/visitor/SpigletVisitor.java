package visitor;
import java.util.*;

import syntaxtree.*;
import utils.*;

public class SpigletVisitor extends GJDepthFirst<SpigletResult, boolean> {

        public StringBuilder result = new StringBuilder("");
        int UsedTemp;

        SpigletVisitor(int UsedTemp) {
                this.UsedTemp = UsedTemp;
        }

        void put(String cur) {
                result.append(cur);
                return;
        }

        // ****
        public SpigletResult visit(NodeListOptional n, boolean isPut) {
                if (n.present()) {
                        for (Enumeration<Node> e = n.elements(); e.hasMoreElements(); ) {
                                e.nextElement().accept(this, isPut);
                                put("\n");
                        }
                return null;
        }

        public SpigletResult visit(Goal n, boolean isPut) {
                put("MAIN\n");
                n.f1.accept(this, true);
                put("END\n");
                n.f2.accept(this, true);
                n.f3.accept(this, true);
                return null;
        }

        public SpigletResult visit(Label n, boolean isPut) {
                put(n.f0.tokenImage + " ");
                return null;
        }

        public SpigletResult visit(Procedure n, boolean isPut) {
                n.f0.accept(this, true);
                put("[ " + n.f2.f0.tokenImage + " ]\n");
                n.f4.accept(this, true);
                return null;
        }

        public SpigletResult visit(NoOpStmt n, boolean isPut) {
                put("NOOP\n");
                return null;
        }

        public SpigletResult visit(ErrorStmt n, boolean isPut) {
                put("ERROR\n");
                return null;
        }

        public SpigletResult visit(JumpStmt n, boolean isPut) {
                put("JUMP " + n.f1.f0.tokenImage);
                return null;
        }

        public SpigletResult visit(HStoreStmt n, boolean isPut) {
                int savedTemp = UsedTemp;
                SpigletResult exp1 = n.f1.accept(this, false);
                if (!exp1.isTemp()) {
                        int temp = UsedTemp++;
                        put("MOVE TEMP " + temp + " " + exp1 + "\n");
                        exp1 = new SpigletResult("TEMP " + temp, true);
                }
                SpigletResult exp2 = n.f3.accept(this, false);
                if (!exp2.isTemp()) {
                        int temp = UsedTemp++;
                        put("MOVE TEMP " + temp + " " + exp2 + "\n");
                        exp2 = new SpigletResult("TEMP " + temp, true);
                }
                put("HSTORE " + exp1 + " " + n.f2.f0.tokenImage + " " + exp2 + "\n");
                UsedTemp = savedTemp;
                return null;
        }

        public SpigletResult visit(CJumpStmt n, boolean isPut) {
                int savedTemp = UsedTemp;
                SpigletResult exp = n.f1.accept(this, false);
                if (!exp.isTemp()) {
                        int temp = UsedTemp++;
                        put("MOVE TEMP " + temp + " " + exp + "\n");
                        exp = new SpigletResult("TEMP " + temp, true);
                }
                put("CJUMP " + exp + " " + n.f2.f0.tokenImage + "\n");
                UsedTemp = savedTemp;
                return null;
        }

        public SpigletResult visit(HLoadStmt n, boolean isPut) {
                int savedTemp = UsedTemp;
                SpigletResult Temp = n.f1.accept(this, false);
                SpigletResult exp = n.f2.accept(this, false);
                if (!exp.isTemp()) {
                        int temp = UsedTemp++;
                        put("MOVE TEMP " + temp + " " + exp + "\n");
                        exp = new SpigletResult("TEMP " + temp, true);
                }
                put("HLOAD " + Temp + " " + exp + " " + n.f3.f0.tokenImage + "\n");
                UsedTemp = savedTemp;
                return null;
        }

        public SpigletResult visit(PrintStmt n, boolean isPut) {
                int savedTemp = UsedTemp;
                SpigletResult exp = n.f1.accept(this, false);
                if (!exp.isSimple()) {
                        int temp = UsedTemp++;
                        put("MOVE TEMP " + temp + " " + exp + "\n");
                        exp = new SpigletResult("TEMP " + temp, true);
                }
                put("PRINT " + exp + "\n");
                UsedTemp = savedTemp;
                return null;
        }

        public SpigletResult visit(MoveStmt n, boolean isPut) {
                SpigletResult exp = n.f2.accept(this, false);
                put("MOVE TEMP " + n.f1.f1.f0.tokenImage + " " + exp);
                return null;
        }

        public SpigletResult visit(Exp n, boolean isPut) {
                return n.f0.accept(this, false);
        }

        public SpigletResult visit(StmtExp n, boolean isPut) {
                int savedTemp = UsedTemp;
                SpigletResult stmtlist = n.f1.accept(this, false);
                SpigletResult exp = n.f3.accept(this, false);
                String tmp = "";
                tmp += "BEGIN\n" + stmtlist + " RETURN\n";
                if (!exp.simple) {
                        int temp = UsedTemp++;
                        tmp += "MOVE TEMP " + temp + " " + exp + "\n";
                        exp = new SpigletResult("TEMP " + temp, true);
                }
                tmp += exp + "\nEND\n";
                UsedTemp = savedTemp;
                if (isPut) {
                        put(tmp);
                        return null;
                } else {
                        return new SpigletResult(tmp, false);
                }
        }

        public SpigletResult visit(Call n, boolean isPut) {
                int savedTemp = UsedTemp;
                String tmp = "";
                SpigletResult exp = n.f1.accept(this, false);
                SpigletResult explist = n.f3.accept(this, false);
                tmp += "CALL ";
                if (!exp.isSimple()) {
                        int temp = UsedTemp++;
                        tmp += "MOVE TEMP " + temp + " " + exp + "\n";
                        exp = new SpigletResult("TEMP " + temp, true);
                }
                tmp += exp + " ( ";
                // ****
                tmp += explist + " )\n";
                return new SpigletResult(tmp, false);
        }

        public SpigletResult visit(HAllocate n, boolean isPut) {
                int savedTemp = UsedTemp;
                String tmp = "";
                SpigletResult exp = n.f1.accept(this, false);
                if (!exp.isSimple()) {
                        int temp = UsedTemp++;
                        tmp += "MOVE TEMP " + temp + " " + exp + "\n";
                        exp = new SpigletResult("TEMP " + temp, true);
                }
                tmp += "HALLOCATE " + exp + "\n";
                UsedTemp = savedTemp;
                return new SpigletResult(tmp, false);
        }

        public SpigletResult visit(BinOp n, boolean isPut) {
                int savedTemp = UsedTemp;
                // ****
                SpigletResult op = n.f0.accept(this);
                SpigletResult exp1 = n.f1.accept(this, false);
                SpigletResult exp2 = n.f2.accept(this, false);
                String tmp = "";
                tmp += op + " ";
                if (!exp1.isTemp()) {
                        int temp = UsedTemp++;
                        tmp += "MOVE TEMP " + temp + " " + exp1 + "\n";
                        exp1 = new SpigletResult("TEMP " + temp, true);
                }
                tmp += exp1 + " ";
                if (!exp2.simple) {
                        int temp = UsedTemp++;
                        tmp += "MOVE TEMP " + temp + " " + exp2 + "\n";
                        exp2 = new SpigletResult("TEMP " + temp, true);
                }
                tmp += exp2 + "\n";
                UsedTemp = savedTemp;
                return new SpigletResult(tmp, false)
        }

        public SpigletResult visit(Operator n) {
                return n.f0.accept(this, false);
        }

        public SpigletResult visit(Temp n, boolean isPut) {
                return new SpigletResult("TEMP " + n.f1.f0.tokenImage, true);
        }

        public SpigletResult visit(IntegerLiteral n, boolean isPut) {
                return new SpigletResult(n.f0.tokenImage, true);
        }

        public SpigletResult visit(Label n, boolean isPut) {
                return new SpigletResult(n.f0.tokenImage, true);
        }

}