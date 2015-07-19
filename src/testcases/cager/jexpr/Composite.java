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
		c == lc + rc + 1  
		&& (this#0.5 left(ol, lc)) 
		&& (this#0.5 right(or, rc)) 
			
predicate parent() =
	exists Composite op, int c, double k:
	this.parent -> op &&
	(this#0.5 count(c)) &&
	( (op != null ~=> (op#k parent())) &&
		 ( (op#0.5 left(this, c)) ||
		   (op#0.5 right(this, c))
		  ) &&
		 (op==null ~=> (this#0.5 count(c)))
	)
		
		
void Composite() 
ensures this.count->c && (c==1) &&
	this.left->l && (l==null) &&
	this.right->r && (r==null) &&
	this.parent->p && (p==null)
{
	this.count = 1;
	this.left = null;
	this.right = null;
	this.parent = null; 
}


void updateCount() 
int k,
int c, int c1, int c2, int c3,
Composite ol, Composite or, Composite op:	
requires this.parent -> op &&
	unpacked(this#1.0 count(c)) &&
	(this#0.5 left(ol, c1)) &&
	(this#0.5 right(or, c2)) &&
	(op!=null ~=> unpacked(op#k count(c3)))
ensures (this#1.0 count(c1+c2+1))
{
// Existential variables for 
// unpack(ol#0.5 count(c1)).
// The programmer should put all the 
// existential variables the he/she declares in the
//beginning of the program.
Composite ol1;
Composite or1;
int lc1;
int rc1;

// Existential variables for 
// unpack(or#0.5 count(c2)).
Composite ol2;
Composite or2;
int lc2;
int rc2;

int newc = 1;
unpack(this#0.5 left(ol, c1)[op]);
if (this.left != null) {
	unpack(ol#0.5 count(c1)[ol1, or1, lc1, rc1]);	 
	newc = newc + this.left.count;
	pack(ol#0.5 count(c1)[ol1, or1, lc1, rc1]);
}
pack(this#0.5 left(ol, c1)[op]);

unpack(this#0.5 right(or, c2)[op]);	
if (this.right != null) {
	unpack(or#0.5 count(c2)[ol2, or2, lc2, rc2]);
	newc = newc + this.right.count;
	pack(or#0.5 count(c2)[ol2, or2, lc2, rc2]);
}
pack(this#0.5 right(or, c2)[op]);		
this.count = newc; 
pack(this#1.0 count(newc)[ol, or, c1, c2]);
}

//This version assumes this is the right child of opp.
//We need a very similar version of updateCountRec, where
//we assume this is the left child of opp.
void updateCountRec() 
~ double k1, double k, double k2:
Composite opp, int lcc,
Composite or, Composite ol,
int lc, int rc:
requires unpacked(this#k1 parent()) &&
	this.parent -> opp &&
	(opp != this) &&
  (((( (opp != null) ~=> (opp#k parent()) &&
     ((opp#0.5 left(this, lcc)) || (opp#0.5 right(this, lcc))
     )) || 
     ((opp == null) ~=> (this#0.5 count(lcc)))
     )))  &&
   unpacked(this#0.5 count(lcc)) &&
   (this#0.5 left(ol, lc)) &&
   (this#0.5 right(or, rc))
ensures (this#k2 parent())
{
// Existential variables for unpack(opp#0.5 count(opp.count))
Composite oll;
Composite orr;
int llc;
int rrc;


//We already have access to this.parent from the precondition of 
//this function.
if (this.parent != null) {
	splitFrac(opp#k parent(), 2);
	unpack(opp#k/2 parent()[opp.parent, opp.count]);
	//We get opp#1/2 count(lccc) from unpacking opp in parent()
	unpack(opp#0.5 count(opp.count)[oll, this, llc, lcc]);
	if (this == right[parent[this]]) {
		//The rule in the formal system should be that
		//if we have two object propositions with different parameters
		//that are both packed, then the parameters should be the same.
		addFrac(opp#0.5 right(this, lcc), opp#0.5 right(orr, rrc));
		//Explain why we need the full fraction!!!
		unpack(opp#1.0 right(this, lcc)[opp.parent]);
		addFrac(unpacked(this#0.5 count(lcc)), this#0.5 count(lcc));
		
		this.updateCount()[lcc, ol, or, opp, lc, rc, opp.count];
		pack(this#k2 parent()[opp, lc + rc + 1]);
		pack(opp#1.0 right(this, lcc)[opp.parent]);
			
		this.parent.updateCountRec()[opp.parent, opp.count, opp.left, this, opp.left.count, lc + rc + 1];	
	} else if (this == left[parent[this]]){
		addFrac(opp#0.5 left(this, lcc), opp#0.5 left(oll, llc));
		//Explain why we need the full fraction!!!
		unpack(opp#1.0 left(this, lcc));
		addFrac(unpacked(this#0.5 count(lcc)), this#0.5 count(lcc));
		
		this.updateCount()[lcc, ol, or, opp, lc, rc, opp.count];
		pack(this#k2 parent()[opp, lc + rc + 1]);
		pack(opp#1.0 left(this, lcc)[opp.parent]);
			
		this.parent.updateCountRec()[opp.parent, opp.count, this, opp.right, opp.left.count, lc + rc + 1];

	}		
 } else {
	addFrac(this#0.5 count(lcc), unpacked(this#0.5 count(lcc)));
	//After this addFrac, we know that this is unpacked in count()
	 this.updateCount()[lcc, ol, or, opp, lc, rc, opp.count];
	splitFrac(unpacked(this#1.0 count(lcc)), 2);
	pack(this#k2 parent()[this.parent, lc + rc + 1]);
	}
}


void setLeft(Composite l) 
~ double k1, double k2, double k:
requires (this != l);
// TODO Might need to add !=null for other objects too
requires (l != null);
requires this.right != this.parent;
requires l != this.parent;
requires this != this.right;
requires this#k1 parent();
requires unpacked (l#k2 parent());	
ensures (this#k parent());
{
// Existentially quantified variable for UnpackParent(this,lcc)
int lcc;
// Existentially quantified variables for UnpackCount(this,lcc)
// The variable rc is also used in the call to updateCountRec()
Composite or;
int rc;

if (l.parent==null) {	
	l.parent = this;
	unpack(this#k1 parent()[this.parent, lcc]);
	unpack(this#0.5 count(lcc)[null, or, 0, rc]);
	addFrac(this#0.5 left(null, 0), this#0.5 left(null, 0));
	unpack(this#1.0 left(null, 0)[this.parent]);	
	this.left = l;
	int lc;
	pack(this#1.0 left(l, lc));	
	this.updateCountRec(); 
	pack(l#k2 parent());	
	}
}

}
