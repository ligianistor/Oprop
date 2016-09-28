package x;

class SimpleCell {
	int val;
	SimpleCell next;
	
	predicate PredVal() = exists int v : this.val -> v && v<15
			
	predicate PredNext() = exists SimpleCell obj : 
		this.next -> obj && (obj#0.34 PredVal())	

	void changeVal(int r) 
	requires (this#1 PredVal()) && (r<15) 
	ensures this#1 PredVal()
	{
		unpack(this#1 PredVal())[this.val];
		this.val = r;
		pack(this#1 PredVal())[r];
	}
	
	void main() {
		SimpleCell c = new SimpleCell(PredVal()[2])(2, null);
		SimpleCell a = new SimpleCell(PredNext()[c])(2, c);
		SimpleCell b = new SimpleCell(PredNext()[c])(3, c);
		
		unpack(a#1 PredNext())[c];
		unpack(b#1 PredNext())[c];
		addFrac(c#k PredVal(), c#0.34 PredVal(), c#0.34 PredVal());
		c.changeVal(4);
		
	}
}
