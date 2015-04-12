package testcases.cager.jexpr;

class Composite { 
	int count;
	Composite left;
	Composite right;
	Composite parent;	  
	
	predicate left(Composite ol, int lc) =
			true ~=> true

}
