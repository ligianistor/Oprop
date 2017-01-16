package testcases.cager.jexpr;

class StateContext { 
Statelike myState; 

	predicate BasicFieldsContext() = exists m:StateLike :: this.myState -> m

	predicate stateLive() = exists m:StateLike :: this.myState -> m && (m instanceof StateLive)
	predicate stateSleep() = exists m:StateLike :: this.myState -> m && (m instanceof StateSleep)
	predicate stateLimbo() = exists m:StateLike :: this.myState -> m && (m instanceof StateLimbo)
	predicate stateContextMultiple2() = exists m:StateLike :: this.myState -> m && m#1 StateMultipleOf2() 
	predicate stateContextMultiple3() = exists m:StateLike :: this.myState -> m && m#1 StateMultipleOf3() 

StateContext(Statelike newState) 
ensures (this#1.0 stateContextMultiple2()) || (this#1.0 stateContextMultiple3())
{ 
		this.myState = newState; 
} 

IntCell computeResult(int num) 
requires (this#1.0 BasicFieldsContext())
ensures (this#1.0 stateContextMultiple3())
ensures (this#1.0 stateLive() ~=> (this#1.0 stateLimbo())) 
&& 
(this#1.0 stateLimbo() ~=> this#1.0 stateSleep())
&&
(this#1.0 stateSleep() ~=> this#1.0 stateLive())
{ 
	return this.myState.computeResult(this, num); 
} 

IntCell computeResult2(int num) 
ensures (this#1 stateContextMultiple2())
ensures (this#1.0 stateLive() ~=> (this#1.0 stateSleep())) 
&& 
(this#1.0 stateSleep() ~=> (this#1.0 stateLimbo())) 
&&
(this#1.0 stateLimbo() ~=> (this#1.0 stateLive()))
{ 
	return this.myState.computeResult2(this, num); 
} 

boolean stateContextCheckMultiplicity3() 
requires this#1.0 stateContextMultiple3() 
ensures this#1.0 stateContextMultiple3() 
{ 
	return this.myState.checkMod3(); 
} 

boolean stateContextCheckMultiplicity2() 
requires this#1.0 stateContextMultiple2() 
ensures this#1.0 stateContextMultiple2()
{ 
	return this.myState.checkMod2(); 
} 
}