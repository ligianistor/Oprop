package cager.jexpr.ast;

import java.util.LinkedList;

public class FractionManipulationStatement {
	private String ifCondition;
	private LinkedList<String> formalParameters  = new LinkedList<String>();
	private LinkedList<String> actualParameters  = new LinkedList<String>();
	private String predName;
	private String fractionObject; 
	private double fraction;
	
	public String getIfCondition() {
		return ifCondition;
	}
	
	public LinkedList<String> getFormalParameters() {
		return formalParameters;
	}
	
	public LinkedList<String> getActualParameters() {
		return actualParameters;
	}
	
	public String getPredName() {
		return predName;
	}
	
	public String getFractionObject() {
		return fractionObject;
	}
	
	public double getFraction() {
		return fraction;
	}
	
	private boolean areLinkedListsEqual(LinkedList<String> other, LinkedList<String> thisList) {
		boolean result = true;
		for (int i=0; i<other.size(); i++) {
			if (!thisList.get(i).equals(other.get(i)))
				result = false;
		}
		return result;
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
			areLinkedListsEqual(o1.getFormalParameters(), formalParameters) &&
			areLinkedListsEqual(o1.getActualParameters(), actualParameters) &&
			predName.equals(o1.getPredName()) &&
			fractionObject.equals(o1.getFractionObject()) &&
			(fraction == o1.getFraction()) 
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
		for (int i=0; i<formalParameters.size(); i++) {
			result *= formalParameters.get(i).hashCode();
		}
		for (int i=0; i<actualParameters.size(); i++) {
			result *= formalParameters.get(i).hashCode();
		}
		result *= predName.hashCode();
		result *= fractionObject.hashCode();
	    return result;
	  }
}
