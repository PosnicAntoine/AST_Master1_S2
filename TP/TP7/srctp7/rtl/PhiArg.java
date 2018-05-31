package rtl;

public class PhiArg {
	public final Operand operand;
	public final Block block;
	
	public PhiArg(Operand operand, Block block) {
		this.operand = operand;
		this.block = block;
	}
	
	public String toString() {
		return "[ "+operand+", "+block.label+" ]";
	}
}
