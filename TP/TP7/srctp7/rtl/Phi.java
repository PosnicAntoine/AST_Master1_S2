package rtl;

import java.util.List;

public class Phi implements DefKind {
	public final Ident target;
	public final List<PhiArg> args;

	public Phi(Ident target, List<PhiArg> args) {
		this.target = target;
		this.args = args;
	}

	public String toString() {
		String res = target.toString()+" = phi";
		int i = 0;
		for (PhiArg arg : args) {
			if (i>0) res = res+",";
			res = res+" "+arg.toString();
			i++;
		}
		return res;
	}   
	
	public <A> A accept(DefKindVisitor<A> v) {
		return v.visit(this);
	}

}
