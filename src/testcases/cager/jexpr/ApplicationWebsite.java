package testcases.cager.jexpr;

//mapOfColleges acts as a factory and cache for College flyweight objects
//so that the complicated value of endowment does not need to be calculated every time,
//if the college is already in the cache.
// It makes more sense to incorporate MapCollege here and have mapOfColleges as a field
// than have MapCollege as a separate class.

class ApplicationWebsite {
	map<int, College> mapOfColleges;
	int maxSize; // the maxSize of entries in the map

	predicate applicationWebsiteField() = exists m:map<int, College> :: 
	this.mapOfColleges -> m
			
	predicate KeyValuePair(int key, College value) = 
			exists m:map<int, College> :: this.mapOfColleges -> m && (m[key] == value)
	
	predicate isEntryNull(int key1) = exists m : map<int, College> :: 
		(this.mapOfColleges -> m) && (m[key1] == null)
				
	void createMapCollege(int maxSize1) 
	ensures (forall j:int :: (j<=maxSize1) => this#1.0 isEntryNull(j))
	{
		this.maxSize = maxSize1;
		this.makeMapNull(maxSize1);		
	}
		
	void makeMapNull(int i)
	requires this#1.0 MapOfCollegesField()
	ensures (forall j:int :: (j<=i) => this#1.0 isEntryNull(j))
	{
		if (i==0) {
			this.mapOfColleges[i] = null;	
		} else {
			this.makeMapNull(i-1);
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
			College c = new College(CollegeFields()[collegeNumber])(collegeNumber);
			this.put(collegeNumber, c);
		}
		College ret = this.get(collegeNumber);
		return ret;
	}
	
	ApplicationWebsite(int maxSize)
	ensures this#1.0 applicationWebsiteField()
	//TODO is this a place where the existentials should be 
	//spelled out 
	{
		createMapCollege(maxSize);	
	}

College submitApplicationGetCollege(int collegeNumber) 
~double k1, k2, k3:
requires this#k3 applicationWebsiteField()
ensures (result#k1 collegeBuildingsFew() ||
	     result#k2 collegeBuildingsMany())
{
	College college = this.mapOfColleges.lookup(collegeNumber);
	return college;
}

void main() {
	ApplicationWebsite website = new ApplicationWebsite(applicationWebsiteField())(5);
	College college = website.submitApplicationGetCollege(2);
	StudentApplication app1 = new StudentApplication(StudentAppFacilitiesFew())(college, 3);
	StudentApplication app2 = new StudentApplication(StudentAppFacilitiesFew())(college, 2);
	app1.checkFacilitiesFew();
	app1.changeApplicationFew(34);
	app2.checkFacilitiesFew();
	
	College college2 = website.submitApplicationGetCollege(56);
	StudentApplication app3 = new StudentApplication(StudentAppFacilitiesMany())(college2, 45);
	StudentApplication app4 = new StudentApplication(StudentAppFacilitiesMany())(college2, 97);
	app3.checkFacilitiesMany();
	app3.changeApplicationMany(78);
	app4.checkFacilitiesMany();
}
}