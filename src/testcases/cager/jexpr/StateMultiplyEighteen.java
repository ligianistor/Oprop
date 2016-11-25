package testcases.cager.jexpr;

class StateMultiplyEighteen implements Statelike {
	
	IntCell cell;
	
predicate BasicFields() = this.cell ->c
	
predicate StateMultipleOf18() = this.cell ->c && c#1 MultipleOf18() 
predicate StateMultipleOf3() = this.cell -> c && c#1 MultipleOf3() 
predicate StateMultipleOf2() = this.cell -> c && c#1 MultipleOf2() 

IntCell computeResult(StateContext context, int num) 
requires this#1.0 BasicFields()
ensures this#1.0 StateMultipleOf18() && context#1.0 state6() 
{ 
	this.cell.setValue(num*18); 
	context.setState(new StateMultiplySix()[]); 
	return this.cell; 
}  
 
IntCell computeResult2(StateContext context, int num) 
requires this#1.0 BasicFields()
ensures this#1.0 StateMultipleOf18() && context#1.0 state12()
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
boolean checkMod2() { 
	return (this.cell.getValueInt() % 2 == 0); 
} 
}