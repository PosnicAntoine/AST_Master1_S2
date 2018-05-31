package rtl;

public interface Instr extends DefKind {

	public void accept(InstrVisitor v);
	
	public <A> A accept(InstrRetVisitor<A> v);

	public <A> A accept(DefKindVisitor<A> v);

}

