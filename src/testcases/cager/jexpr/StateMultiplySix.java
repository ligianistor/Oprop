package testcases.cager.jexpr;

class StateMultiplySix implements Statelike { 
	
	IntCell cell;
	
	predicate BasicFields() = this.cell ->c

	predicate StateMultipleOf6() = this.cell -> c && c#1 MultipleOf6() 
	predicate StateMultipleOf3() = this.cell -> c && c#1 MultipleOf3() 
	predicate StateMultipleOf2() = this.cell -> c && c#1 MultipleOf2() 

IntCell computeResult(StateContext context, int num) 
requires this#1.0 BasicFields()
ensures this#1.0 StateMultipleOf6() && context#1.0 state18() 
{ 
	context.setState(new StateMultiplyEighteen()[]); 
	this.cell.setValue(num*6); 
	return this.cell; 
} 
 
IntCell computeResult2(StateContext context, int num) 
requires this#1.0 BasicFields()
ensures this#1.0 StateMultipleOf6() && context#1.0 state12()
{ 
	context.setState(new StateMultiplyTwelve()[]); 
	this.cell.setValue(num*6); 
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