package tp7;

import java.io.FileInputStream;

import rtl.Function;
import rtl.Program;

class MyTest {

	public static void main(String[] args) {
		try {
			java.io.InputStream in = new FileInputStream("/private/student/7/17/15006817/workspace/AST_TP7/srctp7/rtl/examples/exCpSSA.rtl");
			Program prog = rtl.Parser.run(in);
			for (Function f: prog.functions) {
				System.out.println(f.headerToString());
				System.out.println("SSA CONSTANT ANALYSIS");
				rtl.SSAInformation info1 = new rtl.SSAInformation(f);
				TP7SSAConstPropagation cp = new TP7SSAConstPropagation(info1);
				cp.show();
				System.out.println("SSA INFORMATION");
				TP7SSAInformation info2 = new TP7SSAInformation(f);
				info2.show();
			}
		} catch (Throwable e) {
			System.out.println("Constant analysis failed: " + e.getMessage());
		}

	}
}
