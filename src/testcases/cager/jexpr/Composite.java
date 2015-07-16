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
Composite ol, Composite or, Composite opp:	
requires this.parent -> opp &&
	unpacked(this#1.0 count(c)) &&
	(this#0.5 left(ol, c1)) &&
	(this#0.5 right(or, c2)) &&
	unpacked(opp#k count(c3))
ensures (this#1.0 count(c1+c2+1))
{
int newc = 1;
unpack(this#0.5 left(ol, c1));
if (this.left != null) {
	unpack(ol#0.5 count(c1));	 
	newc = newc + left.count;
	pack(ol#0.5 count(c1));
}
pack(this#0.5 left(ol, c1));

unpack(this#0.5 right(or, c2));	
if (this.right != null) {
	unpack(or#0.5 count(c2));
	newc = newc + right.count;
	pack(or#0.5 count(c2));
}
pack(this#0.5 right(or, c2));		
this.count = newc; 
pack(this#1.0 count(newc));
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
//We already have access to this.parent from the precondition of 
//this function.
if (this.parent != null) {
	splitFrac(opp#k parent(), 2);
	unpack(opp#k/2 parent());
	//We get opp#1/2 count(lccc) from unpacking opp in parent()
	int lccc;
	unpack(opp#0.5 count(lccc));
	//These variables are for existentially quantified variables inside the 
	//count predicate that we just unpacked.
	int rrc;
	Composite orr;
	//These instantiates are because the programmer knows what to
	//instantiate to what.
	//Instantiate the existentially quantified variable to this.
	//Maybe we do not need these instantiate()
	instantiate(orr, this);
	instantiate(rrc, lcc);
	//The rule in the formal system should be that
	//if we have two object propositions with different parameters
	//that are both packed, then the parameters should be the same.
	addFrac(opp#0.5 right(this, lcc), opp#0.5 right(orr, rrc));
	//Explain why do we need the full fraction!!!
	unpack(opp#1.0 right(this, lcc));
	addFrac(unpacked(this#0.5 count(lcc)), this#0.5 count(lcc));
	unpack(this#1.0 count(lcc));
	
	this.updateCount();
	pack(opp#1.0 right(this, lcc));
		
	this.parent.updateCountRec();
	k3:real;
	pack(opp#k3 parent());
	pack(this#k2 parent());
		
 } else {
	addFrac(this#0.5 count(lcc), unpacked(this#0.5 count(lcc)));
	//After this addFrac, we know that this is unpacked in count()
	 this.updateCount();
	splitFrac(unpacked(this#1.0 count(lcc)), 2);
	pack(this#k2 parent());
	}
}


void setLeft(Composite l) 
~ double k1, double k2, double k:
requires (this != l) &&
	(this#0.5 left(null, 0)) &&
	( (this#k1 parent()) ||
	 (l#k2 parent())	
	)
ensures (this#k parent())
{
unpack(l#k2 parent());
if (l.parent==null) {	
	l.parent= this;
	unpack(this#k1 parent());
	int lcc;
	unpack(this#0.5 count(lcc));
	instantiate(ol, null);
	instantiate(llc, 0);
	addFrac(this#0.5 left(null, 0), this#0.5 left(null, 0));
	unpack(this#1.0 left(null, 0));	
	this.left = l;
	int lc;
	pack(this#1.0 left(l, lc));	
	this.updateCountRec(); 
	pack(l#k2 parent());	
	}
}

//We do not need to prove this,
//it is analogous to setLeft
void setRight(Composite r) 
~ double k1, double k2, double k:
requires (this != r) &&
	(this#0.5 right(null, 0)) &&
	( (this#k1 parent()) ||
	 (r#k2 parent())	
	)
ensures (this#k parent())
{
		
if (r.parent==null) {
			
	r.parent = this;
			
	this.right = r;
			
	this.updateCountRec(); 
		
	}
}

}
