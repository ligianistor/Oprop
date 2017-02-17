package testcases.cager.jexpr;

class ClientSum {
  Sum sumClient;

  predicate ClientSumOK() =
      exists double k, Sum s :: this.sumClient->s && (s#k SumOK()) 
	
  predicate ClientSumGreater0() =
      exists double k, Sum s :: this.sumClient->s && (s#k SumGreater0()) 

  ClientSum(Sum sum1) 
    ensures (this.sumClient == sum1);
  { 
    this.sumClient = sum1; 
  }
	
  boolean checkSumIsOK() 
  ~double k1, k2:
    requires unpacked(this#k1 ClientSumOK())
    requires (this.sumClient#k2 SumOK())
    ensures unpacked(this#k1 ClientSumOK())
    ensures (this.sumClient#k2 SumOK())
  {
    return this.sumClient.sumIsOK();
  }

  boolean checkSumGreater0() 
  ~double k1, k2:
    requires unpacked(this#k1 ClientSumGreater0())
    requires (this.sumClient#k2 SumGreater0())
    ensures unpacked(this#k1 ClientSumGreater0())
    ensures (this.sumClient#k2 SumGreater0())
  {
    return this.sumClient.sumIsGreater0();
  }

  void main1() 
  ~double k:
  {
    Sum s = new ProxySum(BasicFields()[5])(5);
    unpack(s#1.0 BasicFields())[5];
    s.calculateSum();
    ClientSum client1 = new ClientSum(ClientSumOK()[])(s);
    ClientSum client2 = new ClientSum(ClientSumOK()[])(s);
    unpack(client1#1.0 ClientSumOK())[s];
    client1.checkSumIsOK();
    transfer(s#k BasicFields(), s#k SumOK());
    unpack(s#k BasicFields())[5];
    s.calculateSum();
    unpack(client2#1.0 ClientSumOK())[s];
    client2.checkSumIsOK();
  }

  void main2() 
  ~double k:
  {
    Sum s2 = new ProxySum(BasicFields()[7])(7);
    unpack(s2#1.0 BasicFields())[7];
    s2.addOneToSum();
    ClientSum client3 = new ClientSum(ClientSumGreater0()[])(s2);
    ClientSum client4 = new ClientSum(ClientSumGreater0()[])(s2);
    unpack(client3#1.0 ClientSumGreater0())[s2];
    client3.checkSumGreater0();
    transfer(s2#k BasicFields(), s2#k SumGreater0());
    unpack(s2#k BasicFields())[7];
    s2.addOneToSum(); 
    unpack(client4#1.0 ClientSumGreater0())[s2];
    client4.checkSumGreater0();
  }
}