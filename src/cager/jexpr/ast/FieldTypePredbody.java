package cager.jexpr.ast;

import java.util.LinkedList;

public class FieldTypePredbody {
	LinkedList<FieldAndTypePair> formalParameters ;
	LinkedList<FieldAndTypePair> existentialParameters;
	String predBody;
	
	public FieldTypePredbody(
			LinkedList<FieldAndTypePair> formalParameters,
			LinkedList<FieldAndTypePair> existentialParameters,
			String predBody
			) {
		this.formalParameters = new LinkedList<FieldAndTypePair>();
		this.formalParameters = formalParameters;
		this.existentialParameters =  new LinkedList<FieldAndTypePair>();
		this.existentialParameters = existentialParameters;
		this.predBody = predBody;
	}
	
	public FieldTypePredbody() {
		this.formalParameters = new LinkedList<FieldAndTypePair>();
		this.existentialParameters = new LinkedList<FieldAndTypePair>();
		this.predBody = "";
	}
	
	public LinkedList<FieldAndTypePair> getFormalParameters() {
		return formalParameters;
	}
	
	public LinkedList<FieldAndTypePair> getExistentialParameters() {
		return existentialParameters;
	}
	
	public String getPredicateBody() {
		return predBody;
	}
	
	public FieldTypePredbody concatToPredicateBody(String s) {
		predBody = predBody.concat(s);
		return this;
	}
	
	public FieldTypePredbody addParam(String name, String type) {
		formalParameters.add(new FieldAndTypePair(name, type));
		return this;
	}
	
	public FieldTypePredbody addExistentialParam(String name, String type) {
		existentialParameters.add(new FieldAndTypePair(name, type));
		return this;
	}


}
