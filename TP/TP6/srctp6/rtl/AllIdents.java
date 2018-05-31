package rtl;

import java.util.HashSet;
import java.util.Set;

public class AllIdents {

	public static Set<Ident> compute(Function f) {
		Set<Ident> defs = new HashSet<>(f.params);
		DefInstr visitor = new DefInstr(defs);
		for (Block b : f.blocks)
			for (Instr i : b.instrs)
				i.accept(visitor);
		return defs;
	}

	private static class DefInstr implements InstrVisitor {
		
		private Set<Ident> defs;

		DefInstr(Set<Ident> defs) {
			this.defs = defs;
		}

		public void visit(Assign a) {
			defs.add(a.ident);
		}

		public void visit(BuiltIn bi) {
			if (bi.target!=null) defs.add(bi.target);
		}	

		public void visit(Call c) {
			defs.add(c.target);
		}

		public void visit(MemRead mr) {
			defs.add(mr.ident);
		}

		public void visit(MemWrite mw) {				
		}
	}
}

