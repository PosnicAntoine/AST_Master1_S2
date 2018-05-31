package tp3;

import java.io.FileInputStream;

import jdk.nashorn.internal.codegen.DumpBytecode;
import rtl.Heap;
import rtl.Int;
import rtl.Interpreter;
import rtl.Program;
import rtl.Ptr;
import rtl.Value;
import rtl.interpreter.ErrorException;

public class TP3Heap extends Heap {

	private int totalLenBlock;

	public static void main(String[] args) {
		Program prog = new Program();
		try {
			String path = "/private/student/7/17/15006817/workspace/AST_TP3/srctp3/"; // A MODIFIER
			java.io.InputStream in = new FileInputStream(path+"rtl/examples/CopyLinkedList.rtl");
			prog = rtl.Parser.run(in); // la classe tp3.Test utilise System.in à la place
			

		} catch (Throwable e) {
			System.out.println("TP3 test failed: " + e.getMessage());
		}
		
		Interpreter interpreter = new Interpreter();
		Heap heap = new TP3Heap(interpreter,11,12); // 11 champs max, 12 objets max
		interpreter.setHeap(heap);
		interpreter.run(prog);
	}

	TP3Heap(Interpreter interpr, int objectMaxSize, int maxNumberObjects) {
		super(interpr, objectMaxSize, maxNumberObjects);

		//FINISHED
		this.totalLenBlock = this.objectMaxSize + this.firstField;
		this.freelist = 0;

		for (int i = 1; i < this.memory.length; i+=this.totalLenBlock) {
			if (i+this.totalLenBlock > this.memory.length) {
				break;
			}
			this.memory[i] = new Ptr(i + 1 + this.objectMaxSize);
		}
//		freelistPush(143);
//		dump();
		//ENDFINISHED
	}

	Integer freelist; // adresse du premier objet de la freelist (null ssi la freelist est vide) 

	// ajoute l'objet à l'adresse index en tête de la freelist
	public void freelistPush(int index) {
		//FINISHED		
		if(!this.freelistIsEmpty()){
			Ptr topPtr = new Ptr(this.freelist);
			this.memory[index+1]=topPtr;
		}else{
			this.memory[index+1]=null;
		}
		this.freelist= index;
		//ENDFINISHED
	}

	// depile un object de la freelist et renvoie son adresse
	// lance une exception ErrorException si la freelist est vide
	public Integer freelistPop() {
		//FINISHED
		Integer res = this.freelist;
		if(this.memory[this.freelistHead()+1] != null){
			this.freelist = ((Ptr) this.memory[this.freelistHead()+1]).offset;
		}else{
			this.freelist = null;
		}
		return res;
		//ENDFINISHED
	}

	// renvoie l'adresse du premier objet de la freelist
	// renvoie null ssi le freelist est vide
	public Integer freelistHead() {
		return freelist;
	}	

	// alloue un objet avec size champs initialisés à 0
	// renvoie l'adresse du début de l'objet
	public Ptr alloc(int size) {
		//FINISHED Pre req: firstField never change from value 2
		if (this.freelistIsEmpty()){
			markAndSweep();
			if(this.freelistIsEmpty()){
				throw new ErrorException("No more free memory left");
			}
		}
		
		Ptr res = new Ptr(this.freelistHead());
		
		Integer n = freelistPop()+1;
		this.memory[n] = new Int(size);
		for(int i =1; i <=size; i++){
			this.memory[n+i]= new Int(0);
		}
		return res;
		//ENDFINISHED
	}			

	// renvoie true ssi l'objet à l'adresse p est marqué
	public boolean marked(Ptr p) {
		//FINISHED
		//Integer resmodulo = p.offset % this.totalLenBlock;
		return (memory[p.offset /*- resmodulo*/] != null);
		//ENDFINISHED test fa
	}

	// marque l'objet à l'adresse p
	public void mark(Ptr p) {
		//FINISHED
		//Integer resmodulo = p.offset % this.totalLenBlock;
		memory[p.offset /*- resmodulo*/] = new Int(1);
		//ENDFINISHED
	}

	// enleve la marque de l'objet à l'adresse p
	public void unmark(Ptr p) {
		//FINISHED
		//Integer resmodulo = p.offset % this.totalLenBlock;
		memory[p.offset /*- resmodulo*/] = null;
		//ENDFINISHED
	}

	// visite (parcours en profondeur) l'objet à l'adresse p
	// attention p n'est pas forcement l'adresse du début de l'objet
	// generalement ce sera l'adresse de son premier champs
	public void visit(Ptr p) {
		//FINISHED
		//Integer resmodulo = p.offset % this.totalLenBlock;
		if(this.memory[p.offset/*resmodulo*/] == null){
			mark(p);
			Integer sizeOfObject = ((Int) this.memory[p.offset + 1]).get();
			for(int i = p.offset + this.firstField; i < p.offset + this.firstField + sizeOfObject; i++){
				Value v = memory[i];
				if(this.memory[i] instanceof Ptr){
					visit((Ptr) this.memory[i]);
				}
			}
		}
		//ENDFINISHED
		
	}


}



