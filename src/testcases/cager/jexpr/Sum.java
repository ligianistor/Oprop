package testcases.cager.jexpr;

interface Sum {
	
predicate basicFields();
predicate sumOK(); 
predicate sumGreater0();
//might need to add another predicate that simply gives access to the fields

void sumConstr(int n1);

double calculateSum();
requires this#1.0 basicFields()
ensures this#1.0 sumOK()

boolean sumIsOK();
requires this#1.0 sumOK()
ensures this#1.0 sumOK()

boolean sumIsGreater0();
requires this#1.0 sumGreater0()
ensures this#1.0 sumGreater0()

}