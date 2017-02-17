package testcases.cager.jexpr;

class StateLimbo implements Statelike { 
  IntCell cell;
	
  predicate StateMultipleOf3() = exists IntCell c, double k : 
      this.cell -> c && (c#k MultipleOf(33)) 
  predicate StateMultipleOf2() = exists IntCell c, double k : 
      this.cell -> c && (c#k MultipleOf(14))

  StateLimbo() 
  {
    IntCell temp = new IntCell(0);
    this.cell = temp; 		
  }
	
  StateLimbo(IntCell c) 
  ensures this.cell == c;
  {
    this.cell = c; 		
  }

  IntCell computeResult(StateContext context, int num)
  ~double k, k2:
    requires (context#k stateContextMultiple3()) 
    ensures (context#k stateContextMultiple3()) && 
      (context#k2 stateSleep())
  { 
    IntCell i1 = new IntCell(MultipleOf(33)[num*33])(33, num*33);
    StateLike r = new StateSleep(StateMultipleOf3()[i1])(i1);
    context.setState3(s); 
    return r; 
  }  

  IntCell computeResult2(StateContext context, int num) 
  ~double k, k2:
    requires (context#k stateContextMultiple2()) 
    ensures (context#k stateContextMultiple2()) && 
      (context#k2 stateLive())
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
    return (this.cell.getValueInt() % 33 == 0); 
    pack(this#k StateMultipleOf3());
  }  

  boolean checkMod2() 
  ~double k:
    requires this#k StateMultipleOf2()
    ensures this#k StateMultipleOf2()
  { 
    unpack(this#k StateMultipleOf2());
    return (this.cell.getValueInt() % 14 == 0); 
    pack(this#k StateMultipleOf2());
  } 
}
