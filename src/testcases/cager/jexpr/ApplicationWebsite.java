package testcases.cager.jexpr;

class ApplicationWebsite {
MapCollege mapOfAvailableColleges;

predicate applicationWebsiteField() = exists m:MapCollege :: 
	this.mapOfAvailableColleges -> m
	
ApplicationWebsite(int maxSize)
	ensures this#1.0 applicationWebsiteField()
	//TODO is this a place where the existentials should be 
	//spelled out 
{
		mapOfAvailableColleges = new MapCollege(maxSize);	
}

College submitApplicationGetCollege(int collegeNumber) 
~double k1, k2, k3:
requires this#k3 applicationWebsiteField()
ensures (result#k1 collegeBuildingsFew() ||
	     result#k2 collegeBuildingsMany())
{
	College college = this.mapOfAvailableColleges.lookup(collegeNumber);
	return college;
}

void main() {
	ApplicationWebsite website = new ApplicationWebsite(applicationWebsiteField())(5);
	College college = website.submitApplicationGetCollege(2);
	StudentApplication app1 = new StudentApplication(StudentAppFacilitiesFew())(college, 3);
	StudentApplication app2 = new StudentApplication(StudentAppFacilitiesFew())(college, 2);
	app1.checkFacilitiesFew();
	app1.changeApplicationFew(34);
	app2.checkFacilitiesFew();
	
	College college2 = website.submitApplicationGetCollege(56);
	StudentApplication app3 = new StudentApplication(StudentAppFacilitiesMany())(college2, 45);
	StudentApplication app4 = new StudentApplication(StudentAppFacilitiesMany())(college2, 97);
	app3.checkFacilitiesMany();
	app3.changeApplicationMany(78);
	app4.checkFacilitiesMany();
}
}