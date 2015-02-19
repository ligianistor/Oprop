package cager.jexpr.visitor;

import java.util.LinkedList;

public class ObjPropString {
	String object;
	double maxFrac;
	double minFrac;
	double exactFrac;
	String fracParam;
	String predicateName;
	LinkedList<String> params;
	
	ObjPropString(String object_, String fracParam_,String predicateName_, LinkedList<String> params_) 
		{
		object = object_;
		fracParam = fracParam_;
		predicateName = predicateName_;
		params = new LinkedList<String>();
		params = params_;
			
		// All these fields below have default values.
	    // The value -1 is an error flag.
		maxFrac = -1;
		minFrac = -1;
		exactFrac = -1;		
	}
	
	String getObject() {
		return object;
	}
	
	public LinkedList<String> getParams() {
		return params;
	}
	
	void setObject(String newObject) {
		object = newObject;
	}
	
	String getName() {
		return predicateName;
	}
	
	public void addParam(String param) {
		params.add(param);
	}
	
	void setMaxFrac(double max) {
		maxFrac = max;
		
	}
	
	void setMinFrac(double min) {
		 minFrac = min;
	}
	
	void setExactFrac(double e) {
		exactFrac = e;
	}
	
	double getMaxFrac() {
		return maxFrac;
		
	}
	
	double getMinFrac() {
		return minFrac;
	}
	
	double getExactFrac() {
		return exactFrac;
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

		if (object.equals(o1.getObject()) && predicateName.equals(o1.getName()))
		{
			return true;
		}

		return false;
	}
	
	void print() {
		String objProp="  "+object+"@"+fracParam+" "+predicateName+"(";
		int numberParams = params.size();
		for (int i = 0; i < numberParams-1; i++) {
			objProp = objProp.concat(params.get(i)+",");	
	}
		if (numberParams >=1){
			objProp = objProp.concat(params.get(numberParams-1));	
		}
		objProp = objProp.concat(")");	
		System.out.println(objProp);
	
	}
	
	@Override
	public int hashCode(){
	    return (object.hashCode() * predicateName.hashCode());
	  }

}
