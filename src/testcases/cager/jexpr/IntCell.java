package testcases.cager.jexpr;

class IntCell {
	
//for state
predicate IntCellMany() = exists divi:int, val:int :: 
						this.divider -> divi &&  this.value -> val && 
						(this.value/this.divider >= 10.0)
predicate IntCellFew() = exists divi:int, val:int :: 
						this.divider -> divi &&  this.value -> val && 
						(this.value/this.divider <= 4.0)
/*
 * These are no longer needed because
 * they are all expressed in terms of MultipleOf above.
predicate MultipleOf21() = this.value->v && (v % 21 == 0) 
predicate MultipleOf16() = this.value->v && (v % 16 == 0) 
predicate MultipleOf15() = this.value->v && (v % 15 == 0) 
predicate MultipleOf14() = this.value->v && (v % 14 == 0) 
predicate MultipleOf33() = this.value->v && (v % 33 == 0) 
predicate MultipleOf4() = this.value->v && (v % 4 == 0) 
*/

int divider;
int value;

IntCell(int divi, int val) {
	this.value  = x;
}

void setValue(int x) {
	this.value = x;
}

int getValueInt() {
	return this.value;
}
}