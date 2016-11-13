package testcases.cager.jexpr;

class College {
	
int collegeNumber;
int numberBuildings;

/*
predicate collegeBuildingsMany() = 
	exists c:int, n:int ::
		(this.collegeNumber -> c) && (this.numberBuildings -> n) && 
		(n == 10 * c)
		
predicate collegeBuildingsFew() = exists c:int, n:int ::
	(this.collegeNumber -> c) && (this.numberBuildings -> n) && (n == 4 * c) 
*/
College(int number, int multNumber) {
	this.collegeNumber = number;
	this.numberBuildings = this.collegeNumber * multNumber;
}

int getCollegeNumber() {
	return this.collegeNumber;
}

IntCell getNumberFacilities(int campusNumber) 
//ensures result#1 MultipleOf(collegeNumber)
{
return new IntCell(campusNumber * this.collegeNumber);
}


boolean checkFewBuildings() 
//requires this#1 collegeBuildingsFew()
{
return (this.numberBuildings == 4 * this.collegeNumber);
}


boolean checkManyBuildings() 
//requires this#1 collegeBuildingsMany()
{
return (this.numberBuildings == 10 * this.collegeNumber);
}
}