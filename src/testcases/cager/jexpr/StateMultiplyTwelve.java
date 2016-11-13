package testcases.cager.jexpr;

class StateMultiplyTwelve implements Statelike { 
predicate StateMultipleOf12() = cell -> c && c@1 MultipleOf12() 
predicate StateMultipleOf3() = cell -> c && c@1 MultipleOf3() 
predicate StateMultipleOf2() = cell -> c && c@1 MultipleOf2() 

--> this@1 StateMultipleOf12() && context@1 state18() 
public Integer computeResult(final StateContext context, final int num) { 
context.setState(new StateMultiplyEighteen()); 
cell.setValue(num*12); 
return cell.getValueInteger(); 
}  

--> this@1 StateMultipleOf12() && context@1 state6() 
public Integer computeResult2(final StateContext context, final int num) { 
context.setState(new StateMultiplySix()); 
cell.setValue(num*12); 
return cell.getValueInteger(); 
}   

boolean checkMod3() { 
return (cell.getValueInt() % 3 == 0); 
}  
boolean checkMod2() { 
return (cell.getValueInt() % 2 == 0); 
} 
}
