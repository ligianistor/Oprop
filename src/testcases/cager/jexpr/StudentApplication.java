package testcases.cager.jexpr;

class StudentApplication {
College college; 
int campusNumber; 
IntCell facilities;
int collegeNumber;

predicate studentApplicationFields() = exists c:College,
			camp:int, fac:IntCell, num:int ::
			(this.college -> c) && (this.campusNumber -> camp) &&
			(this.facilities -> fac) && (this.collegeNumber -> num)
			

predicate facilitiesOK() = exists f:IntCell, c:int :: 
	this.facilities -> f && this.collegeNumber -> c &&
	(f#1.0 MultipleOf(c))

StudentApplication(College college, int campusNumber) 
requires this#1.0 studentApplicationFields()
ensures this#1.0 facilitiesOK()
{
		this.college = college;
		this.campusNumber = campusNumber;
		this.facilities = this.college.getNumberFacilities(campusNumber);
		this.collegeNumber = this.college.getCollegeNumber();
}

boolean checkNumberFacilities() 
requires this#1.0 facilitiesOK()
ensures this#1.0 facilitiesOK()
{        
	return (this.facilities.getValueInt() % this.collegeNumber == 0);
}
}

