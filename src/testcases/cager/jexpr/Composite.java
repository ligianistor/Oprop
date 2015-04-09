package testcases.cager.jexpr;

class Composite {
	int count;
	Composite left;
	Composite right;
	Composite parent;
	
	Composite() {
		this.count = 1;
		this.left = null;
		this.right = null;
		this.parent = null; 
	}

	void updateCountRec() {
	if (this.parent != null) {
		this.updateCount();
		this.parent.updateCountRec();
	 } else {
		 this.updateCount(); 
	 }
	}

	void updateCount() {
	int newc = 1;
	if (this.left != null)
	  newc = newc + left.count;
	if (this.right != null)
	  newc = newc + right.count;
	this.count = newc; 
	}

	void setLeft(Composite l) {
		if (l.parent==null) {
			l.parent= this;
			this.left = l;
			this.updateCountRec(); 
		}
	}

	void setRight(Composite r) {
		if (r.parent==null) {
			r.parent = this;
			this.right = r;
			this.updateCountRec(); 
		}
	}

}
