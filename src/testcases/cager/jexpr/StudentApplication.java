package testcases.cager.jexpr;

class StudentApplication {
College college; 
int campusNumber; 
IntCell facilities;

// facilites is college.collegeNumber * campusNumber
// Since facilities is always calculated, it doesn't need
// to be provided together with the other witnesses for existentials

predicate studentApplicationFields() = 
			exists c:College, campNum:int::
			(this.college -> c) && (this.campusNumber -> campNum) 
		
predicate StudentAppFacilitiesMany() = exists col:College, campNum:int :: 
	this.college -> col && this.campusNumber -> campNum &&
	(col#1.0 collegeFacilitiesMany(campNum)[])
	
predicate StudentAppFacilitiesFew() = exists col:College, campNum:int :: 
	this.college -> col && this.campusNumber -> campNum &&
	(col#1.0 collegeFacilitiesFew(campNum)[])

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