package testcases.cager.jexpr;

class RealSum implements Sum {
	
	// this predicate does not have to satisfy any special properties
	predicate basicFields() = exists s1:double, n1:int :: this.sum -> s1 && this.n -> n1
			
	predicate sumOK() = exists s1:double, n1:int :: this.n -> n1 && this.sum -> (n1 * (n1+1) /2)
	
	predicate sumGreater0() = exists s1:double :: this.sum -> s1 && s1 > 0

	int n;
	double sum;
	
RealSum(int n1)
{
	this.n = n1;
	this.calculateRealSum(n1);
}
	
void addOneToSum(int n1)
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
{ /*
	if (n1 == 0) {
		this.sum = 0;
		return this.sum;
	} else {
		this.sum = n1 + this.calculateRealSum(n1-1);
		return this.sum;
	}
	*/ 
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