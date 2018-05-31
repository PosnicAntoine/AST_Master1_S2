package rtl;

public interface OperandVisitorNoRetValue {

	public void visit(Ident id);
	public void visit(LitInt li);
	
}
