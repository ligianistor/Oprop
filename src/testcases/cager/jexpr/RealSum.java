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
~double k:
requires this#k basicFields()
ensures this#k sumGreater0()
ensures result > 0
{
	this.n = n1;
	double temp = this.calculateRealSum(this.n);
	this.sum = temp+1;
	//transfer(toObjectProp, fromObjectProp);
	transfer(this#k sumGreater0(), this#k sumOK());
	pack(this#k sumGreater0())[this.sum];
	return this.sum;
}

double calculateSum() 
~double k:
requires this#k sumOK()
ensures unpacked(this#k sumOK())
ensures result == (this.n*(this.n+1)/2)
{
	unpack(this#k sumOK())[this.n]
	return this.sum;
}

double calculateRealSum(int n1)
~double k:
requires this#k basicFields()
ensures unpacked(this#k sumOK())
ensures this.sum == this.n * (this.n + 1) / 2;
ensures result > 0
{ 
	unpack(this#k basicFields())[this.sum, this.n];
	this.sum = (n1 * (n1+1) / 2);
	return this.sum;
	transfer(this#k sumOK(), this#k basicFields());
}

boolean sumIsOK() 
~double k:
requires this#k sumOK()
ensures this#k sumOK()
{
	unpack(this#k sumOK())[this.n];
	return (this.sum == (this.n * (this.n+1) / 2));
	pack(this#k sumOK())[this.n];
}

boolean sumIsGreater0()
~double k:
requires this#k sumGreater0()
ensures this#k sumGreater0()
{
	unpack(this#k sumGreater0())[this.sum];
	return (this.sum > 0);
	pack(this#k sumGreater0())[this.sum];
}
}