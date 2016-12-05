package testcases.cager.jexpr;

class StateClient { 
	
StateContext scon; 

	predicate stateClientMultiple2() = exists k:real, s:StateContext :: this.scon->s && s#k stateContextMultiple2()  
	predicate stateClientMultiple3() = exists k:real, s:StateContext :: this.scon->s && s#k stateContextMultiple3()  

boolean stateClientCheckMultiplicity3() 
requires this#1 stateClientMultiple3() 
ensures this#1 stateClientMultiple3() 
{ 
return scon.stateContextCheckMultiplicity3(); 
} 
 
boolean stateClientCheckMultiplicity2() 
requires this#1 stateClientMultiple2()
ensures this#1 stateClientMultiple2()
{ 
return scon.stateContextCheckMultiplicity2(); 
} 

void main() { 
	Statelike st1 = new StateLive();
StateContext scontext1 = new StateContext(stateContextMultiple3()[])(st1); 
StateClient sclient1 = new StateClient(stateClientMultiple3()[])(scontext1);
StateClient sclient2 = new StateClient(stateClientMultiple3()[])(scontext1);
scontext1.computeResultSC(1); 
sclient1.stateClientCheckMultiplicity3(); 
scontext1.computeResultSC(2); 
sclient2.stateClientCheckMultiplicity3(); 
scontext1.computeResultSC(3); 
sclient1.stateClientCheckMultiplicity3(); 

Statelike st2 = new StateLive();
StateContext scontext2 = new StateContext(stateContextMultiple2()[])(st2); 
StateClient sclient3 = new StateClient(stateClientMultiple2()[])(scontext2);
StateClient sclient4 = new StateClient(stateClientMultiple2()[])(scontext2); 
scontext2.computeResult2SC(1); 
sclient3.stateClientCheckMultiplicity2(); 
scontext2.computeResult2SC(2); 
sclient4.stateClientCheckMultiplicity2();  
scontext2.computeResult2SC(3); 
sclient3.stateClientCheckMultiplicity2(); 				
} 
}