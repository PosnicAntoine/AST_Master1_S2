package rtl;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import rtl.graph.ColorGraph;
import rtl.graph.Graph.Node;
import rtl.graph.InterferenceGraph;
import rtl.graph.RtlCFG;
import rtl.interpreter.ErrorException;

public class Transform {

	public static void main(String[] args) {
		try {
			//String path = "/Users/david/Works/svn/lande/teaching/Master1/AST/src/";
			//java.io.InputStream in = new FileInputStream(path+"rtl/examples/MoreThan4.rtl");
			Program prog = rtl.Parser.run(System.in);
			transform(prog);
			prog.print();
		} catch (Throwable e) {
			System.out.println("Transformation failed: " + e.getMessage());
		}
	}

	public static void transform(Program p) {
		for (Function f : p.functions) 
			new Transform().transform(f);
	}

	public final void transform(Function f) {
		// on ne change pas les parametres
		for (Block b : f.blocks) {
			List<Instr> newInstrs = new ArrayList<>();
			for (Instr i : b.instrs) {
				TransformInstr visitor = new TransformInstr();
				i.accept(visitor);
				newInstrs.addAll(visitor.res.addBefore);
				if (visitor.res.newInstr!=null) newInstrs.add(visitor.res.newInstr);
				newInstrs.addAll(visitor.res.addAfter);
			}
			b.instrs.clear();
			b.instrs.addAll(newInstrs);
			
			TransformEndInstr visitor = new TransformEndInstr();
			TransformEndInstrResult res = b.getEnd().accept(visitor);
			newInstrs.addAll(res.addBefore);
			b.setEnd(res.newInstr);
		}
	}

	public class TransformInstrResult {
		public final List<Instr> addBefore = new ArrayList<>();
		final Instr newInstr;
		public final List<Instr> addAfter = new ArrayList<>();
		public TransformInstrResult(Instr newInstr) {
			this.newInstr = newInstr;
		}
	}
	
	public class TransformEndInstrResult {
		final List<Instr> addBefore = new ArrayList<>();
		final EndInstr newInstr;
		public TransformEndInstrResult(EndInstr newInstr) {
			this.newInstr = newInstr;
		}
	}
	
	public TransformInstrResult transform(Assign a) {
		return new TransformInstrResult(a);
	}

	public TransformInstrResult transform(BuiltIn bi) {
		return new TransformInstrResult(bi);
	}

	public TransformInstrResult transform(Call c) {
		return new TransformInstrResult(c);
	}

	public TransformInstrResult transform(MemRead mr) {
		return new TransformInstrResult(mr);
	}

	public TransformInstrResult transform(MemWrite mw) {
		return new TransformInstrResult(mw);
	}

	public TransformEndInstrResult transform(Goto g) {
		return new TransformEndInstrResult(g);
	}

	public TransformEndInstrResult transform(Branch br) {
		return new TransformEndInstrResult(br);
	}

	public TransformEndInstrResult transform(Return ret) {
		return new TransformEndInstrResult(ret);
	}

	class TransformInstr implements InstrVisitor {

		TransformInstrResult res;

		public void visit(Assign a) {
			res = transform(a); 
		}

		public void visit(BuiltIn bi) {
			res = transform(bi);
		}

		public void visit(Call c) {
			res = transform(c);
		}

		public void visit(MemRead mr) {
			res = transform(mr);
		}

		public void visit(MemWrite mw) {
			res = transform(mw);
		}						
	}


	class TransformEndInstr implements EndInstrVisitor<TransformEndInstrResult> {

		public TransformEndInstrResult visit(Goto g) {
			return transform(g);
		}

		public TransformEndInstrResult visit(Branch br) {
			return transform(br);
		}

		public TransformEndInstrResult visit(Return r) {
			return transform(r);
		}
	}


}
