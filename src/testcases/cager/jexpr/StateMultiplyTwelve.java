package testcases.cager.jexpr;

class StateMultiplyTwelve implements Statelike { 

	predicate StateMultipleOf12() = cell -> c && c#1 MultipleOf12() 
	predicate StateMultipleOf3() = cell -> c && c#1 MultipleOf3() 
	predicate StateMultipleOf2() = cell -> c && c#1 MultipleOf2() 

IntCell computeResult(StateContext context, int num) 
ensures this#1.0 StateMultipleOf12() && context#1.0 state18() 
{ 
	context.setState(new StateMultiplyEighteen()); 
	cell.setValue(num*12); 
	return cell; 
}  

IntCell computeResult2(StateContext context, int num) 
ensures this#1.0 StateMultipleOf12() && context#1.0 state6() 
{ 
	context.setState(new StateMultiplySix()); 
	cell.setValue(num*12); 
	return cell; 
}   

boolean checkMod3() { 
	return (cell.getValueInt() % 3 == 0); 
}  

boolean checkMod2() { 
	return (cell.getValueInt() % 2 == 0); 
} 
}
