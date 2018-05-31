package rtl;

import java.io.FileInputStream;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import rtl.graph.RtlCFG;
import rtl.graph.Graph.Node;

public class FreshIdentGenerator {

	String prefix;
	int fresh = 0;

	public FreshIdentGenerator(String prefix, Function f) {
		this.prefix = prefix;
		AllDefs defs = new AllDefs(f);
		while (defs.prefixAppears(prefix))
			prefix = "_"+prefix; // on ajoute suffisamment de "_" pour que le prefixe soit frais
	}
	
	public Ident fresh() {
		return new Ident(prefix+(fresh++));
	}

	private static boolean stringIsInt(String str) {
		try{
			int num = Integer.parseInt(str);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	class AllDefs {
		private Function f;
		private Set<Ident> defs;

		public AllDefs(Function f) {
			this.f = f;
		}

		public boolean prefixAppears(String prefix) {
			if (this.defs==null) compute();
			for (Ident id : defs) {
				String s = id.toString();
				if (s.startsWith(prefix)) {
					String suff = s.substring(prefix.length(), s.length());
					if (stringIsInt(suff)) return true;
				}
			}
			return false;
		}

		private void compute() {
			this.defs = new HashSet<>(this.f.params);
			DefInstr visitor = new DefInstr();
			for (Block b : f.blocks)
				for (Instr i : b.instrs)
					i.accept(visitor);
		}

		private class DefInstr implements InstrVisitor {

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
}
