package cager.jexpr.ast;

public class FieldAndTypePair {
	String name;
	String type;
	
	public FieldAndTypePair(String n, String t) {
		name = n;
		type = t;
	}
	
	public String getName() {
		return name;
	}
	
	public String getType() {
		return type;
	}
}


