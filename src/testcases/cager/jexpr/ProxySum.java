package testcases.cager.jexpr;

class ProxySum implements Sum {
	
	predicate basicFields() = exists RealSum rs, double s1, int n1 :: 
					this.realSum -> rs && this.sum -> s1 && this.n -> n1 && n1>0
			
	predicate sumOK() = exists int n1 :: this.n -> n1 && n1>0 && this.sum -> (n1 * (n1+1) / 2)
			
	predicate sumGreater0() = exists double s1 :: this.sum -> s1 && s1 > 0

	RealSum realSum;
	double sum;
	int n;
	
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
~double k:
requires this#k basicFields()
ensures this#k sumOK()
{
	if (this.realSum == null) {
		this.realSum = new RealSum(sumOK()[this.n])(this.n);
	} 
	this.sum = this.realSum.calculateSum(this.n);
	return this.sum; 
}

void addOneToSum(int n1)
~double k:
requires this#1.0 basicFields()
ensures this#1.0 sumGreater0()
{
	this.n = n1;
	
	if (this.realSum == null) {
		this.realSum = new RealSum(sumOK()[this.n])(this.n);
	} 
	this.realSum.addOneToSum(this.n) ;
}

boolean sumIsOK()
~double k:
requires this#k sumOK()
ensures this#k sumOK()
{
	return (this.sum == (this.n * (this.n + 1) / 2));
}

boolean sumIsGreater0()
~double k:
requires this#k sumGreater0()
ensures this#k sumGreater0()
{
	return (this.sum > 0);
}
}