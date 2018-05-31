package tp7;

import java.io.FileInputStream;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;
import rtl.*;

public class TP7SSAInformation {
	
	private Function f;
	private Map<Ident,DefKind> kind;
	private Map<Ident,Set<Ident>> defUse;
	
	
	public TP7SSAInformation(Function f) {
		this.f = f;
	}
	
	// renvoie l'ensemble des variables définies dans la fonction SSA
	public Set<Ident> idents() {
		if (kind==null) compute();
		return kind.keySet();
	}

	// renvoie le type de (l'unique) définition associée à x dans la fonction SSA
	public DefKind kind(Ident x) {
		if (kind==null) compute();
		if (!kind.containsKey(x)) throw new rtl.interpreter.ErrorException("kind failed on ident "+x);
		return kind.get(x);
	}
	
	// renvoie l'ensemble des variables dont la définition utilise la variable x
	public Set<Ident> uses(Ident x) {
		if (defUse==null) compute();
		if (!kind.containsKey(x)) throw new rtl.interpreter.ErrorException("uses failed on ident "+x);
		if (!defUse.containsKey(x)) return new HashSet<>();
		return defUse.get(x);
	}

	// affiches les informations calculées 
	public void show() {
		compute();
		System.out.println("DEFS :");
		for (Ident id : idents())
			if (kind.get(id) instanceof Ident)
				System.out.println("  "+id+" is defined by parameter "+kind.get(id));
			else 
				System.out.println("  "+id+" is defined by "+kind(id));
		System.out.println("USES :");
		for (Ident id : idents())
			System.out.println("  "+id+" is used by "+uses(id));
	}
	
	private void compute() {
		this.kind = new Hashtable<>();
		this.defUse = new Hashtable<>();
		//TODO construction de kind et defUse
	}	
	
	// enregistre le fait que l'ident use est utilisé lors de la définition de def
	private void addDefUse(Ident def, Ident use) {
		if (!defUse.containsKey(use)) defUse.put(use, new HashSet<>());
		defUse.get(use).add(def);
	}
	
	// enregistre la définition de def comme étant d
	private void addDef(Ident def, DefKind d) {
		if (kind.containsKey(def)) throw new rtl.interpreter.ErrorException("ident "+def+" is defined twice");
		kind.put(def, d);
	}
	
}
