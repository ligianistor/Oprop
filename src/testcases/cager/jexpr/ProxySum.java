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
~double k1, k2:
requires unpacked(this#k1 basicFields()) && (this.n > 0)
requires (this.realSum != null) ~=> (this.realSum#k2 sumOK()[this.n])
ensures this#k1 sumOK()
{
	if (this.realSum == null) {
		this.realSum = new RealSum(sumOK()[this.n])(this.n);
	} 
	this.sum = this.realSum.calculateSum(this.n);
    transfer(this#k1 sumOK(), this#k1 basicFields());
    pack(this#k1 sumOK());
	return this.sum; 
}

void addOneToSum(int n1)
~double k1, k2:
requires unpacked(this#k1 basicFields()) && (this.n > 0)
requires (this.realSum != null) ~=> (this.realSum#k2 basicFields()[this.n])
ensures this#k1 sumGreater0()
{
		if (this.realSum == null) {
			this.realSum = new RealSum(sumOK()[this.n])(this.n);
			transfer(this.realSum#1.0 basicFields(), this.realSum#1.0 sumOK())
		} 
		this.sum = this.realSum.addOneToSum(this.n);
	    transfer(this#k1 sumGreater0(), this#k1 basicFields());
	    pack(this#k1 sumGreater0());
		return this.sum; 
}

boolean sumIsOK()
~double k:
requires this#k sumOK()
ensures this#k sumOK()
{
	unpack(this#k sumOK())[this.n];
	return (this.sum == (this.n * (this.n + 1) / 2));
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