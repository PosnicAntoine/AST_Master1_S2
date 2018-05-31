package tp0;

import rtl.graph.Graph;
import rtl.graph.Graph.Node;
import rtl.graph.FlowGraph;
import rtl.*;
import rtl.interpreter.Eval;

import java.io.FileInputStream;
import java.util.Set;

public class RtlFlowGraph extends FlowGraph {
	
	public Object instr(Node n) {
		return null; //TODO
	}

	public RtlFlowGraph(Function f) {
		 //TODO
	}

	public Set<Ident> def(Node node) {
		return null; //TODO
	}

	public Set<Ident> use(Node node) {
		return null; //TODO
	}

	public boolean isMove(Node node) {
		return false; //TODO
	}

	public static void main(String[] args) {
		try {
			String mypath = "/Users/david/Works/svn/lande/teaching/Master1/AST/src"; //mettre votre chemin absolu
			Program prog = rtl.Parser.run(new FileInputStream(mypath+"/rtl/examples/Factorial.rtl")); 
			for (Function f: prog.functions) {
				System.out.println("func "+f.name.toString());
				new RtlFlowGraph(f).show(System.out);
			}
		} catch (Throwable e) {
			System.out.println("RTL flow graph generator failed: " + e.getMessage());
		}

	}
	
}
