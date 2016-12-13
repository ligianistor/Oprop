package testcases.cager.jexpr;

// MapCollege acts as a factory and cache for College flyweight objects
// so that the complicated value of endowment does not need to be calculated every time,
// if the college is already in the cache.
class MapCollege {
	map<int, College> mapOfColleges;
	int maxSize; // the maxSize of entries in the map
	
	predicate MapOfCollegesField() = exists m:map<int, College> :: mapOfColleges -> m
			
	predicate KeyValuePair(int key, College value) = 
			exists m:map<int, College> :: mapOfColleges -> m && (mapOfColleges[key] == value)
	
	predicate isEntryNull(int key1) = exists m : map<int, College> :: 
		(this.mapOfColleges -> m) && (m[key1] == null)
		
	MapCollege(int maxSize1) 
	ensures (forall j:int :: (j<=maxSize1) => this#1.0 isEntryNull(j))
	{
		this.maxSize = maxSize1;
		makeMapNull(maxSize);		
	}
		
	void makeMapNull(int i)
	requires this#1.0 MapOfCollegesField()
	ensures (forall j:int :: (j<=i) => this#1.0 isEntryNull(j))
	{
		if (i==0) {
			this.mapOfColleges[i] = null;	
		} else {
			makeMapNull(i-1);
		}
	}

	boolean  containsKey(int key1) 
	requires this#1.0 MapOfCollegesField()
	ensures (result == true) && (exists c:College ==> (this#1.0 KeyValuePair(key1, c))
	        ||
	        (result == false) && (this#1.0 KeyValuePair(key1, null))
	{
		boolean b = true;
		if (this.mapOfColleges[key1] == null) {
			b = false;	
		} 
		return b;
	}
	
	void put(int key1, College college1) 
	requires this#1.0 MapOfCollegesField()
	ensures this#1.0 KeyValuePair(key1, college1)
	{
		this.mapOfColleges[key1] = college1;	
	}
	
	College get(int key1) 
	requires this#1.0 MapOfCollegesField()
	ensures this#1.0 KeyValuePair(key1, result)
	{
		College c;
		c = this.mapOfColleges[key1];
		return c;
	}
	
	College lookup(int collegeNumber) 
	requires this#1.0 MapOfCollegesField()
	ensures this#1.0 KeyValuePair(collegeNumber, result)
	{
		if (!this.containsKey(collegeNumber)) {
			College c = new College()(collegeNumber);
			this.put(collegeNumber, c);
		}
		College ret = this.get(collegeNumber);
		return ret;
	}
}
