package testcases.cager.jexpr;

class ApplicationWebsite {
// Each ApplicationWebsite has its own mapOfAvailableColleges 
MapCollege mapOfAvailableColleges;

predicate applicationWebsiteField() = exists m:MapCollege :: 
	this.mapOfAvailableColleges -> m

College submitApplicationGetCollege(
int collegeNumber, 
int multNumber,
int campusNumber
) 
~double k1, k2, k3:
requires this#k3 applicationWebsiteField()
ensures ((multNumber == 4) ~=> result#k1 collegeBuildingsFew()) &&
((multNumber == 10) ~=> result#k2 collegeBuildingsMany())
{
	College college = this.mapOfAvailableColleges.lookup(collegeNumber, multNumber);
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