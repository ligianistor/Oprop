package testcases.cager.jexpr;

class StateSleep implements Statelike {
  IntCell cell;
	
  predicate StateMultipleOf3() = exists IntCell c, double k : 
      this.cell -> c && (c#k MultipleOf(15)) 
  predicate StateMultipleOf2() = exists IntCell c, double k : 
      this.cell -> c && (c#k MultipleOf(16)) 

  StateSleep() 
  {
    IntCell temp = new IntCell(0);
    this.cell = temp; 		
  }

  StateSleep(IntCell c) 
  ensures this.cell == c;
  {
    this.cell = c; 		
  }

  IntCell computeResult(StateContext context, int num) 
  ~double k, k2:
    requires (context#k StateContextMultiple3()) 
    ensures (context#k StateContextMultiple3()) && (context#k2 StateLive())
  { 
    IntCell i1 = new IntCell(MultipleOf(21)[num*21])(21, num*21);
    StateLike r = new StateSleep(StateMultipleOf3()[i1])(i1);
    context.setState3(s); 
    return r; 
  }  
 
  IntCell computeResult2(StateContext context, int num) 
  ~double k, k2:
    requires (context#k StateContextMultiple2()) 
    ensures (context#k StateContextMultiple2()) && (context#k2 StateLimbo())
  { 
	IntCell i1 = new IntCell(MultipleOf(14)[num*14])(14, num*14);
    StateLike r = new StateLive(StateMultipleOf2()[i1])(i1);
    context.setState2(r); 
    return r;
  }  

  boolean checkMod3() 
  ~double k:
    requires this#k StateMultipleOf3()
    ensures this#k StateMultipleOf3()
  { 
    unpack(this#k StateMultipleOf3());
    return (this.cell.getValueInt() % 3 == 0);
    pack(this#k StateMultipleOf3());
  }  

  boolean checkMod2() 
  ~double k:
    requires this#k StateMultipleOf2()
    ensures this#k StateMultipleOf2()
  { 
    unpack(this#k StateMultipleOf2());
    return (this.cell.getValueInt() % 2 == 0); 
    pack(this#k StateMultipleOf2());
  } 
}