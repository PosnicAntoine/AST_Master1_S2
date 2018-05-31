package rtl;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import rtl.interpreter.ErrorException;

public class Interpreter {

	Heap heap;

	public void setHeap(Heap h) {
		this.heap = h;
	}

	Map<Ident,Value> lvar = new Hashtable<Ident,Value>();
	Stack<Map<Ident,Value>> callstack = new Stack<Map<Ident,Value>>();

	public void run(Program p) {
		run(p.getMain());		
	}

	Value run(Function f) {
		return run(f.getEntry());
	}

	Value run(Function f, List<Value> args) {
		int n = args.size();
		for (int i=0; i<n; i++) 
			lvar.put(f.params.get(i), args.get(i));
		return run(f);
	}

	Value run(Block b) {
		for (Instr i : b.instrs)
			i.accept(new EvalInstr());
		return b.getEnd().accept(new EvalEndInstr());
	}


	class EvalInstr implements InstrVisitor{

		public void visit(Assign a) {
			lvar.put(a.ident, a.operand.accept(new EvalOperand()));
		}

		public void visit(BuiltIn bi) {
			List<Value> args = evalOperands(bi.args);
			Value res = null;
			if (bi.operator=="Alloc") {
				if (args.size()!=1) throw new ErrorException("builtIn operator "+bi.operator+" expects 1 argument",bi.target);
				Value v = args.get(0);
				if (!(v instanceof Int)) throw new ErrorException("builtIn operator "+bi.operator+" expects an argument of type int",bi.target);						
				int size = ((Int) v).get();
				res = heap.alloc(size);
			} else if (bi.operator=="PrintInt") {
				if (args.size()!=1) throw new ErrorException("builtIn operator "+bi.operator+" expects 1 argument",bi.target);
				Value v = args.get(0);
				if (!(v instanceof Int)) throw new ErrorException("builtIn operator "+bi.operator+" expects an argument of type int",bi.target);						
				int arg = ((Int) v).get();
				System.out.println(arg);
				return;
			} else if (bi.operator=="Add") {
				if (args.size()!=2) throw new ErrorException("builtIn operator "+bi.operator+" expects 2 arguments",bi.target);
				Value v = args.get(1);
				if (!(v instanceof Int)) throw new ErrorException("builtIn operator "+bi.operator+" expects a 2nd argument of type int",bi.target);						
				Value arg0 = args.get(0);
				int arg1 = ((Int) v).get();
				res = arg0.add(arg1);
			} else if (bi.operator=="Sub") {
				if (args.size()!=2) throw new ErrorException("builtIn operator "+bi.operator+" expects 2 arguments",bi.target);
				Value v0 = args.get(0);
				if (!(v0 instanceof Int)) throw new ErrorException("builtIn operator "+bi.operator+" expects a 1st argument of type int",bi.target);						
				int arg0 = ((Int) v0).get();
				Value v1 = args.get(1);
				if (!(v1 instanceof Int)) throw new ErrorException("builtIn operator "+bi.operator+" expects a 2nd argument of type int",bi.target);						
				int arg1 = ((Int) v1).get();
				res = new Int(arg0-arg1);
			} else if (bi.operator=="Mul") {
				if (args.size()!=2) throw new ErrorException("builtIn operator "+bi.operator+" expects 2 arguments",bi.target);
				Value v0 = args.get(0);
				if (!(v0 instanceof Int)) throw new ErrorException("builtIn operator "+bi.operator+" expects a 1st argument of type int",bi.target);						
				int arg0 = ((Int) v0).get();
				Value v1 = args.get(1);
				if (!(v1 instanceof Int)) throw new ErrorException("builtIn operator "+bi.operator+" expects a 2nd argument of type int",bi.target);						
				int arg1 = ((Int) v1).get();
				res = new Int(arg0*arg1);
			} else if (bi.operator=="Lt") {
				if (args.size()!=2) throw new ErrorException("builtIn operator "+bi.operator+" expects 2 arguments",bi.target);
				Value v0 = args.get(0);
				if (!(v0 instanceof Int)) throw new ErrorException("builtIn operator "+bi.operator+" expects a 1st argument of type int",bi.target);						
				int arg0 = ((Int) v0).get();
				Value v1 = args.get(1);
				if (!(v1 instanceof Int)) throw new ErrorException("builtIn operator "+bi.operator+" expects a 2nd argument of type int",bi.target);						
				int arg1 = ((Int) v1).get();
				res = new Int((arg0<arg1)?1:0);
			} else if (bi.operator=="And") {
				if (args.size()!=2) throw new ErrorException("builtIn operator "+bi.operator+" expects 2 arguments",bi.target);
				Value v0 = args.get(0);
				if (!(v0 instanceof Int)) throw new ErrorException("builtIn operator "+bi.operator+" expects a 1st argument of type int",bi.target);						
				int arg0 = ((Int) v0).get();
				Value v1 = args.get(1);
				if (!(v1 instanceof Int)) throw new ErrorException("builtIn operator "+bi.operator+" expects a 2nd argument of type int",bi.target);						
				int arg1 = ((Int) v1).get();
				if (arg0!=0 && arg0!=1) throw new ErrorException("builtIn operator "+bi.operator+" expects its 1st argument to be 0 or 1 (boolean)",bi.target);
				if (arg1!=0 && arg1!=1) throw new ErrorException("builtIn operator "+bi.operator+" expects its 2nd argument should be 0 or 1 (boolean)",bi.target);
				res = new Int((arg0==1 && arg1==1)?1:0);
			} else {
				throw new ErrorException("wrong builtIn operator "+bi.operator,bi.target);
			}
			lvar.put(bi.target,res);
		}

