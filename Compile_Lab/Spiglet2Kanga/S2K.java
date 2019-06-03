import spiglet.*;
import visitor.*;
import syntaxtree.*;
import java.io.*;
import java.util.*;
import utils.*;

public class S2K {
    static public void main(String[] args) {
        try {
            if (args.length < 1) {
                usage(args);
                return;
            }
            InputStream in = new FileInputStream(args[0]);
            Node root = new SpigletParser(in).Goal();

            HashMap<String, Method> mMethod = new HashMap<String, Method>();
            HashMap<String, Integer> mLabel = new HashMap<String, Integer>();

            GetVertexVisitor VVis = new GetVertexVisitor(mMethod, mLabel);
            root.accept(VVis);
            
            GetGraphVisitor GVis = new GetGraphVisitor(mMethod, mLabel);
            root.accept(GVis);

            RegAlloc alloc = new RegAlloc(mMethod);
            alloc.LinearScan();

            S2KVisitor s2k= new S2KVisitor(mMethod);
            root.accept(s2k);
            String code = s2k.code.toString();
            // new SpigletParser(in);
			// Node AST = SpigletParser.Goal();
			// // visit 1: Get Flow Graph Vertex
			// AST.accept(new GetFlowGraphVertex());
			// // visit 2: Get Flow Graph
			// AST.accept(new GetFlowGraph());
			// // Linear Scan Algorithm on Flow Graph
			// new Temp2Reg().LinearScan();
			// // visit 3: Spiglet->Kanga
            // AST.accept(new Spiglet2Kanga());
            
            String outputfile = null;
            if (args.length > 1) {
                outputfile = args[1];
            } else {
                outputfile = args[0].substring(0, args[0].length() - 4) + ".kg";
            }
            OutputStream out = new FileOutputStream(outputfile);
            out.write(code.getBytes());
            out.close();
            System.out.println(String.format("%s -> %s is finished.", args[0], outputfile));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return;
    }
    static void usage(String[] args) {
        System.out.println(String.format("Usage:\n"));
        System.out.println(String.format("\tjava S2K input_spiglet_file [output_kanga_file]"));
    }
}