package testcases.cager.jexpr;

class StateContext { 
  Statelike myState; 

  predicate StateLive() = exists StateLike m  : this.myState -> m && (m instanceof StateLive)
  predicate StateSleep() = exists StateLike m : this.myState -> m && (m instanceof StateSleep)
  predicate StateLimbo() = exists StateLike m : this.myState -> m && (m instanceof StateLimbo)
  predicate StateContextMultiple2() = exists StateLike m, double k : 
      this.myState -> m && (m#k StateMultipleOf2())
  predicate StateContextMultiple3() = exists StateLike m, double k : 
      this.myState -> m && (m#k StateMultipleOf3()) 

  StateContext(Statelike newState) 
  ensures this.myState == newState;
  { 
    this.myState = newState; 
  } 
	
  void setState2(Statelike newState) 
  ~double k1, k2:
    requires unpacked(this#k1 StateContextMultiple2())
    requires newState#k2 StateMultipleOf2()
    ensures this#k1 StateContextMultiple2()[newState] 
  {
    this.myState = newState; 
    pack(this#k1 StateContextMultiple2())[newState]; 
  }

  void setState3(Statelike newState) 
  ~double k1, k2:
    requires unpacked(this#k1 StateContextMultiple3())
    requires newState#k2 StateMultipleOf3()
    ensures this#k1 StateContextMultiple3()[newState] 
  {
    this.myState = newState; 
    pack(this#k1 StateContextMultiple3())[newState]; 
  }

  IntCell computeResultSC(int num) 
  ~double k1, k2:
    requires unpacked(this#k1 StateContextMultiple3())
    requires (this.myState#k2 StateMultipleOf3())
    ensures (this#1.0 StateContextMultiple3())
    ensures (this#k1 StateContextMultiple3())
  { 
    return this.myState.computeResult(this, num); 
  } 

  IntCell computeResult2SC(int num) 
  ~double k1, k2:
    requires unpacked(this#k1 StateContextMultiple2())
    requires (this.myState#k2 StateMultipleOf2())
    ensures (this#k1 StateContextMultiple2())
  { 
    return this.myState.computeResult2(this, num); 
  } 

  boolean stateContextCheckMultiplicity3() 
  ~double k:
    requires this#k StateContextMultiple3() 
    ensures this#k StateContextMultiple3() 
  { 
    unpack(this#k StateContextMultiple3())[this.myState]
    return this.myState.checkMod3(); 
    pack(this#k StateContextMultiple3())[this.myState]
  } 

  boolean stateContextCheckMultiplicity2() 
  ~double k:
    requires this#k StateContextMultiple2() 
    ensures this#k StateContextMultiple2()
  { 
    unpack(this#k StateContextMultiple2())[this.myState]
    return this.myState.checkMod2(); 
    pack(this#k StateContextMultiple2())[this.myState]
  } 
}