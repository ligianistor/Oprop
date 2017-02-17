package testcases.cager.jexpr;

class StudentApplication {
  College college; 
  int campusNumber; 
  IntCell facilities;

  predicate StudentApplicationFields() = 
      exists College c, int campNum:
      (this.college -> c) && (this.campusNumber -> campNum) 
		
  predicate StudentAppFacilitiesMany(int fa) = 
      exists double k, College col, int campNum :
      this.college -> col && this.campusNumber -> campNum &&
      (col#k CollegeFacilitiesMany(fa)[col.number])
	
  predicate StudentAppFacilitiesFew(int fa) = 
      exists double k, College col, int campNum :
      this.college -> col && this.campusNumber -> campNum &&
      (col#k CollegeFacilitiesFew(fa)[col.number])

  StudentApplication(College col, int campusNum) 
  ~double k:
    requires campusNum > 0;
    requires unpacked(col#k CollegeNumberField())
    requires col.collegeNumber > 0 
    ensures (this.college == col);
    ensures (this.campusNumber == campusNum);
    ensures ( (campusNum <= 4) && (campusNum > 0) ) ~=> 
      (col#k CollegeFacilitiesFew(this.facilities));
    ensures (campusNum >= 10) ~=> 
      (col#k CollegeFacilitiesMany(this.facilities));
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
  ~double k, k2:
    requires newCampusNumber > 0;
    requires this#k StudentAppFacilitiesFew()
    ensures this#k StudentAppFacilitiesFew()
  {
    unpack(this#k StudentAppFacilitiesFew());
    this.campusNumber = newCampusNumber % 4;
    transfer(this.college#k2 CollegeNumberField(), this.college#k2 CollegeFacilitiesFew());
    unpack(this.college#k2 CollegeNumberField());
    this.facilities = this.college.getNumberFacilities(this.campusNumber);
    pack(this#k StudentAppFacilitiesFew());
  }

  void changeApplicationMany(int newCampusNumber)
  ~double k, k2:
    requires newCampusNumber > 0;
    requires this#k StudentAppFacilitiesMany()
    ensures this#k StudentAppFacilitiesMany()
  {
    unpack(this#k StudentAppFacilitiesMany());
    this.campusNumber = newCampusNumber * 10 + 1;
    transfer(this.college#k2 CollegeNumberField(), this.college#k2 CollegeFacilitiesMany());
    unpack(this.college#k2 CollegeNumberField());
    this.facilities = this.college.getNumberFacilities(this.campusNumber);
    pack(this#k StudentAppFacilitiesMany());
  }

  boolean checkFacilitiesFew() 
  ~double k:
    requires this#k StudentAppFacilitiesFew()
    ensures this#k StudentAppFacilitiesFew()
  {        
    unpack(this#k StudentAppFacilitiesFew());
    return (this.facilities.getValueInt() <= 4 * this.campusNumber);
    pack(this#k StudentAppFacilitiesFew())
  }

  boolean checkFacilitiesMany() 
  ~double k:
    requires this#k StudentAppFacilitiesMany()
    ensures this#k StudentAppFacilitiesMany()
  {        
    unpack(this#k StudentAppFacilitiesMany());
    return (this.facilities.getValueInt() >= 10 * this.campusNumber);
    pack(this#k StudentAppFacilitiesMany());
  }
}