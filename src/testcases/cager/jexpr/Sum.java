package testcases.cager.jexpr;

interface Sum {
predicate sumOK(); 
predicate sumGreater0();

void sumConstr(int n1);
int calculateSum();
boolean sumIsOK();
boolean sumIsGreater0();
}