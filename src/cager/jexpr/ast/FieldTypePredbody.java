package cager.jexpr.ast;

import java.util.LinkedList;

public class FieldTypePredbody {
	LinkedList<FieldAndTypePair> formalParameters ;
	String predBody;
	
	public FieldTypePredbody(
			LinkedList<FieldAndTypePair> formalParameters,
			String predBody
			) {
		this.formalParameters = new LinkedList<FieldAndTypePair>();
		this.formalParameters = formalParameters;
		this.predBody = predBody;
	}
	
	public FieldTypePredbody() {
		this.formalParameters = new LinkedList<FieldAndTypePair>();
		this.predBody = "";
	}
	
	public LinkedList<FieldAndTypePair> getFormalParameters() {
		return formalParameters;
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


}
