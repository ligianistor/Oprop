package testcases.cager.jexpr;

class RealSum implements Sum {
	
	predicate sumOK() = exists s1:int, n1:int :: sum -> s1 && n -> n1 &&
			( s1 == n1 * (n1+1) /2 )
			predicate sumGreater0() = exists s1:int :: sum -> s1 && s1 > 0;

	int n;
	int sum = 0;

RealSum(int n1) {
	n = n1; 
	calculateRealSum(n);
}

void calculateRealSum(int n1) {
	this.sum = n1 * (n1 + 1) / 2;
}

int calculateSum() { 
	return sum;
}

boolean sumIsOK() {
	return (sum == (n * (n+1) / 2));
}

boolean sumIsGreater0() {
	return (sum > 0);
}
}