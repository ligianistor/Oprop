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
		DoubleCount dc0 = new DoubleCount(OK())(2, 4);
		Share s1 = new Share(ShareCount())(dc0);
		Share s2 = new Share(ShareCount())(dc0);
		s1.touch();
		s2.touch();
	}
}
