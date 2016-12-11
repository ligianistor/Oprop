package testcases.cager.jexpr;

class ApplicationWebsite {
// Each ApplicationWebsite has its own mapOfAvailableColleges 
MapCollege mapOfAvailableColleges;
// might need one map for Few and another one for Many

predicate applicationWebsiteField() = exists m:MapCollege :: 
	this.mapOfAvailableColleges -> m

College submitApplicationGetCollege(
		int collegeNumber, 
		int campusNumber
) 
~double k1, k2, k3:
requires this#k3 applicationWebsiteField()
ensures ((campusNumber <= 4) ~=> result#k1 collegeFacilitiesFew(campusNumber)) &&
		((campusNumber >= 10) ~=> result#k2 collegeFacilitiesMany(campusNumber))
{
		// Could allow the college to not satisfy an invariant right when declared, but the
		// first time when a value is assigned to it.
	College college;
	if (campusNumber <= 4) {
		college = this.mapOfAvailableColleges.lookupOrPutFew(collegeNumber, campusNumber);
	} else  if (campusNumber >= 10){
		college = this.mapOfAvailableColleges.lookupOrPutMany(collegeNumber, campusNumber);
	}
	return college;
}

void main() {
	College college = new College(collegeFacilitiesFew()[])(2);
	StudentApplication app1 = new StudentApplication()(college, 3);
	StudentApplication app2 = new StudentApplication()(college, 2);
	app1.checkFewFacilities();
	app1.changeApplicationFew(34);
	app2.checkFewFacilities();
	
	College college2 = new College(collegeFacilitiesMany()[])(56);
	StudentApplication app3 = new StudentApplication()(college2, 45);
	StudentApplication app4 = new StudentApplication()(college2, 97);
	app1.checkManyFacilities();
	app1.changeApplicationMany(78);
	app2.checkManyFacilities();
	
	ApplicationWebsite website = new ApplicationWebsite();
	College college3 = website.submitApplicationGetCollege(2, 4);
	college3.checkFewFacilities();
	College college4 = website.submitApplicationGetCollege(3, 10);
	college4.checkManyFacilities();
}
}