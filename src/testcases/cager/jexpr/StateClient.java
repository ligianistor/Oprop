package testcases.cager.jexpr;

class StateClient { 
  StateContext scon; 

  predicate StateClientMultiple2() = 
      exists double k, StateContext s :: this.scon->s && (s#k StateContextMultiple2())  
  predicate StateClientMultiple3() = 
      exists double k, StateContext s :: this.scon->s && (s#k StateContextMultiple3())
	
  StateClient(StateContext s)
  ensures this.scon == s
  {
    this.scon = s;
  }

  boolean stateClientCheckMultiplicity3() 
  ~double k:
    requires this#k StateClientMultiple3() 
    ensures this#k StateClientMultiple3() 
  { 
    unpack(this#k StateClientMultiple3())[this.scon]
    return this.scon.stateContextCheckMultiplicity3(); 
    pack(this#k StateClientMultiple3())[this.scon]
  } 
 
  boolean stateClientCheckMultiplicity2() 
  ~double k:
    requires this#k StateClientMultiple2()
    ensures this#k StateClientMultiple2()
  { 
    unpack(this#k StateClientMultiple2())[this.scon]
    return this.scon.stateContextCheckMultiplicity2(); 
    pack(this#k StateClientMultiple2())[this.scon]
  } 

  void main1() 
  ~double k:
  { 
    IntCell i1 = new IntCell(MultipleOf(15))(15);
    Statelike st1 = new StateLive(StateMultipleOf3())(i1);
    StateContext scontext1 = new StateContext(stateContextMultiple3()[], stateLive()[])(st1); 
    StateClient sclient1 = new StateClient(stateClientMultiple3()[])(scontext1);
    StateClient sclient2 = new StateClient(stateClientMultiple3()[])(scontext1);
    unpack(scontext1#k stateClientMultiple3())[scontext1.myState]
    scontext1.computeResultSC(1); 
    sclient1.stateClientCheckMultiplicity3();
    unpack(scontext1#k stateClientMultiple3())[scontext1.myState]
    scontext1.computeResultSC(2); 
    sclient2.stateClientCheckMultiplicity3(); 
    unpack(scontext1#k stateClientMultiple3())[scontext1.myState]
    scontext1.computeResultSC(3); 
    sclient1.stateClientCheckMultiplicity3(); 
  }

  void main2() 
  ~double k:
  { 
    IntCell i2 = new IntCell(MultipleOf(14))(14);
    Statelike st2 = new StateLive(StateMultipleOf2())(i2);
    StateContext scontext2 = new StateContext(stateContextMultiple2()[], stateLive()[])(st2); 
    StateClient sclient3 = new StateClient(stateClientMultiple2()[])(scontext2);
    StateClient sclient4 = new StateClient(stateClientMultiple2()[])(scontext2); 
    unpack(scontext1#k stateClientMultiple2())[scontext2.myState]
    scontext2.computeResult2SC(1); 
    sclient3.stateClientCheckMultiplicity2(); 
    unpack(scontext1#k stateClientMultiple2())[scontext2.myState]
    scontext2.computeResult2SC(2); 
    sclient4.stateClientCheckMultiplicity2();  
    unpack(scontext1#k stateClientMultiple2())[scontext2.myState]
    scontext2.computeResult2SC(3); 
    sclient3.stateClientCheckMultiplicity2(); 				
  } 
}