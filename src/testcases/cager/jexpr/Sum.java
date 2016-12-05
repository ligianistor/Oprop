package testcases.cager.jexpr;

interface Sum {
	
predicate basicFields();
predicate sumOK(); 
predicate sumGreater0();

double calculateSum(int n1);
requires this#1.0 basicFields()
ensures this#1.0 sumOK()

boolean sumIsOK();
requires this#1.0 sumOK()
ensures this#1.0 sumOK()

boolean sumIsGreater0();
requires this#1.0 sumGreater0()
ensures this#1.0 sumGreater0()

}