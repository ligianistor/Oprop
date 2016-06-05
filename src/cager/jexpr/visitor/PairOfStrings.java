package cager.jexpr.visitor;

public class PairOfStrings {
  String ensuresForallPacked;
  String ensuresForallFractions;
  
  public PairOfStrings() {
	  ensuresForallPacked = "";
	  ensuresForallFractions = "";
  }
  
  void setEnsuresForallPacked(String s) {
	  ensuresForallPacked = s;
  }
  
  void setEnsuresForallFractions(String s) {
	  ensuresForallFractions = s;
  }
  
  String getEnsuresForallPacked() {
	  return ensuresForallPacked;
  }
  
  String getEnsuresForallFractions() {
	  return ensuresForallFractions;
  }
  
  void concatPacked(String s) {
	  ensuresForallPacked = ensuresForallPacked.concat(s);
  }
  
  void concatFractions(String s) {
	  ensuresForallFractions = ensuresForallFractions.concat(s);
  }
  
}
