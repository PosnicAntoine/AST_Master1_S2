package rtl;

public interface EndInstrVisitorNoRetValue {

	public void visit(Goto g);
	public void visit(Branch br);
	public void visit(Return r);	
	
}
