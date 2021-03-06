package testcases.cager.jexpr;

class IntCell {
  int divider;
  int value;
	
  predicate BasicIntCell() = exists int divi, int val : 
      this.divider -> divi &&  this.value -> val
							
  predicate MultipleOf(int a) = exists int v :
      this.divider -> a &&  this.value -> v &&
      ( (v - int(v/a)*a ) == 0 )						
							
  predicate IntCellMany(int quot) = exists int divi, int val : 
      this.divider -> divi &&  this.value -> val && (quot >= 10)
  predicate IntCellFew(int quot) = exists int divi, int val : 
      this.divider -> divi &&  this.value -> val && (quot <= 4)
							
  IntCell(int divider1, int value1) 
    ensures this.value == value1;
    ensures this.divider == divider1;
  {
    this.value  = value1;
    this.divider = divider1;
  }

  int getValueInt() 
  ~double k:
    requires this#k BasicIntCell()
    ensures this#k BasicIntCell()
  {
    unpack(this#k BasicIntCell());
    return this.value;
    pack(this#k BasicIntCell());
  }
}