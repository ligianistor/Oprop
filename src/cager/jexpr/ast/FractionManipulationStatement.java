package cager.jexpr.ast;

public class FractionManipulationStatement {
	private String ifCondition;
	private String predName;
	private String fractionObject; 
	private String fractionValue;
	private String predName2; 
	// This is for the case
	// when we have ifcondition ==> objectProposition1 || objectProposition2
	// This is just a case of what can appear in the pre- and post-conditions
	// and the generalization (maybe write a linked list instead of 
	// only being able to handle an or with 2 disjuncts)
	// is left as future work.
	// This means that I need to add the infrastructure
	// to know if I am in the second disjunct.
	private String fractionObject2; 
	private String fractionValue2;
	
	
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
		predName2 = "";
		fractionObject2 = "";
		fractionValue2 = "";
		
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
	
	public String getPredName2() {
		return predName2;
	}
	
	public String getFractionObject2() {
		return fractionObject2;
	}
	
	public String getFraction2() {
		return fractionValue2;
	}
	
	public void setPredName2(String pred2) {
		predName2 = pred2;
	}
	
	public void setFractionObject2(String fracObj2) {
		fractionObject2 = fracObj2;
	}
	
	public void setFraction2(String frac2) {
		fractionValue2 = frac2;
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
			fractionValue.equals(o1.getFraction()) &&
			predName2.equals(o1.getPredName2()) &&
			fractionObject2.equals(o1.getFractionObject2()) &&
			fractionValue2.equals(o1.getFraction2())
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
		result *= predName2.hashCode();
		result *= fractionObject2.hashCode();
	    return result;
	  }
	
	public void writeOut() {
		System.out.println("FractionManipulationStatement: " + ifCondition +" "+ 
				predName + " "+ fractionObject +
				"||"+
				predName2 + " "+ fractionObject2 +
				"\n");
	}
}
