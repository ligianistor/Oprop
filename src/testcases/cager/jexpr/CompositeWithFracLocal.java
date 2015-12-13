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
	(op != this) &&
	(this#0.5 count(c)) &&
	(op != null ~=> (op#k parent())) &&
	( ((op!=null) && (op.left == this)) ~=> op#0.5 left(this, c)) &&
	( ((op!=null) && (op.right == this)) ~=> op#0.5 right(this, c)) &&
	(op==null ~=> (this#0.5 count(c)))
	
		
void updateCount() 
~double k:
int c, int c1, int c2, int c3,
Composite ol, Composite or, Composite op:	
requires this.parent -> op &&
	unpacked(this#1.0 count(c)) &&
	(this#0.5 left(ol, c1)) &&
	(this#0.5 right(or, c2)) &&
	(op!=null ~=> unpacked(op#k count(c3)))
ensures (this#1.0 count(c1+c2+1))
// For all fractions that are not mentioned in the pre-condition,
// assume they are 0.
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

int newc; 

newc = 1;
unpack(this#0.5 left(ol, c1))[op];
(ol != null) ~=> (fracCount[ol] := fracCount[ol] + 0.5);
if (this.left != null) {
	unpack(ol#0.5 count(c1))[ol1, or1, lc1, rc1];	
	fracLeft[ol] := fracLeft[ol] + 0.5; 
	fracRight[ol] := fracRight[ol] + 0.5; 
	newc = newc + this.left.count;
	pack(ol#0.5 count(c1))[ol1, or1, lc1, rc1];
	fracLeft[ol] := fracLeft[ol] - 0.5;
	fracRight[ol] := fracRight[ol] - 0.5;
}
pack(this#0.5 left(ol, c1))[op];
(ol!=null) ~=> fracCount[ol] := fracCount[ol] - 0.5;

unpack(this#0.5 right(or, c2))[op];	
(or!=null) ~=> fracCount[or] := fracCount[or] + 0.5;

if (this.right != null) {
	unpack(or#0.5 count(c2))[ol2, or2, lc2, rc2];
	fracLeft[or] := fracLeft[or] + 0.5; 
	fracRight[or] := fracRight[or] + 0.5; 
	newc = newc + this.right.count;
	pack(or#0.5 count(c2))[ol2, or2, lc2, rc2];
	fracLeft[or] := fracLeft[or] - 0.5;
	fracRight[or] := fracRight[or] - 0.5;
}
pack(this#0.5 right(or, c2))[op];
(or != null) ~=> (fracCount[or] := fracCount[or] - 0.5);
this.count = newc; 
pack(this#1.0 count(newc))[ol, or, c1, c2];
fracLeft[this] := fracLeft[this] - 0.5;
fracRight[this] := fracRight[this] - 0.5;
}

//This version assumes this is the right child of opp.
//We need a very similar version of updateCountRec, where
//we assume this is the left child of opp.
void updateCountRec() 
// TODO 
// Shouldn't we say that k>0 here for all such k??
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
   // For all frac that are not mentioned in the pre-condition
   // they are assumed to be 0.
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
	// Even though an object proposition is unpacked, we still hold the
	// fraction to it as before.
	unpack(opp#k/2 parent())[opp.parent, opp.count];
	//We get opp#1/2 count(lccc) from unpacking opp in parent()
	fracCount[opp] := fracCount[opp] +0.5;
	(opp.parent != null) ~=> (fracParent[opp.parent] := fracParent[opp.parent] + k);
	(opp.parent != null) ~=> (fracLeft[opp.parent] := fracLeft[opp.parent] + 0.5);
	(opp.parent != null) ~=> (fracRight[opp.parent] := fracRight[opp.parent] + 0.5);
	(opp.parent == null) ~=> (fracCount[opp]:=fracCount[opp] + 0.5);
	
	unpack(opp#0.5 count(opp.count))[oll, this, llc, lcc];
	fracLeft[opp] := fracLeft[opp] + 0.5;
	fracRight[opp] := fracRight[opp] + 0.5;
		
	
	if (this == this.parent.right) {
		//The rule in the formal system should be that
		//if we have two object propositions with different parameters
		//that are both packed, then the parameters should be the same.
		addFrac(opp#0.5 right(this, lcc), opp#0.5 right(orr, rrc));
		fracRight[opp] := 0.5 + 0.5;
		//Explain why we need the full fraction!!!
		unpack(opp#1.0 right(this, lcc))[opp.parent];
		(this!=null) ~=> fracCount[this] := fracCount[this] + 0.5;
		
		addFrac(unpacked(this#0.5 count(lcc)), this#0.5 count(lcc));
		fracCount[this] := 0.5 + 0.5;
		
		this.updateCount()[lcc, ol, or, opp, lc, rc, opp.count];
		fracLeft[this] := fracLeft[this] - 0.5;
		fracRight[this] := fracRight[this] - 0.5;
		(opp!=null) ~=> fracCount[opp] := fracCount[opp] - k;
		pack(this#k2 parent())[opp, lc + rc + 1];
		fracCount[this] := fracCount[this] - 0.5;
		if (opp!=null) { fracParent[opp] := fracParent[opp] - k; }
		
		predicate parent() =
				exists Composite op, int c, double k:
				this.parent -> op &&
				(op != this) &&
				(this#0.5 count(c)) &&
				( (op != null ~=> (op#k parent())) &&
					 ( (op#0.5 left(this, c)) ||
					   (op#0.5 right(this, c))
					  ) &&
					 (op==null ~=> (this#0.5 count(c)))
				)
		
		pack(opp#1.0 right(this, lcc))[opp.parent];
				
				predicate right(Composite or, int rc) =
						exists Composite op :
						this.right -> or && 
						this.parent -> op &&
						(or== null ~=> (rc == 0) ) &&
						(or != null ~=> ((or#0.5 count(rc)) && (or!=this) && (or!=op) ))
			
		this.parent.updateCountRec()[opp.parent, opp.count, opp.left, this, opp.left.count, lc + rc + 1];	
	} else if (this == this.parent.left){
		addFrac(opp#0.5 left(this, lcc), opp#0.5 left(oll, llc));
		//Explain why we need the full fraction!!!
		unpack(opp#1.0 left(this, lcc))[opp.parent];
		addFrac(unpacked(this#0.5 count(lcc)), this#0.5 count(lcc));
		
		this.updateCount()[lcc, ol, or, opp, lc, rc, opp.count];
		pack(this#k2 parent())[opp, lc + rc + 1];
		pack(opp#1.0 left(this, lcc))[opp.parent];
			
		this.parent.updateCountRec()[opp.parent, opp.count, this, opp.right, opp.left.count, lc + rc + 1];
	}		
 } else {
	addFrac(this#0.5 count(lcc), unpacked(this#0.5 count(lcc)));
	//After this addFrac, we know that this is unpacked in count()
	 this.updateCount()[lcc, ol, or, opp, lc, rc, opp.count];
	splitFrac(unpacked(this#1.0 count(lcc)), 2);
	pack(this#k2 parent())[this.parent, lc + rc + 1];
	}
}

void setLeft(Composite l) 
~ double k1, double k2, double k:
requires (this != l) && (l != null) && (this.right != this.parent) &&
		(l != this.parent) && (this != this.right) && (this#k1 parent()) &&
		unpacked (l#k2 parent())	
ensures (this#k parent())
{
// Existentially quantified variable for UnpackParent(this,lcc)
int lcc;
// Existentially quantified variables for UnpackCount(this,lcc)
// The variable rc is also used in the call to updateCountRec()
Composite or;

if (l.parent==null) {	
	l.parent = this;
	unpack(this#k1 parent())[this.parent, lcc];
	unpack(this#0.5 count(lcc))[null, or, 0, this.right.count];
	addFrac(this#0.5 left(null, 0), this#0.5 left(null, 0));
	unpack(this#1.0 left(null, 0))[this.parent];	
	this.left = l;
	this.left.count = l.left.count;
	pack(this#1.0 left(l, l.left.count))[this.parent];
	splitFrac(this#1.0 left(l, l.left.count));
	pack(l#k2 parent())[l.parent, l.left.count];	
	this.updateCountRec()[this.parent, lcc, l, this.right, l.left.count, this.right.count]; 	
	}
}

}
