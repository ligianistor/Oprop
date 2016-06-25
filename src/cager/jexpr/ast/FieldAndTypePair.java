package cager.jexpr.ast;

public class FieldAndTypePair implements Comparable{
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
	
	@Override
public boolean equals(Object obj) {
		
		if (obj == this) {
            return true;
        }
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }
      
        FieldAndTypePair o1 = (FieldAndTypePair) obj;

		if (name.equals(o1.getName()) && type.equals(o1.getType()))
		{
			return true;
		}

		return false;
	}
	
	
	@Override
	public int hashCode(){
	    return (name.hashCode() * type.hashCode());
	  }
	
	public int compareTo(Object o)
	{
		String result = name+" "+type;
		return result.compareTo(((FieldAndTypePair)o).getName()+" "+((FieldAndTypePair)o).getType());
	}
	
}


