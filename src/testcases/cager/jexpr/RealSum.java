package testcases.cager.jexpr;

class RealSum implements Sum {
  int n;
  double sum;
  
  predicate BasicFields() = exists double s1, int n1 : 
      this.sum -> s1 && this.n -> n1 && n1>0
  predicate SumOK() = exists int n1 : 
      this.n -> n1 && n1>0 && this.sum -> (n1 * (n1+1) / 2)
  predicate SumGreater0() = exists double s1 : this.sum -> s1 && s1 > 0

  RealSum(int n1)
    requires n1 > 0
    ensures this.n == n1;
    ensures this.sum == (n1*(n1+1)/2);
  {
    this.n = n1;
    this.sum = 0;
    pack(this#1 BasicFields())[0, n1];
    this.calculateRealSum(n1);
  }
	
  double addOneToSum(int n1)
  ~double k:
    requires this#k BasicFields()
    ensures this#k SumGreater0()
    ensures result > 0
  {
    this.n = n1;
    double temp = this.calculateRealSum(this.n);
    this.sum = temp+1;
    transfer(this#k SumGreater0(), this#k SumOK());
    pack(this#k SumGreater0())[this.sum];
    return this.sum;
  }

  double calculateSum() 
  ~double k:
    requires this#k SumOK()
    ensures unpacked(this#k SumOK())
    ensures result == (this.n*(this.n+1)/2)
  {
    unpack(this#k SumOK())[this.n]
    return this.sum;
  }

  double calculateRealSum(int n1)
  ~double k:
    requires this#k BasicFields()
    ensures unpacked(this#k SumOK())
    ensures this.sum == this.n * (this.n + 1) / 2;
    ensures result > 0
  { 
    unpack(this#k BasicFields())[this.sum, this.n];
    this.sum = (n1 * (n1+1) / 2);
    return this.sum;
    transfer(this#k SumOK(), this#k BasicFields());
  }

  boolean sumIsOK() 
  ~double k:
    requires this#k SumOK()
    ensures this#k SumOK()
  {
    unpack(this#k SumOK())[this.n];
    return (this.sum == (this.n * (this.n+1) / 2));
    pack(this#k SumOK())[this.n];
  }

  boolean sumIsGreater0()
  ~double k:
    requires this#k SumGreater0()
    ensures this#k SumGreater0()
  {
    unpack(this#k SumGreater0())[this.sum];
    return (this.sum > 0);
    pack(this#k SumGreater0())[this.sum];
  }
}