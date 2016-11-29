package testcases.cager.jexpr;

class StateLive implements Statelike { 
	
	IntCell cell;
	
	predicate BasicFieldsState() = this.cell ->c

	//predicate StateMultipleOf6() = this.cell -> c && c#1 MultipleOf6() 
	//predicate StateMultipleOf3() = this.cell -> c && c#1 MultipleOf3() 
	//predicate StateMultipleOf2() = this.cell -> c && c#1 MultipleOf2() 

	predicate StateMultipleOf3() = this.cell -> c && c#1 MultipleOf15() 
	predicate StateMultipleOf2() = this.cell -> c && c#1 MultipleOf14() 
	
	/*
	StateMultiplySix(IntCell ic)
	{
		this.cell = ic;
	}
	*/
IntCell computeResult(StateContext context, int num) 
requires (this#1.0 BasicFieldsState()) && && (context#1.0 BasicFieldsContext())
ensures (this#1.0 StateMultipleOf3()) && (context#1.0 stateLimbo())
{ 
	context.setState(new StateLimbo()[]); 
	this.cell.setValue(num*15); 
	return this.cell; 
} 
 
IntCell computeResult2(StateContext context, int num) 
requires (this#1.0 BasicFieldsState()) && && (context#1.0 BasicFieldsContext())
ensures (this#1.0 StateMultipleOf2()) && (context#1.0 stateSleep())
{ 
	context.setState(new StateSleep()[]); 
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