package testcases.cager.jexpr;

class ApplicationWebsite {
//List<StudentApplication> listStudentApplications;
ListOfAvailableColleges listOfAvailableColleges;

ApplicationWebsite() {
	this.listOfAvailableColleges = new ListOfAvailableColleges();
}

College submitApplicationGetCollege(
int collegeNumber, 
int multNumber,
int campusNumber
) 
ensures ((multNumber == 4) ~=> result#1 collegeBuildingsFew()) &&
((multNumber == 10) ~=> result#1 collegeBuildingsMany())
{
	College college = this.listOfAvailableColleges.lookup(collegeNumber, multNumber);
	//StudentApplication studentApplication = 
	//		new StudentApplication(college, campusNumber);
	//this.listStudentApplications.add(studentApplication);
	return college;
}

void main() {
	ApplicationWebsite website = new ApplicationWebsite();
	College college1 = website.submitApplicationGetCollege(2, 4, 2);
	college1.checkFewBuildings();
	College college2 = website.submitApplicationGetCollege(3, 10, 3);
	college2.checkManyBuildings();

	College college = new College(collegeBuildingsFew()[])(2, 4);
	StudentApplication app1 = new StudentApplication(college, 3);
	StudentApplication app2 = new StudentApplication(college, 5);

	app1.checkNumberFacilities();
	app2.checkNumberFacilities();
}
}