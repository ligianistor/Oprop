package cager.jexpr.visitor;

public class TupleOfEnsures {
  String ensuresForallPacked;
  String ensuresForallFractions;
  String ensuresForallParam;
  
  public TupleOfEnsures() {
	  ensuresForallPacked = "";
	  ensuresForallFractions = "";
	  ensuresForallParam = "";
  }
  
  void setEnsuresForallPacked(String s) {
	  ensuresForallPacked = s;
  }
  
  void setEnsuresForallFractions(String s) {
	  ensuresForallFractions = s;
  }
  
  void setEnsuresForallParam(String s) {
	  ensuresForallParam = s;
  }
  
  String getEnsuresForallPacked() {
	  return ensuresForallPacked;
  }
  
  String getEnsuresForallFractions() {
	  return ensuresForallFractions;
  }
  
  String getEnsuresForallParam() {
	  return ensuresForallParam;
  }
  
  void concatPacked(String s) {
	  ensuresForallPacked = ensuresForallPacked.concat(s);
  }
  
  void concatFractions(String s) {
	  ensuresForallFractions = ensuresForallFractions.concat(s);
  }
  
  void concatParam(String s) {
	  ensuresForallParam = ensuresForallParam.concat(s);
  }
  
}
