package testcases.cager.jexpr;

class RealSum implements Sum {
	
	// in both implementations, all predicates need to have the
	// same number and type of existential variables
	predicate sumOK() = exists s1:int, n1:int :: sum -> s1 && n -> n1 &&
			( s1 == n1 * (n1+1) /2 )
	predicate sumGreater0() = exists s1:int :: sum -> s1 && s1 > 0;

	int n;
	int sum;

RealSum(int n1) {
	n = n1; 
	calculateRealSum(n);
}

void calculateRealSum(int n1) {
	this.sum = n1 * (n1 + 1) / 2;
}

int calculateSum() { 
	return this.sum;
}

boolean sumIsOK() {
	return (this.sum == (n * (n+1) / 2));
}

boolean sumIsGreater0() {
	return (this.sum > 0);
}
}