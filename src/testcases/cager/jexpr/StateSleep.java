package testcases.cager.jexpr;

class StateSleep implements Statelike {
	
	IntCell cell;
	
predicate StateMultipleOf3() = exists IntCell c, double k : this.cell -> c && (c#k MultipleOf(15)) 
predicate StateMultipleOf2() = exists IntCell c, double k : this.cell -> c && (c#k MultipleOf(16)) 

StateSleep() 
{
		IntCell temp = new IntCell(0);
		this.cell = temp; 		
}

StateSleep(IntCell c) 
ensures this.cell == c;
{
	this.cell = c; 		
}

IntCell computeResult(StateContext context, int num) 
~ double k, k2:
	requires (context#k stateContextMultiple3()) 
	ensures (context#k stateContextMultiple3()) && (context#k2 stateLive())
{ 
	IntCell i1 = new IntCell(MultipleOf(15)[num*15])(15, num*15);
	StateLike r = new StateSleep(StateMultipleOf3()[i1])(i1);
	context.setState3(s); 
	return r; 
}  
 
IntCell computeResult2(StateContext context, int num) 
~ double k, k2:
	requires (context#k stateContextMultiple2()) 
	ensures (context#k stateContextMultiple2()) && (context#k2 stateLimbo())
{ 
	IntCell i1 = new IntCell(MultipleOf(16)[num*16])(16, num*16);
	StateLike r = new StateLive(StateMultipleOf2()[i1])(i1);
	context.setState2(r); 
	return r;
}  

boolean checkMod3() 
~double k:
requires this#1.0 StateMultipleOf3()
ensures this#1.0 StateMultipleOf3()
{ 
	return (this.cell.getValueInt() % 3 == 0); 
}  
boolean checkMod2() 
~double k:
requires this#1.0 StateMultipleOf2()
ensures this#1.0 StateMultipleOf2()
{ 
	return (this.cell.getValueInt() % 2 == 0); 
} 
}