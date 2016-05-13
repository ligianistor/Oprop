package cager.jexpr.ast;

public class FractionManipulationStatement {
	private String ifCondition;
	private String predName;
	private String fractionObject; 
	private String fractionValue;
	private int disjunctionNumber;
	
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
		//The fraction manipulation is not usually in a disjunction
		//and the -1 represents just this.
		disjunctionNumber = -1;
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
	
	public void setDisjunctionNumber(int d) {
		disjunctionNumber = d;
	}
	
	public int getDisjunctionNumber() {
		return disjunctionNumber;
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
			(disjunctionNumber == o1.getDisjunctionNumber())
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
		result *= disjunctionNumber;
	    return result;
	  }
	
	public void writeOut() {
		System.out.println("FractionManipulationStatement: " + ifCondition +" "+ 
				predName + " "+ fractionObject + " " + disjunctionNumber + "\n");
	}
}
