package cager.jexpr.visitor;

import java.util.LinkedList;

public class FracString {
	String nameFrac;
	String field;
	//The formal parameter is always "this".
	//The actual object has to be passed in to the getStatementFracString() method.
	
	LinkedList<String> parameters = new LinkedList<String>();
	double minBound;
	double maxBound;
	
	public FracString() {
		nameFrac = null;
		field = null;
		minBound = -1;
		maxBound = -1;
		parameters = new LinkedList<String>();
	}
	
	public FracString(String n, String f, double min) {
		nameFrac = n;
		field = f;
		minBound = min;
		//maxBound does not usually appear
		//because a FracString looks like 
		//fracOK[dc[this]] > 0.0
		maxBound = 100;
	}
	
	void setNameFrac(String n) {
		nameFrac = n;
	}
	
	void setField(String f) {
		field = f;
	}
	
	void setMinBound(double m) {
		minBound = m;
	}
	
	void setMaxBound(double m) {
		maxBound = m;
	}
	
	void setParameters(LinkedList<String> params) {
		    parameters.clear();
    		for (int pf = 0; pf < params.size(); pf++) {
    			addParameter(params.get(pf));        
    		}
    	}
	
	void addParameter(String s) {
		parameters.add(s);
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
	
	LinkedList<String> getParameters() {
		return parameters;
	}
	
	@Override
public boolean equals(Object obj) {
		
		if (obj == this) {
            return true;
        }
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }
      
        ObjPropString o1 = (ObjPropString) obj;

		if (nameFrac.equals(o1.getObject()) && field.equals(o1.getName()))
		{
			return true;
		}

		return false;
	}
	
	
	@Override
	public int hashCode(){
	    return (nameFrac.hashCode() * field.hashCode());
	  }
	
	public void printParams() {
		String stringParams = "";
		for (int i=0; i<parameters.size();i++) {
			stringParams = stringParams.concat(parameters.get(i)+",");
		}
		System.out.println("params:"+stringParams);
		System.out.println("nameFrac" + nameFrac);
		System.out.println("field" + field);
	}
	
	//TODO 
	// I need to substitute this or field[this] with the actual parameter
	// if the method is being called when the constructor is called.
	String getStatementFracString(boolean inRequires, String actualObject) {
		String stringParams = "";
		for (int i=0; i<parameters.size();i++) {
			stringParams = stringParams.concat(parameters.get(i)+",");
		}
		
		String statement = "\t";
		statement = statement.concat(nameFrac);
		if (field == null) {
			statement = statement.concat("["+stringParams + actualObject+ "] := " + nameFrac + "["+
						stringParams + actualObject+ "]");
		} else {
			statement = statement.concat("[" + stringParams + field + "["+actualObject+ "]] := " + 
						nameFrac + "[" + stringParams + field + "["+ actualObject+ "]]");
		}
		
		//TODO We need to consider more cases about the values of min and max.
		if (inRequires) {
			if (minBound == 0) {
				statement = statement.concat(" / 2.0;\n");
			}
		} // If we go on the else branch, we are in Ensures. 
		else {
			if (minBound == 0) {
				statement = statement.concat(" * 2.0;\n");
			}
			
		}
		
		return statement;
				
	}

}
