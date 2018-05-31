package rtl;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public abstract class Heap {

	public Value[] memory;
	public Interpreter interpr;

	public final int objectMaxSize;
	public final int maxNumberObjects;
	public final int firstField = 2;

	public Heap (Interpreter interpr, int objectMaxSize, int maxNumberObjects) {
		this.interpr = interpr;
		this.objectMaxSize = objectMaxSize;
		this.maxNumberObjects = maxNumberObjects;
		memory = new Value[maxNumberObjects * (objectMaxSize+firstField)];
	}

	private void dumpCell(int i, String annot) {
		System.out.printf("%3d: %8s%s\n",i,memory[i],annot);			
	}

	public void dump() {
		System.out.print("DUMPING MEMORY (freelist ");
		if (freelistIsEmpty()) System.out.println("is empty)");
		else System.out.printf("starts at %d)\n", freelistHead());
		for (int i = 0; i<maxNumberObjects; i++) {
			dumpCell(i * (objectMaxSize+firstField), "  // markField");
			dumpCell(i * (objectMaxSize+firstField) +1, "  // nextField or sizeField");
			for (int j=0;j<objectMaxSize;j++)
				dumpCell(i * (objectMaxSize+firstField)+j+2, "");
		}
	}

	abstract public Ptr alloc(int size);

	public Value read(Ptr p) { 
		return memory[p.offset+firstField];			
	}

	public void store(Ptr p, Value v) {
		memory[p.offset+firstField] = v;
	}				

	abstract public void freelistPush(int offset);

	public boolean freelistIsEmpty() { 
		return (freelistHead())==null; 
	}	

	abstract public Integer freelistHead();

	abstract public Integer freelistPop();

	abstract public boolean marked(Ptr p); 

	abstract public void mark(Ptr p);

	abstract public void unmark(Ptr p);

	abstract public void visit(Ptr p); 

	public void markAndSweep() {
		System.err.print("GC starts... ");
		System.err.print("marking... ");
		for (Ptr p : roots()) {
			if (!marked(p)) visit(p);
		}
		System.err.print("sweeping... ");
		int countUnmarked = 0;
		for (int i = 0; i<maxNumberObjects; i++) {
			Ptr p = new Ptr(i * (objectMaxSize+firstField));
			if (marked(p)) unmark(p); 
			else {
				countUnmarked++;
				freelistPush(p.offset);
			}
		}
		System.err.printf("%d objects have been pushed in the freelist\n",countUnmarked);
	}

	// construit la liste des racines dans l'état actuel de l'interpréteur 
	public List<Ptr> roots() {
		LinkedList<Ptr> roots = new LinkedList<Ptr>();
		addRoots(roots,interpr.lvar);
		for (Map<Ident,Value> lvar : interpr.callstack) addRoots(roots,lvar);
		return roots; 
	}

	private void addRoots(LinkedList<Ptr> roots, Map<Ident,Value> lvar) {
		for (Value v : lvar.values()) 
			if (v instanceof Ptr) roots.add((Ptr) v);
	}

}

