package tp7;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import rtl.constant.BotOrIntOrTop;
import rtl.constant.ConstMap;
import rtl.constant.IntOrTop;
import rtl.constant.SSAConstMap;
import rtl.*;

public class TP7SSAConstPropagation {

	private SSAConstMap map;
	private SSAInformation info;
	Queue<Ident> workset; //usage optionnel

	public TP7SSAConstPropagation(SSAInformation info) {
		this.info = info;
		map = SSAConstMap.buildBot(info.idents());
		workset = new LinkedList<>();
		System.out.println("INIT:\n"+map.toString());
		onePass(0);
	}
	
	public void onePass(int i){
		DefKindVisitor<SSAConstMap> v = new DKV(map);
		
		String precMap = map.toString();
		
		for (Ident x : info.idents()) {
			DefKind d = info.kind(x);
			map = d.accept(v);
			System.out.println(map.toString());
		}
		if(!precMap.equals(map.toString())){
			System.out.println("_________________________________________________________________________________________");
			onePass(i+1);
		}
	}
	
	

	public void show() {
		System.out.println("\nRESULT SSA CONSTANT ANALYSIS:");
		for (Ident id : info.idents())
			System.out.println("  "+id+" = "+map.get(id));
	}


	class DKV implements DefKindVisitor<SSAConstMap> {
		
		SSAConstMap map;
		
		public DKV(SSAConstMap map) {
			this.map = map;
		}

		@Override
		public SSAConstMap visit(Ident param) {
			System.out.println("IDENT: "+param.toString());
			
			BotOrIntOrTop top = BotOrIntOrTop.buildTop();
			map.set(param, top);
			return map;
		}

		@Override
		public SSAConstMap visit(Assign a) {
			System.out.println("ASSIGN: "+a.toString());
			
			OperandVisiteur ov = new OperandVisiteur(map);
			this.map.set(a.ident, a.operand.accept(ov));
			return this.map;
		}

		@Override
		public SSAConstMap visit(BuiltIn bi) {
			OperandVisiteur ov = new OperandVisiteur(map);
			
			System.out.println("BUILTIN: "+bi.toString());

			BotOrIntOrTop fst = bi.args.get(0).accept(ov);
			BotOrIntOrTop snd = bi.args.get(1).accept(ov);
			
			if(fst.isBot() || snd.isBot()){
				this.map.set(bi.target, BotOrIntOrTop.buildBot());
				return map;
			}else if(bi.operator.equals(bi.ADD)){
				if(fst.isTop() || snd.isTop()){
					this.map.set(bi.target, BotOrIntOrTop.buildTop());
					return map;
				}else{
					this.map.set(bi.target, BotOrIntOrTop.buildInt(fst.getInt()+snd.getInt()));
					return map;
				}
			}else if(bi.operator.equals(bi.SUB)){
				if(fst.isTop() || snd.isTop()){
					this.map.set(bi.target, BotOrIntOrTop.buildTop());
					return map;
				}else{
					this.map.set(bi.target, BotOrIntOrTop.buildInt(fst.getInt()-snd.getInt()));
					return map;
				}
				
			}else if(bi.operator.equals(bi.MUL) || bi.operator.equals(bi.AND)){
				if(fst.isTop() || snd.isTop()){
					if(fst.getInt() == 0 || snd.getInt() == 0){
						this.map.set(bi.target, BotOrIntOrTop.buildInt(0));
						return map;
					}else{
						this.map.set(bi.target, BotOrIntOrTop.buildTop());
						return map;
					}
				}else{
					this.map.set(bi.target, BotOrIntOrTop.buildInt(fst.getInt() * snd.getInt()));
					return map;
				}
				
			}else if(bi.operator.equals(bi.LT)){	
				if(fst.isTop() || snd.isTop()){
					this.map.set(bi.target, BotOrIntOrTop.buildTop());
					return this.map;
				}else{
					if(fst.getInt() < snd.getInt()){
						this.map.set(bi.target, BotOrIntOrTop.buildInt(1));
						return this.map;
					}else{
						this.map.set(bi.target, BotOrIntOrTop.buildInt(0));
						return this.map;
					}
				}	
			}
			
			
			return this.map;
		}

		@Override
		public SSAConstMap visit(Call c) {
			System.out.println("CALL: "+c.toString());
			
			this.map.set(c.target, BotOrIntOrTop.buildTop());
			return this.map;
		}

		@Override
		public SSAConstMap visit(MemRead mr) {
			System.out.println("MEMREAD: "+mr.toString());
			
			this.map.set(mr.ident, BotOrIntOrTop.buildTop());
			return this.map;
		}

		@Override
		public SSAConstMap visit(Phi phi) {
			System.out.println("PHI: "+phi.toString());
			
			OperandVisiteur ov = new OperandVisiteur(map);
			BotOrIntOrTop prec = BotOrIntOrTop.buildBot();
			for(PhiArg pa : phi.args){
				prec = prec.join(pa.operand.accept(ov));
			}
			this.map.set(phi.target, prec);
			return this.map;
		}
	

	}
	
	class OperandVisiteur implements OperandVisitor<BotOrIntOrTop>{
		SSAConstMap map;
		
		public OperandVisiteur(SSAConstMap map) {
			this.map = map;
		}
		
		@Override
		public BotOrIntOrTop visit(Ident id) {
			if(!map.get(id).isTop() && !map.get(id).isBot()){
				return BotOrIntOrTop.buildInt(map.get(id).getInt());
			}else if(map.get(id).isBot()){
				return BotOrIntOrTop.buildBot();
			}else{
				return BotOrIntOrTop.buildTop();
			}
		}

		@Override
		public BotOrIntOrTop visit(LitInt li) {
			return BotOrIntOrTop.buildInt(li.getVal());
		}
		
	}
	
}

