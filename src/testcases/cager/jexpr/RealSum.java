package testcases.cager.jexpr;

class RealSum implements Sum {
	
	// in both implementations, all predicates need to have the
	// same number and type of existential variables
	
	predicate basicFields() = exists s1:double, n1:int :: this.sum -> s1 && this.n -> n1
			
	predicate sumOK() = exists s1:double, n1:int :: this.sum -> s1 && this.n -> n1 &&
			( s1 == n1 * (n1+1) /2 )
	predicate sumGreater0() = exists s1:double :: this.sum -> s1 && s1 > 0

	int n;
	double sum;
	
	/*
	RealSum() {
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
	return calculateRealSum(this.n);
}

double calculateRealSum(int n1) { 
	if (n1 == 0) {
		this.sum := 0;
		return this.sum;
	} else {
		this.sum := n1 + calculateRealSum(n1-1);
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
ensures this#1.0 sumGreater0(){
	return (this.sum > 0);
}
}