package testcases.cager.jexpr;

class StateMultiplyEighteen implements Statelike {
	
	IntCell cell;
	
predicate StateMultipleOf18() = cell ->c && c#1 MultipleOf18() 
predicate StateMultipleOf3() = cell -> c && c#1 MultipleOf3() 
predicate StateMultipleOf2() = cell -> c && c#1 MultipleOf2() 

IntCell computeResult(StateContext context, int num) 
ensures this#1.0 StateMultipleOf18() && context#1.0 state6() 
{ 
	cell.setValue(num*18); 
	context.setState(new StateMultiplySix()); 
	return cell; 
}  
 
IntCell computeResult2(StateContext context, int num) 
ensures this#1.0 StateMultipleOf18() && context#1.0 state12()
{ 
	cell.setValue(num*18); 
	context.setState(new StateMultiplyTwelve()); 
	return cell; 
}  

boolean checkMod3() { 
	return (cell.getValueInt() % 3 == 0); 
}  
boolean checkMod2() { 
	return (cell.getValueInt() % 2 == 0); 
} 
}