package testcases.cager.jexpr;

interface Sum {
	
predicate basicFields();
predicate sumOK(); 
predicate sumGreater0();

double calculateSum(int n1);
requires this#1.0 basicFields()
ensures this#1.0 sumOK()

double addOneToSum(int n1)
requires this#1.0 basicFields()
ensures this#1.0 sumGreater0()

boolean sumIsOK();
~double k:
requires this#k sumOK()
ensures this#k sumOK()

boolean sumIsGreater0();
~double k:
requires this#k sumGreater0()
ensures this#k sumGreater0()

}