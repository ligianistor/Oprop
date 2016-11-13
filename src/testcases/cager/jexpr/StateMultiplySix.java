package testcases.cager.jexpr;

class StateMultiplySix implements Statelike { 
/*
	predicate StateMultipleOf6() = cell -> c && c#1 MultipleOf6() 
	predicate StateMultipleOf3() = cell -> c && c#1 MultipleOf3() 
	predicate StateMultipleOf2() = cell -> c && c#1 MultipleOf2() 
*/
public IntCell computeResult(StateContext context, int num) 
//ensures this#1 StateMultipleOf6() && context#1 state18() 
{ 
	context.setState(new StateMultiplyEighteen()); 
	cell.setValue(num*6); 
	return cell; 
} 
 
public IntCell computeResult2(StateContext context, int num) 
//ensures this#1 StateMultipleOf6() && context#1 state12()
{ 
	context.setState(new StateMultiplyTwelve()); 
	cell.setValue(num*6); 
	return cell; 
} 

public boolean checkMod3() { 
	return (cell.getValueInt() % 3 == 0); 
} 
public boolean checkMod2() { 
	return (cell.getValueInt() % 2 == 0); 
} 
}