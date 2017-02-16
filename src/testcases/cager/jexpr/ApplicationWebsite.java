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

	//-->I left off here 
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
	
	procedure lookup(colNum:int, this:Ref) returns (r:Ref)
	modifies mapOfColleges, packedCollegeNumberField, fracCollegeNumberField,collegeNumber,
	       endowment, packedKeyValuePair, packedApplicationWebsiteField;
	requires (colNum<=maxSize[this]) && (0<colNum);
	requires packedApplicationWebsiteField[this];
	requires (fracApplicationWebsiteField[this] > 0.0);
	ensures packedKeyValuePair[this, colNum];
	ensures	(fracKeyValuePair[this, colNum] > 0.0);
	ensures packedCollegeNumberField[r];
	ensures fracCollegeNumberField[r] > 0.0;

	//ensures this#k KeyValuePair(colNum, result)
	{
	var temp:bool;
	var c:Ref;
	call temp := containsKey(colNum, this);
	call UnpackApplicationWebsiteField(mapOfColleges[this], this);
	packedApplicationWebsiteField[this]:=false;
	if (temp == false) {
		call ConstructCollege(colNum, c);
		packedCollegeNumberField[c] := false;
		call PackCollegeNumberField(colNum, c);
		packedCollegeNumberField[c] := true;
		fracCollegeNumberField[c] := 1.0;
	    
		call put(colNum, c, this);
	}
	call r:= get(colNum, this);
	}
	
ApplicationWebsite(int maxSize)
{
		createMapCollege(maxSize);	
}

