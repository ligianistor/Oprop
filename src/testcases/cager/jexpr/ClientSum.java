package testcases.cager.jexpr;

class ClientSum {
Sum sumClient;

predicate clientSumOK() =
	exists double k, Sum s :: this.sumClient->s && (s#k sumOK()) 
	
predicate clientSumGreater0() =
	exists double k, Sum s :: this.sumClient->s && (s#k sumGreater0()) 

ClientSum(Sum sum1) 
ensures (this.sumClient == sum1);
{ 
	this.sumClient = sum1; 
}
	
boolean checkSumIsOK() 
~double k1, k2:
requires unpacked(this#k1 clientSumOK())
requires (this.sumClient#k2 sumOK())
ensures unpacked(this#k1 clientSumOK())
ensures (this.sumClient#k2 sumOK())
{
	return this.sumClient.sumIsOK();
}

boolean checkSumGreater0() 
~double k1, k2:
requires unpacked(this#k1 clientSumGreater0())
requires (this.sumClient#k2 sumGreater0())
ensures unpacked(this#k1 clientSumGreater0())
ensures (this.sumClient#k2 sumGreater0())
{
	return this.sumClient.sumIsGreater0();
}

void main1() 
~double k:
{
	Sum s = new ProxySum(basicFields()[5])(5);
	unpack(s#1.0 basicFields())[5];
	s.calculateSum();
	ClientSum client1 = new ClientSum(clientSumOK()[])(s);
	ClientSum client2 = new ClientSum(clientSumOK()[])(s);
	unpack(client1#1 clientSumOK())[s];
	client1.checkSumIsOK();
	transfer(s#k basicFields(), s#k sumOK());
	unpack(s#k basicFields())[5];
	s.calculateSum();
	unpack(client2#1 clientSumOK())[s];
	client2.checkSumIsOK();
}

void main2() 
~double k:
{
	Sum s2 = new ProxySum(basicFields()[7])(7);
	unpack(s2#1.0 basicFields())[7];
	s2.addOneToSum();
	ClientSum client3 = new ClientSum(clientSumGreater0()[])(s2);
	ClientSum client4 = new ClientSum(clientSumGreater0()[])(s2);
	unpack(client3#1 clientSumGreater0())[s2];
	client3.checkSumGreater0();
	transfer(s2#k basicFields(), s2#k sumGreater0());
	unpack(s2#k basicFields())[7];
	s2.addOneToSum(); 
	unpack(client4#1 clientSumGreater0())[s2];
	client4.checkSumGreater0();
}
}