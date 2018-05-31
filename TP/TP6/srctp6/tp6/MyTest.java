package tp6;

import java.io.FileInputStream;

import rtl.DataFlowDebug;
import rtl.Function;
import rtl.Program;
import rtl.graph.RtlCFG;

class MyTest {

	public static void main(String[] args) {
		try {
			String path = "/private/student/7/17/15006817/workspace/AST_TP6/srctp6/";
			java.io.InputStream in = new FileInputStream(path+"rtl/examples/ExCp1.rtl");
			Program prog = rtl.Parser.run(in);
			for (Function f: prog.functions) {
				System.out.println("CONSTANT ANALYSIS");
				System.out.println(f.headerToString());
				RtlCFG g = new RtlCFG(f);
				DataFlowDebug debug = new DataFlowDebug(f,g); 
				TP6ConstantInference ae = new TP6ConstantInference(f,g,debug);
				debug.show(System.out,true);
			}
		} catch (Throwable e) {
			System.out.println("Constant analysis failed: " + e.getMessage());
		}

	}
}
