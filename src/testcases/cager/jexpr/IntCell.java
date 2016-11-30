package testcases.cager.jexpr;

class IntCell {
	
//for flyweight
predicate MultipleOf(int a) = this.value->v && (v % a == 0)
	
//for state
predicate MultipleOf21() = this.value->v && (v % 21 == 0) 
predicate MultipleOf16() = this.value->v && (v % 16 == 0) 
predicate MultipleOf15() = this.value->v && (v % 15 == 0) 
predicate MultipleOf14() = this.value->v && (v % 14 == 0) 
predicate MultipleOf33() = this.value->v && (v % 33 == 0) 
predicate MultipleOf4() = this.value->v && (v % 4 == 0) 

int value;

IntCell(int x) {
	this.value  = x;
}

void setValue(int x) {
	this.value = x;
}

int getValueInt() {
	return this.value;
}
}