package rtl.constant;

import java.util.Hashtable;
import java.util.Map;
import java.util.Set;

import rtl.*;
import rtl.interpreter.ErrorException;

public class ConstMap {

	private final Map<Ident,IntOrTop> map;
	private ConstMap() {
		this.map = null;
	}
	private ConstMap(IntOrTop v, Set<Ident> s) {
		this.map = new Hashtable<>();
		for (Ident id : s) this.map.put(id, v);
	}
	private ConstMap(ConstMap cm) {
		this.map = new Hashtable<>(cm.map);
	}
	// construction de BOT
	static public ConstMap buildBot() {
		return new ConstMap();
	}
	// teste si this est égal à BOT
	public boolean isBot() {
		return this.map==null;
	}
	private ConstMap copy() {
		return new ConstMap(this);
	}
	// construction d'une fonction de domaine dom, où chaque ident est associé à TOP
	static public ConstMap buildTop(Set<Ident> dom) {
		return new ConstMap(IntOrTop.buildTop(),dom);
	}
	// renvoie le join de this est mp
	public ConstMap join(ConstMap mp) {
		if (isBot()) return mp;
		if (mp.isBot()) return this;
		ConstMap res = copy();
		for (Ident id : map.keySet())
			res.map.put(id, map.get(id).join(mp.get(id)));
		return res;
	}
	// applique la fonction this sur id, échoue si this est égal à BOT
	public IntOrTop get(Ident id) {
		if (isBot()) throw new ErrorException("ConstMap: get is forbidden on BOT");
		if (!this.map.containsKey(id)) throw new ErrorException("ConstMap: get on uninitialized ident "+id);
		return this.map.get(id);
	}
	// renvoie une nouvelle fonction égal à la fonction this,
	// sauf pour l'ident id qui est associé à v;
	// échoue si this est égal à BOT
	public ConstMap set(Ident id, IntOrTop v) {
		if (isBot()) throw new ErrorException("ConstMap: put is forbidden on BOT");
		if (!this.map.containsKey(id)) throw new ErrorException("ConstMap: put on uninitialized ident "+id);
		ConstMap res = this.copy();
		res.map.put(id, v);
		return res;
	}
	@Override public boolean equals(Object o) {
		if (!(o instanceof ConstMap)) return false;
		ConstMap cm = (ConstMap) o;
		if (this.map==null) return cm.map==null;
		return this.map.equals(cm.map);
	}
	@Override public String toString() {
		if (this.isBot()) return "BOT";
		else return this.map.toString();
	}		
}

