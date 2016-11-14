package testcases.cager.jexpr;

interface Statelike { 
	predicate StateMultipleOf3(); 
	predicate StateMultipleOf2(); 
	IntCell cell =  new IntCell(1);
	IntCell computeResult(StateContext context, int num); 
	IntCell computeResult2(StateContext context, int num); 
	boolean checkMod3(); 
	boolean checkMod2(); 
}