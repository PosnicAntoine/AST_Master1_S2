class CopyLinkedList{
    public static void main(String[] a){
	System.out.println(new CLL().Start());
    }
}

class CList{
    int elem ;
    CList next ;
    boolean end ;

    // Initialize the node list as the last node
    public boolean Init(){
	end = true ;
	return true ;
    }

    // Initialize the values of a new node
    public boolean InitNew(int v_elem, CList v_next, boolean v_end){
	end = v_end ;
	elem = v_elem ;
	next = v_next ;
	return true ;
    }
    
    // Insert a new node at the beginning of the list
    public CList Insert(int new_elem){
	boolean ret_val ;
	CList aux02 ;
	aux02 = new CList();
	ret_val = aux02.InitNew(new_elem,this,false);
	return aux02 ;
    }

    public int GetElem(){
	return elem ;
    }
    
    public boolean SetElem(int e){
	elem = e ;
	return true ;
    }

    public CList GetNext(){
	return next ;
    }

    public boolean SetNext(CList v_next){
	next = v_next ;
	return true ;
    }
    
    public boolean GetEnd(){
	return end ;
    }

    public boolean SetEnd(boolean b){
	end = b ;
	return true ;
    }
    
    // Print the linked list
    public boolean Print(){
	CList aux01 ;
	boolean var_end ;
	int  var_elem ;

	aux01 = this ;
	var_end = end ;
	var_elem = elem ;
	while (!var_end){
	    System.out.println(var_elem);
	    aux01 = aux01.GetNext() ;
	    var_end = aux01.GetEnd();
	    var_elem = aux01.GetElem();
	}

	return true ;
    }

}

class CLL{

    public int Start(){

	CList head;
	CList copy;
	CList current;
	boolean aux01;

	head = new CList();
	aux01 = head.Init();

	head = head.Insert(1);
	head = head.Insert(2);
	head = head.Insert(3);
	head = head.Insert(4);
	head = head.Insert(5);

	aux01 = head.Print();

	// starting copy
	System.out.println(11111111);
	
	copy = new CList();
	current = copy;
	while (!head.GetEnd()) {
	    aux01 = current.SetElem(head.GetElem());
	    aux01 = current.SetEnd(false);
	    aux01 = current.SetNext(new CList());
	    current = current.GetNext();
	    head = head.GetNext();
	}
	aux01 = current.SetEnd(true);

	aux01 = copy.Print();
	
	return 0 ;
	
	
    }
    
}
    
