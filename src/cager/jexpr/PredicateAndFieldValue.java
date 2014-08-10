package cager.jexpr;

import cager.jexpr.visitor.ObjPropString;

public class PredicateAndFieldValue {
	String predicate;
	String fieldValue;
	
	public PredicateAndFieldValue(String predicate_, String fieldValue_) {
		predicate = predicate_;
		fieldValue = fieldValue_;
	}
	
	 public boolean equals(Object obj) {
		 if (obj == this) {
	            return true;
	        }
	        if (obj == null || obj.getClass() != this.getClass()) {
	            return false;
	        }
	      
	        PredicateAndFieldValue o = (PredicateAndFieldValue) obj;
		 
	      return (o.predicate).equals(this.predicate) &&
	    		  (o.fieldValue).equals(this.fieldValue);
	 }
	
	  public int hashCode(){
		    return (predicate.hashCode() * fieldValue.hashCode());
		  }
	 
	  public void print() {
		  System.out.println("predicateAndField" + predicate + " " + fieldValue);
	  }
	
}
