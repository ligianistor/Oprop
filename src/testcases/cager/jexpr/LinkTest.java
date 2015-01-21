package testcases.cager.jexpr;

class Link {

	int val;
    Link next;
 
    predicate UniRange(int x, int y) =
    	exists int v, Link o: this.val ->v && this.next -> o 
    	&& v >= x && v <= y &&
    	((o#1 UniRange(x,y)) || o == null)
    
    void add(int z)
    int x, int y: //SpecExpression with list of declaration
    requires x < y && (this#1 UniRange(x,y))
    ensures (this#1 UniRange(x+z,y+z))
    {
    	this.val = this.val + z;
    	if (this.next != null) 
    	  {this.next.add(z);
    	  this.next.add(x);
    	  this.next.add(y);
    	  }
    }
  


}
