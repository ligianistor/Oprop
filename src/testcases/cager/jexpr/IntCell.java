package testcases.cager.jexpr;

class IntCell {
	
//for flyweight
//predicate MultipleOf(int a) = value->v && (v % a == 0)
	
//for proxy
	/*
predicate MultipleOf3() = value->v && (v % 3 == 0) 
predicate MultipleOf2() = value->v && (v % 2 == 0) 
predicate MultipleOf6() = value->v && (v % 6 == 0) 
predicate MultipleOf18() = value->v && (v % 18 == 0) 
*/

int value;

IntCell(int x) {
	value  = x;
}

void setValue(int x) {
	value = x;
}

Integer getValueInteger() {
	return new Integer(value);
}	

int getValueInt() {
	return value;
}
}