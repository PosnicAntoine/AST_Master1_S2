package rtl.constant;

import rtl.interpreter.ErrorException;

public class IntOrTop {

	private final Integer val;
	private IntOrTop() {
		this.val = null;
	}
	private IntOrTop(int i) {
		this.val = i;
	}
	// construction de TOP
	static public IntOrTop buildTop() {
		return new IntOrTop();
	}
	// construction d'une constante entière
	static public IntOrTop buildInt(int i) {
		return new IntOrTop(i);
	}
	// teste si this est TOP
	public boolean isTop() {
		return this.val==null;
	}
	// renvoie i si this est un entier i,
	// échoue avec une exception si this est TOP
	public int getInt() {
		if (isTop()) throw new ErrorException("IntOrOp: getInt is forbidden on TOP");
		return this.val;
	}		
	// renvoie une nouvelle valeur égal au join de this et v
	public IntOrTop join(IntOrTop v) {
		if (equals(v)) return this;
		else return buildTop();
	}
	public String toString() {
		if (this.val==null) return "T";
		else return this.val.toString(); 
	}
	public boolean equals(Object o) {
		if (!(o instanceof IntOrTop)) return false;
		IntOrTop i = (IntOrTop) o;
		if (this.val==null) return i.val==null;
		return this.val.equals(i.val);
	}
}

