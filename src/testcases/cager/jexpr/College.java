package testcases.cager.jexpr;

class College {
	
int collegeNumber; //intrinsic state
int numberFacilities; //extrinsic state, initialized to 0 here 
// only in setCo

predicate CollegeNumberField(int c) = 
	this.collegeNumber -> c

predicate NumberFacilitiesField(int n) = 
	this.numberFacilities -> n

predicate collegeFacilitiesMany() = 
	exists c:int, n:int ::
		(this.collegeNumber -> c) && (this.numberFacilities -> n) && 
		(n >= 10 * c)
		
predicate collegeFacilitiesFew() = exists c:int, n:int ::
	(this.collegeNumber -> c) && (this.numberFacilities -> n) && (n <= 4 + c) 

void setCollege(int number, int campusNumber) {
	this.collegeNumber = number;
	this.numberFacilities = this.collegeNumber * campusNumber;
}

int getCollegeNumber() {
	return this.collegeNumber;
}

IntCell getNumberFacilities(int campusNumber) 
requires this#1 CollegeNumberField(this.collegeNumber)
ensures result#1 MultipleOf(this.collegeNumber)
{
	this.numberFacilities = this.collegeNumber * campusNumber;
return new IntCell(campusNumber * this.collegeNumber);
}

boolean checkFewFacilities() 
requires this#1 collegeFacilitiesFew()
ensures this#1 collegeFacilitiesFew()
{
return (this.numberFacilities == 4 * this.collegeNumber);
}

boolean checkManyFacilities() 
requires this#1 collegeFacilitiesMany()
ensures this#1 collegeFacilitiesMany()
{
return (this.numberFacilities == 10 * this.collegeNumber);
}
}
