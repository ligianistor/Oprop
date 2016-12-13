package testcases.cager.jexpr;

class College {
	
int collegeNumber; //intrinsic state
int endowment; // also intrinsic state that needs to be stored for each college

predicate CollegeNumberField() = 
	exists c:int :: this.collegeNumber -> c 

predicate collegeFacilitiesMany(int num) = 
	exists c:int::
		(this.collegeNumber -> c) && 
		(num >= 10 * c)
		
predicate collegeFacilitiesFew(int num) = exists c:int ::
	(this.collegeNumber -> c) && (num <= 4 * c) 

College(int number) 
ensures this#1.0 CollegeNumberField()[number]
{
	this.collegeNumber = number;
	this.endowment = (this.collegeNumber *1000) - 5;
}

int getCollegeNumber() 
ensures this#1.0 CollegeNumberField()[result]
{
	return this.collegeNumber;
}

// the method that calculates the extrinsic state
IntCell getNumberFacilities(int campusNumber) 
requires this#1 CollegeNumberField(this.collegeNumber)
ensures result#1 MultipleOf(this.collegeNumber)
{
	return new IntCell(this.collegeNumber * campusNumber);
}

boolean checkFewFacilities(int num) 
requires this#1 collegeFacilitiesFew(num)
ensures this#1 collegeFacilitiesFew(num)
{
return (num <= 4 * this.collegeNumber);
}

boolean checkManyFacilities(int num) 
requires this#1 collegeFacilitiesMany(num)
ensures this#1 collegeFacilitiesMany(num)
{
return (num >= 10 * this.collegeNumber);
}
}
