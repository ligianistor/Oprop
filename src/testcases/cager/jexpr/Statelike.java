package testcases.cager.jexpr;

interface Statelike { 
	predicate BasicFields();
	predicate StateMultipleOf3(); 
	predicate StateMultipleOf2(); 
	
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

