package testcases.cager.jexpr;

class StateMultiplyTwelve implements Statelike { 
	/*
	predicate StateMultipleOf12() = cell -> c && c#1 MultipleOf12() 
	predicate StateMultipleOf3() = cell -> c && c#1 MultipleOf3() 
	predicate StateMultipleOf2() = cell -> c && c#1 MultipleOf2() 
*/
public IntCell computeResult(final StateContext context, final int num) 
//ensures this#1 StateMultipleOf12() && context#1 state18() 
{ 
	context.setState(new StateMultiplyEighteen()); 
	cell.setValue(num*12); 
	return cell; 
}  

public IntCell computeResult2(final StateContext context, final int num) 
//ensures this#1 StateMultipleOf12() && context#1 state6() 
{ 
	context.setState(new StateMultiplySix()); 
	cell.setValue(num*12); 
	return cell; 
}   

public boolean checkMod3() { 
	return (cell.getValueInt() % 3 == 0); 
}  

public boolean checkMod2() { 
	return (cell.getValueInt() % 2 == 0); 
} 
}
