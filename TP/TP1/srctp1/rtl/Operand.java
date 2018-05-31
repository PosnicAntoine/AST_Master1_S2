package rtl;

public interface Operand {

	public <V> V accept(OperandVisitor<V> v);
	public void accept(OperandVisitorNoRetValue v);
	
}
