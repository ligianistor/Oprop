package testcases.cager.jexpr;

class ApplicationWebsite {
// Each ApplicationWebsite has its own mapOfAvailableColleges 
MapCollege mapOfAvailableColleges;

predicate applicationWebsiteField() = exists m:MapCollege :: 
	this.mapOfAvailableColleges -> m

College submitApplicationGetCollege(
int collegeNumber, 
int campusNumber
) 
~double k1, k2, k3:
requires this#k3 applicationWebsiteField()
ensures ((campusNumber == 4) ~=> result#k1 collegeFacilitiesFew()) &&
((campusNumber == 10) ~=> result#k2 collegeFacilitiesMany())
{
	College college = this.mapOfAvailableColleges.lookup(collegeNumber, campusNumber);
	return college;
}

void main() {
	ApplicationWebsite website = new ApplicationWebsite();
	College college1 = website.submitApplicationGetCollege(2, 4);
	college1.checkFewFacilities();
	College college2 = website.submitApplicationGetCollege(3, 10);
	college2.checkManyFacilities();

	College college = new College(collegeFacilitiesFew()[])(2, 4);
	StudentApplication app1 = new StudentApplication()();
	app1.setStudentApplication(college, 3);
	StudentApplication app2 = new StudentApplication()();
	app2.setStudentApplication(college, 5);

	app1.checkNumberFacilities();
	app2.checkNumberFacilities();
}
}