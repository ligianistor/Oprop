package cager.jexpr.ast;

import java.util.LinkedList;

public class FractionManipulationStatement {
	private String ifCondition;
	private String predName;
	private String fractionObject; 
	private String fractionValue;
	
	public FractionManipulationStatement(
			String ifCondition_,
			String predName_,
			String fractionObject_,
			String fractionValue_		
	) {
		ifCondition = ifCondition_;
		predName = predName_;
		fractionObject = fractionObject_;
		fractionValue = fractionValue_;
	}
	
	
	public String getIfCondition() {
		return ifCondition;
	}
	
	public String getPredName() {
		return predName;
	}
	
	public String getFractionObject() {
		return fractionObject;
	}
	
	public String getFraction() {
		return fractionValue;
	}
	
	@Override
public boolean equals(Object obj) {
		
		if (obj == this) {
            return true;
        }
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }
      
        FractionManipulationStatement o1 = (FractionManipulationStatement) obj;

		if ( ifCondition.equals(o1.getIfCondition()) &&
			predName.equals(o1.getPredName()) &&
			fractionObject.equals(o1.getFractionObject()) &&
			(fractionValue.equals(o1.getFraction()))
			) 
			// TODO maybe I don't need to compare to the actual value of the fraction
		{
			return true;
		}
		return false;
	}
	
	
	@Override
	public int hashCode(){
		int result = 1;
		result *= ifCondition.hashCode();
		result *= predName.hashCode();
		result *= fractionObject.hashCode();
	    return result;
	  }
	
	public void writeOut() {
		System.out.println("FractionManipulationStatement: " + ifCondition +" "+ 
				predName + " "+ fractionObject +"\n");
	}
}
