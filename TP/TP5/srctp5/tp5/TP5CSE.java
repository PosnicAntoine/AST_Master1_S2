package tp5;

import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;

import rtl.graph.Graph.Node;
import rtl.graph.RtlCFG;
import rtl.*;
import rtl.Transform.TransformInstrResult;

public class TP5CSE extends Transform {

	public static void transform(Program p) {
		for (Function f : p.functions) 
			new TP5CSE(f).transform(f);
	}

	private RtlCFG cfg;
	private Map<Node,Set<Node>> reachExprs; 
	private FreshIdentGenerator genIdent;

	TP5CSE(Function f) {
		cfg = new RtlCFG(f);
		
		// afin de pouvoir générer des variable fraiches dont le prefixe sera "cse"
		genIdent = new FreshIdentGenerator("cse", f);
		// genIdent.fresh() renvoie ensuite une nouvelle variable fraiche à chaque appel

		AvailableExpressions ae = new AvailableExpressions(f, cfg); //version enseignant
		reachExprs = ae.reachableExpressions();
		
		//TODO			
	}	

	//TODO redefinir les méthodes de la classe Transform qui vous interessent
	//public TransformInstrResult transform(Assign a)
	//public TransformInstrResult transform(BuiltIn bi)
	//public TransformInstrResult transform(Call c)
	//public TransformInstrResult transform(MemRead mr)
	//public TransformInstrResult transform(MemWrite mw)
}
