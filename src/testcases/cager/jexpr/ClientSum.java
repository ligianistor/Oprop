package testcases.cager.jexpr;

class ClientSum {
Sum sum;
ClientSum(Sum sum1) { sum = sum1; }
	

boolean checkSumIsOK() 
requires s#1.0 sumOK() 
{
	return sum.sumIsOK();
}

boolean checkSumGreater0() 
requires s#1.0 sumGreater0()
{
	return sum.sumIsGreater0();
}

void main() {
	Sum s = new ProxySum(sumOK()[])(5);
	ClientSum client1 = new ClientSum(s);
	ClientSum client2 = new ClientSum(s);
	s.calculateSum();
	client1.checkSumIsOK();
	s.calculateSum();
	client2.checkSumIsOK();

	Sum s2 = new Sum(sumGreater0()[])(7);
	ClientSum client3 = new ClientSum(s);
	ClientSum client4 = new ClientSum(s);
	s.calculateSum();
	client3.checkSumGreater0();
	s.calculateSum();
	client4.checkSumGreater0();
}
}