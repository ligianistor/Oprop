package x;

class DoubleCount {
	int val;
	int dbl;
	
	predicate OK() = exists int v, int d : 
			         this.val -> v && this.dbl -> d &&
			         d == 2*d
	
	void increment()
	int k:
	requires this#k OK()
	ensures this#k OK()
	{
		val = val + 1;
		dbl = dbl + 2;
	}
}
