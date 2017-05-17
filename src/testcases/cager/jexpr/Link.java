package testcases.cager.jexpr;

class Link {

	int val;
    Link next;
 
 	predicate Range(int x, int y) =
    	exists int v, Link o, double k: this.val -> v && this.next -> o 
    	&& v >= x && v <= y &&
    	((o#k Range(x,y)) || o == null) 
    	
    Link(int v, Link n)
    ensures (this.val == v) && (this.next == n)
    {
    	this.val = v;
    	this.next = n;
    }
    	
    void addModulo11(int x)
    ~ double k:
    requires x >= 0 && (this#k Range(0,10))
    ensures (this#k Range(0,10))
    {
    	unpack(this#k Range(0,10))[this.val, this.next];
    	this.val = (this.val + x)%11;
    	pack(this#k Range(0,10))[this.val, this.next];
    	if (this.next != null) {
    		this.next.addModulo11(x);
    		}
    }
 
    void main() {
		Link l1 = new Link(Range(0,10)[3, null])(3, null);
		Link l2 = new Link(Range(0,10)[4, l1])(4, l1);
		Link l3 = new Link(Range(0,10)[5, l2])(5, l2);
		l3.addModulo11(20);
	}
}
