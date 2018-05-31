package rtl;

public interface Instr {

	public void accept(InstrVisitor v);
	
	public <A> A accept(InstrRetVisitor<A> v);
	
}

