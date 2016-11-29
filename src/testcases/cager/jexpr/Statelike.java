package testcases.cager.jexpr;

interface Statelike { 
	predicate BasicFieldsState();
	predicate StateMultipleOf3(); 
	predicate StateMultipleOf2(); 

	// if computeResult() has a precondition like
	// StateMultipleOf3, depending on which actual implementation calls computeResult()
	// it is the implementation of predicate StateMultipleOf3 in that implementation
	// that has to hold.
	
	
	IntCell computeResult(StateContext context, int num);
	requires (this#1.0 BasicFieldsState()) && (context#1.0 BasicFieldsContext())
	ensures (this#1.0 StateMultipleOf3()) && (context#1.0 state6() || context#1.0 state18()) 
	
	IntCell computeResult2(StateContext context, int num); 
	requires (this#1.0 BasicFieldsState()) && (context#1.0 BasicFieldsContext())
	ensures (this#1.0 StateMultipleOf2()) && (context#1.0 state12() || context#1.0 state6())
	
	boolean checkMod3(); 
	requires this#1.0 StateMultipleOf3()
	ensures this#1.0 StateMultipleOf3()
	
	boolean checkMod2(); 
	requires this#1.0 StateMultipleOf2()
	ensures this#1.0 StateMultipleOf2()
}

//StateLive, StateSleep, StateLimbo
