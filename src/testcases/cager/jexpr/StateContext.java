package testcases.cager.jexpr;

class StateContext { 
private Statelike myState; 
/*
	predicate state6() = myState instanceof StateMultiplySix 
	predicate state18() = myState instanceof StateMultiplyEighteen 
	predicate state12() = myState instanceof StateMultiplyTwelve 
	predicate stateContextMultiple2() = myState#1 StateMultipleOf2() 
	predicate stateContextMultiple3() = myState#1 StateMultipleOf3() 
	predicate stateContextMultiple6() = myState#1 StateMultipleOf6() 
	predicate stateContextMultiple18() = myState#1 StateMultipleOf18() 
	predicate stateContextMultiple12() = myState#1 StateMultipleOf12() 
*/

StateContext() 
//ensures this#1 state6() 
{ 
	setState(new StateMultiplySix()); 
} 

void setState(Statelike newState) { 
	myState = newState; 
} 


public IntCell computeResult(int num) 
/*
ensures (this#1 state6() ~=> (this#1 stateContextMultiple6() && this#1 state18())) 
&& 
(this#1 state18() ~=> (this#1 stateContextMultiple18() && this#1 state6())) 
*/
{ 
	return myState.computeResult(this, num); 
} 

public IntCell computeResult2(int num) 
/*
ensures (this#1 state6() ~=> (this#1 stateContextMultiple6() && this#1 state12())) 
&& 
(this#1 state12() ~=> (this#1 stateContextMultiple12() && this#1 state6())) 
*/
{ 
	return myState.computeResult2(this, num); 
} 

boolean stateContextCheckMultiplicity3() 
//requires this#1 stateContextMultiple3() 
{ 
	return myState.checkMod3(); 
} 

boolean stateContextCheckMultiplicity2() 
//requires this#1 stateContextMultiple2() 
{ 
	return myState.checkMod2(); 
} 
}