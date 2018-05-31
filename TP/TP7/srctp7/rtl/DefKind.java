package rtl;

public interface DefKind {

	public <A> A accept(DefKindVisitor<A> v);
	
}
