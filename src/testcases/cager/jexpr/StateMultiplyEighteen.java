package testcases.cager.jexpr;

class StateMultiplyEighteen implements Statelike {
	
	IntCell cell;
	
predicate BasicFieldsState() = this.cell ->c
	
predicate StateMultipleOf18() = this.cell ->c && c#1 MultipleOf18() 
predicate StateMultipleOf3() = this.cell -> c && c#1 MultipleOf3() 
predicate StateMultipleOf2() = this.cell -> c && c#1 MultipleOf2() 

//predicate StateMultipleOf3() = this.cell -> c && c#1 MultipleOf18() 
//predicate StateMultipleOf2() = this.cell -> c && c#1 MultipleOf18() 
// StateMultipleOf3 and StateMultipleOf2 have different implementations in
// each implementing class.

/*
StateMultiplyEighteen(IntCell ic)
{
	this.cell = ic;
}
this is commented out because it will be generated automatically 
in the boogie files
*/

IntCell computeResult(StateContext context, int num) 
requires (this#1.0 BasicFieldsState()) && (context#1.0 BasicFieldsContext())
ensures (this#1.0 StateMultipleOf18()) && (context#1.0 state6())
{ 
	this.cell.setValue(num*18); 
	context.setState(new StateMultiplySix()[]); 
	return this.cell; 
}  
 
IntCell computeResult2(StateContext context, int num) 
requires (this#1.0 BasicFieldsState()) && (context#1.0 BasicFieldsContext())
ensures (this#1.0 StateMultipleOf18()) && (context#1.0 state12())
{ 
	this.cell.setValue(num*18); 
	context.setState(new StateMultiplyTwelve()[]); 
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