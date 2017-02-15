package testcases.cager.jexpr;

class StudentApplication {
College college; 
int campusNumber; 
IntCell facilities;

// facilites is college.collegeNumber * campusNumber
// Since facilities is always calculated, it doesn't need
// to be provided together with the other witnesses for existentials

predicate studentApplicationFields() = 
			exists College c, int campNum::
			(this.college -> c) && (this.campusNumber -> campNum) 
		
predicate StudentAppFacilitiesMany(int fa) = 
	exists double k, College col, int campNum :: 
	this.college -> col && this.campusNumber -> campNum &&
	(col#k collegeFacilitiesMany(fa)[col.number])
	
predicate StudentAppFacilitiesFew(int fa) = 
	exists double k, College col, int campNum :: 
	this.college -> col && this.campusNumber -> campNum &&
	(col#k collegeFacilitiesFew(fa)[col.number])

StudentApplication(College col, int campusNum) 
~double k:
requires campusNum > 0;
requires unpacked(col#k CollegeNumberField())
requires col.collegeNumber > 0 
ensures (this.college == col);
ensures (this.campusNumber == campusNum);
ensures ( (campusNum <= 4) && (campusNum > 0) ) ~=> (col#k CollegeFacilitiesFew(this.facilities));
ensures (campusNum >= 10) ~=> (col#k CollegeFacilitiesMany(this.facilities));
{
		this.college = col;
		this.facilities = this.college.getNumberFacilities(campusNum, col.collegeNumber);
		this.campusNumber = campusNum;	
		if (0 < campusNum  && campusNum <= 4) {
			transfer(col#k CollegeFacilitiesFew(this.facilities), col#k CollegeNumberField());
			pack(col#k CollegeFacilitiesFew(this.facilities));
		} else if (campusNum >= 10) {
			transfer(col#k CollegeFacilitiesMany(this.facilities), col#k CollegeNumberField());
			pack(col#k CollegeFacilitiesMany(this.facilities));
		}
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