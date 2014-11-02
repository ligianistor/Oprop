package testcases.cager.jexpr;

class Share {
	DoubleCount dc;
	
	predicate ShareCount() = exists DoubleCount d : 
	       this.dc -> d && (d#1 OK())
	       
	       		//dc.increment();
	

	
}
