package testcases.cager.jexpr;

class Share {
	DoubleCount dc;
	
	predicate ShareCount() = exists DoubleCount d : 
		       this.dc -> d && (d#1 OK())
		       
	void touch() 
	int k:
	requires this#k ShareCount()
	ensures this#k ShareCount()
	{
		this.dc.increment();
	}
	
	void main() 
	{
		DoubleCount dc = new DoubleCount();
		Share s1 = new Share(dc);
		Share s2 = new Share(dc);
		s1.touch();
		s2.touch();
	}
}
