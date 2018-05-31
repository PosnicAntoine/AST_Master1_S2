package tp5;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.text.html.HTMLDocument.Iterator;

import rtl.*;
import rtl.graph.*;
import rtl.graph.Graph.Node;
public class TP5AvailableExpr {

	private static abstract class Expr {
		// teste si l'expression contient la variable id
		abstract boolean containsIdent(Ident id);
	}

	private static class ReadExpr extends Expr {
		// la sous-classe des expressions représentant une lecture
		final MemRef mr;
		ReadExpr(MemRef mr) {
			this.mr = mr;
		}
		@Override public boolean equals(Object o) {
			if (!(o instanceof ReadExpr)) return false;
			return mr.equals(((ReadExpr) o).mr);
		}
		@Override public int hashCode() {
			return mr.hashCode() * 2;
		}
		@Override public String toString() {
			return mr.toString();
		}
		boolean containsIdent(Ident id) {
			return mr.ident.equals(id);
		}
	}

	private static class BuiltInExpr extends Expr {
		// la sous-classe des expressions représentant 
		// une addition, soustraction, multiplication,
		// comparaison ou 'et' booléen. 
		final String operator; 
		final List<Operand> args;
		BuiltInExpr(String operator, List<Operand> args) {
			this.operator = operator;
			this.args = args;
		}
		@Override public boolean equals(Object o) {
			if (!(o instanceof BuiltInExpr)) return false;
			BuiltInExpr e = (BuiltInExpr) o;
			return operator.equals(e.operator) && args.equals(e.args);
		}
		@Override public int hashCode() {
			return (operator.hashCode() + 31 * args.hashCode()) * 2 + 1;
		}
		@Override public String toString() {	
			String s = args.toString();
			return operator+"("+s.substring(1, s.length()-1)+")";	
		}		
		boolean containsIdent(Ident id) {
			return args.contains(id);
		}
	}	

	private Map<Node,Set<Expr>> aeIn = new Hashtable<>();
	private Map<Node,Set<Expr>> aeOut = new Hashtable<>();

	private Map<Node,Set<Expr>> aeInTmp = new Hashtable<>();
	private Map<Node,Set<Expr>> aeOutTmp = new Hashtable<>();
	private final DataFlowDebug debug;
	private Function f;
	private RtlCFG cfg;

	public TP5AvailableExpr(Function f, RtlCFG cfg, DataFlowDebug debug) {
		this.f = f;
		this.cfg = cfg;
		this.debug = debug;
		build();
	}

	public TP5AvailableExpr(Function f, RtlCFG cfg) {
		this(f,cfg,null);
	}

	private void build() {
		Node entry = cfg.entry(); // noeud d'entrée 

		Set<Expr> allExprs = new HashSet<>();
		for(Node n : cfg.nodes()){
			allExprs.add(exprAtNode(n));
		}
		allExprs.remove(null);
		System.out.println("allExpr:" +allExprs);
		//expressions apparaissant dans la fonction
		for (Node n : cfg.nodes()) { 
			aeOut.put(n, new HashSet<>());
			aeIn.put(n, new HashSet<>());
		}
		for (Node n : cfg.nodes()) {
			for (Expr e : allExprs) {
				if (!n.equals(entry)) {
					aeIn.get(n).add(e);
				}
				aeOut.get(n).add(e);
			}
		}
		System.out.println("IN:"+aeIn.toString());
		System.out.println("OUT:"+aeOut.toString());

		onePass();

		while (!isFixedPoint()) {
			for (Node n : this.aeIn.keySet()) {
				Set<Expr> set = new HashSet<Expr>(aeIn.get(n));
				this.aeInTmp.put(n, set);
			}
			for (Node n : this.aeOut.keySet()) {
				Set<Expr> set = new HashSet<Expr>(aeOut.get(n));
				this.aeOutTmp.put(n, set);
			}
			// FINISHED
			onePass();
		}
	}

	private void onePass() {
		System.out.println("-------------------------------------------------------");
		System.out.println("-------------------------------------------------------");
		System.out.println("-------------------------------------------------------");
		for (Node n : cfg.nodes()) {
			processIn(n);

			System.out.println("____________________"+n+"___________________");
			System.out.println("IN:"+aeIn.toString());
			System.out.println("OUT:"+aeOut.toString());
			System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");

			processOut(n);
			System.out.println("____________________"+n+"___________________");
			System.out.println("IN:"+aeIn.toString());
			System.out.println("OUT:"+aeOut.toString());
			System.out.println("_______________________________________");
		}
		System.out.println("-------------------------------------------------------");
		System.out.println("INtmp:"+aeOutTmp.toString());
		System.out.println("OUTtmp:"+aeInTmp.toString());

		if (debug!=null) debug.recordCurrentMaps(aeIn, aeOut);// ne pas modifier cette ligne
	}

	private void processIn(Node n) {
		Set<Node> setNtmp = new  HashSet<Graph.Node>(n.pred());
		java.util.Iterator<Node> it = setNtmp.iterator();
		while (it.hasNext()) {
			Node ntmp = it.next();
			aeIn.get(n).retainAll(aeOut.get(ntmp));
		}
	}

	private void processOut(Node n) {
		aeOut.put(n, new HashSet<>());
		aeOut.get(n).addAll(aeIn.get(n));
		Expr expr = exprAtNode(n);
		System.out.println(n +" "+expr);
		if (expr != null) {
			aeOut.get(n).remove(cfg.def(n).iterator().next());
			aeOut.get(n).add(expr);
			System.out.println(n +"="+aeOut.get(n));
		}
	}

	private boolean isFixedPoint() {
		boolean finish = true;
		for (Node n : cfg.nodes()) {
			finish = finish && this.aeIn.get(n).equals(this.aeInTmp.get(n)) && this.aeOut.get(n).equals(this.aeOut.get(n));
		}
		return finish;
	}

	// renvoie l'expression apparaissant au noeud n, null sinon
	private Expr exprAtNode(Node n) {
		Object o = cfg.instr(n);
		if(o instanceof Instr){
			Instr instr = (Instr) o;
			if(instr instanceof BuiltIn){
				BuiltIn bi = (BuiltIn) instr;
				return new BuiltInExpr(bi.operator, bi.args);
			}else if(instr instanceof MemRef){
				MemRef memes = (MemRef) instr;
				return new ReadExpr(memes);
			}
		}
		return null;
	}

	// pour tout noeud n, où une expression e est utilisée et disponible,
	// on attache l'ensemble des noeuds où une variable w a reçu 
	// l'expression e en un noeud n'. Il doit exister un chemin de n' à n
	// le long duquel les variables de l'expression e ne sont pas redéfinies.
	public Map<Node,Set<Node>> reachableExpressions() {
		Map<Node,Set<Node>> reachExpr = new Hashtable<>();
		//TODO
		return reachExpr;
	}

}
