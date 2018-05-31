package rtl;

import java.util.List;
import java.util.Set;

import rtl.interpreter.ErrorException;

import java.util.Arrays;
import java.util.HashSet;

public class BuiltIn implements Instr {

	public static String ALLOC = "Alloc";
	public static String PRINT = "PrintInt";
	public static String ADD = "Add";
	public static String SUB = "Sub";
	public static String MUL = "Mul";
	public static String AND = "And";
	public static String LT = "Lt";
	
	private static Set<String> operatorNames = 
			new HashSet<String>(Arrays.asList(new String[] { ALLOC, PRINT, ADD, SUB, MUL, AND, LT }));
	public final String operator; // must belongs to operatorNames
	public final Ident target; // null if no target
	public final List<Operand> args;

	public BuiltIn(String operator, Ident target, List<Operand> args) {
		if (!operatorNames.contains(operator)) throw new ErrorException(operator+ "is not a valid builtin operator");
		this.operator = operator;
		this	.target = target;
		this.args = args;
	}

	public String toString() {
		String res = "";
		if (target!=null) res = target.toString()+" = ";
		res = res+operator+"("+Call.stringOfList(args)+")";
		return res;
	}	

	public void accept(InstrVisitor v) {
		v.visit(this);
	}

	public <A> A accept(InstrRetVisitor<A> v) {
		return v.visit(this);
	}

	public <A> A accept(DefKindVisitor<A> v) {
		return v.visit(this);
	}


}
