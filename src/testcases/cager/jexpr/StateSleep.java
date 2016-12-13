package testcases.cager.jexpr;

class StateSleep implements Statelike {
	
	IntCell cell;
	
predicate BasicFields() = exists c:IntCell : this.cell ->c

predicate StateMultipleOf3() = exists c:IntCell : this.cell -> c && c#1 MultipleOf33() 
predicate StateMultipleOf2() = exists c:IntCell : this.cell -> c && c#1 MultipleOf4() 

IntCell computeResult(StateContext context, int num) 
requires (this#1.0 BasicFields()) && (context#1.0 BasicFieldsContext())
ensures (this#1.0 StateMultipleOf3()) && (context#1.0 stateLive())
{ 
	StateLike s = new StateLive()[];
	this.cell.setValue(num*33); 
	context.setState(s); 
	return this.cell; 
}  
 
IntCell computeResult2(StateContext context, int num) 
requires (this#1.0 BasicFields()) && (context#1.0 BasicFieldsContext())
ensures (this#1.0 StateMultipleOf2()) && (context#1.0 stateLimbo())
{ 
	StateLike s = new StateLimbo()[];
	this.cell.setValue(num*4); 
	context.setState(s); 
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