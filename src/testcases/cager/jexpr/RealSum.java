package testcases.cager.jexpr;

class RealSum implements Sum {
	
	predicate basicFields() = exists double s1, int n1 :: this.sum -> s1 && this.n -> n1 && n1>0
			
	predicate sumOK() = exists int n1 :: this.n -> n1 && n1>0 && this.sum -> (n1 * (n1+1) / 2)
	
	predicate sumGreater0() = exists double s1 :: this.sum -> s1 && s1 > 0

	int n;
	double sum;
	
RealSum(int n1)
requires n1 > 0
ensures this.n == n1;
ensures this.sum == (n1*(n1+1)/2);
{
	this.n = n1;
	this.sum = 0;
	pack(this#1 basicFields())[0, n1];
	this.calculateRealSum(n1);
}
	
double addOneToSum(int n1)
requires this#1.0 basicFields()
ensures this#1.0 sumGreater0()
{
	this.n = n1;
	double temp = this.calculateRealSum(this.n);
	this.sum = temp+1;
}

double calculateSum() 
requires this#1.0 basicFields()
ensures this#1.0 sumOK()
{
	return this.sum;
}

double calculateRealSum(int n1)
requires this#1.0 basicFields()
ensures this#1.0 sumOK()
{ 
	this.sum = (n1 * (n1+1) / 2);
	return this.sum;
}

boolean sumIsOK() 
~double k:
requires this#k sumOK()
ensures this#k sumOK()
{
	return (this.sum == (this.n * (this.n+1) / 2));
}

boolean sumIsGreater0()
~double k:
requires this#k sumGreater0()
ensures this#k sumGreater0()
{
	return (this.sum > 0);
}
}