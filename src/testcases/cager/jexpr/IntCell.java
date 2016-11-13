package testcases.cager.jexpr;

class IntCell {
	
//predicate MultipleOf(int a) = value->v && (v % a == 0)

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