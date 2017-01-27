package testcases.cager.jexpr;

class StateLive implements Statelike { 
	
	IntCell cell;
	
	predicate BasicFields() = exists c:IntCell : this.cell -> c

	predicate StateMultipleOf3() = exists c:IntCell : this.cell -> c && (c#1.0 MultipleOf15())
	predicate StateMultipleOf2() = exists c:IntCell : this.cell -> c && (c#1.0 MultipleOf14()) 
	
StateLive() 
{
		IntCell temp = new IntCell(0);
		this.cell = temp; 		
}
	
StateLive(IntCell c) 
{
		this.cell = c; 		
}
	
Statelike computeResult(StateContext context, int num) 
requires (this#1.0 BasicFields()) && (context#1.0 BasicFieldsContext())
ensures (context#1.0 stateContextMultiple3()) && (context#1.0 stateLimbo())
{ 
	StateLike s = new StateLimbo()();
	context.setState(s); 
	s.cell.setValue(num*15); 
	return s;
} 
 
IntCell computeResult2(StateContext context, int num) 
requires (this#1.0 BasicFields()) && (context#1.0 BasicFieldsContext())
ensures (this#1.0 StateMultipleOf2()) && (context#1.0 stateSleep())
{ 
	StateLike s = new StateSleep()();
	context.setState(s); 
	this.cell.setValue(num*14); 
	return this.cell; 
} 

boolean checkMod3() 
requires this#1.0 StateMultipleOf3()
ensures this#1.0 StateMultipleOf3()
{ 
	return (this.cell.getValueInt() % 3 == 0); 
} 

boolean checkMod2()
requires this#1.0 StateMultipleOf2()
ensures this#1.0 StateMultipleOf2()
{ 
	return (this.cell.getValueInt() % 2 == 0); 
} 
}