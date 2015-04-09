package testcases.cager.jexpr;

class Link {

	int val;
    Link next;
 
 	predicate Range(int x, int y) = true

    	
    void addModulo11(int x)
    ~ double k: //int k 
    requires (this#k Range(0,10))
    ensures (this#k Range(0,10))
    {
    	pack(this#k Range(0,10));

    	this.next.addModulo11(x);
    		
    }

}
