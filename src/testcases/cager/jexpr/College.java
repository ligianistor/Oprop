package testcases.cager.jexpr;

class College {
	
int collegeNumber; //intrinsic state
int endowment; // also intrinsic state that needs to be stored for each college

predicate CollegeNumberField() = 
	exists int c :: this.collegeNumber -> c && (c>0)

predicate collegeFacilitiesMany(int numFacilities) = 
	exists int c :: 
	this.collegeNumber -> c && 
	(numFacilities >= 10 * c)
		
predicate collegeFacilitiesFew(int numFacilities) = 
	exists int c :: 
	this.collegeNumber -> c &&
	(numFacilities <= 4 * c)

College(int number) 
ensures	this.collegeNumber == number;
{
	this.collegeNumber = number;
	this.endowment = (number *1000) - 5;
}

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
// result should be a reserved word in Oprop
int getNumberFacilities(int campNum, int colNum) 
~double k:
requires unpacked(this#k CollegeNumberField())
requires (this.collegeNumber == colNum);
requires campNum > 0;
requires colNum > 0;
ensures result == colNum * campNum;
{
	return colNum * campNum;
}

}
