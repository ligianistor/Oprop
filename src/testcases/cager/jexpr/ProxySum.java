package testcases.cager.jexpr;

class ProxySum implements Sum {
	
	predicate basicFields() = exists s1:double, n1:int :: this.sum -> s1 && this.n -> n1
	
	predicate sumOK() = exists s1:double, n1:int :: this.sum -> s1 && this.n -> n1 &&
			( s1 == n1 * (n1 + 1) / 2 )
	predicate sumGreater0() = exists s1:double :: this.sum -> s1 && s1 > 0

	RealSum realSum = null;
	double sum;
	int n;

/*
ProxySum() {
	sum = 0;
	n = 0;	
}
 */
	
void sumConstr(int n1) { 
	this.n = n1; 
	calculateSum();
}

double calculateSum()
requires this#1.0 basicFields()
ensures this#1.0 sumOK()
{
	if (realSum == null) {
		realSum = new RealSum(this.n);
	} 
	this.sum = realSum.calculateSum();
	return this.sum; 
}

boolean sumIsOK()
requires this#1.0 sumOK()
ensures this#1.0 sumOK()
{
	return (this.sum == (this.n * (this.n + 1) / 2));
}

boolean sumIsGreater0()
requires this#1.0 sumGreater0()
ensures this#1.0 sumGreater0()
{
	return (this.sum > 0);
}
}