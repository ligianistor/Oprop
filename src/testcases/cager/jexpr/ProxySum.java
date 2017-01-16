package testcases.cager.jexpr;

class ProxySum implements Sum {
	
	predicate basicFields() = exists s1:double, n1:int :: this.sum -> s1 && this.n -> n1
	
	predicate sumOK() = exists n1:int :: this.n -> n1 && this.sum -> (n1 * (n1 + 1) / 2 ) 
	predicate sumGreater0() = exists s1:double :: this.sum -> s1 && s1 > 0

	RealSum realSum = null;
	double sum;
	int n;
	
ProxySum(int n1)
{
	this.n = n1;
	this.sum = 0;
}
	
double calculateSum()
requires this#1.0 basicFields()
ensures this#1.0 sumOK()
{
	if (realSum == null) {
		realSum = new RealSum(sumOK()[this.n])(this.n);
	} 
	this.sum = this.realSum.calculateSum(this.n);
	return this.sum; 
}

double addOneToSum(int n1)
requires this#1.0 basicFields()
ensures this#1.0 sumGreater0()
{
	this.n = n1;
	
	if (realSum == null) {
		realSum = new RealSum(sumOK()[0,0])(0, 0);
	} 
	this.sum = this.realSum.addOneToSum(this.n) ;
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