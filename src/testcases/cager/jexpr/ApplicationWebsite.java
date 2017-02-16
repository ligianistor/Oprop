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

	boolean  containsKey(int key1) 
	~double k, k2:
	requires this#k applicationWebsiteField()
	ensures (result == true) && (exists c:College ==> (this#k2 KeyValuePair(key1, c))
	ensures (result == false) && (this#k2 KeyValuePair(key1, null))
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
	~double k, k2:
	requires unpacked(this#k applicationWebsiteField())
	requires (this#k2 KeyValuePair(key1, null))
	ensures (this#k2 KeyValuePair(key1, college1))
/*
	{
		unpack(this#k2 KeyValuePair(key1, null));
		this.mapOfColleges[key1] = college1;	
		pack(this#k2 KeyValuePair(key1, college1));
	}
*/
	
	College get(int key1) 
	~ double k1, k2:
	requires (exists College col : (this#k1 KeyValuePair(key1, col)))
	ensures this#k1 KeyValuePair(key1, result)
	ensures result#k2 CollegeNumberField()
/*	{
		College c;
		c = this.mapOfColleges[key1];
		return c;
	}
*/

	College lookup(int colNum) 
	~double k1, k2, k3:
	requires (0 < colNum);
	requires this#k1 applicationWebsiteField()
	ensures this#k2 KeyValuePair(colNum, result)
	ensures result#k3 CollegeNumberField()
	{
		boolean b = this.containsKey(colNum);
		unpack(this#k1 applicationWebsiteField());
		if (!b) {
			College c = new College(CollegeNumberField()[colNum])(colNum);
			this.put(colNum, c);
		}
		College result = this.get(colNum);
		return result;
	}

// this is the constructor and that's why we can access the fields in the pre and postconditions			
ApplicationWebsite(int maxSize1)
requires (maxSize1>=0);
ensures (forall int j :: ((j<=maxSize1) && (j>=0)) ~=> this#1.0 KeyValuePair(j, null));
ensures this.maxSize == maxSize1;
{
	this.maxSize = maxSize1;
	this.makeMapNull(maxSize1);		
}

College submitApplicationGetCollege(int collegeNumber) 
~double k1, k2:
requires this#k1 applicationWebsiteField()
requires (0<colNum);
ensures result#k2 CollegeNumberField()
{
	College result = this.mapOfColleges.lookup(collegeNumber);
	return result;
}

void main1() 
~double k1, k2 :
{
	ApplicationWebsite website = new ApplicationWebsite(applicationWebsiteField()[100])(100);
	College college = website.submitApplicationGetCollege(2);
	unpack(college#k1 CollegeNumberField());
	StudentApplication app1 = new StudentApplication(StudentAppFacilitiesFew())(college, 3);
	// The programmer puts k when they don't know the exact fraction.
	transfer(college#k2 CollegeNumberField(), college#k2 collegeFacilitiesFew());
	unpack(college#k2 CollegeNumberField());
	StudentApplication app2 = new StudentApplication(StudentAppFacilitiesFew())(college, 2);
	app1.checkFacilitiesFew();
	app1.changeApplicationFew(34);
	app2.checkFacilitiesFew();
}

void main2() 
~double k1, k2:
{
	ApplicationWebsite website = new ApplicationWebsite(applicationWebsiteField())(100);
	College college2 = website.submitApplicationGetCollege(56);
	unpack(college2#k1 CollegeNumberField());
	StudentApplication app3 = new StudentApplication(StudentAppFacilitiesMany())(college2, 45);
	transfer(college2#k2 CollegeNumberField(), college2#k2 collegeFacilitiesMany());
	unpack(college2#k2 CollegeNumberField());
	StudentApplication app4 = new StudentApplication(StudentAppFacilitiesMany())(college2, 97);
	app3.checkFacilitiesMany();
	app3.changeApplicationMany(78);
	app4.checkFacilitiesMany();
}
}