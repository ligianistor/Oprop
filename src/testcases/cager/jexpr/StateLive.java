package testcases.cager.jexpr;

class StateLive implements Statelike { 
	
	IntCell cell;

	predicate StateMultipleOf3() = exists IntCell c, double k : this.cell -> c && (c#k MultipleOf(21))
	predicate StateMultipleOf2() = exists IntCell c, double k : this.cell -> c && (c#k MultipleOf(4)) 
	
StateLive() 
{
		IntCell temp = new IntCell(0);
		this.cell = new StateLive(temp); 		
}
	
StateLive(IntCell c) 
ensures this.cell == c;
{
		this.cell = c; 		
}
	
Statelike computeResult(StateContext context, int num)
~ double k, k2:
requires (context#k stateContextMultiple3()) 
ensures (context#k stateContextMultiple3()) && (context#k2 stateLimbo())
{ 
	IntCell i1 = new IntCell(MultipleOf(21)[num*21])(21, num*21);
	StateLike r = new StateLimbo(StateMultipleOf3()[i1])(i1);
	context.setState3(r); 
	return r;
} 

Statelike computeResult2(StateContext context, int num)
~ double k, k2:
requires (context#k stateContextMultiple2()) 
ensures (context#k stateContextMultiple2()) && (context#k2 stateSleep())
{ 
	IntCell i1 = new IntCell(MultipleOf(4)[num*4])(4, num*4);
	StateLike r = new StateLimbo(StateMultipleOf2()[i1])(i1);
	context.setState2(r); 
	return r;
} 

boolean checkMod3() 
~double k:
requires this#k StateMultipleOf3()
ensures this#k StateMultipleOf3()
{ 
	return (this.cell.getValueInt() % 3 == 0); 
} 

boolean checkMod2()
~double k:
requires this#k StateMultipleOf2()
ensures this#k StateMultipleOf2()
{ 
	return (this.cell.getValueInt() % 2 == 0); 
} 
}