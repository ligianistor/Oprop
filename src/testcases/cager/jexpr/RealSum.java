package testcases.cager.jexpr;

class RealSum implements Sum {
	
	predicate basicFields() = exists s1:double, n1:int :: this.sum -> s1 && this.n -> n1
			
	predicate sumOK() = exists s1:double, n1:int :: this.sum -> s1 && this.n -> n1 &&
			( s1 == n1 * (n1+1) /2 )
	predicate sumGreater0() = exists s1:double :: this.sum -> s1 && s1 > 0

	int n;
	double sum;
	
double addOneToSum(int n1)
requires this#1.0 basicFields()
ensures this#1.0 sumGreater0()
{
	this.n = n1;
	double temp = calculateRealSum(this.n);
	this.sum = temp+1;
	return this.sum;
}

double calculateSum(int n1) 
requires this#1.0 basicFields()[]
ensures this#1.0 sumOK()[]
{
	this.n = n1;
	return calculateRealSum(this.n);
}

double calculateRealSum(int n1)
requires this#1.0 basicFields()[]
ensures this#1.0 sumOK()[]
{ 
	if (n1 == 0) {
		this.sum = 0;
		return this.sum;
	} else {
		this.sum = n1 + calculateRealSum(n1-1);
		return this.sum;
	}
}

boolean sumIsOK() 
requires this#1.0 sumOK()
ensures this#1.0 sumOK()
{
	return (this.sum == (this.n * (this.n+1) / 2));
}

boolean sumIsGreater0()
requires this#1.0 sumGreater0()
ensures this#1.0 sumGreater0()
{
	return (this.sum > 0);
}
}