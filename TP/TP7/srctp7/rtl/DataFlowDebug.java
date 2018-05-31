package rtl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import rtl.graph.FlowGraph;
import rtl.graph.Graph.Node;

public class DataFlowDebug {

	final private List<List<String>> debugHistory = new ArrayList<>();
	final private FlowGraph g;
	final private Function f;
	int columnLength = 4;
	int instrColumnLength = 1;
	int useDefColumnLength = 1;


	public DataFlowDebug(Function f, FlowGraph g) {
		this.f = f;
		this.g = g;
	}

	public void recordCurrentMaps(Map<Node,?> inMap, Map<Node,?> outMap) {
		List<String> debug_pass = new ArrayList<>();
		for (Node n : g.nodes()) {
			debug_pass.add(shorten(inMap.get(n).toString()));
			debug_pass.add(shorten(outMap.get(n).toString()));
		}
		debugHistory.add(debug_pass);	
	}

	public void show(java.io.PrintStream out, boolean verbose) {
		for  (List<String> info : debugHistory) 
			for (String s : info) {
				int length = s.length();
				if (length>columnLength) columnLength = length;
			}		
		columnLength++;
		for (Node n: g.nodes()) {
			Object o = ((rtl.graph.RtlCFG) g).instr(n);
			String s = (o==null)?f.headerToString():o.toString();
			if (s.length() > instrColumnLength) instrColumnLength = s.length();
		}			
		for (Node n: g.nodes()) {
			String s = shorten(g.use(n).toString());
			if (s.length() > useDefColumnLength) useDefColumnLength = s.length();
			s = shorten(g.def(n).toString());
			if (s.length() > useDefColumnLength) useDefColumnLength = s.length();			
		}
		useDefColumnLength++;
		out.print("     "+pad("",2*useDefColumnLength+1+instrColumnLength+1));
		if (verbose) 
			for (int k=0;k<debugHistory.size();k++)
				out.print(pad(Integer.toString(k+1),2*columnLength+1));
		out.println("");
		out.print("   |  "+pad("",instrColumnLength));
		out.print("|");
		out.print(pad("USE",useDefColumnLength));
		out.print(pad("DEF",useDefColumnLength));
		out.print("|");
		for (int k=0;(k<debugHistory.size() && verbose) 
				|| (k<1 && !verbose);k++) {
			out.print(pad(" IN",columnLength));
			out.print(pad("OUT",columnLength));
			out.print("|");
		}
		out.println("");
		int i = 0;
		for (Node n : g.nodes()) {
			out.print(String.format("%0$3s", n.toString()));
			out.print("| ");
			out.print(instrText(n));
			out.print(" |");
			out.print(pad(shorten(g.use(n).toString()),useDefColumnLength));
			out.print(pad(shorten(g.def(n).toString()),useDefColumnLength));
			out.print("|");
			if (verbose)
				for (List<String> info : debugHistory) {
					out.print(debugSet(info.get(2*i)));
					out.print(debugSet(info.get(2*i+1))+"|");
				}
			else {
				List<String> info = debugHistory.get(debugHistory.size()-1);
				out.print(debugSet(info.get(2*i)));
				out.print(debugSet(info.get(2*i+1))+"|");
			}
			out.println("");
			i++;
		}
	}

	private static String pad(String s, int length) {
		String res = s;
		assert (res.length()<length);
		int n = length - res.length();
		for (int k=0;k<n/2;k++) res = res+" ";
		for (int k=0;k<n-n/2;k++) res = " "+res;
		return res;
	}

	private String instrText(Node n) {
		Object o = ((rtl.graph.RtlCFG) g).instr(n);
		String s = (o==null)?f.headerToString():o.toString();
		String res = s;
		for (int k=0;k<instrColumnLength-s.length();k++) res = res+" ";
		return res;
	}

	private String debugSet(String s) {
		return pad(s,columnLength);
	}

	private String shorten(String s) {
		String s1 = (s.charAt(0) == '[')?s.substring(1, s.length()-1):s;
		String s2 = s1.replaceAll(", ", ",");
		return s2;
	}


}
