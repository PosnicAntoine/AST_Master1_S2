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
//			java.io.InputStream in = new FileInputStream("/private/student/7/17/15006817/workspace/TP1/srctp1/rtl/examples/BinaryTree.rtl");// A MODIFIER
//			java.io.InputStream in = new FileInputStream("/private/student/7/17/15006817/workspace/TP1/srctp1/rtl/examples/BubbleSort.rtl");// A MODIFIER
//			java.io.InputStream in = new FileInputStream("/private/student/7/17/15006817/workspace/TP1/srctp1/rtl/examples/ExLiveness.rtl");// A MODIFIER
//			java.io.InputStream in = new FileInputStream("/private/student/7/17/15006817/workspace/TP1/srctp1/rtl/examples/ExLiveness2.rtl");// A MODIFIER
			java.io.InputStream in = new FileInputStream("/private/student/7/17/15006817/workspace/TP1/srctp1/rtl/examples/Factorial.rtl");// A MODIFIER
//			java.io.InputStream in = new FileInputStream("/private/student/7/17/15006817/workspace/TP1/srctp1/rtl/examples/LinearSearch.rtl");// A MODIFIER
//			java.io.InputStream in = new FileInputStream("/private/student/7/17/15006817/workspace/TP1/srctp1/rtl/examples/LinkedList.rtl");// A MODIFIER
//			java.io.InputStream in = new FileInputStream("/private/student/7/17/15006817/workspace/TP1/srctp1/rtl/examples/MoreThan4.rtl");// A MODIFIER
//			java.io.InputStream in = new FileInputStream("/private/student/7/17/15006817/workspace/TP1/srctp1/rtl/examples/QuickSort.rtl");// A MODIFIER
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
	private Map<Node,Set<Ident>> liveInTmp = new Hashtable<Node,Set<Ident>>();
	private Map<Node,Set<Ident>> liveOutTmp = new Hashtable<Node,Set<Ident>>();
	
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
		this.liveInTmp = this.liveIn;
		this.liveOutTmp = this.liveOut;
		for (Node n : g.nodes()) {
			
			// VIVANTESe (l) = (VIVANTESs (l) MOINS def (l)) UNION use (l)
			for (Ident i : liveOut.get(n)) {
				liveIn.get(n).add(i);
			}
			for (Ident i : g.def(n)) {
				liveIn.get(n).remove(i);
			}
			for (Ident i : g.use(n)) {
				liveIn.get(n).add(i);
			}
			
			// VIVANTESs (l) = ENSEMBLE DE TOUTES LES VIVANTESe (l') QUI SONT EN DESSOUS
			for (Node nSuivant : n.succ()) {
				liveOut.get(n).addAll(liveIn.get(nSuivant));
			}
		}
		
		if (debug!=null) debug.recordCurrentLivenessValues(liveIn, liveOut);
	}

	public boolean isFixedPoint(FlowGraph g) {
		// use g ?
		return this.liveIn.equals(this.liveInTmp) && this.liveOut.equals(this.liveOutTmp);
	}

	public Set<Ident> liveIn(Node n) {
		return liveIn.get(n);
	}

	public Set<Ident> liveOut(Node n) {
		return liveOut.get(n);
	}

}
