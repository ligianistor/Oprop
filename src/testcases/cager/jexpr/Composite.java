package testcases.cager.jexpr;

class Composite {
	int count;
	Composite left;
	Composite right;
	Composite parent;	  
	
	predicate count(int c) = 
			exists Composite ol, Composite or,
			int lc, int rc : 
			this.count -> c && c == lc + rc + 1  
			&& (this#0.5 left(ol, lc)) 
			&& (this#0.5 right(or, rc)) 
			
	predicate left(Composite ol, int lc) =
		this.left -> ol &&
		((ol== null ~=> (lc == 0) ) ||
		(ol != null ~=> (ol#0.5 count(lc)))
		)
		
	predicate right(Composite or, int rc) =
		this.right -> or &&
		((or== null ~=> (rc == 0) ) ||
		(or != null ~=> (or#0.5 count(rc)))
		)
	
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
	ensures (this#0.5 parent()) &&
		(this#0.5 left(null,0)) &&
		(this#0.5 right(null, 0))	
	{
		this.count = 1;
		this.left = null;
		this.right = null;
		this.parent = null; 
	}

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
	if (this.parent != null) {
		this.updateCount();
		this.parent.updateCountRec();
	 } else {
		 this.updateCount(); 
	 }
	}

	void updateCount() 
	int c, int c1, int c2, int newc,
	Composite ol, Composite or:	
	requires unpacked(this#1.0 count(c)) &&
		(this#0.5 left(ol, c1)) &&
		(this#0.5 right(or, c2))
	ensures (this#1.0 count(newc))
	{
	int newc = 1;
	if (this.left != null)
	  newc = newc + left.count;
	if (this.right != null)
	  newc = newc + right.count;
	this.count = newc; 
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
		if (l.parent==null) {
			l.parent= this;
			this.left = l;
			this.updateCountRec(); 
		}
	}

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
