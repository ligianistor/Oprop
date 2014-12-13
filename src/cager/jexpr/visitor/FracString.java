package cager.jexpr.visitor;

public class FracString {
	String nameFrac;
	String field;
	//The formal parameter is always "this".
	//The actual object has to be passed in to the getStatementFracString() method.
	double minBound;
	double maxBound;
	
	public FracString() {
		nameFrac = null;
		field = null;
		minBound = -1;
		maxBound = -1;
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
	
	//TODO 
	// I need to substitute this or field[this] with the actual parameter
	// if the method is being called when the constructor is called.
	String getStatementFracString(boolean inRequires, String actualObject) {
		String statement = "\t";
		statement = statement.concat(nameFrac);
		if (field == null) {
			statement = statement.concat("["+actualObject+ "] := " + nameFrac + "["+actualObject+ "]");
		} else {
			statement = statement.concat("[" + field + "["+actualObject+ "]] := " + 
						nameFrac + "[" + field + "["+actualObject+ "]]");
		}
		
		//TODO We need to consider more cases about the values of min and max.
		if (inRequires) {
			if (minBound == 0) {
				statement = statement.concat(" / 2.0;\n");
			}
		} else {
			if (minBound == 0) {
				statement = statement.concat(" * 2.0;\n");
			}
			
		}
		
		return statement;
				
	}

}
