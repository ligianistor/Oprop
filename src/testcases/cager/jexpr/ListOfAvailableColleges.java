package testcases.cager.jexpr;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

// ListOfAvailableColleges acts as a factory and cache for College flyweight objects
class ListOfAvailableColleges {
	Map<Integer, College> listOfColleges = 
			new ConcurrentHashMap<Integer, College>();

	College lookup(int collegeNumber, int multNumber) {
		if (!listOfColleges.containsKey(collegeNumber)) {
			listOfColleges.put(collegeNumber, new College(collegeNumber, multNumber));
		}
		
		return listOfColleges.get(collegeNumber);
	}
}

