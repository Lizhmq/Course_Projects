package visitor;
import java.util.*;

import syntaxtree.*;
import utils.*;

public class MaxTempVisitor extends GJVoidDepthFirst<Integer> {

        public int MaxTemp = 0;
        public void visit(Temp n, Integer p) {
                MaxTemp = Math.max(Integer.parseInt(n.f1.f0.tokenImage), MaxTemp);
        }
}