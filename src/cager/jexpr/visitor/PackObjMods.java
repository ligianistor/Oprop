package cager.jexpr.visitor;

import java.util.LinkedList;

// This is shorthand for PackedObjectModifications.
public class PackObjMods {
	
	String objectString;
	
	//The Integer 1 represents true, -1 represents false.
	//Whenever there is packedPredicate[this]:=false;
	//in the body of a method, we add a -1 to the PackObjMods modifications
	//for "this". The objects PackObjMods correspond to the names of the predicates, 
	//in the hashmap in BoogieVisitor.
	LinkedList<Integer> modifications;
	
	public PackObjMods(String obj) {
		objectString = obj;
		modifications = new LinkedList<Integer>();
	}
	
	// i can only be 1 or -1.
	public void addModification(int i){
		modifications.add(new Integer(i));
	}
	
	public String getObjectString() {
		return objectString;
	}
	
	// Since modifications can contain only 1 and -1,
	// if we do the sum and it is 0, it means that
	// there is an equal number of true and false in the 
	// body of the method and the packed has the same value 
	// as in the beginning.
	// If the sum is not 0, it means the packed was modified.
	public boolean isPackedModified() {
		int sum = 0;
		for (int i=0; i < modifications.size(); i++) {
			Integer intObj = modifications.get(i);
			sum = sum + intObj.intValue();
		}
		
		if (sum == 0) {
			return false;
		} else {
			return true;
		}
	}
	
	@Override
public boolean equals(Object obj) {
		
		if (obj == this) {
            return true;
        }
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }
      
        PackObjMods o1 = (PackObjMods) obj;

		if (objectString.equals(o1.getObjectString()))
		{
			return true;
		}

		return false;
	}
	
	
	@Override
	public int hashCode(){
	    return objectString.hashCode();
	  }
	
}
