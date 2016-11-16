package testcases.cager.jexpr;

class ClientSum {
Sum sumClient;
ClientSum(Sum sum1) { sumClient = sum1; }
	

boolean checkSumIsOK() 
requires sumClient#1.0 sumOK() 
{
	return sumClient.sumIsOK();
}

boolean checkSumGreater0() 
requires sumClient#1.0 sumGreater0()
{
	return sumClient.sumIsGreater0();
}

void main() {
	Sum s = new ProxySum(sumOK()[])(5);
	ClientSum client1 = new ClientSum(s);
	ClientSum client2 = new ClientSum(s);
	s.calculateSum();
	client1.checkSumIsOK();
	s.calculateSum();
	client2.checkSumIsOK();

	Sum s2 = new ProxySum(sumGreater0()[])(7);
	ClientSum client3 = new ClientSum(s2);
	ClientSum client4 = new ClientSum(s2);
	s2.calculateSum();
	client3.checkSumGreater0();
	s2.calculateSum();
	client4.checkSumGreater0();
}
}