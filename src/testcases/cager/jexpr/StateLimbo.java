package testcases.cager.jexpr;

class StateLimbo implements Statelike { 
	
	IntCell cell;
	
	predicate BasicFields() = exists c:IntCell : this.cell ->c
	
	predicate StateMultipleOf3() = exists c:IntCell : this.cell -> c && c#1 MultipleOf21() 
	predicate StateMultipleOf2() = exists c:IntCell : this.cell -> c && c#1 MultipleOf16()
	
	/*
	StateLimbo(IntCell ic)
	{
		this.cell = ic;
	}
	*/

IntCell computeResult(StateContext context, int num)
requires (this#1.0 BasicFields()) && (context#1.0 BasicFieldsContext())
ensures (this#1.0 StateMultipleOf3()) && (context#1.0 stateSleep())
{ 
	StateLike s = new StateSleep()[];
	context.setState(s); 
	this.cell.setValue(num*21); 
	return this.cell; 
}  

IntCell computeResult2(StateContext context, int num) 
requires (this#1.0 BasicFields()) && (context#1.0 BasicFieldsContext())
ensures (this#1.0 StateMultipleOf2()) && (context#1.0 stateLive() )
{ 
	StateLike s = new StateLive()[];
	context.setState(s); 
	this.cell.setValue(num*16); 
	return this.cell; 
}   

boolean checkMod3()
requires this#1.0 StateMultipleOf3()
ensures this#1.0 StateMultipleOf3()
{ 
	return (this.cell.getValueInt() % 21 == 0); 
}  

boolean checkMod2() 
requires this#1.0 StateMultipleOf2()
ensures this#1.0 StateMultipleOf2()
{ 
	return (this.cell.getValueInt() % 16 == 0); 
} 
}
