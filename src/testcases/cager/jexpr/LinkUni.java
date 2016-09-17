package testcases.cager.jexpr;

class Link {

	int val;
    Link next;
 
 	predicate Range(int x, int y) =
    	exists int v, Link o, double k: this.val -> v && this.next -> o 
    	&& v >= x && v <= y &&
    	((o#k Range(x,y)) || o == null) 
 
    predicate UniRange(int x, int y) =
    	exists int v, Link o: this.val ->v && this.next -> o 
    	&& v >= x && v <= y &&
    	((o#1 UniRange(x,y)) || o == null)
    	
    void addModulo11(int x)
    ~ double k: //int k 
    requires x >= 0 && (this#k Range(0,10))
    ensures (this#k Range(0,10))
    {
    	unpack(this#k Range(0,10));
    	this.val = (this.val + x)%11;
    	pack(this#k Range(0,10));
    	if (this.next != null) {
    		this.next.addModulo11(x);
    		}
    }
    
    void add(int z)
    int x, int y: //SpecExpression with list of declaration
    requires x < y && (this#1 UniRange(x,y))
    ensures (this#1 UniRange(x+z,y+z))
    {
    	unpack(this#1 UniRange(x,y));
    	this.val = this.val + z;
    	if (this.next != null) {
    		
    		// We add the x and y here whenever we call a function
    		// that needs to instantiate the forall variables.
    		// But not the k's, which are fractions.
    		this.next.add(z)[x,y];
    	}
    	pack(this#1 UniRange(x+z,y+z));
    }
 
    
    void main() {
		Link l1 = new Link(Range(0,10)[3, null])(3, null);
		Link l2 = new Link(Range(0,10)[4, l1])(4, l1);
		Link l3 = new Link(Range(0,10)[5, l2])(5, l2);
		l3.addModulo11(20);
	}
}
