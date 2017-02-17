package testcases.cager.jexpr;

class ProxySum implements Sum {
  RealSum realSum;
  double sum;
  int n;
	
  predicate BasicFields() = exists RealSum rs, double s1, int n1 : 
      this.realSum -> rs && this.sum -> s1 && this.n -> n1 && n1>0
  predicate SumOK() = exists int n1 : this.n -> n1 &&
      n1>0 && this.sum -> (n1 * (n1+1) / 2)
  predicate SumGreater0() = exists double s1 : this.sum -> s1 && s1 > 0

  ProxySum(int n1)
    ensures this.n == n1;
    ensures this.sum == 0;
    ensures this.realSum == null;
  {
    this.n = n1;
    this.sum = 0;
    this.realSum = null;
  }

  double calculateSum()
  ~double k1, k2:
    requires unpacked(this#k1 BasicFields()) && (this.n > 0)
    requires (this.realSum != null) ~=> (this.realSum#k2 SumOK()[this.n])
    ensures this#k1 SumOK()
  {
    if (this.realSum == null) {
      this.realSum = new RealSum(SumOK()[this.n])(this.n);
    } 
    this.sum = this.realSum.calculateSum(this.n);
    transfer(this#k1 SumOK(), this#k1 BasicFields());
    pack(this#k1 SumOK());
    return this.sum; 
  }

  void addOneToSum(int n1)
  ~double k1, k2:
    requires unpacked(this#k1 BasicFields()) && (this.n > 0)
    requires (this.realSum != null) ~=> (this.realSum#k2 BasicFields()[this.n])
    ensures this#k1 SumGreater0()
  {
    if (this.realSum == null) {
      this.realSum = new RealSum(SumOK()[this.n])(this.n);
      transfer(this.realSum#1.0 BasicFields(), this.realSum#1.0 SumOK())
    } 
    this.sum = this.realSum.addOneToSum(this.n);
    transfer(this#k1 SumGreater0(), this#k1 BasicFields());
    pack(this#k1 SumGreater0());
    return this.sum; 
  }

  boolean sumIsOK()
  ~double k:
    requires this#k SumOK()
    ensures this#k SumOK()
  {
    unpack(this#k SumOK())[this.n];
    return (this.sum == (this.n * (this.n + 1) / 2));
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