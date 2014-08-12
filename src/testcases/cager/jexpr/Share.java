package testcases.cager.jexpr;

class Share {
	DoubleCount dc;
	
	predicate ShareCount() = exists DoubleCount d : 
		       this.dc -> d && (d#1/2 OK())
		       //instead of 1/2 might need to put 50
		
	public Share(DoubleCount dc2) {
		dc = dc2;
	}

		       
	public void touch() 
	int k:
	requires this#k ShareCount()
	ensures this#k ShareCount()
	{
	    dc.increment();
	  }
	
	
	public static void main(String[] args) 
	//might need to put specs here
	//and in between the statements inside
	{
		DoubleCount dc = new DoubleCount();
		Share s1 = new Share(dc);
		Share s2 = new Share(dc);
		s1.touch();
		//invariant is kept
		//s2 can rely on invariant
		s2.touch();
	}
}
