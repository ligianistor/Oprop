package cager.jexpr.visitor;

//This class is used to represent and get to the binary expressions inside predicates.
public class BinExprString {
	String binaryExpression;
	//This is the actual object.
	//The binaryExpression is always written in terms of "this", 
	//but we can replace it by this object.
	String object;
	
	public BinExprString(String binaryExpression_, String object_) {
		binaryExpression = binaryExpression_;
		object = object_;
	}
	
	String getBinaryExpression() {
		return binaryExpression;
	}
	
	String getObject() {
		return object;
	}
		
	@Override
	//Two objects are equal if the field, operand2 and operator are equal.
	public boolean equals(Object obj) {
		
		if (obj == this) {
            return true;
        }
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }
      
        BinExprString o1 = (BinExprString) obj;

		if (
				binaryExpression.equals(o1.getBinaryExpression()) &&
				object.equals(o1.getObject())
			)
		{
			return true;
		}

		return false;
	}
	
	@Override
	public int hashCode(){
	    return (binaryExpression.hashCode() * object.hashCode());
	  }
	
	

}
