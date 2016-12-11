package testcases.cager.jexpr;

//MapCollege acts as a factory and cache for College flyweight objects
class MapCollege {
	map<int, College> mapOfColleges;
	int maxSize; // the maxSize of entries in the map
	
	int size;
	
	predicate isEntryNull(int key1) = exists m : map<int, College> :: 
		(this.mapOfColleges -> m) && (m[key1] == null)
		
	void makeMapNull(i : int)
	ensures (forall j:int :: (j<=i) => this#1.0 isEntryNull(j))
	{
		if (i==0) {
			this.mapOfColleges[i] = null;	
		} else {
			makeMapNull(i-1);
		}
	}

	bool containsKey(int key1) 
	
	{
		boolean b = true;
		if (this.mapOfColleges[key1] == null) {
			b = false;	
		} 
		return b;
	}
	
	void put(int key1, College college1) 
	
	{
		this.mapOfColleges[key1] = college1;	
	}
	
	College get(int key1) 
	
	{
		College c;
		c = this.mapOfColleges[key1];
		return c;
	}
	
	College lookupOrPutFew(int collegeNumber, int multNumber) 
	
	{
		if (!this.containsKey(collegeNumber)) {
			College c = new College()(collegeNumber);
			this.put(collegeNumber, c);
		}
		College ret = this.get(collegeNumber);
		return ret;
	}
	
	College lookupOrPutMany(int collegeNumber, int multNumber) 
	
	{
		if (!this.containsKey(collegeNumber)) {
			College c = new College()(collegeNumber);
			this.put(collegeNumber, c);
		}
		College ret = this.get(collegeNumber);
		return ret;
	}
}
