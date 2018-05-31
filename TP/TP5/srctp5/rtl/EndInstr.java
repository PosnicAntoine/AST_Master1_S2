package rtl;

public interface EndInstr {

	public <V> V accept(EndInstrVisitor<V> v);
	public void accept(EndInstrVisitorNoRetValue v);
	
}

