package rtl;

public class MemRef {
	public final Ident ident;
	public final int offset;

	public MemRef(Ident ident, int offset) {
		this.ident = ident;
		this.offset = offset;
	}

	@Override public boolean equals(Object o) {
		if (!(o instanceof MemRef)) return false;
		MemRef mr = (MemRef) o;
		return ident.equals(mr.ident) && offset==mr.offset;
	}
	
	@Override public int hashCode() {
		return ident.hashCode() + 31 * offset;
	}
	
	@Override public String toString() {
		String offsetString;
		if (offset==0) offsetString = "";
		else if (offset>0) offsetString = "+"+offset;
		else offsetString = ""+offset;
		return "["+ident.toString()+offsetString+"]";
	}
}

