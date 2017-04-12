package x;

class SimpleCell {
	int val;
	SimpleCell next;
	
	predicate PredVal() = exists int v : this.val -> v && v<15
			
	predicate PredNext() = exists SimpleCell obj : 
		this.next -> obj && (obj#0.34 PredVal())	
		
	// SimpleCell() {}
	SimpleCell(int v, SimpleCell n) 
	ensures (this.val == v) && (this.next == n)
	{
		this.val = v;
		this.next = n;
	}

	void changeVal(int r) 
	~double k : requires (this#k PredVal()) && (r<15) 
	ensures this#k PredVal()
	{
		unpack(this#k PredVal())[this.val];
		this.val = r;
		pack(this#k PredVal())[r];
	}
	
	void main() {
		SimpleCell c = new SimpleCell(PredVal()[2])(2, null);
		SimpleCell a = new SimpleCell(PredNext()[c])(2, c);
		SimpleCell b = new SimpleCell(PredNext()[c])(3, c);
		
		unpack(a#1 PredNext())[c];
		unpack(b#1 PredNext())[c];
		c.changeVal(4);	
	}
}
