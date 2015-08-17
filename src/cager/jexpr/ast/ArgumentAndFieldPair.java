package cager.jexpr.ast;

public class ArgumentAndFieldPair {
	String argument;
	String field;
	
	public ArgumentAndFieldPair(String n, String t) {
		argument = n;
		field = t;
	}
	
	//Constructor for when we only know the name
	//of the argument and we do not know which field 
	//it corresponds to.
	public ArgumentAndFieldPair(String n) {
		argument = n;
		field = "";
	}
	
	public String getArgument() {
		return argument;
	}
	
	public String getField() {
		return field;
	}
	
	public void setField(String f) {
		field = f;
	}
	
	@Override
public boolean equals(Object obj) {
		
		if (obj == this) {
            return true;
        }
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }
      
        ArgumentAndFieldPair o1 = (ArgumentAndFieldPair) obj;

		if (argument.equals(o1.getArgument()) && field.equals(o1.getField()))
		{
			return true;
		}

		return false;
	}
	
	
	@Override
	public int hashCode(){
	    return (argument.hashCode() * field.hashCode());
	  }
	
	
}
