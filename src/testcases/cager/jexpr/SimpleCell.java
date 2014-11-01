package testcases.cager.jexpr;

public class SimpleCell {
	int val;
	SimpleCell next;
	
	predicate PredVal() = this.val -> v && v<15
			
	predicate PredNext() = this.next -> obj && obj#34 PredVal()	

	public SimpleCell(int i, SimpleCell obj) {
		val = i;
		obj = next;
	}

	void changeVal(int r) 
	requires this#100 PredVal()
	ensures this#100 PredVal()
	{
		val = r;
	}
	
	public static void main(String[] args) {
		
		SimpleCell c = new SimpleCell(2, null);
		
		SimpleCell a = new SimpleCell(2, c);
		SimpleCell b = new SimpleCell(3, c);
		
		c.changeVal(4);
		
	}
}