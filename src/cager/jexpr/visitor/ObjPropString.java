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

}
