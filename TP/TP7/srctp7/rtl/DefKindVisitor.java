package rtl;

public interface DefKindVisitor<V> {

	public V visit(Assign a);
	public V visit(BuiltIn bi);
	public V visit(Call c);
	public V visit(MemRead mr);

	public V visit(Phi phi);

	public V visit(Ident param);	
}
