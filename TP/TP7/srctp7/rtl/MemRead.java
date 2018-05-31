package rtl;

public class MemRead implements Instr {
	public final Ident ident;
	public final MemRef memRef;

	public MemRead(Ident ident, MemRef memRef) {
		this.ident = ident;
		this.memRef = memRef;
	}

	public String toString() {
		return ident.toString()+" = "+memRef.toString();
	}

	public void accept(InstrVisitor v) {
		v.visit(this);
	}
	
	public <A> A accept(InstrRetVisitor<A> v) {
		return v.visit(this);
	}
	
	public <A> A accept(DefKindVisitor<A> v) {
		return v.visit(this);
	}

}

