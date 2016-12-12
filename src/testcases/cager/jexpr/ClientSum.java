package testcases.cager.jexpr;

class ClientSum {
Sum sumClient;

predicate clientSumOK() =
	exists k:real, s:Sum :: this.sumClient->s && s#k sumOK() 
	
predicate clientSumGreater0() =
	exists k:real, s:Sum :: this.sumClient->s && s#k sumGreater0() 

ClientSum(Sum sum1) { this.sumClient = sum1; }
	
boolean checkSumIsOK() 
requires this#1.0 clientSumOK()
ensures this#1.0 clientSumOK()
{
	return sumClient.sumIsOK();
}

boolean checkSumGreater0() 
requires this#1.0 clientSumGreater0()
ensures this#1.0 clientSumGreater0()
{
	return sumClient.sumIsGreater0();
}

void main() {
	Sum s = new ProxySum(sumOK()[])(0, 0);
	ClientSum client1 = new ClientSum(clientSumOK()[])(s);
	ClientSum client2 = new ClientSum(clientSumOK()[])(s);
	s.calculateSum(5);
	client1.checkSumIsOK();
	s.calculateSum(5);
	client2.checkSumIsOK();

	Sum s2 = new ProxySum(sumGreater0()[])(0, 0);
	ClientSum client3 = new ClientSum(clientSumGreater0()[])(s2);
	ClientSum client4 = new ClientSum(clientSumGreater0()[])(s2);
	s2.calculateSum(7);
	client3.checkSumGreater0();
	s2.addOneToSum(7); // takes the value that is in the proxy
	// if it's there, otherwise if this is the first time
	//it's called, calculates sum
	client4.checkSumGreater0();
}
}