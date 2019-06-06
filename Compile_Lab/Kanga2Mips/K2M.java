import kanga.*;
import visitor.*;
import syntaxtree.*;
import java.io.*;
import java.util.*;
//import utils.*;

public class K2M {
    static public void main(String[] args) {
        try {
            if (args.length < 1) {
                usage(args);
                return;
            }
            InputStream in = new FileInputStream(args[0]);
            Node root = new KangaParser(in).Goal();

            MipsVisitor v = new MipsVisitor();
            String code = root.accept(v);
            
            String outputfile = null;
            if (args.length > 1) {
                outputfile = args[1];
            } else {
                outputfile = args[0].substring(0, args[0].length() - 3) + ".s";
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
        System.out.println(String.format("\tjava K2M input_kanga_file [output_mips_file]"));
    }
}