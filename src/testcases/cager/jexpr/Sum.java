package testcases.cager.jexpr;

interface Sum {
	
predicate basicFields();
predicate sumOK(); 
predicate sumGreater0();

double calculateSum(int n1);
~double k:
requires this#k basicFields()
ensures this#k sumOK()

double addOneToSum(int n1);
~double k:
requires this#k basicFields()
ensures this#k sumGreater0()

boolean sumIsOK();
~double k:
requires this#k sumOK()
ensures this#k sumOK()

boolean sumIsGreater0();
~double k:
requires this#k sumGreater0()
ensures this#k sumGreater0()

}