package tp2;

import java.io.FileInputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import rtl.Function;
import rtl.Ident;
import rtl.Liveness;
import rtl.Program;
import rtl.graph.AbstractInterferenceGraph;
import rtl.graph.Graph.Node;
import rtl.graph.InterferenceGraph;
import rtl.graph.RtlCFG;

public class TP2ColorGraph {

	public static void main(String[] args) {
		try {
			String path = "D:/Workspace/AST_TP2/srctp2/";
			java.io.InputStream in = new FileInputStream(path+"rtl/examples/LinkedList.rtl");
			Program prog = rtl.Parser.run(in);
			for (Function f: prog.functions) {
				System.out.println(f.headerToString());
				RtlCFG g = new RtlCFG(f);          // construction du control flow graphe avec la version enseignant
				Liveness live = new Liveness(g);   // construction de l'analyse
				live.build();                      // de liveness avec la version enseignant
				InterferenceGraph igraph = new InterferenceGraph(g,live); // construction du graphe d'interference avec la version enseignant
				TP2ColorGraph cg = new TP2ColorGraph(igraph,4);
				System.out.println("coloration : "+cg.color);
				System.out.println("variables spillées : "+cg.spilled);
			}
		} catch (Throwable e) {
			System.out.println("Graph coloring failed: " + e.getMessage());
		}
	}

	public Map<Ident,Integer> color; //après construction de l'objet, contient la couleur (un entier positif ou nul) des variables colorées
	public Set<Ident> spilled;       //après construction de l'objet, contient les variables spillées 
	public Stack<Node> pile = new Stack<Node>();
	
	
	// CONSEIL #1 : ne pas modifier le contenu de graphe g pendant la coloration !
	// CONSEIL #2 : de toutes façons, l'operation rmEdge de la classe Graph ne fonctionne pas sur les graphes non-orientés !
	public TP2ColorGraph(AbstractInterferenceGraph g, int nbMaxColor) {
		
		color = new HashMap<Ident, Integer>();
		spilled = new HashSet<Ident>();
		
		boolean test = true;
		while (test) {
			test = false;
			for (Node n : g.nodes()) {
				if (!pile.contains(n) && !spilled.contains(g.ident(n))) {
					int cpt_adj = 0;
					for (Node a : n.adj()) {
						if (!pile.contains(a) && !spilled.contains(g.ident(a))) {
							cpt_adj++;
						}
					}
					if (cpt_adj < nbMaxColor) {
						pile.push(n);
						test = true;
					}
				}
			}
			if (!test) {
				for (Node n : g.nodes()) {
					if (!pile.contains(n) && !spilled.contains(g.ident(n))) {
						spilled.add(g.ident(n));
						test = true;
						break;
					}
				}
			}
		}
		
		while(!pile.isEmpty()){
			Node p = pile.pop();
			Set<Integer> colorNonAvailable = new HashSet<Integer>();
			for (Node a : p.adj()) {
				if (color.containsKey(g.ident(a))) {
					colorNonAvailable.add(color.get(g.ident(a)));
				}
			}
			for (int i = 0; i < nbMaxColor; i++) {
				if(!colorNonAvailable.contains(i)){
					color.put(g.ident(p), i);
				}
			}
		}
		
		if (!test(g)) {
			System.out.println("PROBLEME MEME COULEUR ADJACENTE");
		}	
	}
	
	public boolean test(AbstractInterferenceGraph g) {
		for (Node n : g.nodes()) {
			for (Node a : n.adj()) {
				if (color.containsKey(n) && color.containsKey(a)) {
					if (color.get(n) == color.get(a)) {
						return false;
					}
				}
			}
		}
		return true;
	}

	// Cette version doit produire une coloration qui étend la map precolored
	public TP2ColorGraph(AbstractInterferenceGraph g, int nbMaxColor, Map<Ident,Integer> precolored) {
		//TODO
	}

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

}
