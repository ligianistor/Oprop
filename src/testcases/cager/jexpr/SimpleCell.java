package x;

class SimpleCell {
	int val;
	SimpleCell next;
	
	predicate PredVal() = exists int v : this.val -> v && v<15
			
	predicate PredNext() = exists SimpleCell obj : this.next -> obj && (obj#0.34 PredVal())	

	void changeVal(int r) 
	requires this#1 PredVal()
	ensures this#1 PredVal()
	{
		unpack(this#1 PredVal());
		this.val = r;
		pack(this#1 PredVal());
	}
	
	void main() {
		SimpleCell c = new SimpleCell(PredVal())(2, null);
		SimpleCell a = new SimpleCell(PredNext())(2, c);
		SimpleCell b = new SimpleCell(PredNext())(3, c);
		
		unpack(a#1 PredNext());
		unpack(b#1 PredNext());
		addFrac(c#0.34 PredVal(), c#0.34 PredVal(), c#0.32 PredVal());
		c.changeVal(4);
		
	}
}
