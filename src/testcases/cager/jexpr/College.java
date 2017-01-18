package testcases.cager.jexpr;

class College {
	
int collegeNumber; //intrinsic state
int endowment; // also intrinsic state that needs to be stored for each college

predicate CollegeNumberField() = 
	exists c:int :: this.collegeNumber -> c 
	
predicate CollegeFields() = 
	exists c:int :: 
	this.collegeNumber -> c && 
	this.endowment -> (c*1000 - 5)

predicate collegeFacilitiesMany(int campusNum) = 
		(campusNum >= 10)
		
predicate collegeFacilitiesFew(int campusNum) = 
		(campusNum <= 4) 

College(int number) 
ensures this#1.0 CollegeFields()[number, (number*1000) - 5]
{
	this.collegeNumber = number;
	this.endowment = (number *1000) - 5;
}

//result should be a reserved word in Oprop
int getCollegeNumber() 
ensures this#1.0 CollegeNumberField()[result]
{
	return this.collegeNumber;
}

// the method that calculates the extrinsic state
// This method is not related to College, only slightly.
// A predicate about IntCellhas to hold about the result. 
IntCell getNumberFacilities(int campNum) 
ensures (campNum>=10) ==> (result#1.0 IntCellMany());
ensures (campNum<=4) ==> (result#1.0 IntCellFew());
{
	IntCell res;
	if (campNum>=10) {
		res = new IntCell(IntCellMany()[this.collegeNumber, this.collegeNumber * campNum])
						 (this.collegeNumber, this.collegeNumber * campNum);
	} else if (campNum<=4) {
		res = new IntCell(IntCellFew()[this.collegeNumber, this.collegeNumber * campNum])
		 (this.collegeNumber, this.collegeNumber * campNum);
	}
	return res;
}

}
