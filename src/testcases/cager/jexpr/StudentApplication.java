package testcases.cager.jexpr;

class StudentApplication {
College college; 
int campusNumber; 
// TODO Maybe I need to add here collegeNumber too?
IntCell facilities;

predicate studentApplicationFields() = exists c:College,
			camp:int, fac:IntCell::
			(this.college -> c) && (this.campusNumber -> camp) &&
			(this.facilities -> fac) 
			

predicate StudentAppFacilitiesMany() = exists col:College, f:IntCell, c:int :: 
	this.college -> col && this.campusNumber -> c && this.facilities -> f &&
	(col#1.0 collegeFacilitiesMany()[col.collegeNumber, col.numberFacilities])
	
predicate StudentAppFacilitiesFew() = exists col:College, f:IntCell, c:int :: 
	this.college -> col && this.campusNumber -> c && this.facilities -> f &&
	(col#1.0 collegeFacilitiesFew()[col.collegeNumber, col.numberFacilities])

void setStudentApplication(College college, int campusNumber) 
requires this#1.0 studentApplicationFields()
ensures this#1.0 facilitiesOK()
{
		this.college = college;
		this.campusNumber = campusNumber;
		this.facilities = this.college.getNumberFacilities(campusNumber);
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

boolean checkNumberFacilities() 
requires this#1.0 facilitiesOK()
ensures this#1.0 facilitiesOK()
{        
	return (this.facilities.getValueInt() % this.campusNumber == 0);
}

boolean checkFacilitiesFew() 
requires this#1.0 StudentAppFacilitiesFew()
ensures this#1.0 StudentAppFacilitiesFew()
{        
	return (this.facilities.getValueInt() >= 10 * this.campusNumber);
}

boolean checkFacilitiesMany() 
requires this#1.0 StudentAppFacilitiesMany()
ensures this#1.0 StudentAppFacilitiesMany()
{        
	return (this.facilities.getValueInt() % this.campusNumber == 0);
}


(n >= 10 * c)

predicate collegeFacilitiesFew() = exists c:int, n:int ::
(this.collegeNumber -> c) && (this.numberFacilities -> n) && (n <= 4 + c) 
}

