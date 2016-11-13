package testcases.cager.jexpr;

class ProxySum implements Sum {
predicate sumOK() = exists s1:int, n1:int :: sum -> s1 && n -> n1 &&
( s1 == n1 * (n1 + 1) / 2 )
predicate sumGreater0() = exists s1:int :: sum -> s1 && s1 > 0;

private RealSum realSum = null;
private int sum = 0;
private int n = 0;
 
public ProxySum(int n1) { 
this.n = n1; 
}

public int calculateSum() {
if (realSum == null) {
realSum = new RealSum(n);
} 
sum = realSum.calculateSum();
return sum; 
}

public boolean sumIsOK() {
return (sum == (n * (n + 1) / 2));
}

public boolean sumIsGreater0() {
return (sum > 0);
}
}