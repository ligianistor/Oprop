package testcases.cager.jexpr;

// ListOfAvailableColleges acts as a factory and cache for College flyweight objects
class ListOfAvailableColleges {
	MapCollege mapOfColleges;

	College lookup(int collegeNumber, int multNumber) {
		if (!mapOfColleges.containsKey(collegeNumber)) {
			mapOfColleges.put(collegeNumber, new College(collegeNumber, multNumber));
		}
		
		return mapOfColleges.get(collegeNumber);
	}
}

