package cager.jexpr.visitor;

import java.util.LinkedList;

public class ObjPropString {
	String object;
	int maxFrac;
	int minFrac;
	String fracParam;
	String name;
	String[] params;
	
	ObjPropString(String object_, String fracParam_,String name_, LinkedList<String> params_) 
		{
		object = object_;
		fracParam = fracParam_;
		name = name_;
		int numberParams = params_.size();
		params = new String[numberParams];
		
			for (int i = 0; i < numberParams; i++) {
				params[i] = params_.get(i);	
		}
	}
	
	String getObject() {
		return object;
	}
	
	String getName() {
		return name;
	}
	
	void setMaxFrac(int max) {
		maxFrac = max;
		
	}
	
	void setMinFrac(int min) {
		 minFrac = min;
	}
	
	int getMaxFrac() {
		return maxFrac;
		
	}
	
	int getMinFrac() {
		return minFrac;
	}
	
	@Override
public boolean equals(Object obj) {
		
		if (obj == this) {
            return true;
        }
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }
      
        ObjPropString o1 = (ObjPropString) obj;

		if (object.equals(o1.getObject()) && name.equals(o1.getName()))
		{
			return true;
		}

		return false;
	}
	
	void print() {
		String objProp=object+"@"+fracParam+" "+name+"(";
		int numberParams = params.length;
		for (int i = 0; i < numberParams-1; i++) {
			objProp = objProp.concat(params[i]+",");	
	}
		if (numberParams >=1){
			objProp = objProp.concat(params[numberParams-1]);	
		}
		objProp = objProp.concat(")");	
		System.out.println(objProp);
	
	}
	
	@Override
	public int hashCode(){
	    return (object.hashCode() * name.hashCode());
	  }

}
