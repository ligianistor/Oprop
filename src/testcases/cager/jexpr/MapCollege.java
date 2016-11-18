package testcases.cager.jexpr;

class MapCollege {
	map<int, College> mapOfColleges;
	 
	// this will be put in the constructor of MapCollege
	// =new map<int, College>();
	// map will be represented in boogie as a map of map
	
	int size;
	
	bool containsKey(int key1);
	
	void put(int key1, College college1);
	
	College get(int key1);
}
