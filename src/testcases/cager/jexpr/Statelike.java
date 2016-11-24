package testcases.cager.jexpr;

interface Statelike { 
	predicate StateMultipleOf3(); 
	predicate StateMultipleOf2(); 
	IntCell cell;
	// if computeResult() has a precondition like
	// StateMultipleOf3, depending on which actual implementation calls computeResult()
	// it is the implementation of predicate StateMultipleOf3 in that implementation
	// that has to hold.
	IntCell computeResult(StateContext context, int num); 
	IntCell computeResult2(StateContext context, int num); 
	boolean checkMod3(); 
	boolean checkMod2(); 
}