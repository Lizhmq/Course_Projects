package visitor;
import java.util.*;

import syntaxtree.*;
import utils.*;

public class MaxTempVisitor extends GJVoidDepthFirst<Integer> {

        public int MaxTemp = 0;
        public int visit(Temp n) {
                MaxTemp = max(n.f1.f0.tokenImage, MaxTemp);
                return MaxTemp;
        }
}