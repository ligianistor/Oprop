package testcases.cager.jexpr;

class StateMultiplyTwelve implements Statelike { 
	
	IntCell cell;
	
	predicate BasicFieldsState() = this.cell ->c

	//predicate StateMultipleOf12() = this.cell -> c && c#1 MultipleOf12() 
	//predicate StateMultipleOf3() = this.cell -> c && c#1 MultipleOf3() 
	//predicate StateMultipleOf2() = this.cell -> c && c#1 MultipleOf2()
	
	predicate StateMultipleOf3() = this.cell -> c && c#1 MultipleOf21() 
	predicate StateMultipleOf2() = this.cell -> c && c#1 MultipleOf16()
	
	/*
	StateMultiplyTwelve(IntCell ic)
	{
		this.cell = ic;
	}
	*/

IntCell computeResult(StateContext context, int num)
requires (this#1.0 BasicFieldsState()) && (context#1.0 BasicFieldsContext())
ensures (this#1.0 StateMultipleOf3()) && (context#1.0 stateSleep())
{ 
	context.setState(new StateSleep()[]); 
	this.cell.setValue(num*21); 
	return this.cell; 
}  

IntCell computeResult2(StateContext context, int num) 
requires (this#1.0 BasicFieldsState()) && (context#1.0 BasicFieldsContext())
ensures (this#1.0 StateMultipleOf2()) && (context#1.0 stateLive() )
{ 
	context.setState(new StateLive()[]); 
	this.cell.setValue(num*16); 
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
