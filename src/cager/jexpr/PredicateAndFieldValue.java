package cager.jexpr;

import cager.jexpr.visitor.ObjPropString;

public class PredicateAndFieldValue {
	String predicate;
	String fieldValue;
	
	public PredicateAndFieldValue(String predicate_, String fieldValue_) {
		predicate = predicate_;
		fieldValue = fieldValue_;
	}
	
	public String getPredicate() {
		return predicate;
	}
	
	public String getFieldValue() {
		return fieldValue;
	}
		
	 public boolean equals(Object obj) {
		 if (obj == this) {
	            return true;
	        }
	        if (obj == null || obj.getClass() != this.getClass()) {
	            return false;
	        }
	      
	        PredicateAndFieldValue o = (PredicateAndFieldValue) obj;
	        		 
	      if (predicate.equals(o.getPredicate()) &&
	    	 fieldValue.equals(o.getFieldValue()))
	      {
	    	  return true;}
	      else {
	    	  return false;}

	 }
	
	  public int hashCode(){
		    return (predicate.hashCode() * fieldValue.hashCode());
		  }
	 
	  public void println() {
		  System.out.println("predicateAndField: " + predicate + " " + fieldValue);
	  }
	
}
