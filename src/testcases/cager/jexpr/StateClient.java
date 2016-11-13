package testcases.cager.jexpr;

class StateClient { 
private StateContext scon; 
predicate stateClientMultiple2() = exists k:real :: scon@k stateContextMultiple2()  
predicate stateClientMultiple3() = exists k:real :: scon@k stateContextMultiple3()  
public StateClient(StateContext scon1) { 
scon = scon1; 
} 
this@1 stateClientMultiple3() --> 
boolean stateClientCheckMultiplicity3() { 
return scon.stateContextCheckMultiplicity3(); 
} 

this@1 stateClientMultiple2() --> 
boolean stateClientCheckMultiplicity2() { 
return scon.stateContextCheckMultiplicity2(); 
} 

public static void main() { 
final StateContext scontext1 = new StateContext(stateContextMultiple3())(); 
StateClient sclient1 = new StateClient(stateClientMultiple3())(scontext1); 
StateClient sclient2 = new StateClient(stateClientMultiple3())(scontext1); 
scontext1.computeResult(1); 
sclient1.stateClientCheckMultiplicity3(); 
scontext1.computeResult(2); 
sclient2.stateClientCheckMultiplicity3(); 
scontext1.computeResult(3); 
sclient1.stateClientCheckMultiplicity3(); 

final StateContext scontext2 = new StateContext(stateContextMultiple2())(); 
StateClient sclient3 = new StateClient(stateClientMultiple2())(scontext2); 
StateClient sclient4 = new StateClient(stateClientMultiple2())(scontext2); 
scontext2.computeResult2(1); 
sclient3.stateClientCheckMultiplicity2(); 
scontext2.computeResult2(2); 
sclient4.stateClientCheckMultiplicity2();  
scontext2.computeResult2(3); 
sclient3.stateClientCheckMultiplicity2(); 				
} 