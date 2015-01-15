package testcases.cager.jexpr;

class Link {

	int val;
    Link next;
 
 	predicate Range(int x, int y) =
    	exists int v, Link o, int k: this.val -> v && this.next -> o 
    	&& v >= x && v <= y &&
    	((o#k Range(x,y)) || o == null) 
 
    predicate UniRange(int x, int y) =
    	exists int v, Link o: this.val ->v && this.next -> o 
    	&& v >= x && v <= y &&
    	((o#1 UniRange(x,y)) || o == null)
    
    void add()
    int x, int y: //SpecExpression with list of declaration
    requires x < y && (this#1 UniRange(x,y))
    ensures (this#1 UniRange(x,y))
    {
    	this.val = this.val;
    	if (this.next != null) 
    	  {this.next.add(1);}
    }
 
}
