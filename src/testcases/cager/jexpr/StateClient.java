package testcases.cager.jexpr;

class StateClient { 
	
StateContext scon; 

	predicate stateClientMultiple2() = 
		exists double k, StateContext s :: this.scon->s && (s#k stateContextMultiple2())  
	predicate stateClientMultiple3() = 
		exists double k, StateContext s :: this.scon->s && (s#k stateContextMultiple3())
	
StateClient(StateContext s)
ensures this.scon == s
{
		this.scon = s;
}

boolean stateClientCheckMultiplicity3() 
~double k:
requires this#k stateClientMultiple3() 
ensures this#k stateClientMultiple3() 
{ 
	unpack(this#k stateClientMultiple3())[this.scon]
	return this.scon.stateContextCheckMultiplicity3(); 
	pack(this#k stateClientMultiple3())[this.scon]
} 
 
boolean stateClientCheckMultiplicity2() 
~double k:
requires this#k stateClientMultiple2()
ensures this#k stateClientMultiple2()
{ 
	unpack(this#k stateClientMultiple2())[this.scon]
	return this.scon.stateContextCheckMultiplicity2(); 
	pack(this#k stateClientMultiple2())[this.scon]
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

void main2() { 
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