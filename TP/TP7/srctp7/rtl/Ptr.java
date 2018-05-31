package rtl;

public class Ptr implements Value {
	
	public final int offset; // position dans la mémoire globale heap

	public String toString() {
		return "Ptr[" + offset + "]";
	}

	public Ptr(int offset) {
		this.offset = offset;
	}

	public Value add(int i) {
		return new Ptr(offset+i);
	}

}
