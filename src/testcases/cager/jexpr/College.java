package testcases.cager.jexpr;

class College {
	
int collegeNumber; //intrinsic state
int endowment; // also intrinsic state that needs to be stored for each college

// this is the extrinsic state that does not get initiated until later
int facilitiesCol;

predicate CollegeNumberField() = 
	exists int c :: this.collegeNumber -> c 

predicate collegeFacilitiesMany(int numFacilities) = 
	exists int c :: 
	this.collegeNumber -> c && 
	this.facilitiesCol -> numFacilities && (numFacilities >= 10 * c)
		
predicate collegeFacilitiesFew(int numFacilities) = 
	exists int c :: 
	this.collegeNumber -> c &&
	this.facilitiesCol -> numFacilities && (numFacilities <= 4 * c)

College(int number) 
ensures	this.collegeNumber == number;
ensures	this.endowment == (number *1000) - 5;
{
	this.collegeNumber = number;
	this.endowment = (number *1000) - 5;
}

//result should be a reserved word in Oprop
int getCollegeNumber() 
~ double k:
requires this#k CollegeNumberField()
ensures this#k CollegeNumberField()
{
	unpack(this#k CollegeNumberField());	
	return this.collegeNumber;
	pack(this#k CollegeNumberField());	
}

// the method that calculates the extrinsic state
// This method is not related to College, only slightly.
// A predicate about IntCellhas to hold about the result. 
int getNumberFacilities(int campNum, int colNum) 
~double k:
requires unpacked(this#k CollegeNumberField())
requires (this.collegeNumber == colNum);
requires campNum > 0;
requires colNum > 0;
ensures result == colNum * campNum;
ensures result > 0;

{
	return colNum * campNum;
}

}
