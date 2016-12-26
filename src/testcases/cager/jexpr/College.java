package testcases.cager.jexpr;

class College {
	
int collegeNumber; //intrinsic state
int endowment; // also intrinsic state that needs to be stored for each college

predicate CollegeNumberField() = 
	exists c:int :: this.collegeNumber -> c 
	
predicate CollegeFields() = 
	exists c:int, e:int :: 
	this.collegeNumber -> c && 
	this.endowment -> e

predicate collegeFacilitiesMany(int num) = 
	exists c:int::
		(this.collegeNumber -> c) && 
		(num >= 10 * c)
		
predicate collegeFacilitiesFew(int num) = exists c:int ::
	(this.collegeNumber -> c) && (num <= 4 * c) 

College(int number) 
ensures this#1.0 CollegeFields()[number, (number*1000)-5]
{
	this.collegeNumber = number;
	this.endowment = (number *1000) - 5;
}

int getCollegeNumber() 
ensures this#1.0 CollegeNumberField()[result]
{
	return this.collegeNumber;
}

// the method that calculates the extrinsic state
IntCell getNumberFacilities(int campusNumber) 
requires this#1.0 CollegeNumberField(this.collegeNumber)
ensures result#1.0 MultipleOf(this.collegeNumber)
{
	return new IntCell(this.collegeNumber * campusNumber);
}

boolean checkFewFacilities(int num) 
requires this#1.0 collegeFacilitiesFew(num)
ensures this#1.0 collegeFacilitiesFew(num)
{
return (num <= 4 * this.collegeNumber);
}

boolean checkManyFacilities(int num) 
requires this#1.0 collegeFacilitiesMany(num)
ensures this#1.0 collegeFacilitiesMany(num)
{
return (num >= 10 * this.collegeNumber);
}
}
