package cager.jexpr.ast;

import java.util.Comparator;

import cager.jexpr.visitor.ObjPropString;

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


