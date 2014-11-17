package testcases.cager.jexpr;

class SimpleCell {
	int val;
	SimpleCell next;
	
	predicate PredVal() = exists int v : this.val -> v && v<15
			
	predicate PredNext() = exists SimpleCell obj : this.next -> obj && (obj#34 PredVal())	

	void changeVal(int r) 
	requires this#100 PredVal()
	ensures this#100 PredVal()
	{
		this.val = r;
	}
	
	void main() {
		SimpleCell c = new SimpleCell(PredVal)(2, null);
		
		SimpleCell a = new SimpleCell(PredNext)(2, c);
		SimpleCell b = new SimpleCell(PredNext)(3, c);
		
		c.changeVal(4);
		
	}
}
