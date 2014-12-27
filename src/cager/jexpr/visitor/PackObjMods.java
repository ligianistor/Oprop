package cager.jexpr.visitor;

import java.util.LinkedList;

// This is shorthand for PackedObjectModifications.
public class PackObjMods {
	
	String objectString;
	
	//The String "1" represents true, "0" represents false.
	//Whenever there is packedShareCount[this]:=false;
	//in the body of a method, we add a "0" to the PackObjMods modifications
	//for "this". The objects PackObjMods correspond to the names of the predicates, 
	//in the hashmap in BoogieVisitor.
	LinkedList<String> modifications;
	
	public PackObjMods(String obj) {
		objectString = obj;
		modifications = new LinkedList<String>();
	}
	
	public void addModification(boolean b){
		if (b) {
			modifications.add("1");
		} else {
			modifications.add("0");
		}
	}
	
	public String getObjectString() {
		return objectString;
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
