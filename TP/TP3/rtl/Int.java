package rtl;

public class Int implements Value {

	public final int val;

	public String toString() { return "Int[" + val + "]"; }

	public Int(int val) { this.val = val; }

	public int get() { return val; }

	public Value add(int i) { return new Int(val+i);	}
	
}
