package tp6;

import java.io.FileInputStream;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;

import rtl.*;
import rtl.constant.*;
import rtl.graph.FlowGraph;
import rtl.graph.RtlCFG;
import rtl.interpreter.ErrorException;
import rtl.graph.Graph.Node;

public class TP6ConstantInference {

	private Map<Node,ConstMap> ctIn = new Hashtable<>();
	private Map<Node,ConstMap> ctOut = new Hashtable<>();
	private Map<Node,ConstMap> ctInPrevious = new Hashtable<>();
	private Map<Node,ConstMap> ctOutPrevious = new Hashtable<>();
	private final DataFlowDebug debug;
	private RtlCFG g;
	private Set<Ident> allVars;

	public TP6ConstantInference(Function f, RtlCFG g, DataFlowDebug debug) {
		this.g = g;		
		allVars = AllIdents.compute(f);		
		this.debug = debug;
		build();
	}

	public TP6ConstantInference(Function f, RtlCFG g) {
		this(f,g,null);
	}


	private ConstMap initial(Node n) {
		Node entry = g.entry();
		if(entry.equals(n)){
			return ConstMap.buildTop(this.allVars);
		}
		return ConstMap.buildBot();
	}

	private ConstMap transfert(Node n, ConstMap cm) {
		if (cm.isBot()) return cm;
		Object o = g.instr(n);
		if (o instanceof Instr) {
			Instr i = (Instr) o;
			VisInstr visInstr = new VisInstr(cm);
			return i.accept(visInstr);
		}
		else return cm; // dans tous les autres cas, pas de changement
	}

	private void build() {
		for (Node n : g.nodes()) { 
			ctIn.put(n, initial(n));
			ctOut.put(n, ConstMap.buildBot());
		}
		System.out.println("_______________________________________________________________________________________________");
		printCts("Initial");
		onePass(g);
		
		printCts("OnePass n°1");
		int counter = 2;
		while (!isFixedPoint(g)){
			onePass(g);	
			printCts("OnePass n°"+counter);
			counter++;
		}
	}

	private void printCts(String step){
		System.out.println(step+":");
		System.out.println("ctIn:  "+this.ctIn.toString());
		System.out.println("ctOut: "+this.ctOut.toString());
		System.out.println("_______________________________________________________________________________________________");
	}

	public void onePass(FlowGraph g) {
		for (Node n : g.nodes()) {
			ctInPrevious.put(n, ctIn.get(n)); // copie avec partage car ConstMap immutable
			ctOutPrevious.put(n, ctOut.get(n)); // copie avec partage car ConstMap immutable
			// 
			ConstMap s = initial(n);
			for (Node pred : n.pred()) 
				s = s.join(ctOut.get(pred));
			ctIn.put(n, s);
			//
			ctOut.put(n, transfert(n,ctIn.get(n)));
		}		
		if (debug!=null) debug.recordCurrentMaps(ctIn, ctOut);
	}

	public boolean isFixedPoint(FlowGraph g) {
		return ctIn.equals(ctInPrevious) && ctOut.equals(ctOutPrevious);
	}

	class VisInstr implements InstrRetVisitor<ConstMap>{
		ConstMap cm;
		
		public VisInstr(ConstMap cm){
			this.cm = cm;
		}
		
		@Override
		public ConstMap visit(Assign a) {
			IntOrTop intOrTop = a.operand.accept(new VisOper(this.cm));
			return this.cm.set(a.ident, intOrTop);
			
		}

		@Override
		public ConstMap visit(BuiltIn bi) {
			IntOrTop fst = bi.args.get(0).accept(new VisOper(this.cm));
			IntOrTop snd = bi.args.get(1).accept(new VisOper(this.cm));
			
			if(bi.operator.equals(bi.ADD)){
				if(fst.isTop() || snd.isTop()){
					return this.cm.set(bi.target, IntOrTop.buildTop());
				}else{
					return this.cm.set(bi.target, IntOrTop.buildInt(fst.getInt() + snd.getInt()));
				}
				
			}else if(bi.operator.equals(bi.SUB)){
				if(fst.isTop() || snd.isTop()){
					return this.cm.set(bi.target, IntOrTop.buildTop());
				}else{
					return this.cm.set(bi.target, IntOrTop.buildInt(fst.getInt() - snd.getInt()));
				}
				
			}else if(bi.operator.equals(bi.MUL) || bi.operator.equals(bi.AND)){
				if(fst.getInt() == 0 || snd.getInt() == 0){
					return this.cm.set(bi.target, IntOrTop.buildInt(0));
				}else if(fst.isTop() || snd.isTop()){
					return this.cm.set(bi.target, IntOrTop.buildTop());
				}else{
					return this.cm.set(bi.target, IntOrTop.buildInt(fst.getInt() * snd.getInt()));
				}
				
			}else if(bi.operator.equals(bi.LT)){				
				if(fst.isTop() || snd.isTop()){
					return this.cm.set(bi.target, IntOrTop.buildTop());
				}else{
					if(fst.getInt() < snd.getInt()){
						return this.cm.set(bi.target, IntOrTop.buildInt(1));
					}else{
						return this.cm.set(bi.target, IntOrTop.buildInt(0));
					}
				}				
			}
			return this.cm;
		}
		
		@Override
		public ConstMap visit(Call c) {
			return this.cm.set(c.target, IntOrTop.buildTop());
		}

		@Override
		public ConstMap visit(MemRead mr) {
			return this.cm.set(mr.ident, IntOrTop.buildTop());
		}

		@Override
		public ConstMap visit(MemWrite mw) {
			return this.cm;
		}

	}

	class VisOper implements OperandVisitor<IntOrTop>{
		ConstMap cm;

		public VisOper(ConstMap cm){
			this.cm = cm;
		}
		
		@Override
		public IntOrTop visit(Ident id) {
			return this.cm.get(id);
		}

		@Override
		public IntOrTop visit(LitInt li) {
			return IntOrTop.buildInt(li.getVal());
		}

	}


}