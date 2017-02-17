package testcases.cager.jexpr;

interface Sum {
	
  predicate BasicFields();
  predicate SumOK(); 
  predicate SumGreater0();

  double calculateSum(int n1);
  ~double k:
    requires this#k BasicFields()
    ensures this#k SumOK()

  double addOneToSum(int n1);
  ~double k:
    requires this#k BasicFields()
    ensures this#k SumGreater0()

  boolean sumIsOK();
  ~double k:
    requires this#k SumOK()
    ensures this#k SumOK()

  boolean sumIsGreater0();
  ~double k:
    requires this#k SumGreater0()
    ensures this#k SumGreater0()
}