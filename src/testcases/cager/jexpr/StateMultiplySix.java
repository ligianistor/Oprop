package testcases.cager.jexpr;

class StateMultiplySix implements Statelike { 
predicate StateMultipleOf6() = cell -> c && c@1 MultipleOf6() 
predicate StateMultipleOf3() = cell -> c && c@1 MultipleOf3() 
predicate StateMultipleOf2() = cell -> c && c@1 MultipleOf2() 

--> this@1 StateMultipleOf6() && context@1 state18() 
public Integer computeResult(final StateContext context, final int num) { 
context.setState(new StateMultiplyEighteen()); 
cell.setValue(num*6); 
return cell.getValueInteger(); 
} 

--> this@1 StateMultipleOf6() && context@1 state12() 
public Integer computeResult2(final StateContext context, final int num) { 
context.setState(new StateMultiplyTwelve()); 
cell.setValue(num*6); 
return cell.getValueInteger(); 
} 

boolean checkMod3() { 
return (cell.getValueInt() % 3 == 0); 
} 
boolean checkMod2() { 
return (cell.getValueInt() % 2 == 0); 
} 
}