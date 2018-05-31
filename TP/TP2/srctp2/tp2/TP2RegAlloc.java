package tp2;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import rtl.*;
import rtl.graph.ColorGraph;
import rtl.graph.InterferenceGraph;
import rtl.graph.RtlCFG;
import rtl.interpreter.ErrorException;

public class TP2RegAlloc {

	public static void main(String[] args) {
		try {
			String path = "D:/Workspace/AST_TP2/srctp2/";
			java.io.InputStream in = new FileInputStream(path+"rtl/examples/QuickSort.rtl");
			Program prog = rtl.Parser.run(in);
			transform(prog);
			prog.print();
		} catch (Throwable e) {
			System.out.println("Register allocation construction failed: " + e.getMessage());
		}
	}

	public static void transform(Program p) {
		for (Function f : p.functions) 
			new TP2RegAlloc(f);
	}

	private int nbMaxRegister;
	private Map<Ident,Integer> color = new Hashtable<Ident,Integer>();
	private Map<Ident,Ident> register = new Hashtable<Ident,Ident>();
	private Map<Ident,Integer> memory = new Hashtable<Ident,Integer>();
	private static Ident[] workingRegisters = {new Ident("AX"),new Ident("BX"),new Ident("CX"),new Ident("DX")};
	private static Ident[] tempRegisters = {new Ident("S1"),new Ident("S2"),new Ident("S3"),new Ident("S4")};
	private int counterSpilled = 0;

	TP2RegAlloc(Function f) {
		this.nbMaxRegister = 4;

		// au plus 4 parametres par fonctions [simplification]
		if (f.params.size()>4) throw new ErrorException("trop de parametres pour l'allocation de registre",f.name);

		// on réserve un registre pour chaque parametre de la fonction
		int reservedParams = 0;
		for (Ident p : f.params)
			color.put(p, reservedParams++);

		RtlCFG g = new RtlCFG(f);          
		Liveness live = new Liveness(g);   
		live.build();                      
		InterferenceGraph igraph = new InterferenceGraph(g,live);

		ColorGraph cg = new ColorGraph(igraph,nbMaxRegister,color); // version enseignant

		//on récupére le résultat du coloriage de graphe
		for (Ident id : cg.color.keySet()) {
			register.put(id, workingRegisters[cg.color.get(id)]);
			// pour écrire des commentaires en début de fonction
			f.addComments(id+" --> "+register.get(id));			
		}
		int counter = 0;
		for (Ident id : cg.spilled) {
			memory.put(id, counter);
			// pour écrire des commentaires en début de fonction
			f.addComments(id+" --> [SP+"+memory.get(id)+"]");		
			counter++;
		}
		f.addComments("spilling = "+cg.spilled);	

		//on renomme chaque paramètres de la fonction avec son registre associé
		List<Ident> newParams = new ArrayList<Ident>();
		for (Ident p : f.params) newParams.add(rename(p));
		// une fois la liste des paramétres parcourus, on la vide 
		f.params.clear();
		// puis on la remplie
		for (Ident id : newParams) f.params.add(id);

		// on parcourt les blocs pour modifiers leurs instructions
		for (Block b : f.blocks) {
			// la liste qui va acceuillir les nouvlles instructions 
			List<Instr> newInstrs = new ArrayList<Instr>();
			for (Instr i : b.instrs) {
				TransformInstr visitor = new TransformInstr();
				i.accept(visitor);
				newInstrs.addAll(visitor.newInstr); // l'instruction renommée
				this.counterSpilled = 0;
			}
			b.instrs.clear(); // on vide la liste des instructions de b
			b.instrs.addAll(newInstrs); // on la remplie avec les nouvelles instructions 
			TransformEndInstr visitor = new TransformEndInstr();
			b.setEnd(b.getEnd().accept(visitor));
		}

	}

	
	boolean isRenameNeeded (Ident id) {
		if (memory.get(id) != null) return true;
		return false;
	}
	
	Ident rename (Ident id) {
		if (id==null) return null;
		if (register.containsKey(id)) return register.get(id);
		if (this.counterSpilled < this.nbMaxRegister) this.counterSpilled++ ;return tempRegisters[(this.counterSpilled)-1];
	}
	
	int getOffset (MemRef mr) {
		if (this.memory.containsValue(mr.ident)) return this.memory.get(mr.ident);
		return mr.offset;
	}

	class TransformInstr implements InstrVisitor {

		List<Instr> newInstr = new ArrayList<Instr>();

		List<Operand> renameOps(List<Operand> ops) {
			List<Operand> args = new ArrayList<Operand>();
			for (Operand o : ops)
				args.add(o.accept(new TransformOperand()));
			return args;
		}

		MemRef renameMemRef(MemRef mr) {
			if(isRenameNeeded(mr.ident)) return new MemRef(new Ident("SP"),getOffset(mr));
			return new MemRef(rename(mr.ident),mr.offset);
		}

		public void visit(Assign a) {
			
			newInstr.add(new Assign(rename(a.ident),a.operand.accept(new TransformOperand())));
		}

		public void visit(BuiltIn bi) {
			newInstr.add(new BuiltIn(bi.operator, rename(bi.target)	, renameOps(bi.args)));
		}

		public void visit(Call c) {
			newInstr.add(new Call(rename(c.target), c.getCallee(), renameOps(c.args)));
		}

		public void visit(MemRead mr) {
			newInstr.add(new MemRead(rename(mr.ident),renameMemRef(mr.memRef)));
		}

		public void visit(MemWrite mw) {
			newInstr.add(new MemWrite(renameMemRef(mw.memRef), mw.operand.accept(new TransformOperand())));
		}						
	}


	class TransformEndInstr implements EndInstrVisitor<EndInstr> {

		public EndInstr visit(Goto g) {
			return g;
		}

		public EndInstr visit(Branch br) {
			return new Branch(br.condition.accept(new TransformOperand()),br.thenTarget,br.elseTarget);
		}

		public EndInstr visit(Return r) {
			if (r.operand==null) return r;
			return new Return(r.operand.accept(new TransformOperand()));
		}
	}



	class TransformOperand implements OperandVisitor<Operand> {

		public Operand visit(Ident id) {
			return rename(id);
		}

		public Operand visit(LitInt li) {
			return li;
		}

	}

}
