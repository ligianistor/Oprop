package cager.jexpr.ast;

public class ArgumentAndFieldPair {
	String argument;
	String field;
	
	public ArgumentAndFieldPair(String n, String t) {
		argument = n;
		field = t;
	}
	
	public String getArgument() {
		return argument;
	}
	
	public String getField() {
		return field;
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
