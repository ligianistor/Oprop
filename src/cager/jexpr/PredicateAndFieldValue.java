package cager.jexpr;

public class PredicateAndFieldValue {
	String predicate;
	String fieldValue;
	
	public PredicateAndFieldValue(String predicate_, String fieldValue_) {
		predicate = predicate_;
		fieldValue = fieldValue_;
	}
	
	 public boolean equals(Object o) {
	      return (((PredicateAndFieldValue)o).predicate).equals(this.predicate) &&
	    		  (((PredicateAndFieldValue)o).fieldValue).equals(this.fieldValue);
	 }
	
	  public int hashCode(Object o){
		    return (predicate.hashCode() * fieldValue.hashCode());
		  }
	 
	  public void print() {
		  System.out.println("predicateAndField" + predicate + " " + fieldValue);
	  }
	
}
