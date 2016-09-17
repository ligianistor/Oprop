package testcases.cager.jexpr;

class Share {
	DoubleCount dc;
	
	predicate ShareCount() = 
	  exists DoubleCount d : 
	  this.dc -> d && (d#0.1 OK())
		       
	void touch() 
	~ double k:
	requires this#k ShareCount()
	ensures this#k ShareCount()
	{
		unpack(this#k ShareCount())[this.dc];
		this.dc.increment();
		pack(this#k ShareCount())[this.dc];
	}
	
	void main() 
	{
		DoubleCount dc0 = new DoubleCount(OK()[2, 4])(2, 4);
		Share s1 = new Share(ShareCount()[dc0])(dc0);
		Share s2 = new Share(ShareCount()[dc0])(dc0);
		s1.touch();
		s2.touch();
	}
}
