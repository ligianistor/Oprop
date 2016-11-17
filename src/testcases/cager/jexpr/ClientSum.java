package testcases.cager.jexpr;

class ClientSum {
Sum sumClient;

predicate clientSumOK() =
	exists k:real, s:Sum :: sumClient->s && s#k sumOK() 
	
predicate clientSumGreater0() =
	exists k:real, s:Sum :: sumClient->s && s#k sumGreater0() 

ClientSum(Sum sum1) { sumClient = sum1; }
	

boolean checkSumIsOK() 
requires this#1.0 clientSumOK()
{
	return sumClient.sumIsOK();
}

boolean checkSumGreater0() 
requires this#1.0 clientSumGreater0()
{
	return sumClient.sumIsGreater0();
}

void main() {
	Sum s = new ProxySum(sumOK()[])();
	s.sumConstr(5);
	ClientSum client1 = new ClientSum(clientSumOK()[])(s);
	ClientSum client2 = new ClientSum(clientSumOK()[])(s);
	s.calculateSum();
	client1.checkSumIsOK();
	s.calculateSum();
	client2.checkSumIsOK();

	Sum s2 = new ProxySum(sumGreater0()[])();
	s2.sumConstr(7);
	ClientSum client3 = new ClientSum(clientSumGreater0()[])(s2);
	ClientSum client4 = new ClientSum(clientSumGreater0()[])(s2);
	s2.calculateSum();
	client3.checkSumGreater0();
	s2.calculateSum();
	client4.checkSumGreater0();
}
}