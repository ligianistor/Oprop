package testcases.cager.jexpr;

class StateMultiplyEighteen implements Statelike { 
predicate StateMultipleOf18() = cell ->c && c@1 MultipleOf18() 
predicate StateMultipleOf3() = cell -> c && c@1 MultipleOf3() 
predicate StateMultipleOf2() = cell -> c && c@1 MultipleOf2() 

private int count = 0; 

--> this@1 StateMultipleOf18() && context@1 state6() 
public Integer computeResult(final StateContext context, final int num) { 
cell.setValue(num*18); 
context.setState(new StateMultiplySix()); 
return cell.getValueInteger(); 
}  

--> this@1 StateMultipleOf18() && context@1 state12() 
public Integer computeResult2(final StateContext context, final int num) { 
cell.setValue(num*18); 
context.setState(new StateMultiplyTwelve()); 
return cell.getValueInteger(); 
}  

boolean checkMod3() { 
return (cell.getValueInt() % 3 == 0); 
}  
boolean checkMod2() { 
return (cell.getValueInt() % 2 == 0); 
} 
}}