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
	(((op != null) && (op.left == this)) ~=> op#0.5 left(this, c)) &&
	(((op != null) && (op.right == this)) ~=> op#0.5 right(this, c)) &&
	(op == null ~=> (this#0.5 count(c)))
// After calling unpack() we do not need annotations because in the translation 
// the body of the unpack() procedure says "frac == old(frac) + 0.5", for 
// example.
		
void updateCount() 
~double k:
int c, int c1, int c2, int c3,
Composite ol, Composite or, Composite op:	
requires this.parent -> op &&
	unpacked(this#1.0 count(c)) &&
	(this#0.5 left(ol, c1)) &&
	(this#0.5 right(or, c2)) &&
	(op!=null ~=> unpacked(op#k count(c3))) &&
	(unpacked(op#k left(op.left, c))
	|| unpacked(op#k right(op.right,c))
	|| (op == null))
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
if (this.left != null) {
	unpack(ol#0.5 count(c1))[ol1, or1, lc1, rc1];	
	newc = newc + this.left.count;
	pack(ol#0.5 count(c1))[ol1, or1, lc1, rc1];
	fracLeft[ol] := fracLeft[ol] - 0.5;
	fracRight[ol] := fracRight[ol] - 0.5;
}
pack(this#0.5 left(ol, c1))[op];
(ol!=null) ~=> fracCount[ol] := fracCount[ol] - 0.5;

unpack(this#0.5 right(or, c2))[op];	

if (this.right != null) {
	unpack(or#0.5 count(c2))[ol2, or2, lc2, rc2];
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

void updateCountRec() 
~ double k1, double k, double k2:
Composite opp, int lcc,
Composite or, Composite ol,
int lc, int rc:
requires unpacked(this#k1 parent()) &&
	this.parent -> opp &&
	(opp != this) &&
   ( (opp != null) ~=> (opp#k parent()) ) && 
   ( (opp != null) &&	(opp.left == this)) ~=>	 (opp#0.5 left(this, lcc)) &&
   ( (opp != null) && (opp.right == this)) ~=>	 (opp#0.5 right(this, lcc))
      &&
     ((opp == null) ~=> (this#0.5 count(lcc)))
      &&
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

assume (forall y:Ref :: (fracRight[y] >= 0.0) );
assume (forall y:Ref :: (fracLeft[y] >= 0.0) );
assume (forall y:Ref :: (fracCount[y] >= 0.0) );
assume (forall y:Ref :: (fracParent[y] >= 0.0) );

//We already have access to this.parent from the precondition of 
//this function.
if (this.parent != null) {
	// Even though an object proposition is unpacked, we still hold the
	// fraction to it as before.
	unpack(opp#k/2 parent())[opp.parent, opp.count];
	//We get opp#1/2 count(lccc) from unpacking opp in parent()
	
	unpack(opp#0.5 count(opp.count))[oll, this, llc, lcc];
			
	if (this == this.parent.right) {
		//The rule in the formal system should be that
		//if we have two object propositions with different parameters
		//that are both packed, then the parameters should be the same.
		addFrac(opp#0.5 right(this, lcc), opp#0.5 right(orr, rrc));
		fracRight[opp] := 0.5 + 0.5;
		//Explain why we need the full fraction!!!
		unpack(opp#1.0 right(this, lcc))[opp.parent];
		
		addFrac(unpacked(this#0.5 count(lcc)), this#0.5 count(lcc));
		fracCount[this] := 0.5 + 0.5;
		
		this.updateCount()[lcc, ol, or, opp, lc, rc, opp.count];
		fracLeft[this] := fracLeft[this] - 0.5;
		fracRight[this] := fracRight[this] - 0.5;

		(opp!=null) ~=> fracCount[opp] := fracCount[opp] - k;
		fracCount[this] := 1.0;
		pack(this#k2 parent())[opp, lc + rc + 1];
		fracCount[this] := fracCount[this] - 0.5;
		if (opp!=null) { fracParent[opp] := fracParent[opp] - k; }
		if ((opp != null) && (left[opp] == this)) {
			fracLeft[opp] := fracLeft[opp] - 0.5;
		}
		if ((opp != null) && (right[opp] == this)) {
			fracRight[opp] := fracRight[opp] - 0.5;
		}
		if (opp == null) {
			fracCount[this] := fracCount[this] - 0.5;
		}
		
		pack(opp#1.0 right(this, lcc))[opp.parent];
		if (this != null) { fracCount[this] := fracCount[this] - 0.5;}
			
		this.parent.updateCountRec()[opp.parent, opp.count, opp.left, this, opp.left.count, lc + rc + 1];	
		if (opp.parent != null) { fracParent[opp.parent] := fracParent[opp.parent] - k; } 
		// I don't need to subtract the fractions of the unpacked object propositions
		// (of the pre-conditions of a method) ??
		if ((opp.parent != null) && (opp.parent.left == opp)) {
			fracLeft[opp.parent] := fracLeft[opp.parent] - 0.5;
		}
		if ((opp.parent != null) && (opp.parent.right == opp)) { 
			fracRight[opp.parent] := fracRight[opp.parent] - 0.5;
		}
		if (opp.parent == null) { 
			fracCount[this.parent] := fracCount[this.parent] - 0.5; 
		}
		fracLeft[this.parent] := fracLeft[this.parent] - 0.5;
		fracRight[this.parent] := fracRight[this.parent] - 0.5;
		
		fracParent[this.parent] := fracParent[this.parent] + k;
		
	} else if (this == this.parent.left){
		addFrac(opp#0.5 left(this, lcc), opp#0.5 left(oll, llc));
		fracLeft[opp] := 0.5 + 0.5;
		//Explain why we need the full fraction!!!
		unpack(opp#1.0 left(this, lcc))[opp.parent];
		
		addFrac(unpacked(this#0.5 count(lcc)), this#0.5 count(lcc));
		fracCount[this] := 0.5 + 0.5;
		
		this.updateCount()[lcc, ol, or, opp, lc, rc, opp.count];
		fracLeft[this] := fracLeft[this] - 0.5;
		fracRight[this] := fracRight[this] - 0.5;
		// Instead of k I can divide and multiply by 2 (and top at 1 if necessary)
		(opp!=null) ~=> fracCount[opp] := fracCount[opp] - k;
		fracCount[this] := 1.0;
		pack(this#k2 parent())[opp, lc + rc + 1];
		fracCount[this] := fracCount[this] - 0.5;
		if (opp!=null) { fracParent[opp] := fracParent[opp] - k; }
		if ((opp != null) && (left[opp] == this)) {
			fracLeft[opp] := fracLeft[opp] - 0.5;
		}
		if ((opp != null) && (right[opp] == this)) {
			fracRight[opp] := fracRight[opp] - 0.5;
		}
		if (opp == null) {
			fracCount[this] := fracCount[this] - 0.5;
		}
			
		pack(opp#1.0 left(this, lcc))[opp.parent];
		if (this != null) { fracCount[this] := fracCount[this] - 0.5;}
			
		this.parent.updateCountRec()[opp.parent, opp.count, this, opp.right, opp.left.count, lc + rc + 1];	
		if (opp.parent != null) { fracParent[opp.parent] := fracParent[opp.parent] - k; } 
		// I need to subtract the fractions of the unpacked object propositions
		// (of the pre-conditions of a method) because I need to know
		// that I have the right fraction to that object proposition,
		// even if it's unpacked.
		if ((opp.parent != null) && (opp.parent.left == opp)) {
			fracLeft[opp.parent] := fracLeft[opp.parent] - 0.5;
		}
		if ((opp.parent != null) && (opp.parent.right == opp)) { 
			fracRight[opp.parent] := fracRight[opp.parent] - 0.5;
		}
		if (opp.parent == null) { 
			fracCount[this.parent] := fracCount[this.parent] - 0.5; 
		}
		fracLeft[this.parent] := fracLeft[this.parent] - 0.5;
		fracRight[this.parent] := fracRight[this.parent] - 0.5;
		
		fracParent[this.parent] := fracParent[this.parent] + k;
	}		
 } else {
	addFrac(this#0.5 count(lcc), unpacked(this#0.5 count(lcc)));
	fracCount[this] := 0.5 + 0.5;
	//After this addFrac, we know that this is unpacked in count()
	//TODO: the order of these is not right.
	this.updateCount()[lcc, ol, or, opp, lc, rc, opp.count];
	// Need to add an assumes that says that all fracs are at most 1.0.
	fracCount[this] := fracCount[this] - 1.0;
	fracLeft[this] := fracLeft[this] - 0.5;
	fracRight[this] := fracRight[this] - 0.5;
	if (opp != null) {
		fracCount[opp] := fracCount[opp] - k;
	}
	fracCount[this] := fracCount[this] + 1.0;
	// I don't know if I need this splitFrac.	
	splitFrac(unpacked(this#1.0 count(lcc)), 2);

	pack(this#k2 parent())[this.parent, lc + rc + 1];
	fracCount[this] := fracCount[this] - 0.5;
	if (opp!=null) { fracParent[opp] := fracParent[opp] - k; }
	if ((opp != null) && (left[opp] == this)) {
		fracLeft[opp] := fracLeft[opp] - 0.5;
	}
	if ((opp != null) && (right[opp] == this)) {
		fracRight[opp] := fracRight[opp] - 0.5;
	}
	if (opp == null) {
		fracCount[this] := fracCount[this] - 0.5;
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
	fracLeft[this] := 0.5 + 0.5;
	
	unpack(this#1.0 left(null, 0))[this.parent];	
	
	this.left = l;
	this.left.count = l.left.count;
	
	pack(this#1.0 left(l, l.left.count))[this.parent];
	if (l != null) {
		fracCount[l] := fracCount[l] + 0.5;
	}

	// Do I need this splitFrac?? 
	splitFrac(this#1.0 left(l, l.left.count));

	pack(l#k2 parent())[l.parent, l.left.count];
	
	fracCount[l] := fracCount[l] - 0.5;
	if (l.parent!=null) { 
		fracParent[l.parent] := fracParent[l.parent] - k; 
	}
	if ((l.parent != null) && (left[l.parent] == l)) {
		fracLeft[l.parent] := fracLeft[l.parent] - 0.5;
	}
	if ((l.parent != null) && (right[l.parent] == l)) {
		fracRight[l.parent] := fracRight[l.parent] - 0.5;
	}
	if (l.parent == null) {
		fracCount[l] := fracCount[l] - 0.5;
	}
	
	this.updateCountRec()[this.parent, lcc, l, this.right, l.left.count, this.right.count]; 
// Whenever we see k, we could replace it with 0.00001
	if (this.parent != null) { fracParent[this.parent] := fracParent[this.parent] - k; } 
	if ((this.parent != null) && (this.parent.left == this)) {
		fracLeft[this.parent] := fracLeft[this.parent] - 0.5;
	}
	if ((this.parent != null) && (this.parent.right == this)) { 
		fracRight[this.parent] := fracRight[this.parent] - 0.5;
	}
	if (this.parent == null) { 
		fracCount[this] := fracCount[this] - 0.5; 
	}
	fracLeft[this] := fracLeft[this] - 0.5;
	fracRight[this] := fracRight[this] - 0.5;
	
	fracParent[this] := fracParent[this] + k;
}		
	
}
}

}
