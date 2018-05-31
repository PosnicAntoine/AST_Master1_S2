package rtl;

public class Ident implements Operand {
	
	private final String str;
	public final DebugInfo debug;
	
	public Ident(String str, DebugInfo debug) {
		this.str = str;
		this.debug = debug;
	}

	public Ident(String str) {
		this.str = str;
		this.debug = null;
	}

	@Override
	public String toString() { return str; }
	
	@Override
	public int hashCode() { return str.hashCode(); }
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Ident)) return false;
		return str.equals(obj.toString());
	}

	public <V> V accept(OperandVisitor<V> v) {
		return v.visit(this);
	}

	public void accept(OperandVisitorNoRetValue v) {
		v.visit(this);
	}

}
