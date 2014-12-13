package cager.jexpr.visitor;

public class FracString {
	String nameFrac;
	String field;
	//the object is always "this"
	double minBound;
	double maxBound;
	
	public FracString(String n, String f, double min) {
		nameFrac = n;
		field = f;
		minBound = min;
		//maxBound does not usually appear
		//because a FracString looks like 
		//fracOK[dc[this]] > 0.0
		maxBound = 100;
	}
	
	String getNameFrac() {
		return nameFrac;
	}
	
	String getField() {
		return field;
	}
	
	double getMinBound() {
		return minBound;
	}
	
	double getMaxBound() {
		return maxBound;
	}

}
