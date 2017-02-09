package testcases.cager.jexpr;

class StateContext { 
Statelike myState; 

	predicate stateLive() = exists StateLike m  : this.myState -> m && (m instanceof StateLive)
	predicate stateSleep() = exists StateLike m : this.myState -> m && (m instanceof StateSleep)
	predicate stateLimbo() = exists StateLike m : this.myState -> m && (m instanceof StateLimbo)
	predicate stateContextMultiple2() = exists StateLike m, double k : this.myState -> m && (m#k StateMultipleOf2())
	predicate stateContextMultiple3() = exists StateLike m, double k : this.myState -> m && (m#k StateMultipleOf3()) 

StateContext(Statelike newState) 
ensures this.myState == newState;
{ 
		this.myState = newState; 
} 
	
void setState2(Statelike newState) 
~ double k1, k2:
requires unpacked(this#k1 stateContextMultiple2())
requires newState#k2 StateMultipleOf2()
ensures this#k1 stateContextMultiple2()[newState] 
{
	this.myState = newState; 
	pack(this#k1 stateContextMultiple2())[newState]; 
}

void setState3(Statelike newState) 
~ double k1, k2:
requires unpacked(this#k1 stateContextMultiple3())
requires newState#k2 StateMultipleOf3()
ensures this#k1 stateContextMultiple3()[newState] 
{
	this.myState = newState; 
	pack(this#k1 stateContextMultiple3())[newState]; 
}

IntCell computeResultSC(int num) 
~double k1, k2:
requires unpacked(this#k1 stateContextMultiple3())
requires (this.myState#k2 stateMultipleOf3())
ensures (this#1.0 stateContextMultiple3())
ensures (this#k1 stateContextMultiple3())
{ 
	return this.myState.computeResult(this, num); 
} 

IntCell computeResult2SC(int num) 
~double k1, k2:
requires unpacked(this#k1 stateContextMultiple2())
requires (this.myState#k2 stateMultipleOf2())
ensures (this#k1 stateContextMultiple2())
{ 
	return this.myState.computeResult2(this, num); 
} 

boolean stateContextCheckMultiplicity3() 
~double k:
requires this#k stateContextMultiple3() 
ensures this#k stateContextMultiple3() 
{ 
	unpack(this#k stateContextMultiple3())[this.myState]
	return this.myState.checkMod3(); 
	pack(this#k stateContextMultiple3())[this.myState]
} 

boolean stateContextCheckMultiplicity2() 
~double k:
requires this#k stateContextMultiple2() 
ensures this#k stateContextMultiple2()
{ 
	unpack(this#k stateContextMultiple2())[this.myState]
	return this.myState.checkMod2(); 
	pack(this#k stateContextMultiple2())[this.myState]
} 
}