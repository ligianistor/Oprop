package testcases.cager.jexpr;

class ClientSum {
Sum sumClient;

predicate clientSumOK() =
	exists k:real, s:Sum :: this.sumClient->s && (s#k sumOK()) 
	
predicate clientSumGreater0() =
	exists k:real, s:Sum :: this.sumClient->s && (s#k sumGreater0()) 

ClientSum(Sum sum1) { this.sumClient = sum1; }
	
boolean checkSumIsOK() 
~double k:
requires this#k clientSumOK()
ensures this#k clientSumOK()
{
	return sumClient.sumIsOK();
}

boolean checkSumGreater0() 
~double k:
requires this#k clientSumGreater0()
ensures this#k clientSumGreater0()
{
	return sumClient.sumIsGreater0();
}

void main() {
	Sum s = new ProxySum(sumOK()[5])(5);
	ClientSum client1 = new ClientSum(clientSumOK()[])(s);
	ClientSum client2 = new ClientSum(clientSumOK()[])(s);
	s.calculateSum();
	client1.checkSumIsOK();
	s.calculateSum();
	client2.checkSumIsOK();

	Sum s2 = new ProxySum(sumGreater0()[])(0, 0);
	ClientSum client3 = new ClientSum(clientSumGreater0()[])(s2);
	ClientSum client4 = new ClientSum(clientSumGreater0()[])(s2);
	s2.addOneToSum(7);
	client3.checkSumGreater0();
	s2.addOneToSum(7); 
	client4.checkSumGreater0();
}
}