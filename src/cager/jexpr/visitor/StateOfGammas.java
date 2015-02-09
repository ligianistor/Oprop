package cager.jexpr.visitor;

import java.util.LinkedList;

public class StateOfGammas {
	
	LinkedList<ObjPropString> GammaPacked0;
	LinkedList<ObjPropString> GammaUnpacked0;
	LinkedList<String> GammaPiecesOfObjProp0;
	//resultString is the succession of packed[] := .. that
	//is actually being put in the translation.
	String resultString;
	
	public StateOfGammas() {
		GammaPacked0 = new LinkedList<ObjPropString>();
		GammaUnpacked0 = new LinkedList<ObjPropString>();
		GammaPiecesOfObjProp0 = new LinkedList<String>();
		resultString = "";
	}
	
	public void setGammaPacked(LinkedList<ObjPropString> GammaPacked_){
		GammaPacked0 = GammaPacked_;
	}
	
	public void setGammaUnpacked(LinkedList<ObjPropString> GammaUnpacked_){
		GammaUnpacked0 = GammaUnpacked_;
	}
	
	public void setGammaPiecesOfObjProp(LinkedList<String> GammaPiecesOfObjProp_){
		GammaPiecesOfObjProp0= GammaPiecesOfObjProp_;
	}
	
	public void setResultString(String resultString_){
		resultString= resultString_;
	}
	
	public LinkedList<ObjPropString> getGammaPacked(){
		return GammaPacked0;
	}
	
	public LinkedList<ObjPropString> getGammaUnpacked(){
		return GammaUnpacked0;
	}
	
	public LinkedList<String> getGammaPiecesOfObjProp(){
		return GammaPiecesOfObjProp0;
	}
	
	public String getResultString(){
		return resultString;
	}
	

}
