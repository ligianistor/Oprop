package testcases.cager.jexpr;

class Share {
	DoubleCount dc;
	
	predicate ShareCount() = exists DoubleCount d : 
		       this.dc -> d && (d#1 OK())
		
	void Share(DoubleCount dc2) {
		dc = dc2;
	}

		       
	void touch() 
	int k:
	requires this#k ShareCount()
	ensures this#k ShareCount()
	{
	 dc.increment();
	}
	
	

}
