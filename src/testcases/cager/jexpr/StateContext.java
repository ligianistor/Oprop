package testcases.cager.jexpr;

class StateContext { 
private Statelike myState; 

predicate state6() = myState instanceof StateMultiplySix 
predicate state18() = myState instanceof StateMultiplyEighteen 
predicate state12() = myState instanceof StateMultiplyTwelve 
predicate stateContextMultiple2() = myState@1 StateMultipleOf2() 
predicate stateContextMultiple3() = myState@1 StateMultipleOf3() 
predicate stateContextMultiple6() = myState@1 StateMultipleOf6() 
predicate stateContextMultiple18() = myState@1 StateMultipleOf18() 
predicate stateContextMultiple12() = myState@1 StateMultipleOf12() 

--> this@1 state6() 
StateContext() { 
setState(new StateMultiplySix()); 
} 
void setState(final Statelike newState) { 
myState = newState; 
} 

--> (this@1 state6() -> (this@1 stateContextMultiple6() && this@1 state18())) 
&& 
(this@1 state18() -> (this@1 stateContextMultiple18() && this@1 state6())) 
public Integer computeResult(final int num) { 
return myState.computeResult(this, num); 
} 

--> (this@1 state6() -> (this@1 stateContextMultiple6() && this@1 state12())) 
&& 
(this@1 state12() -> (this@1 stateContextMultiple12() && this@1 state6())) 
public Integer computeResult2(final int num) { 
return myState.computeResult2(this, num); 
} 

this@1 stateContextMultiple3() --> 
boolean stateContextCheckMultiplicity3() { 
return sc.checkMod3(); 
} 

this@1 stateContextMultiple2() --> 
boolean stateContextCheckMultiplicity2() { 
return sc.checkMod2(); 
} 
}