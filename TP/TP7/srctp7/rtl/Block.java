package rtl;

import java.util.ArrayList;
import java.util.List;

public class Block {

	public final Ident label;
	public final List<Phi> phis;
	public final List<Instr> instrs;
	private EndInstr end;

	public Block(Ident label, EndInstr end) {
		this.label = label;
		this.phis = new ArrayList<>();
		this.instrs = new ArrayList<>();
		this.end = end;
	}

	public EndInstr getEnd() {
		return end;
	}

	public void setEnd(EndInstr end) {
		this.end = end;
	}
	
	public void addPhi(Phi phi) {
		phis.add(phi);
	}

	public void addInstr(Instr i) {
		instrs.add(i);
	}

	public void print() {
		System.out.println("  "+this.label.toString()+":");
		for (Phi phi : this.phis)
			System.out.println("    "+phi);		
		for (Instr instr : this.instrs)
			System.out.println("    "+instr);
		System.out.println("    "+this.end);		
	}
}
