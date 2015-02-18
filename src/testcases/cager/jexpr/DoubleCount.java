package testcases.cager.jexpr;

class DoubleCount {
	int val;
	int dbl;
	
	predicate OK() = exists int v, int d : 
			         this.val -> v && this.dbl -> d &&
			         d == 2*v
	
	void increment()
	double k:
	requires this#k OK()
	ensures this#k OK()
	{
		unpack(this#k OK());
		this.val = this.val + 1;
		this.dbl = this.dbl + 2;
		pack(this#k OK());
	}
}
