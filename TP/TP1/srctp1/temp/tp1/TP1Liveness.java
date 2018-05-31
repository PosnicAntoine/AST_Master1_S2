package tp1;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import rtl.*;
import rtl.graph.FlowGraph;
import rtl.graph.RtlCFG;
import rtl.graph.Graph.Node;
import rtl.interpreter.ErrorException;

public class TP1Liveness {


	public static void main(String[] args) {
		try {
			java.io.InputStream in = new FileInputStream("/Users/david/Works/svn/lande/teaching/Master1/AST/src/rtl/examples/ExLiveness.rtl");// A MODIFIER
			Program prog = rtl.Parser.run(in);
			for (Function f: prog.functions) {
				System.out.println(f.headerToString());
				FlowGraph g = new RtlCFG(f);
				LivenessDebug debug = new LivenessDebug(g);
				TP1Liveness live = new TP1Liveness(g,debug);
				live.build();
				debug.show(System.out);
			}
		} catch (Throwable e) {
			System.out.println("Liveness analysis failed: " + e.getMessage());
		}
	}

	private Map<Node,Set<Ident>> liveIn = new Hashtable<Node,Set<Ident>>();
	private Map<Node,Set<Ident>> liveOut = new Hashtable<Node,Set<Ident>>();
	private final LivenessDebug debug;
	private FlowGraph g;

	public TP1Liveness(FlowGraph g, LivenessDebug debug) {
		this.g = g;
		this.debug = debug;
	}

	public void build() {
		for (Node n : g.nodes()) { 
			liveIn.put(n, new HashSet<Ident>());
			liveOut.put(n, new HashSet<Ident>()); 
		}		
		onePass(g);
		while (!isFixedPoint(g)) 
			onePass(g);		
	}

	public void onePass(FlowGraph g) {
		for (Node n : g.nodes()) {
			//TODO
		}		
		if (debug!=null) debug.recordCurrentLivenessValues(liveIn, liveOut); // NE PAS MODIFIER
	}

	public boolean isFixedPoint(FlowGraph g) {
		return true;//TODO
	}

	public Set<Ident> liveIn(Node n) {
		return liveIn.get(n);
	}

	public Set<Ident> liveOut(Node n) {
		return liveOut.get(n);
	}

}