		public void visit(Call c) {
			List<Value> args = evalOperands(c.args);
			if (args.size()!=c.getCallee().params.size())
				throw new ErrorException("function call error, "+c.getCallee().params.size()+" arguments are expected",c.target);
			callstack.push(lvar);
			lvar = new Hashtable<Ident,Value>();
			Value res = run(c.getCallee(),args);
			lvar = callstack.pop();
			lvar.put(c.target, res);
		}

		Ptr evalMemRef(MemRef mr) {
			Value v = readLocal(mr.ident).add(mr.offset);
			if (v instanceof Int) throw new ErrorException("variable "+mr.ident+" should containe a pointer, not an int",mr.ident);
			return (Ptr) v;
		}

		public void visit(MemRead mr) {
			Ptr ptr = evalMemRef(mr.memRef);			
			lvar.put(mr.ident, heap.read(ptr));
		}

		public void visit(MemWrite mw) {
			Ptr ptr = evalMemRef(mw.memRef);
			heap.store(ptr, mw.operand.accept(new EvalOperand()));			
		}

		List<Value> evalOperands(List<Operand> l) {
			List<Value> res = new ArrayList<Value>();
			for (Operand o : l) res.add(o.accept(new EvalOperand()));
			return res;
		}
	}

	private Value readLocal(Ident id) {
		if (!lvar.containsKey(id)) throw new ErrorException("variable "+id+" is read before being written",id);
		return lvar.get(id);
	}

	class EvalOperand implements OperandVisitor<Value> {

		public Value visit(Ident id) {
			return readLocal(id);
		}

		public Value visit(LitInt li) {
			return new Int(li.getVal());
		}

	}

	class EvalEndInstr implements EndInstrVisitor<Value> {

		public Value visit(Goto g) {
			return run(g.target);
		}

		public Value visit(Branch br) {
			Value v = br.condition.accept(new EvalOperand());
			if (!(v instanceof Int)) throw new ErrorException("branch condition should be of type int",br.thenTarget.label);
			Int cond = (Int) v;
			if (cond.get()!=0 && cond.get()!=1) throw new ErrorException("branch condition should be 0 or 1 (boolean)",br.elseTarget.label);
			if (cond.get()==1)
				return run(br.thenTarget);
			else
				return run(br.elseTarget);
		}

		public Value visit(Return r) {
			if (r.operand==null) return null;
			return r.operand.accept(new EvalOperand());			
		}

	}

}


