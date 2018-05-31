package tp5;

import java.io.FileInputStream;

import rtl.DataFlowDebug;
import rtl.Function;
import rtl.Program;
import rtl.graph.RtlCFG;

class MyTest {

	public static void main(String[] args) {
		try {
			String path = "/private/student/7/17/15006817/workspace/AST_TP5/srctp5/";
			java.io.InputStream in = new FileInputStream(path+"rtl/examples/ExED2.rtl");
			Program prog = rtl.Parser.run(in);
			for (Function f: prog.functions) {
				System.out.println("AVAILABLE EXPRESSION ANALYSIS");
				System.out.println(f.headerToString());
				RtlCFG g = new RtlCFG(f);
				DataFlowDebug debug = new DataFlowDebug(f,g); 
				TP5AvailableExpr ae = new TP5AvailableExpr(f,g,debug);
				debug.show(System.out,false);
				
				System.out.println("REACHABLE EXPRESSION ANALYSIS");
				g.enableNodeShowLines();
				System.out.println(ae.reachableExpressions().toString());
				
				System.out.println("COMMON SUB-EXPRESSION TRANSFORMATION");
				TP5CSE.transform(prog);
				prog.print();
			}
		} catch (Throwable e) {
			System.out.println("Available expressions analysis failed: " + e.getMessage());
		}

	}
}
