package testcases.cager.jexpr;

//mapOfColleges acts as a factory and cache for College flyweight objects
//so that the complicated value of endowment does not need to be calculated every time,
//if the college is already in the cache.
// It makes more sense to incorporate MapCollege here and have mapOfColleges as a field
// than have MapCollege as a separate class.

class ApplicationWebsite {
	map<int, College> mapOfColleges;
	int maxSize; // the maxSize of entries in the map

	predicate applicationWebsiteField() = exists double k, map<int, College> m :: 
		this.mapOfColleges -> m && (forall int j :: (  ((j<=this.maxSize) && (j>=0)) ~=> 
        this#k KeyValuePair(j, m[j]) ));
			
	predicate KeyValuePair(int key, College value) = 
			exists m:map<int, College> :: this.mapOfColleges -> m && (m[key] == value)
	
    // called in the constructor and that's why we can access the fields in the pre and postconditions			
	void createMapCollege(int maxSize1) 
	requires (maxSize1>=0);
	ensures (forall int j :: ((j<=maxSize1) && (j>=0)) ~=> this#1.0 KeyValuePair(j, null));
	ensures this.maxSize == maxSize1;
	{
		this.maxSize = maxSize1;
		this.makeMapNull(maxSize1);		
	}
		
	void makeMapNull(int i)
	requires (i >= 0);
	ensures (forall int j :: ((j<=i) && (j>=0)) => this#1.0 KeyValuePair(j, null));
	{
		if (i==0) {
			this.mapOfColleges[i] = null;
			pack(this#1.0 KeyValuePair(i, null));
		} else {
			this.makeMapNull(i-1);
			this.mapOfColleges[i] = null;
			pack(this#1.0 KeyValuePair(i, null));
		}
	}

	//------->left off here
	boolean  containsKey(int key1) 
	~double k
	requires this#k MapOfCollegesField()
	ensures (result == true) && (exists c:College ==> (this#1.0 KeyValuePair(key1, c))
	        ||
	        (result == false) && (this#1.0 KeyValuePair(key1, null))
/*	
   {
		boolean b = true;
		if (this.mapOfColleges[key1] == null) {
			b = false;	
		} 
		return b;
	}
*/
	
	void put(int key1, College college1) 
	requires this#1.0 MapOfCollegesField()
	ensures this#1.0 KeyValuePair(key1, college1)
/*
	{
		this.mapOfColleges[key1] = college1;	
	}
*/
	
	College get(int key1) 
	requires this#1.0 MapOfCollegesField()
	ensures this#1.0 KeyValuePair(key1, result)
/*	{
		College c;
		c = this.mapOfColleges[key1];
		return c;
	}
*/
	College lookup(int collegeNumber) 
	~double k1, k2:
	requires this#k1 MapOfCollegesField()
	ensures this#k2 KeyValuePair(collegeNumber, result)
	{
		if (!this.containsKey(collegeNumber)) {
			College c = new College(CollegeNumberField()[collegeNumber])(collegeNumber);
			this.put(collegeNumber, c);
		}
		College ret = this.get(collegeNumber);
		return ret;
	}
	
	ApplicationWebsite(int maxSize)
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