procedure ConstructApplicationWebsite(maxSize1:int, this:Ref)
modifies maxSize, mapOfColleges, packedKeyValuePair, fracKeyValuePair;
requires (maxSize1>=0);
ensures (forall j:int :: ((j<=maxSize1) && (j>=0)) ==> (packedKeyValuePair[this, j] && (fracKeyValuePair[this, j]==1.0)) ) ;
ensures maxSize[this] == maxSize1;
{
	call createMapCollege(maxSize1, this);
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

procedure submitApplicationGetCollege(colNum:int, this:Ref) returns (r: Ref)
modifies mapOfColleges, packedCollegeNumberField, fracCollegeNumberField, collegeNumber, endowment,
        packedKeyValuePair, packedApplicationWebsiteField;
requires packedApplicationWebsiteField[this];
requires (fracApplicationWebsiteField[this] > 0.0);
requires (colNum<=maxSize[this]) && (0<colNum);
ensures packedCollegeNumberField[r];
ensures fracCollegeNumberField[r] > 0.0;
{
	call r := lookup(colNum, this);
  
}

void main1() {
	ApplicationWebsite website = new ApplicationWebsite(applicationWebsiteField())(5);
	College college = website.submitApplicationGetCollege(2);
	StudentApplication app1 = new StudentApplication(StudentAppFacilitiesFew())(college, 3);
	StudentApplication app2 = new StudentApplication(StudentAppFacilitiesFew())(college, 2);
	app1.checkFacilitiesFew();
	app1.changeApplicationFew(34);
	app2.checkFacilitiesFew();
}

procedure main1(this:Ref) 
modifies mapOfColleges, packedApplicationWebsiteField,
  packedStudentAppFacilitiesFew, packedStudentAppFacilitiesMany,
  packedCollegeNumberField, fracCollegeNumberField, collegeNumber, endowment,
  fracApplicationWebsiteField, college, facilities, campusNumber, 
  fracMultipleOf, packedMultipleOf, fracStudentAppFacilitiesFew,
  fracStudentAppFacilitiesMany, maxSize, divider, value,
  fracCollegeFacilitiesFew, fracCollegeFacilitiesMany,
  packedKeyValuePair, fracKeyValuePair,
  packedCollegeFacilitiesFew, packedCollegeFacilitiesMany, packedCollegeNumberField,
  fracCollegeNumberField;
requires (forall  x:Ref :: ( packedCollegeFacilitiesFew[x]));
requires (forall y:Ref :: ( packedStudentAppFacilitiesFew[y]));
{
	var website : Ref;
	var college : Ref;
	var app1, app2: Ref;
  	var tempbo : bool;
	assume (app1 != app2);

	call ConstructApplicationWebsite(100, website);
	packedApplicationWebsiteField[website] := false;
	call PackApplicationWebsiteField(mapOfColleges[website], website);
	packedApplicationWebsiteField[website] := true;
	fracApplicationWebsiteField[website] := 1.0;

	call college := submitApplicationGetCollege(2, website);

 	call UnpackCollegeNumberField(collegeNumber[college], college);
 	packedCollegeNumberField[college] := false;
	call ConstructStudentApplication(college, 3, app1);

	packedStudentAppFacilitiesFew[app1] := false;
	call PackStudentAppFacilitiesFew(facilities[app1], college, 3, app1);
	packedStudentAppFacilitiesFew[app1] := true;
	fracStudentAppFacilitiesFew[app1] := 1.0;
	fracCollegeFacilitiesFew[college] := fracCollegeFacilitiesFew[college] / 2.0;

 	//transfer
  	packedCollegeNumberField[college] := packedCollegeFacilitiesFew[college];
  	fracCollegeNumberField[college] := fracCollegeFacilitiesFew[college];
  
   	call UnpackCollegeNumberField(collegeNumber[college], college);
    	packedCollegeNumberField[college] := false;
	call ConstructStudentApplication(college, 2, app2);
	packedStudentAppFacilitiesFew[app2] := false;
	call PackStudentAppFacilitiesFew(facilities[app2], college, 2, app2);
	packedStudentAppFacilitiesFew[app2] := true;
	fracStudentAppFacilitiesFew[app2] := 1.0;
	fracCollegeFacilitiesFew[college] := fracCollegeFacilitiesFew[college] / 2.0;
	call tempbo := checkFacilitiesFew(app1);
	call changeApplicationFew(34, app1);
	call tempbo := checkFacilitiesFew(app2);
}


void main2() {
	ApplicationWebsite website = new ApplicationWebsite(applicationWebsiteField())(5);
	College college2 = website.submitApplicationGetCollege(56);
	StudentApplication app3 = new StudentApplication(StudentAppFacilitiesMany())(college2, 45);
	StudentApplication app4 = new StudentApplication(StudentAppFacilitiesMany())(college2, 97);
	app3.checkFacilitiesMany();
	app3.changeApplicationMany(78);
	app4.checkFacilitiesMany();
}

procedure main2(this:Ref) 
modifies mapOfColleges, packedApplicationWebsiteField,
  packedStudentAppFacilitiesFew, packedStudentAppFacilitiesMany,
  packedCollegeNumberField, fracCollegeNumberField, collegeNumber, endowment,
  fracApplicationWebsiteField, college, facilities, campusNumber, 
  fracMultipleOf, packedMultipleOf, fracStudentAppFacilitiesFew,
  fracStudentAppFacilitiesMany, maxSize, divider, value,
  fracCollegeFacilitiesFew, fracCollegeFacilitiesMany,
  packedKeyValuePair, fracKeyValuePair,
  packedCollegeFacilitiesFew, packedCollegeFacilitiesMany,
  packedCollegeNumberField, fracCollegeNumberField;
requires (forall  x:Ref :: ( packedCollegeFacilitiesMany[x]));
requires (forall y:Ref :: ( packedStudentAppFacilitiesMany[y]));
{
	var website : Ref;
	var college2 : Ref;
	var app3, app4 : Ref;
  	var tempbo : bool;
	assume (app3 != app4);
  
  	call ConstructApplicationWebsite(100, website);
	packedApplicationWebsiteField[website] := false;
	call PackApplicationWebsiteField(mapOfColleges[website], website);
	packedApplicationWebsiteField[website] := true;
	fracApplicationWebsiteField[website] := 1.0;
  
	call college2 := submitApplicationGetCollege(56, website);

   	call UnpackCollegeNumberField(collegeNumber[college2], college2);
    	packedCollegeNumberField[college2] := false;
	call ConstructStudentApplication(college2, 45, app3);

	packedStudentAppFacilitiesMany[app3] := false;
  
	call PackStudentAppFacilitiesMany(facilities[app3], college2, 45, app3);
	packedStudentAppFacilitiesMany[app3] := true;
	fracStudentAppFacilitiesMany[app3] := 1.0;
	fracCollegeFacilitiesMany[college2] := fracCollegeFacilitiesMany[college2] / 2.0;
  
   	//transfer
  	packedCollegeNumberField[college2] := packedCollegeFacilitiesMany[college2];
  	fracCollegeNumberField[college2] := fracCollegeFacilitiesMany[college2];
   	call UnpackCollegeNumberField(collegeNumber[college2], college2);
    	packedCollegeNumberField[college2] := false;
	call ConstructStudentApplication(college2, 97, app4);
	packedStudentAppFacilitiesMany[app4] := false;
	call PackStudentAppFacilitiesMany(facilities[app4], college2, 97, app4);
	packedStudentAppFacilitiesMany[app4] := true;
	fracStudentAppFacilitiesMany[app4] := 1.0;
	fracCollegeFacilitiesMany[college2] := fracCollegeFacilitiesMany[college2] / 2.0;

	call tempbo := checkFacilitiesMany(app3);
	call changeApplicationMany(78, app3);
  
	call tempbo := checkFacilitiesMany(app4);
}

}