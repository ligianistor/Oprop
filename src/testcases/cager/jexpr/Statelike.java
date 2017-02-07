package testcases.cager.jexpr;

interface Statelike { 
	predicate StateMultipleOf3(); 
	predicate StateMultipleOf2(); 
	
	IntCell computeResult(StateContext context, int num);
	~ double k, k2:
		requires (context#k stateContextMultiple3()) 
		ensures (context#k stateContextMultiple3())
	
	IntCell computeResult2(StateContext context, int num); 
	~ double k, k2:
		requires (context#k stateContextMultiple2()) 
		ensures (context#k stateContextMultiple2())
	
	boolean checkMod3(); 
	~double k:
		requires this#k StateMultipleOf3()
		ensures this#k StateMultipleOf3()
	
	boolean checkMod2(); 
	~double k:
		requires this#k StateMultipleOf2()
		ensures this#k StateMultipleOf2()
}

