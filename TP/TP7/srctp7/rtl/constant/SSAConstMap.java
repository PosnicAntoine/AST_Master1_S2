package rtl.constant;

import java.util.Hashtable;
import java.util.Map;
import java.util.Set;

import rtl.*;
import rtl.interpreter.ErrorException;

public class SSAConstMap {

	private final Map<Ident,BotOrIntOrTop> map = new Hashtable<>();
	private SSAConstMap() {
	}
	private SSAConstMap(BotOrIntOrTop v, Set<Ident> s) {
		for (Ident id : s) this.map.put(id, v);
	}
	// construction d'une fonction de domaine dom, où chaque ident est associé à BOT
	static public SSAConstMap buildBot(Set<Ident> dom) {
		return new SSAConstMap(BotOrIntOrTop.buildBot(),dom);
	}
	// applique la fonction this sur id
	public BotOrIntOrTop get(Ident id) {
		if (!this.map.containsKey(id)) throw new ErrorException("SSAConstMap: get on uninitialized ident "+id);
		return this.map.get(id);
	}
	// modifie la map courante en associant l'ident id à la valeur v;
	public void set(Ident id, BotOrIntOrTop v) {
		if (!this.map.containsKey(id)) throw new ErrorException("SSAConstMap: set on uninitialized ident "+id);
		this.map.put(id, v);
	}
	@Override public boolean equals(Object o) {
		if (!(o instanceof SSAConstMap)) return false;
		SSAConstMap cm = (SSAConstMap) o;
		return this.map.equals(cm.map);
	}
	@Override public String toString() {
		return this.map.toString();
	}		
}

