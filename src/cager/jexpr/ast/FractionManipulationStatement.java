package cager.jexpr.ast;

import java.util.LinkedList;

import cager.jexpr.visitor.BinExprString;

public class FractionManipulationStatement implements WriteOut, Comparable<Object> {
	private String ifCondition;
	private String predName;
	private String fractionObject; 
	private String fractionValue;
	private int disjunctionNumber;
	private boolean isPacked;
	//The objects of type BinExprString here actually represent pairs 
	//of strings: the first one is the name of the parameter, such as paramPredVal,
	//and the second one is the actual value for that parameter, stored as a string.
	private LinkedList<BinExprString> params = new LinkedList<BinExprString>();
	
	public FractionManipulationStatement(
			String ifCondition_,
			String predName_,
			String fractionObject_,
			String fractionValue_,
			LinkedList<BinExprString> params_
	) {
		ifCondition = ifCondition_;
		predName = predName_;
		fractionObject = fractionObject_;
		fractionValue = fractionValue_;
		//The fraction manipulation is not usually in a disjunction
		//and the -1 represents just this.
		disjunctionNumber = -1;
		// The object propositions are usually packed than not.
		isPacked = true;
		params = params_;
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
	
	public LinkedList<BinExprString> getParams() {
		return params;
	}
	
	public void setDisjunctionNumber(int d) {
		disjunctionNumber = d;
	}
	
	public int getDisjunctionNumber() {
		return disjunctionNumber;
	}
	
	public void setIsPacked(boolean b) {
		isPacked = b;
	}
	
	public boolean getIsPacked() {
		return isPacked;
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
			// The disjunction number needs to be put here
			// because there could be two statements that appear 
			// twice, but in two disjunctions. And they are not the same,
			// the disjunction number sets them apart.
			(disjunctionNumber == o1.getDisjunctionNumber()) &&
			(isPacked == o1.getIsPacked())
			) 
			// TODO maybe I don't need to compare to the actual value of the fraction
		{
			return true;
		}
		return false;
	}
	
	// whether 2 objects are equal from the packed point of view,
	// even if their fractions are not equal
public boolean equalsForPacked(Object obj) {	
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
			(isPacked == o1.getIsPacked())
			) 
		{
			return true;
		}
		return false;
	}

// whether 2 objects are equal from the fractions point of view
// even if their parameters or packing state are not equal
public boolean equalsForFractions(Object obj) {
	
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
		fractionValue.equals(o1.getFraction()) 
		) 
	{
		return true;
	}
	return false;
}

//TODO even these equalsFor functions can be made into 1
//whether 2 objects are equal from the parameters point of view
//even if their fractions or packing state are not equal
public boolean equalsForParams(Object obj) {
 if (obj == this) {
     return true;
 }
 if (obj == null || obj.getClass() != this.getClass()) {
     return false;
 }

 FractionManipulationStatement o1 = (FractionManipulationStatement) obj;
 boolean result = true;
	if ( ifCondition.equals(o1.getIfCondition()) &&
		predName.equals(o1.getPredName()) &&
		fractionObject.equals(o1.getFractionObject())
		) 
	{
		LinkedList<BinExprString> paramso1 = o1.getParams();
		for (int i=0;i<paramso1.size();i++){
			if (!paramso1.get(i).equals(params.get(i))) {
				result = false;
				break;
			}
		}
	}
	return result;
}
	
	@Override
	public int hashCode(){
		int result = 1;
		result *= ifCondition.hashCode();
		result *= predName.hashCode();
		result *= fractionObject.hashCode();
		result *= disjunctionNumber;
		result *= isPacked?1:2;
	    return result;
	  }
	
	public void writeOut() {
		System.out.println("FractionManipulationStatement: " + ifCondition +" "+ 
				predName + " "+ fractionObject + " " + fractionValue + " " +
				+ disjunctionNumber + " " + isPacked + "\n");
	}

	@Override
	public int compareTo(Object o) {
		    if(this.equals((FractionManipulationStatement)o))
		        return 0;
		    else if (this.predName.compareTo(((FractionManipulationStatement)o).getPredName()) > 0)
		        return 1;
		    else 
		        return -1;
	}
}
