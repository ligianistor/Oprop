package testcases.cager.jexpr;

class Composite {
int count;
Composite left;
Composite right;
Composite parent;

predicate left(Composite ol, int lc) =
	exists Composite op :
	this.parent -> op &&
	this.left -> ol &&
	(ol== null ~=> (lc == 0) ) &&
	(ol != null ~=> ((ol#0.5 count(lc)) && (ol!=this) && (ol!=op) ))
	
		
predicate right(Composite or, int rc) =
	exists Composite op :
	this.right -> or && 
	this.parent -> op &&
	(or== null ~=> (rc == 0) ) &&
	(or != null ~=> ((or#0.5 count(rc)) && (or!=this) && (or!=op) ))
  
	
predicate count(int c) = 
		exists Composite ol, Composite or,
		int lc, int rc : 
		this.count -> c && 
		(c == lc + rc + 1)		
		&& (this#0.5 left(ol, lc)) 
		&& (this#0.5 right(or, rc)) 
		
predicate parent() =
	exists Composite op, int c, double k:
	this.parent -> op &&
	(op != this) &&
	(this#0.5 count(c)) &&
	(op != null ~=> (op#k parent())) &&
	(((op != null) && (op.left == this)) ~=> op#0.5 left(this, c)) &&
	(((op != null) && (op.right == this)) ~=> op#0.5 right(this, c)) &&
	(op == null ~=> (this#0.5 count(c)))
	
Composite(int c, Composite l, Composite r, Composite p) 
ensures (this.count == c) && (this.left == l) && (this.right == r)
		&& (this.parent == p)
{
	this.count = c;
	this.left = l;
	this.right = r;
	this.parent = p;
}

void updateCount() 
~double k, double k1, double k2:
int c, Composite ol, Composite or, Composite op,
int c1, int c2, int c3:	
requires 
    ((op!=null) ~=> (unpacked(op#k1 left(op.left, op.left.count)) || 
    				unpacked(op#k2 right(op.right, op.right.count)))) &&
	this.parent -> op &&
	unpacked(this#1.0 count(c)) &&
	(this#0.5 left(ol, c1)) &&
	(this#0.5 right(or, c2)) &&
	(op!=null ~=> unpacked(op#k count(c3))) 
ensures (this#1.0 count(c1+c2+1)) &&
    ( (op!=null) ~=> (unpacked(op#k1 left(op.left, op.left.count)) || 
    				unpacked(op#k2 right(op.right, op.right.count)) ) ) &&
    (op!=null ~=> unpacked(op#k count(c3)))  &&
    (this#0.0 left(this.left, this.left.count)) && 
    (this#0.0 right(this.right, this.right.count))
{
int newc;

newc = 1;
unpack(this#0.5 left(ol, c1))[op];
if (this.left != null) {
	unpack(ol#0.5 count(c1))[ol.left, ol.right, ol.left.count, ol.right.count];	 
	newc = newc + this.left.count;
	pack(ol#0.5 count(c1))[ol.left, ol.right, ol.left.count, ol.right.count];
}
pack(this#0.5 left(ol, c1))[op];

unpack(this#0.5 right(or, c2))[op];	
if (this.right != null) {
	unpack(or#0.5 count(c2))[or.left, or.right, or.left.count, or.right.count];
	newc = newc + this.right.count;
	pack(or#0.5 count(c2))[or.left, or.right, or.left.count, or.right.count];
}
pack(this#0.5 right(or, c2))[op];		
this.count = newc; 
pack(this#1.0 count(newc))[ol, or, c1, c2];
}

void updateCountRec() 
~ double k1, double k, double k2, double k3:
Composite opp, int lcc,
Composite ol, Composite or,
int lc, int rc:
requires unpacked(this#k1 parent()) &&
	this.parent -> opp &&
	(opp != this) &&
   ( (opp != null) ~=> (opp#k parent()) ) && 
  ( ( (opp != null) &&	(opp.left == this)) ~=>	 (opp#0.5 left(this, lcc))) &&
    ( ( (opp != null) && (opp.right == this)) ~=> (opp#0.5 right(this, lcc)))
      &&
      ((opp == null) ~=> (unpacked(this#0.5 count(lcc))))
      &&
   unpacked(this#0.5 count(lcc)) &&
   (this#0.5 left(ol, lc)) &&
   (this#0.5 right(or, rc))
ensures (this#k2 parent()) && ( (opp != null) ~=> (opp#k3 parent()) ) 
{
if (this.parent != null) {
	splitFrac(opp#k parent(), 2);
	unpack(opp#k/2 parent())[opp.parent, opp.count];
	unpack(opp#0.5 count(opp.count))[opp.left, opp.right, opp.left.count, opp.right.count];

	if (this == this.parent.right) {
		addFrac(opp#0.5 right(this, lcc), opp#0.5 right(opp.right, opp.right.count));
		unpack(opp#1.0 right(this, lcc))[opp.parent];
		addFrac(unpacked(this#0.5 count(lcc)), this#0.5 count(lcc));
		
		this.updateCount()[lcc, ol, or, opp, lc, rc, opp.count];
		pack(this#k2 parent())[opp, lc + rc + 1];
		pack(opp#1.0 right(this, lc + rc + 1))[opp.parent];
			
		this.parent.updateCountRec()[opp.parent, opp.count, opp.left, this, opp.left.count, lc + rc + 1];	
		
	} else {
		addFrac(opp#0.5 left(this, lcc), opp#0.5 left(opp.left, opp.left.count));
		unpack(opp#1.0 left(this, lcc))[opp.parent];
		addFrac(unpacked(this#0.5 count(lcc)), this#0.5 count(lcc));
		
		this.updateCount()[lcc, ol, or, opp, lc, rc, opp.count];
		pack(this#k2 parent())[opp, lc + rc + 1];
		pack(opp#1.0 left(this, lc + rc + 1))[opp.parent];
			
		this.parent.updateCountRec()[opp.parent, opp.count, this, opp.right, lc + rc + 1, opp.right.count];
	}		
 } else {
	addFrac(this#0.5 count(lcc), unpacked(this#0.5 count(lcc)));
	 this.updateCount()[lcc, ol, or, opp, lc, rc, opp.count];
	splitFrac(unpacked(this#1.0 count(lcc)), 2);
	pack(this#k2 parent())[this.parent, lc + rc + 1];
	}
}

void setLeft(Composite l) 
~ double k1, double k2, double k, double k3:
requires (this != l) && (l != null) && (this.left != this.parent) &&
		(l != this.parent) && (l != this.right) &&
		(this != this.right) && (this != this.left) && (this#k1 parent()) &&
		(l#k2 parent())	
ensures (this#k parent()) && (l#k3 parent())
{
unpack(l#k2 parent())[l.parent, l.count];
if (l.parent==null) {	
	l.parent = this;
	unpack(this#k1 parent())[this.parent, this.count];
	unpack(this#0.5 count(this.count))[null, this.right, 0, this.right.count];
	addFrac(this#0.5 left(null, 0), this#0.5 left(null, 0));
	unpack(this#1.0 left(null, 0))[this.parent];	
	this.left = l;
	this.left.count = l.left.count;
	pack(this#1.0 left(l, l.left.count))[this.parent];
	splitFrac(this#1.0 left(l, l.left.count));
	pack(l#k2 parent())[l.parent, l.left.count];	
	this.updateCountRec()[this.parent, this.count, l, this.right, l.left.count, this.right.count]; 	
} else {
	pack(l#k2 parent())[l.parent, l.count];	
}
	
}

}