package testcases.cager.jexpr;

class StudentApplication {
College college; 
IntCell facilities;
int campusNumber; 

predicate studentApplicationFields() = exists c:College,
			camp:int, fac:IntCell::
			(this.college -> c) && (this.campusNumber -> camp) &&
			(this.facilities -> fac) 
			
predicate StudentAppFacilitiesMany() = exists col:College, f:IntCell, c:int :: 
	this.college -> col && this.campusNumber -> c && this.facilities -> f &&
	(col#1.0 collegeFacilitiesMany(c)[col.collegeNumber])
	
predicate StudentAppFacilitiesFew() = exists col:College, f:IntCell, c:int :: 
	this.college -> col && this.campusNumber -> c && this.facilities -> f &&
	(col#1.0 collegeFacilitiesFew(c)[col.collegeNumber])

StudentApplication(College col, int campusNum) 
ensures this#1.0 studentApplicationFields()
{
		this.college = col;
		this.facilities = this.college.getNumberFacilities(campusNum);
		this.campusNumber = campusNum;	
}
	
void changeApplicationFew(int newCampusNumber)
requires this#1.0 StudentAppFacilitiesFew()
ensures this#1.0 StudentAppFacilitiesFew()
{
	this.campusNumber = newCampusNumber % 4;
	this.facilities = this.college.getNumberFacilities(this.campusNumber);
	
}

void changeApplicationMany(int newCampusNumber)
requires this#1.0 StudentAppFacilitiesMany()
ensures this#1.0 StudentAppFacilitiesMany()
{
	this.campusNumber = newCampusNumber * 10 + 1;
	this.facilities = this.college.getNumberFacilities(this.campusNumber);
}

boolean checkFacilitiesFew() 
requires this#1.0 StudentAppFacilitiesFew()
ensures this#1.0 StudentAppFacilitiesFew()
{        
	return (this.facilities.getValueInt() <= 4 * this.campusNumber);
}

boolean checkFacilitiesMany() 
requires this#1.0 StudentAppFacilitiesMany()
ensures this#1.0 StudentAppFacilitiesMany()
{        
	return (this.facilities.getValueInt() >= 10 * this.campusNumber);
}

}