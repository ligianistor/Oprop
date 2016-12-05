package testcases.cager.jexpr;

class StateContext { 
Statelike myState; 

	predicate BasicFieldsContext() = exists m:StateLike :: this.myState -> m

	predicate stateLive() = myState instanceof StateLive
	predicate stateSleep() = myState instanceof StateSleep
	predicate stateLimbo() = myState instanceof StateLimbo
	predicate stateContextMultiple2() = exists m:StateLike :: this.myState -> m && m#1 StateMultipleOf2() 
	predicate stateContextMultiple3() = exists m:StateLike :: this.myState -> m && m#1 StateMultipleOf3() 

	// This is the constructor of StateContext,
	// which initializes myState with the given value.
	// This constructor will be written automatically in the
	// Boogie translation, no need to put it in this file.
	/*
State(Statelike newState) 	
{ 
	myState = newState; 
} 
*/

IntCell computeResultSC(int num) 
requires (this#1.0 BasicFieldsContext())
ensures (this#1.0 stateContextMultiple3())
ensures (this#1.0 stateLive() ~=> (this#1.0 stateLimbo())) 
&& 
(this#1.0 stateLimbo() ~=> this#1.0 stateSleep())
&&
(this#1.0 stateSleep() ~=> this#1.0 stateLive())
{ 
	return myState.computeResult(this, num); 
} 

IntCell computeResult2SC(int num) 
ensures (this#1 stateContextMultiple2())
ensures (this#1.0 stateLive() ~=> (this#1.0 stateSleep())) 
&& 
(this#1.0 stateSleep() ~=> (this#1.0 stateLimbo())) 
&&
(this#1.0 stateLimbo() ~=> (this#1.0 stateLive()))
{ 
	return myState.computeResult2(this, num); 
} 

boolean stateContextCheckMultiplicity3() 
requires this#1.0 stateContextMultiple3() 
ensures this#1.0 stateContextMultiple3() 
{ 
	return myState.checkMod3(); 
} 

boolean stateContextCheckMultiplicity2() 
requires this#1.0 stateContextMultiple2() 
ensures this#1.0 stateContextMultiple2()
{ 
	return myState.checkMod2(); 
} 
}