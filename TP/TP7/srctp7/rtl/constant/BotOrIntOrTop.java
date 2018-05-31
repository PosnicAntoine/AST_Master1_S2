package rtl.constant;

import rtl.interpreter.ErrorException;

public class BotOrIntOrTop {

	private final Integer val;
	private final boolean is_bot;
	private BotOrIntOrTop(boolean is_bot) {
		this.val = null;
		this.is_bot = is_bot;
	}
	private BotOrIntOrTop(int i) {
		this.val = i;
		this.is_bot = false;
	}
	// construction de BOT
	static public BotOrIntOrTop buildBot() {
		return new BotOrIntOrTop(true);
	}
	// construction de TOP
	static public BotOrIntOrTop buildTop() {
		return new BotOrIntOrTop(false);
	}
	// construction d'une constante entière
	static public BotOrIntOrTop buildInt(int i) {
		return new BotOrIntOrTop(i);
	}
	// teste si this est BOT
	public boolean isBot() {
		return this.val==null && this.is_bot;
	}
	// teste si this est TOP
	public boolean isTop() {
		return this.val==null && !this.is_bot;
	}
	// renvoie i si this est un entier i,
	// échoue avec une exception si this est TOP
	public int getInt() {
		if (isTop()) throw new ErrorException("BotOrIntOrOp: getInt is forbidden on TOP");
		if (isBot()) throw new ErrorException("BotOrIntOrOp: getInt is forbidden on BOT");
		return this.val;
	}		
	// renvoie une nouvelle valeur égal au join de this et v
	public BotOrIntOrTop join(BotOrIntOrTop v) {
		if (equals(v) || v.isBot()) return this;
		else if (this.isBot()) return v;
		else return buildTop();
	}
	public String toString() {
		if (isTop()) return "⊤";
		if (isBot()) return "⊥";
		else return this.val.toString(); 
	}
	public boolean equals(Object o) {
		if (!(o instanceof BotOrIntOrTop)) return false;
		BotOrIntOrTop i = (BotOrIntOrTop) o;
		if (this.val==null) return (i.val==null && this.is_bot==i.is_bot);
		return this.val.equals(i.val);
	}
}

