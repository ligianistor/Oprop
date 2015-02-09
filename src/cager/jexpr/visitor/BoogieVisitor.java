package cager.jexpr.visitor;

import java.io.BufferedWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;
import cager.jexpr.JExprConstants;
import cager.jexpr.ParseException;
import cager.jexpr.PredicateAndFieldValue;
import cager.jexpr.ast.AST;
import cager.jexpr.ast.AllocationExpression;
import cager.jexpr.ast.ArgumentList;
import cager.jexpr.ast.BinaryExpression;
import cager.jexpr.ast.Block;
import cager.jexpr.ast.ClassDeclaration;
import cager.jexpr.ast.CompilationUnit;
import cager.jexpr.ast.CompilationUnits;
import cager.jexpr.ast.DeclarationStatement;
import cager.jexpr.ast.Expression;
import cager.jexpr.ast.FieldAndTypePair;
import cager.jexpr.ast.FieldDeclaration;
import cager.jexpr.ast.FieldSelection;
import cager.jexpr.ast.FieldTypePredbody;
import cager.jexpr.ast.FormalParameter;
import cager.jexpr.ast.FormalParameters;
import cager.jexpr.ast.IdentifierExpression;
import cager.jexpr.ast.IfStatement;
import cager.jexpr.ast.KeywordExpression;
import cager.jexpr.ast.LiteralExpression;
import cager.jexpr.ast.LocalVariableDeclaration;
import cager.jexpr.ast.MethodDeclaration;
import cager.jexpr.ast.MethodSelection;
import cager.jexpr.ast.MethodSpecExpression;
import cager.jexpr.ast.MethodSpecVariable;
import cager.jexpr.ast.MethodSpecVariables;
import cager.jexpr.ast.ObjectProposition;
import cager.jexpr.ast.PredicateDeclaration;
import cager.jexpr.ast.PrimaryExpression;
import cager.jexpr.ast.QuantifierVariable;
import cager.jexpr.ast.ReturnStatement;
import cager.jexpr.ast.StatementExpression;
import cager.jexpr.ast.TypedAST;
import cager.jexpr.ast.UnaryExpression;

/*
 * This class visits the AST to generate the Boogie code.
 */
public class BoogieVisitor extends NullVisitor {
	
	HashMap<PredicateAndFieldValue, String> quantifiedVars = new HashMap<PredicateAndFieldValue, String> ();
	
	//The name of the Java class.
	String className;
	
	//The name of the current local variable
	String localVariableName = "";
	
	//Is this the first method that is being translated 
	boolean isFirstMethod = true;
	
	//Are we in an argument list?
	boolean inArgumentList = false;
	
	//Are we inside an IfStatement?
	//We need this because there are Blocks inside an IfStatement and 
	//they get confused with the Blocks at the beginning of 
	//method declarations.
	boolean inIfStatement = false;
	
	//True iff this program contains the modulo operator.
	boolean programContainsModulo = false;
	boolean writtenModuloFunction = false;
	
	//Does this primary expression contain a methodSelection?
	//Are we inside a method selection statement?
	boolean inMethodSelectionStatement = false;
	
	//The name of the latest identifier or field[this].
	//Its use is in visitMethodSelection.
	String currentIdentifier = "";
		
	String lastPrimaryExpressionType = "";
	
	//For each predicate name, this maps to a list of PackObjMods. 
	//This map needs to be reset in the beginning of each method.
	//The first String represents the name of the predicate.
	HashMap<String, LinkedList<PackObjMods>> packedMods = 
			new HashMap<String, LinkedList<PackObjMods>>();
	
	//For each predicate name, this maps it to its body represented as a String.
	HashMap<String, FieldTypePredbody> paramsPredicateBody = new HashMap<String, FieldTypePredbody>();
	
	//This maps each method name to its String method body.
	HashMap<String, String> methodBody = new HashMap<String, String>();
	
	//This maps each method name to its String method specification(pre- and post-condition).
	HashMap<String, String> methodSpec = new HashMap<String, String>();
	
	//This maps each method name to the parameters of that method.
	//I think they are separated by commas.
	HashMap<String, String> methodParams = new HashMap<String, String>();
	
	//For each name of a field, this map tells us which is the predicate that 
	//has the permission to the field.
	//Might not be correct because there might be more than one predicate for the same
	//field, but they don't exist at the same time in a method. 
	HashMap<String, LinkedList<String>> fieldWhichPredicates = new HashMap<String, LinkedList<String>>();
	
	
	//This gathers the fields in each statement. Not in each block, but in each statement.
	//A statement is a child of a block.
	Set<String> fieldsInStatement = new TreeSet<String>();
	
	//Holds the packed object propositions of a method, starting with 
	//the object propositions in the pre-condition of the method.
	LinkedList<ObjPropString> GammaPacked = new LinkedList<ObjPropString>();
	
	//Holds the unpacked object propositions of a method, starting with 
	//the object propositions that are unpacked in the pre-condition of the method.
	LinkedList<ObjPropString> GammaUnpacked = new LinkedList<ObjPropString>();
	
	//Holds the constituent pieces of an object proposition for a method, starting with 
	//pieces in the pre-condition of the method.
	//We can think of the strings in this linked list as being concatenated by 
	//&&. When we use a pieces, we manipulate the string that contains it.
	LinkedList<String> GammaPiecesOfObjProp = new LinkedList<String>();
	
	//Holds the binary expressions that come from unpacking a predicate,
	//for a method.
	LinkedList<BinExprString> GammaBinExpr = new LinkedList<BinExprString>();
		
	//For each method, this map tells us which are the
	//packed object propositions in the precondition of that method.  
	HashMap<String, LinkedList<ObjPropString>> methodPreconditionsPacked = 
			new HashMap<String, LinkedList<ObjPropString>>();
	
	//For each method, this map tells us which are the
	//unpacked object propositions in the precondition of that method.  
	HashMap<String, LinkedList<ObjPropString>> methodPreconditionsUnpacked = 
			new HashMap<String, LinkedList<ObjPropString>>();
	
	//For each method, this map tells us which are the
	//pieces of object propositions in the precondition of that method.  
	HashMap<String, LinkedList<String>> methodPreconditionsPieces = 
			new HashMap<String, LinkedList<String>>();
		
	//For each method, this map tells us which are the
	//packed object propositions in the postcondition of that method. 
	HashMap<String, LinkedList<ObjPropString>> methodPostconditionsPacked = 
			new HashMap<String, LinkedList<ObjPropString>>();
	
	//For each method, this map tells us which are the
	//unpacked object propositions in the postcondition of that method.
	HashMap<String, LinkedList<ObjPropString>> methodPostconditionsUnpacked = 
			new HashMap<String, LinkedList<ObjPropString>>();
	
	//For each method, this map tells us which are the
	//pieces of object propositions in the postcondition of that method. 
	HashMap<String, LinkedList<String>> methodPostconditionsPieces = 
			new HashMap<String, LinkedList<String>>();
	
	//For each procedure, including the Pack and Unpack ones
	//this map tells us which frac[] are in its "requires" clause
	HashMap<String, LinkedList<FracString>> requiresFrac =
			new HashMap<String, LinkedList<FracString>>();
	
	//For each procedure, including the Pack and Unpack ones
	//this map tells us which frac[] are in its "ensures" clause
	HashMap<String, LinkedList<FracString>>  ensuresFrac = 
			new HashMap<String, LinkedList<FracString>>();
	
	//For each predicate, this map
	//tells us which frac[] are in the body of the predicate
	HashMap<String, LinkedList<FracString>>  predicateFrac = 
			new HashMap<String, LinkedList<FracString>>();
	
	//For each predicate, this map 
	//tells us which ObjPropString are in the body of the predicate
	HashMap<String, LinkedList<ObjPropString>>  predicateObjProp = 
			new HashMap<String, LinkedList<ObjPropString>>();
	
	//For each predicate, this map 
	//tells us which BinExprString are in the body of the predicate
	HashMap<String, LinkedList<BinExprString>>  predicateBinExpr = 
			new HashMap<String, LinkedList<BinExprString>>();
	
	
	//I made a FractionString class.
	//For each Pack and Unpack procedure, but mainly Pack,
	//I have a list of FractionString 's that
	//gets updated in the same way like methodPreconditions.
	//This will let us make updates to fracOK, etc
	//when a Pack procedure is called.
	
	
	//At the beginning of each statement, this is made "".
	//This contains the string of each statement.
	String statementContent;
	
	boolean inStatement = false;
	
	//This becomes true if a method selection was visited in a 
	//statement.
	boolean visitedMethSel = false;
	
	//The file where the output is written.
	BufferedWriter out;
	
	//The name of the current predicate that we are parsing.
	String namePredicate;
	
	//The name of the current method that we are parsing in Oprop 
	//and translating in Boogie. 
	String currentMethod;
	
	//The set of fields of this Oprop class.
	//This includes packed and frac, but fieldsTypes does not.
	Set<String> fields = new TreeSet<String>();
	
	//The arraylist of (field, type) pairs of this Oprop class.
	ArrayList<FieldAndTypePair> fieldsTypes = new ArrayList<FieldAndTypePair>();
	
	//The set of predicates of this Oprop class.
	Set<String> predicates = new TreeSet<String>();
	
	//The set of methods in this Oprop class.
	Set<String> methods = new TreeSet<String>();
	
	//For each method, fieldsInMethod contains the set of fields in that method.
	Set<String> fieldsInMethod = new TreeSet<String> (); 
	
	//This boolean is true iff we are currently parsing an object proposition. 
	boolean insideObjectProposition;
	
	//This boolean is true iff we are currently inside the pre-condition of a method.
	boolean insidePrecondition;
	
	//The string of the object proposition inside which we are at the moment.
	String objectPropString;
	
	//The Boogie Visitors of the files that have been translated before this one.
	BoogieVisitor[] bv;
	
	int numberFilesBefore;
	
	
	public BoogieVisitor(BufferedWriter boogieFile, int n, BoogieVisitor[] bv0) {
		out = boogieFile;
		namePredicate = "";
		currentMethod = ""; 
		insideObjectProposition = false;
		insidePrecondition = false;
		objectPropString = "";
		statementContent = "";
		bv = bv0;
		numberFilesBefore = n;
	}
	
	//public getter functions
	//To use in the subsequent BoogieVisitor classes.
	//Each input Java file is translated using a BoogieVisitor.
	
	public String getClassName() {
		return className;
	}
	
	public Set<String> getPredicates() {
		return predicates;
	}
	
	public Set<String> getMethods() {
		return methods;
	}
	
	public HashMap<String, LinkedList<ObjPropString>> getMethodPreconditionsPacked() {
		return methodPreconditionsPacked;
	}

	public HashMap<String, LinkedList<ObjPropString>> getMethodPostconditionsPacked() {
		return methodPostconditionsPacked;
	}
	
	public HashMap<String, LinkedList<FracString>> getRequiresFrac() {
		return requiresFrac;
	}
	
	public HashMap<String, LinkedList<FracString>>  getEnsuresFrac() {
		return ensuresFrac;
	}

	public HashMap<String, LinkedList<FracString>>  getPredicateFrac() {
		return predicateFrac;
	}
	
	public HashMap<String, LinkedList<ObjPropString>> getPredicateObjProp() {
		return predicateObjProp;
	}
	
	public HashMap<String, LinkedList<BinExprString>> getPredicateBinExpr() {
		return predicateBinExpr;
	}

	//visit methods
	
    public void visitCompilationUnits(CompilationUnits ast) throws ParseException
    {
        visitChildren(ast);
    }
    
    public void visitCompilationUnit(CompilationUnit ast) throws ParseException
    {
    	
    	ClassDeclaration c = (ClassDeclaration)(ast.getChildren()[0]);
    	className = c.getIdentifier().getName();
    		try {
    			if (numberFilesBefore == 0) {
    			out.write("type Ref;\n");
    			out.write("const null: Ref;\n");
    			out.write("\n");
    			} else {
    				out.write("//----------------------------------\n");
    				out.write("//new class\n\n");
    			}
    			
    		}
    		catch (Exception e) {
    			System.err.println("Error: " + e.getMessage());
    		}
        visitChildren(ast);
    }
    
    public void visitFieldDeclaration(FieldDeclaration ast) throws ParseException 
    { 
    	String fieldName = ast.getName();

    	fields.add(fieldName);
    	fieldsTypes.add(new FieldAndTypePair(fieldName, ast.getType().toString()));
    	
    	String fieldType = ast.getType().toString();
    	boolean isClass = false;
    	
    	for (int i=0; i<numberFilesBefore; i++) {
    		if (bv[i].getClassName().equals(fieldType) )
    				isClass = true;
    	}
    	
    	try {
    		if (isClass) {
    		out.write("var "+ fieldName +": [Ref]Ref;\n");
    		}
    		else 
    		out.write("var "+ fieldName +": [Ref]"+fieldType+";\n");
    	}
    	catch (Exception e) {
    		System.err.println("Error: " + e.getMessage());
    	}
    	visitChildren(ast);  
    	}
    
    public void visitPredicateDeclaration(PredicateDeclaration ast) throws ParseException
    { 
    	namePredicate = ast.getIdentifier().getName();
    	predicates.add(namePredicate);
    	paramsPredicateBody.put(namePredicate, new FieldTypePredbody());
    	AST[] children = ast.getChildren();
    	//Visit formal parameters.
    	children[0].accept(this);
    	//TODO
    	//need to take care of formal parameters
    	//Visit expression.
    	children[1].accept(this);	
    }
    
    public void visitQuantifierVariable(QuantifierVariable ast) throws ParseException
  	{    	   	    	
    	visitChildren(ast); 
    }
    
    
 //Since methods are not children of 
 //Predicate, we might not need namePredicate here
    public void visitMethodDeclaration(MethodDeclaration ast) throws ParseException
    {    	
		String moduloTranslation = "function modulo(x:int, y:int) returns (int); \n" +
		"axiom (forall x:int, y:int :: {modulo(x,y)}\n" +
	    "\t ((0 <= x) &&(0 < y) ==> (0 <= modulo(x,y) ) && (modulo(x,y) < y) )\n" +
	    "\t&&\n" +
	    "\t((0 <= x) &&(y < 0) ==> (0 <= modulo(x,y) ) && (modulo(x,y) < -y) )\n" +
	    "\t&&\n" +
	    "\t((x <= 0) &&(0 < y) ==> (-y <= modulo(x,y) ) && (modulo(x,y) <= 0) )\n" +
	    "\t&&\n" +
	    "\t((x <= 0) &&(y < 0) ==> (y <= modulo(x,y) ) && (modulo(x,y) <= 0) )\n" +
	   "\t);\n\n";
		
    	fieldsInMethod = new TreeSet<String> (); 
    	GammaPacked.clear();
  	
    	namePredicate = "";
    	String methodName = ast.getIdentifier().name;
    	methods.add(methodName);
    	
    	methodBody.put(methodName, "");
    	methodSpec.put(methodName, "");
    	methodParams.put(methodName, "");
    	currentMethod = methodName;
    	
    	//When we hit the first method, we write out the constructors for this 
    	//class and the Pack and Unpack procedures. 
    	if (isFirstMethod) {   	
    	//This if is for when the modulo symbol is in the body of a predicate.
		if (programContainsModulo && !writtenModuloFunction){
			try{
			out.write(moduloTranslation);
			}
			catch(Exception e) {
				System.err.println("Error: " + e.getMessage());
			}
			//We only want to write out the modulo function once.
			writtenModuloFunction = true;
		}
		
    	//Write the constructors to out. The constructor that does not pack to anything
    	//and the ones that pack to predicates.
    	makeConstructors(out);
    	
    	Iterator<Entry<String, FieldTypePredbody>> j = 
    			paramsPredicateBody.entrySet().iterator(); 
    
        while(j.hasNext()) {
     	   String currentNamePred = j.next().getKey();
     	    FieldTypePredbody paramsPred = paramsPredicateBody.get(currentNamePred);
     		String predBodyUnprocessed = paramsPred.getPredicateBody();
     		//need to do more processing of the predBody
     		//TODO
     		int i=0;
     		while (predBodyUnprocessed.charAt(i)=='&') i++;
     		String predBody = predBodyUnprocessed.substring(i);
     		
     		try {
			//will need to do something about formal parameters
			out.write("procedure Pack"+currentNamePred+"(");
			writePredParamsOut(currentNamePred, 1);
			out.write("this:Ref);\n"); 
			out.write("\t requires (packed"+currentNamePred+"[this] == false) && \n");
			out.write("\t \t(" + predBody + ");\n"); 
			out.write("\n");
			out.write("procedure Unpack"+currentNamePred+"(");
			writePredParamsOut(currentNamePred, 1);
			out.write("this:Ref);\n");
			out.write("\t requires packed"+currentNamePred+"[");
			writePredParamsOut(currentNamePred, 2);
			out.write("this] &&\n\t \t ");
			//TODO we don't do this for Pack because 
			//we do it in the code after we call the Pack procedure
			
			out.write("(frac"+currentNamePred+"[");
			writePredParamsOut(currentNamePred, 2);
			out.write("this] > 0.0);\n");
			out.write("\t ensures ("+predBody+");\n");
			out.write("\n");
     		}
     		
    		catch (Exception e) {
    			System.err.println("Error: " + e.getMessage());
    		}
     		//We are initializing packedMods here, with the names of all predicates.
     		packedMods.put(currentNamePred, new LinkedList<PackObjMods>());   
        }
        
        //We have to initialize packedMods with the names of the predicates in the 
        //previous classes too.
        for (int i=0; i<numberFilesBefore; i++) {
        	Set<String> predicatesOfi = bv[i].getPredicates();
        	for (String namePredicateOfi : predicatesOfi) {
        		packedMods.put(namePredicateOfi, new LinkedList<PackObjMods>());   
        	}
    				
    	}
          
    	}
    	     		
        //Writing the current procedure out.
    		try {
    			
    			// We reset packedMods here.
    			// Before we enter the body of the current method.
    			Iterator<Entry<String, FieldTypePredbody>> j = paramsPredicateBody.entrySet().iterator(); 
    	    	
    	        while(j.hasNext()) {
    	     	  String currentNamePred = j.next().getKey();
    	     	  packedMods.put(currentNamePred, new LinkedList<PackObjMods>()); 
    	        }
    			
				out.write("procedure "+ast.getIdentifier().getName()+"(this:Ref");
				
				visitChildren(ast);
				
				out.write(methodParams.get(currentMethod));
								
				//Need to automatically detect what is being modified, according to the Boogie manual.
				//We do this after we parse and translate the body of the current method.
				String[] fieldsInMethodArray = fieldsInMethod.toArray(new String[0]);
        		int leng = fieldsInMethodArray.length;
        		if (leng > 0) {
				String modifies = "\t modifies ";

        		for (int k = 0; k < leng - 1; k++) {
        			modifies = modifies.concat(fieldsInMethodArray[k]+",");
        		}

        		modifies = modifies.concat(fieldsInMethodArray[leng-1]+";\n");
			    out.write(modifies);
        		}
				
				out.write(methodSpec.get(currentMethod));
				

				//Here we generate 
				//"ensures (forall x:Ref :: (packedOK[x] == old(packedOK[x])));"
				// or "ensures (forall x:Ref :: ( ((x!=this) && (x!=that) ) ==> (packedOK[x] == old(packedOK[x]))));"
				// and the others.
				
		        Iterator<Entry<String, LinkedList<PackObjMods>>> it = packedMods.entrySet().iterator();
		        while (it.hasNext()) {
		        	Entry<String, LinkedList<PackObjMods>> pairs = 
		        			(Entry<String, LinkedList<PackObjMods>>)it.next();
		        	String nameOfPredicate = pairs.getKey();
		        	LinkedList<PackObjMods> objMods = pairs.getValue();
		        	Set<String> modifiedObjects = new TreeSet<String>();
		        	for (int i = 0; i < objMods.size(); i++)
		        	{
		        		PackObjMods p = objMods.get(i);
		        		if (p.isPackedModified()) {
		        			modifiedObjects.add(p.getObjectString());
		        		}	
		        	}
		        	String ensuresForall = "\t ensures (forall x:Ref :: (";
		        	if (modifiedObjects.isEmpty()) {
		        		ensuresForall = ensuresForall.concat("packed"+nameOfPredicate + 
		        				"[x] == old(packed" + nameOfPredicate +"[x])));");
		        	} else {
		        		String[] modifiedObjectsArray = modifiedObjects.toArray(new String[0]);
		        		int len = modifiedObjectsArray.length;
		        		if (len > 1) {
		        			ensuresForall = ensuresForall.concat("(");
		        		}
		        		for (int k = 0; k < len - 1; k++) {
		        			ensuresForall = ensuresForall.concat("(x!="+modifiedObjectsArray[k]+") &&");
		        		}
		        		
		        		ensuresForall = ensuresForall.concat("(x!="+modifiedObjectsArray[len-1]+") ==> ");
		        		if (len > 1) {
		        			ensuresForall = ensuresForall.concat("(");
		        		}
		        		
		        		ensuresForall = ensuresForall.concat("(packed"+ nameOfPredicate + 
		        				"[x] == old(packed"+nameOfPredicate+"[x]))));");
		        	}
		        	
		        	out.write(ensuresForall+"\n");
		            //it.remove(); avoids a ConcurrentModificationException
		        }
		        
				//write here the function for modulo, if modulo was found in the 
				//body of the procedure.
		        //It is OK for modulo to be added after it was used in the procedure.
				
				if (programContainsModulo && !writtenModuloFunction){
					//add the modulo function to the Beginning of this method
					modifyMethodBody(moduloTranslation);
					//We only want to write out the modulo function once.
					writtenModuloFunction = true;
				}
				
				out.write(methodBody.get(currentMethod));
				

							
			}
		catch (Exception e) {
			System.err.println("Error: " + e.getMessage());
		}
    		
    		isFirstMethod = false;
    }

    public void visitReturnStatement(ReturnStatement ast) throws ParseException
    {
        visitChildren(ast);
    }

    public void visitFieldSelection(FieldSelection ast) throws ParseException
    {
    	String identifierName = ast.getIdentifier().name;
    	
        if (currentMethod != "") {
     		   fieldsInMethod.add(identifierName);   
        }
    	
    	String fieldName = identifierName +"[this]";
    	fieldsInStatement.add(identifierName);
    	currentIdentifier = fieldName;
    	if (insideObjectProposition) {
			  objectPropString = objectPropString.concat(fieldName);
			  }
    	
		  if ((currentMethod != "") && (inStatement) && 
			  !inArgumentList && !inMethodSelectionStatement) {
			  //First remove the last 4 characters in the string statementContent
			  //because they == "this", which shouldn't be there.
			  statementContent = statementContent.substring(0, statementContent.length()-4);
			  statementContent = statementContent.concat(fieldName);  
		  }
		  
		  if ((currentMethod != "") && (inArgumentList) ) {
			  modifyMethodBody(fieldName + ",");
			  
		  }
		  
    	if (!namePredicate.equals("") && !insideObjectProposition){
    		FieldTypePredbody currentParamsPredicateBody = paramsPredicateBody.get(namePredicate);
			paramsPredicateBody.put(
					namePredicate, 
					currentParamsPredicateBody.concatToPredicateBody(fieldName)
			);
			 			 
    	}
    	visitChildren(ast);
    }
    
    public void visitMethodSelection(MethodSelection ast) throws ParseException
    {
    	String identifierBeforeMethSel = currentIdentifier;
    	visitedMethSel = true;
    	// It might be that some object propositions in the "requires" of the call procedure
    	// are already packed and we might not need to pack them. I need to check for that.
    	String methodName = ast.getIdentifier().name;
    	
    	//TODO
    	//We first need to add to GammaPacked the object propositions that we get from 
    	//unpacking the object propositions that we do unpack.
    	//We add these object propositions here and we might need to 
    	//decide what to unpack right here.
    	//We might need to duplicate the unpack code that is now in visitBlock()
    	//and check in that method if the statement is a call to a method,
    	//so that we don't write "unpack" twice to the out string.
    	
    	//The copies of global variables as they were before visiting the children
    	//of the methodSelection node.
    	
    	
    	
    	// When there is a procedure "call" statement in the code, 
    	// we look in the "requires" of that procedure and decide which 
    	// of the object propositions need to be packed and 
    	// which "Pack" needs to be called.
    	// I currently call the Pack procedure for all the object propositions
    	// in the "requires" of the procedure.
    	// In order to find the "requires" of "methodName" we first look in the
    	// "requires" of this class. If the "requires" for "methodName" is empty,
    	// we then look at the previous Boogie visitors and look for the one for 
    	// the class = "lastPrimaryExpressionType".
    	
    	LinkedList<ObjPropString> callMethodPreconditions = 
    			methodPreconditionsPacked.get(methodName);
    	if (callMethodPreconditions == null) {
    		int classOfCallMethod = -1;
        	for (int i=0; i < numberFilesBefore; i++) {
        		if (bv[i].getClassName().equals(lastPrimaryExpressionType))
        			classOfCallMethod = i;
        	}
        	callMethodPreconditions = bv[classOfCallMethod].getMethodPreconditionsPacked().get(methodName);		
    	}
    	
    	if (callMethodPreconditions != null) {
    		String objPropMethPrecondString = objPropForMethodPrecond(
		        	GammaPacked,	
		        	callMethodPreconditions,
		        	identifierBeforeMethSel,
		        	//this has to be modified to the list of parameters
		        	//TODO
		        	new LinkedList<String>()
		        		);
    		statementContent = statementContent.concat(objPropMethPrecondString);


    	}
    	//TODO I need to modify here.
    	statementContent= statementContent + "\t call "+ methodName + "(";
    	visitChildren(ast);
    	statementContent = statementContent +identifierBeforeMethSel+");\n";
    	LinkedList<FracString> currentRequiresFrac = null;
    	LinkedList<FracString> currentEnsuresFrac = null;
    	
    	// Modify the frac variables corresponding to the requires and ensures of 
    	// this method.
    	currentRequiresFrac = 
    			requiresFrac.get(methodName);
    	if (currentRequiresFrac != null) {
    		for (int pf = 0; pf < currentRequiresFrac.size(); pf++) {
                FracString fracString = currentRequiresFrac.get(pf);
        		statementContent = 
        			statementContent.concat(fracString.getStatementFracString(true, identifierBeforeMethSel));
        		fieldsInMethod.add(fracString.getNameFrac());
        	}
    	}
    	
    	currentEnsuresFrac = 
    			ensuresFrac.get(methodName);
    	if (currentEnsuresFrac != null) {
    		for (int pf = 0; pf < currentEnsuresFrac.size(); pf++) {
                FracString fracString = currentEnsuresFrac.get(pf);
        		statementContent = 
        			statementContent.concat(fracString.getStatementFracString(false, identifierBeforeMethSel));
        		fieldsInMethod.add(fracString.getNameFrac());
        	}
    	}
    	
    	// I am looking at methods from other classes also. 
    	// This for should be a while. 
    	int classOfMethod = -1;
    	for (int i=0; i< numberFilesBefore; i++) {
    		if (bv[i].getClassName().equals(lastPrimaryExpressionType))
    			classOfMethod = i;
    	}
    	
    	// There is some duplication of code here -- same as the code above
    	// that is for this BoogieVisitor.
    	if (classOfMethod != -1)  {
        	currentRequiresFrac = bv[classOfMethod].getRequiresFrac().get(methodName);
        	if (currentRequiresFrac != null) {
        		for (int pf = 0; pf < currentRequiresFrac.size(); pf++) {
                    FracString fracString = currentRequiresFrac.get(pf);
            		statementContent = 
            			statementContent.concat(fracString.getStatementFracString(true, identifierBeforeMethSel));
            		fieldsInMethod.add(fracString.getNameFrac());
            	}
        	}
        	
        	currentEnsuresFrac = bv[classOfMethod].getEnsuresFrac().get(methodName);
        	if (currentEnsuresFrac != null) {
        		for (int pf = 0; pf < currentEnsuresFrac.size(); pf++) {
                    FracString fracString = currentEnsuresFrac.get(pf);
            		statementContent = 
            			statementContent.concat(fracString.getStatementFracString(false, identifierBeforeMethSel));
            		fieldsInMethod.add(fracString.getNameFrac());
            	}
        	}
    	}
    	
    	//If the last 2 characters are ";\n" we need to delete them because
    	//they are going to be added at the end of visitStatement.
    	statementContent = statementContent.substring(0, statementContent.length() - 2); 	
    }

    public void visitBinaryExpression(BinaryExpression ast) throws ParseException
    {
    	if (ast.op.getId() == JExprConstants.KEYACCESS){
    		PrimaryExpression e1 = (PrimaryExpression)ast.E1;
    		PrimaryExpression e2 = (PrimaryExpression)ast.E2;
    		FieldSelection f = (FieldSelection)(e1.getChildren()[1]);
    		String nameField = f.getIdentifier().name;
    		
    		// Here we put in the fieldWhichPredicate 
    		// only if we are in the definition of a predicate.
    		if (namePredicate != "" ) {
    			addToFieldWhichPredicates(nameField, namePredicate);
    		}
    		IdentifierExpression i = (IdentifierExpression)(e2.getChildren()[0]);
    		String fieldValue = i.getName();
    		
        	PredicateAndFieldValue pv = new PredicateAndFieldValue(namePredicate, fieldValue);
        	quantifiedVars.put(pv, nameField);     	
    		return;
    	}
    	
    	if (ast.op.getId() == JExprConstants.ASSIGN){
    		helperBinaryExpression(ast , ":=");
    		return;
    	}
    	
    	//This is for modulo == REM 
    	if (ast.op.getId() == JExprConstants.REM){
    		programContainsModulo = true;
    		//The , is like the operator that gets put between operand 1 and 2.
    		helperBinaryExpression(ast, ",");
    		return;
    	}
    	    		
    	helperBinaryExpression(ast , ast.op.getName());
    	return;
    		
    }
    
    void concatToStatementObjProp(String symbol) {
    	if (namePredicate.equals("")) {
  		  try{
  			  if (currentMethod != "") {
  				  statementContent = statementContent.concat(symbol);
  			  }
  			  else {
  				  if (insideObjectProposition) {
  					  objectPropString = objectPropString.concat(symbol);
  				  }
  				  else {
  				  out.write(symbol);
  				  }
  			  }
  		  }
  	      catch (Exception e) {
  	    		System.err.println("Error: " + e.getMessage());
  	      }
  		  }
  		  else {		
  			  FieldTypePredbody currentParamsPredicateBody = paramsPredicateBody.get(namePredicate);
  			  paramsPredicateBody.put(
  					  namePredicate, 
  					  currentParamsPredicateBody.concatToPredicateBody(symbol)
  			  );

  		  }
    }
    
    void helperBinaryExpression(BinaryExpression ast, String operatorSymbol) throws ParseException
    {
    	int initialStatementLength = statementContent.length();
    	if (operatorSymbol == ",") {
    		//TODO this is a modulo binary expression
    		concatToStatementObjProp("modulo(");
    	}
    	AST[] children = ast.getChildren();
		  children[0].accept(this );
		  concatToStatementObjProp(operatorSymbol);
		  children[1].accept(this );
		  
	    	if (operatorSymbol == ",") {
	    		//TODO this is a modulo binary expression
	    		concatToStatementObjProp(")");
	    	}
		  
		  //TODO needs comment for this code
		  if (!namePredicate.equals("")) {
			  Set<String> operatorSet = new HashSet<String>();
			  operatorSet.add("==");
			  operatorSet.add("<");
			  operatorSet.add("<=");
			  operatorSet.add(">");
			  operatorSet.add(">=");
			  if (operatorSet.contains(operatorSymbol)) {
				  String binaryExpression = statementContent.substring(initialStatementLength);
				  BinExprString b = new BinExprString(binaryExpression, "this");
				  modifyPredicateBinExpr(b);
			  } 
		  }
    }
      
    public void visitLiteralExpression(LiteralExpression ast)
  		  throws ParseException
  		  { 
    	String astvalue = ast.value.toString();
    	if (insideObjectProposition) {
			  objectPropString = objectPropString.concat(astvalue);
		  }
    	
		  if ((currentMethod != "") && (inStatement) && !inArgumentList ) {
			  statementContent = statementContent.concat(astvalue);
		  }
		  
		  if ((currentMethod != "") && (inStatement) && (inArgumentList) ) {
			  statementContent = statementContent.concat(astvalue + ",");
		  }
		  
		  if ((currentMethod != "") && (!inStatement) && (inArgumentList) ) {
			 modifyMethodBody(astvalue + ",");
		  }
		  
    	if (!namePredicate.equals("") && !insideObjectProposition){
    		  FieldTypePredbody currentParamsPredicateBody = paramsPredicateBody.get(namePredicate);
			  paramsPredicateBody.put(
					  namePredicate, 
					  currentParamsPredicateBody.concatToPredicateBody(astvalue)
			  );
    	}
    	
    	visitChildren(ast); }
    
    public void visitObjectProposition(ObjectProposition ast) throws ParseException
    {
    	insideObjectProposition = true;
    	
    	FracString fracString = new FracString();
    	TypedAST object  = ast.getObject();
    	objectPropString = "";
    	object.accept(this);
    	String objectString = objectPropString;
    	objectPropString = "";
    	
    	Expression frac = ast.getFraction();
    	
    	frac.accept(this);

    	String fracInObjProp = objectPropString;

    	
    	Expression predDecl = ast.getPredicateDeclaration();
    	
    	AST[] childrenPredDecl = predDecl.getChildren();
    	objectPropString = "";
    	childrenPredDecl[0].accept(this);
    	String predName = objectPropString;

    	objectPropString = "";
    	ArgumentList argList = (ArgumentList)childrenPredDecl[1];
    	
    	LinkedList<String> args = new LinkedList<String>();
    	if (!argList.isEmpty()) {
    	//this is ArgumentList but for this example 
    	//there are no arguments so we set it to empty with new
    	//currentMethod

    		AST[] childrenArgList = argList.getChildren();
    		for (int i=0; i<childrenArgList.length; i++){
    			objectPropString = "";
    			childrenArgList[i].accept(this);
    			args.add(objectPropString);
    		}
    	}
    	ObjPropString objProp =
    			new ObjPropString(objectString, fracInObjProp, 
    	    			predName, args);
    				
    	if (isNumeric(fracInObjProp)) {
    		//TODO
    		//We only had ints so far,
    		//but need to deal with actual fractions on an example.
    		int d = Integer.parseInt(fracInObjProp); 
    		objProp.setExactFrac(d, 1);
    	}
    	    	
    	PredicateAndFieldValue pv = new PredicateAndFieldValue(namePredicate, objectString);
    	String fieldName = quantifiedVars.get(pv);
        String bodyMethodOrPredicate = "";
        fracString.setNameFrac("frac"+predName);
        
    	if (fieldName == null){
    		
    		// This is where FracString is updated
    		// but only when we are inside a predicate.
    	bodyMethodOrPredicate = "packed"+predName+"[" + objectString+
    			          "] && \n \t \t(frac"+predName+"["+ objectString+ "] > 0.0";
    	}
    	else {
    		
    		bodyMethodOrPredicate ="packed"+predName+"[" + fieldName +
			          "[this]] && \n \t \t(frac"+predName+"["+ fieldName + "[this]] > 0.0"; 
    		fracString.setField(fieldName);
    		objProp.setObject(fieldName+"[this]");
    				
    	}
    	
    	// The minBound is the same if the fieldName is null or not. 
    	// We do not need to set this inside the if branches.
    	fracString.setMinBound(0);
    	
    	if (currentMethod!="") {
    		modifyMethodSpec(bodyMethodOrPredicate);
 
    		if (insidePrecondition) {
    			GammaPacked.add(objProp);
    			modifyMethodPreconditions(objProp);
    			modifyRequiresFrac(fracString);
    		} 
    		else {
    			modifyMethodPostconditions(objProp);
    			modifyEnsuresFrac(fracString);
    		}
    	}
    	else {
    		modifyPredicateBody(bodyMethodOrPredicate);
    		//We want to add the FracString to predicateFrac for
    		//the current predicate and the FracString 
    		//frac+predName[fieldname] >0.0.
    		modifyPredicateFrac(fracString);
    		modifyPredicateObjProp(objProp);
    	}
    	insideObjectProposition = false;
    }

   
    public void visitUnaryExpression(UnaryExpression ast) throws ParseException
    {
        visitChildren(ast);
    }

    public void visitPrimaryExpression(PrimaryExpression ast) throws ParseException
    {
    	Expression[] children = (Expression[])ast.getChildren();
    	if (children.length == 2) 
    	{
    		if (children[1] instanceof MethodSelection) {
    			inMethodSelectionStatement = true;
    		}
    	} else if (children.length == 3) {
    		if (children[2] instanceof MethodSelection) {
    			inMethodSelectionStatement = true;
    		}
    	}
    	// Sometimes the type of the primary expression is null.
    	// We are interested only in the type of the primary expression
    	// when it is the parent of methodSelection because that
    	// is when we are going to use the type.
    	if (ast.getType() != null) {
    	lastPrimaryExpressionType = ast.getType().toString();
    	}
        visitChildren(ast);
    }
    
    public void visitFormalParameters(FormalParameters ast) throws ParseException 
  		  { 
    	  	
    	visitChildren(ast);
    	if (currentMethod != "") {
    		modifyMethodParams(")\n"); 
        	}
    
    	}
    
    public void visitFormalParameter(FormalParameter ast) throws ParseException
    {
    	if (ast!=null) {
    		String name = ast.getName();
    		String type = ast.getType().toString();
    	
    		modifyFormalParams(name, type); 
    		//I don't think this node has any children.
    		visitChildren(ast);
    	}
    }
    
    public void visitAllocationExpression(AllocationExpression ast) throws ParseException
    {
    	String predicateOfConstruct = ast.getPredicate();
    	
    	modifyMethodBody("\t call Construct" + ast.getAlloc_func()+predicateOfConstruct+"(");
        visitChildren(ast);
        modifyMethodBody(localVariableName + ");\n");
        
        // We want to modify the frac that we find in the predicate
        // corresponding to this constructor.
        
        LinkedList<FracString> currentPredicateFrac = 
    			predicateFrac.get(predicateOfConstruct);
        if (currentPredicateFrac != null) {
        	
        	for (int pf = 0; pf < currentPredicateFrac.size(); pf++) {
                FracString fracString = currentPredicateFrac.get(pf);
                modifyMethodBody(fracString.getStatementFracString(true, "this"));
        	}
        }
    }
    
    public void visitDeclarationStatement(DeclarationStatement ast) 
  		  throws ParseException 
  		  { 
    	
    	visitChildren(ast); 
    	localVariableName = "";
  		  }
    
    public void visitLocalVariableDeclaration(LocalVariableDeclaration ast) throws ParseException
    {
    	localVariableName = ast.getName();
    	
    	String fieldType = ast.getType().toString();
    	boolean isClass = fieldType.equals(className);
    	
    	for (int i=0; i<numberFilesBefore; i++) {
    		if (bv[i].getClassName().equals(fieldType) )
    				isClass = true;
    	}

    		if (isClass) {
    			modifyMethodBody("\t var " + localVariableName +":Ref;\n");
    		}
    		else 
    			modifyMethodBody("\t var " + localVariableName +":"+ fieldType+";\n");
    	
    	visitChildren(ast);
      }
    
    public void visitKeywordExpression(KeywordExpression ast) throws ParseException
    { 
    	String keywordString;
    	Object value= ast.getValue();
    	if (value == null) {
    		//If it is null, it means the keyword null was encountered.
    		keywordString = "null";
    	}
    	else {
    		keywordString = value.toString();
    	}
    		
        if (namePredicate.equals("")) {
     	   //we are not inside a predicate
     		   
     		   if ((currentMethod != "") && (inArgumentList)) {
     			   modifyMethodBody(keywordString + ",");
     		   }
     		   
     		   if ((currentMethod != "") && (inStatement) && 
     			   !inArgumentList && !inMethodSelectionStatement ) {
     				  statementContent = statementContent.concat(keywordString);
     			  }
     		   
     			   //modify object proposition parts
     		   if (insideObjectProposition) {
     			   objectPropString = objectPropString.concat(keywordString);
     				  }	
        }
        else { 
     	   //we are inside a predicate	  
 		   FieldTypePredbody currentParamsPredicateBody = paramsPredicateBody.get(namePredicate);
     	      	 
     		   if (insideObjectProposition) {
  				  objectPropString = objectPropString.concat(keywordString);
  			  }
      	   else {
      		  paramsPredicateBody.put(
 					  namePredicate, 
 					  currentParamsPredicateBody.concatToPredicateBody(keywordString)
 			  );
      		   
      	   }
    }
}
 
    public void visitIdentifierExpression(IdentifierExpression ast) throws ParseException
    {    	
    	currentIdentifier = ast.name;
    try {
      
       String identifierName = currentIdentifier;      
       PredicateAndFieldValue pv = new PredicateAndFieldValue(namePredicate, identifierName);
       String fieldName = quantifiedVars.get(pv);
       
       if (namePredicate.equals("")) {
    	   //we are not inside a predicate
    	   if (!(fieldName == null)) {
    		   if (insideObjectProposition) {
  				  objectPropString = objectPropString.concat(identifierName);
  			  }
  			  modifyMethodBody(fieldName+ "[this]"); 
    	   }
    	   else {
    		   
    		   if ((currentMethod != "") && (inArgumentList) && (inStatement)) {
    			   statementContent = statementContent.concat(identifierName + ",");
    		   }
    		   
    		   if ((currentMethod != "") && (inArgumentList) && (!inStatement)) {
    			   modifyMethodBody(identifierName + ",");
    		   }
    		   
    		   if ((currentMethod != "") && (inStatement) && 
    			   !inArgumentList && !inMethodSelectionStatement ) {
    				  statementContent = statementContent.concat(identifierName);
    			  }
    		   
    			   //modify object proposition parts
    		   if (insideObjectProposition) {
    			   objectPropString = objectPropString.concat(identifierName);
    				  }	
    	   }
       }
       
       else {
    	   
    	   //we are inside a predicate
			  
		   FieldTypePredbody currentParamsPredicateBody = paramsPredicateBody.get(namePredicate);
    	      	 
    	   if (!(fieldName == null)) {
    	   
    	   if (insideObjectProposition) {
				  objectPropString = objectPropString.concat(identifierName);
			  }
     	   if (!insideObjectProposition) {
     		   
     		  paramsPredicateBody.put(
					  namePredicate, 
					  currentParamsPredicateBody.concatToPredicateBody(fieldName+ "[this]")
			  );
     	   }
    	   }
    	   else {
    		   if (insideObjectProposition) {
 				  objectPropString = objectPropString.concat(identifierName);
 			  }
     	   else {
     		  paramsPredicateBody.put(
					  namePredicate, 
					  currentParamsPredicateBody.concatToPredicateBody(identifierName)
			  );
     		   
     	   }
    	   }
       }
    }
      catch (Exception e) {
    		System.err.println("Error: " + e.getMessage());
      };
      
    	visitChildren(ast);
    }
    
    public void visitIfStatement(IfStatement ast) throws ParseException
    {
    	inMethodSelectionStatement = false;
    	inIfStatement = true;
    	//An if statement can be only inside a method statement.
    	inStatement = true;
    	AST[] children = ast.getChildren();
    	int size = children.length;
    	statementContent = statementContent.concat("if (");
    	
    	children[0].accept(this);
    	statementContent = statementContent.concat(")\n");
    	modifyMethodBody(statementContent);
    	//We make it "" because we have just written it to methodBody.
    	statementContent = "";
    	//children[1] and [2] are Blocks.
    	children[1].accept(this);
    	statementContent = "";
    	if (size == 3) { 
    		modifyMethodBody("else \n");
        	children[2].accept(this);
        	statementContent = "";
    	}
    	
    	inStatement = false;
    	inIfStatement = false;
    }
    
    public void visitStatementExpression(StatementExpression ast)
  		  throws ParseException
  		  { 
    	inStatement = true;
    		visitChildren(ast);
    		try {
    			if (currentMethod != "") {
    				statementContent = statementContent.concat(";\n");
    			}
  			  else {
  				  out.write(";\n");
  			  }
        	}
        	catch (Exception e) {
        		System.err.println("Error: " + e.getMessage());
        	}
    		inStatement = false;
  		  }
    
    public void visitMethodSpecVariable(MethodSpecVariable ast) 
  		  throws ParseException 
  		  {
    	visitChildren(ast); 
    	}
    
    public void visitMethodSpecVariables(MethodSpecVariables ast)
  		  throws ParseException 
  		  { 
    	visitChildren(ast); 
    	}
    
    public void visitMethodSpecExpression(MethodSpecExpression ast) 
  		  throws ParseException 
  		  { 
    	Expression precondition = ast.getPrecondition();
    	Expression postcondition = ast.getPostcondition();
    	if (precondition != null) {
    	modifyMethodSpec("\t requires ");
    	insidePrecondition = true;
    	precondition.accept(this );
    	modifyMethodSpec(");\n");
    	}
    	
    	//TODO
    	//need to createObjPropString and add it to GammaPacked
    	if (postcondition != null) {
    	modifyMethodSpec("\t ensures ");
    	insidePrecondition = false;
    	postcondition.accept(this );
    	modifyMethodSpec(");\n");
    	}
    	
    	}
    
    public void visitBlock(Block ast) 
  		  throws ParseException 
  		  { 
    	modifyMethodBody("{\n");
    	
    	AST[] children = ast.getChildren();
    	for (int i = 0; i < children.length; i++) {  		
    		fieldsInStatement.clear();
    		statementContent = "";
    		visitedMethSel = false;
    		inMethodSelectionStatement = false;
    	    children[i].accept(this);
    	    //write what we are packing or unpacking 
    	    //before writing the statement
    	    if (visitedMethSel == false) {
            for (String fieldName : fieldsInStatement) {
                if (fieldName != null) {   
                	LinkedList<String> predicatesOfField = fieldWhichPredicates.get(fieldName); 
                	if (predicatesOfField != null) {
                		for (int k = 0; k < predicatesOfField.size(); k++) {
                			ObjPropString temp = new ObjPropString("this", "k", 
                			predicatesOfField.get(k), new LinkedList<String>());
                			if (GammaPacked.contains(temp)) {
                				String localNameOfPredicate = predicatesOfField.get(k);
                				modifyMethodBody("\t call Unpack"+localNameOfPredicate+"(this);\n");
                				modifyMethodBody("\t packed"+localNameOfPredicate+"[this]:=false;\n");
                				//TODO
                				//Here I need to add to GammaPacked the object propositions from the body of the
                				//unpacked predicate.
                				//Maybe this should be added at the end of visiMethodSelection().
                				addObjPropToGamma(localNameOfPredicate);              				
                				fieldsInMethod.add("packed"+localNameOfPredicate);
                				modifyPackedMods(localNameOfPredicate, "this", -1);
                				GammaPacked.remove(temp);
                			}
                		} 	
                	}
            }
            }
    	    }
    	    else {
    	    	
    	    }
    	    modifyMethodBody(statementContent);
    	  }
    	
    	if (!inIfStatement) {
    		LinkedList<ObjPropString> thisMethodPostCond = 
    				methodPostconditionsPacked.get(currentMethod);
    		if (thisMethodPostCond != null) {
    			//Pack the object propositions
    			//that are in the postcondition.
    			for (int i = 0; i < thisMethodPostCond.size(); i++) {
    				//TODO
    				//Need to check if this object proposition is also in GammaPacked
    				//if it's not, then might throw an error or send a message
    		
    				ObjPropString o = thisMethodPostCond.get(i);
    				String obj = o.getObject();
    				String name = o.getName();
    		
    				//need to take care of the OK, ok uppercase issue
    				modifyMethodBody("\t call Pack"+name+"("+obj+");\n");
    				modifyMethodBody("\t packed"+name+"["+obj+"]:=true;\n");
    				fieldsInMethod.add("packed"+name);
    				modifyPackedMods(name, obj, 1);
    			}
    		}
    	}
    	
    	modifyMethodBody("}\n ");
  		  }
    
    
    public void visitArgumentList(ArgumentList ast)
  		  throws ParseException 
  		  {  
    	inArgumentList = true;
    	visitChildren(ast); 
    	inArgumentList = false;
    	}
    
    
    void modifyMethodBody(String s) {
    	String currentMethodBody = methodBody.get(currentMethod);
		currentMethodBody = currentMethodBody.concat(s);
		methodBody.put(currentMethod, currentMethodBody);
    }
    
    void modifyPredicateBody(String s) {
		FieldTypePredbody currentParamsPredicateBody = paramsPredicateBody.get(namePredicate);
		paramsPredicateBody.put(
				namePredicate, 
				currentParamsPredicateBody.concatToPredicateBody(s)
		);
    }
    
    void modifyFormalParams(String name, String type) {
		FieldTypePredbody currentParamsPredicateBody = paramsPredicateBody.get(namePredicate);
		if (currentParamsPredicateBody != null) {
		paramsPredicateBody.put(
				namePredicate, 
				currentParamsPredicateBody.addParam(name, type)
		);
		} 
		//TODO else
    }
        
    void modifyMethodSpec(String s) {
    	String currentMethodSpec = methodSpec.get(currentMethod);
		currentMethodSpec = currentMethodSpec.concat(s);
		methodSpec.put(currentMethod, currentMethodSpec);
    }
    
    void modifyMethodParams(String s) {
    	String currentMethodParams = methodParams.get(currentMethod);
		currentMethodParams = currentMethodParams.concat(s);
		methodParams.put(currentMethod, currentMethodParams);
    }
        
    void modifyMethodPreconditions(ObjPropString s) {
    	LinkedList<ObjPropString> currentMethodPreconditions = 
    			methodPreconditionsPacked.get(currentMethod);
    	if (currentMethodPreconditions == null) {
    		currentMethodPreconditions = new LinkedList<ObjPropString>();
    	}
    	currentMethodPreconditions.add(s);
    	methodPreconditionsPacked.put(currentMethod, currentMethodPreconditions);
    }
    
    void modifyMethodPostconditions(ObjPropString s) {
    	LinkedList<ObjPropString> currentMethodPostconditions = 
    			methodPostconditionsPacked.get(currentMethod);
    	if (currentMethodPostconditions == null) {
    		currentMethodPostconditions = new LinkedList<ObjPropString>();
    	}
    	currentMethodPostconditions.add(s);
		methodPostconditionsPacked.put(currentMethod, currentMethodPostconditions);
    }
    
    //adds a FracString to the requiresFrac map for the currentMethod
    void modifyRequiresFrac(FracString s) {
    	LinkedList<FracString> currentRequiresFrac = 
    			requiresFrac.get(currentMethod);
    	if (currentRequiresFrac == null) {
    		currentRequiresFrac = new LinkedList<FracString>();
    	}
    	currentRequiresFrac.add(s);
    	requiresFrac.put(currentMethod, currentRequiresFrac);
    }
    
    //adds a FracString to the ensuresFrac map for the currentMethod
    void modifyEnsuresFrac(FracString s) {
    	LinkedList<FracString> currentEnsuresFrac = 
    			ensuresFrac.get(currentMethod);
    	if (currentEnsuresFrac == null) {
    		currentEnsuresFrac = new LinkedList<FracString>();
    	}
    	currentEnsuresFrac.add(s);
    	ensuresFrac.put(currentMethod, currentEnsuresFrac);
    }
    
    void addToFieldWhichPredicates(String field, String predicate) {
    	LinkedList<String> currentFieldWhichPredicates = 
    			fieldWhichPredicates.get(field);
    	if (currentFieldWhichPredicates == null) {
    		currentFieldWhichPredicates = new LinkedList<String>();
    	}
    	currentFieldWhichPredicates.add(predicate);
    	fieldWhichPredicates.put(field, currentFieldWhichPredicates);
    	
    }
    
    void modifyPredicateFrac(FracString s) {
    	LinkedList<FracString> currentPredicateFrac = 
    			predicateFrac.get(namePredicate);
    	if (currentPredicateFrac == null) {
    		currentPredicateFrac = new LinkedList<FracString>();
    	}
    	currentPredicateFrac.add(s);
    	predicateFrac.put(namePredicate, currentPredicateFrac);    	
    }
    
    void modifyPredicateObjProp(ObjPropString s) {
    	LinkedList<ObjPropString> currentPredicateObjProp = 
    			predicateObjProp.get(namePredicate);
    	if (currentPredicateObjProp == null) {
    		currentPredicateObjProp = new LinkedList<ObjPropString>();
    	}
    	currentPredicateObjProp.add(s);
    	predicateObjProp.put(namePredicate, currentPredicateObjProp);    	
    }
    
    void modifyPredicateBinExpr(BinExprString s) {
    	LinkedList<BinExprString> currentPredicateBinExpr = 
    			predicateBinExpr.get(namePredicate);
    	if (currentPredicateBinExpr == null) {
    		currentPredicateBinExpr = new LinkedList<BinExprString>();
    	}
    	currentPredicateBinExpr.add(s);
    	predicateBinExpr.put(namePredicate, currentPredicateBinExpr);    	
    }
    
    void makeConstructors(BufferedWriter out) {
    	//I also declare the packed and frac global variables for this class.
    	try {
    		
			out.write("type FractionType = [Ref] real;\n");
			out.write("type PackedType = [Ref] bool;\n");
    		for (String p : predicates) {
    			out.write("var packed" + p + ": [");
    			 writePredParamsOut(p, 3);
    			 out.write("Ref] bool;\n");
    			 out.write("var frac" + p + ": [");
    			 writePredParamsOut(p, 3);
    			 out.write("Ref] real;\n");
    			
            	//write constructors for each predicate
    			out.write("\n");
                out.write("procedure Construct" + className + p + "(");
                for (FieldAndTypePair s : fieldsTypes) {
                	out.write(s.getName() + "1: "+ s.getType() + ", ");
            	}
                
                //1 is for writing paramName: type
                writePredParamsOut(p, 1);

                            
                out.write("this: Ref);\n");
                out.write("\t ensures ");
                for (FieldAndTypePair s : fieldsTypes) {
                	out.write("(" + s.getName() + "[this] == "+ s.getName() + "1) &&\n \t \t");
            	}
                
                out.write("(packed"+p+"[");
                //2 is for writing the current value of the parameter in the predicate.
                writePredParamsOut(p, 2);
                out.write("this]) && \n \t \t");
                out.write("(frac"+p+"[");
                writePredParamsOut(p, 2);
                out.write("this] == 1.0);\n \n");
    	  	   	
    		}
    		out.write("\n");
    	}
    	catch (Exception e) {
    		System.err.println("Error: " + e.getMessage());
    	}
    	    	
    	try {
   		
    		//write a constructor that doesn't pack to any predicate
    		out.write("procedure Construct" + className + "(");
            for (FieldAndTypePair s : fieldsTypes) {
            	out.write(s.getName() + "1 :"+ s.getType() + ", ");
        	}
            out.write("this: Ref);\n");
            out.write("\t ensures ");
    		for (int i = 0; i < fieldsTypes.size()-1; i++) {
            	out.write("(" + fieldsTypes.get(i).getName() + "[this] == "+ 
            				fieldsTypes.get(i).getName() + "1) &&\n \t \t ");
        	}
    		out.write("(" + fieldsTypes.get(fieldsTypes.size()-1).getName() + "[this] == "+ 
    				fieldsTypes.get(fieldsTypes.size()-1).getName() + "1); \n \n");
    		
    	}
        	catch (Exception e) {
        		System.err.println("Error: " + e.getMessage());
        	}
    	
    	
    }
        
    static boolean isNumeric(String str) {
      // Match a number with optional '-' and decimal.
      return str.matches("-?\\d+(\\.\\d+)?");  
    }
        
    LinkedList<ObjPropString> objPropToUnpackForFrac(
    	LinkedList<ObjPropString> Gamma0,
    	String namePredicate0,
    	String object0,
    	double needFrac,
    	double haveFrac
    		) {
    	LinkedList<ObjPropString> result = new LinkedList<ObjPropString>();
    	// We assume that haveFrac < needFrac.
    	// We need to check for this before entering this function.
    	Iterator<Entry<String, FieldTypePredbody>> it = paramsPredicateBody.entrySet().iterator();
    	String iterPredicateName = "";
        while (it.hasNext()) {
        	Entry<String, FieldTypePredbody> pairs = (Entry<String, FieldTypePredbody>)it.next();
        	FieldTypePredbody iterParamsPredicateBody = pairs.getValue();
        	String iterPredicateBody = iterParamsPredicateBody.getPredicateBody();
        	//I'm not sure if this should use toLowerCase or not.
        	//It shouldn't, after I fix everything about the name of the predicates.
        	if (iterPredicateBody.toLowerCase().contains("frac" + namePredicate0.toLowerCase())) {
        		iterPredicateName = pairs.getKey();
        		// This break is for the while.
        		// This is just temporary, it should return *all* the predicateBody's 
        		// that contain that predicateName.
        		break;
        	}
        	
            // it.remove(); // avoids a ConcurrentModificationException
            // We do not need this line because at the moment we are not in a 
            // concurrent setting.
           
        }
        if (iterPredicateName != "") {
        	// I assume here that the fraction is the one that I'm looking for.
        	// but actually, if the fraction is not enough, we need to call this
        	// function recursively.
        	ObjPropString temp = new ObjPropString("this", "k", 
        			iterPredicateName, new LinkedList<String>());
        			
        			if (Gamma0.contains(temp)) {
        				result.add(temp);
        				return result;
        			
        			//This needs to be removed from the actual GammaPacked.
        			//Might be able to us the actual GammaPacked, and not Gamma0.	
        				
        			}	
        }
        
    	return null;
   	
    	}
    
    
    // A result of -1 means that the object was not found in the 
    // LinkedList as the first element of some PackObjMods.
    // A PackObjMods has 2 elements: objectString and modifications.
	int getPositionObjectName(LinkedList<PackObjMods> packmod, String objectName) {
		for (int j=0; j < packmod.size(); j++) {
			PackObjMods o = packmod.get(j);
			if (objectName.equals(o.getObjectString())) {
				return j;
			}
		}
		return -1;
	}
	
	//boo comes from boolean
	//name is the name of the predicate
	//obj is the name of the object. It can be this, or dc[this], etc.
	void modifyPackedMods(String name, String obj, int boo) {
		LinkedList<PackObjMods> currentPackObjMods = 
    			packedMods.get(name);
		//Maybe I don't need this if because I 
		//initialize it to the empty LinkedList.
    	if (currentPackObjMods == null) {
    		currentPackObjMods = new LinkedList<PackObjMods>();
    	}
    	int position = getPositionObjectName(currentPackObjMods, obj);
    	if (position == -1) {
    		PackObjMods o = new PackObjMods(obj);
    		o.addModification(boo);
    		currentPackObjMods.add(o); 		
    	} else {
    	PackObjMods o = currentPackObjMods.get(position);
    	o.addModification(boo);
    	currentPackObjMods.set(position, o);
    	}
    	packedMods.put(name, currentPackObjMods);
	}
	
	//This adds to Gamma the object propositions that are in the body of
	//the predicate nameOfPredicate.
	void addObjPropToGamma(String nameOfPredicate) {
		LinkedList<ObjPropString> currentPredicateObjProp = 
    			predicateObjProp.get(nameOfPredicate);
    	if (currentPredicateObjProp != null) {
    		for (int i=0; i<currentPredicateObjProp.size(); i++) {
    			ObjPropString o = currentPredicateObjProp.get(i);
    			GammaPacked.add(o);
    		}
    		
    	}		
	}
	
	StateOfGammas unpackToGetPiece(
			LinkedList<ObjPropString> GammaPacked0, 
			String piece) {
		return new StateOfGammas();
	}
	
	StateOfGammas unpackToGetUnpacked(
			LinkedList<ObjPropString> GammaPacked0, 
			ObjPropString unpackedObjProp) {
		return new StateOfGammas();
	}
	
	StateOfGammas packPiecesToGetPacked(
			LinkedList<String> GammaPiecesOfObjProp0, 
			ObjPropString packedObjProp
			) {
		return new StateOfGammas();
	}
	
	StateOfGammas unpackToGetPacked(
			LinkedList<ObjPropString> GammaPacked0,
        	LinkedList<ObjPropString> GammaUnpacked0,
        	LinkedList<String> GammaPiecesOfObjProp0,
        	ObjPropString packedObjProp
			) {
		String result = "";
		StateOfGammas state = new StateOfGammas();
		/*
		 *  	
    		for (int j=0; j < methPreconditions.size(); j++) {
    			//TODO
    			// Need to check if these object propositions are in GammaPacked.
    			// Might need another map for knowing which object propositions are packed and which are
    			// unpacked. Or a more general way of writing the class ObjPropString.
    			// Or another GammaUnpacked that is like Gamma, but for the current unpacked object propositions.
    			ObjPropString o = methPreconditions.get(j);
    			o.setObject(caller);
    			String name = o.getName();
    			
    			//Only call packing if this object proposition is not already packed and in Gamma.
    			if (!GammaPacked.contains(o)) {
    				//need to take care of the OK, ok uppercase issue
    				statementContent = statementContent + "\t call Pack"+name+"("+caller+");\n";
    				statementContent = statementContent + "\t packed"+name+"["+caller+"]:=true;\n";
    				fieldsInMethod.add("packed"+name);
    				modifyPackedMods(name, caller, 1);
    			}	    			
    		}
        	return result;
		 */
		
		
		
		
		
	}
	
	//TODO put comment here
	//
	//
    String objPropForMethodPrecond(
    		LinkedList<ObjPropString> GammaPacked0,
        	LinkedList<ObjPropString> GammaUnpacked0,
        	LinkedList<String> GammaPiecesOfObjProp0,
        	LinkedList<ObjPropString> methodPreconditionsPacked0,
        	LinkedList<ObjPropString> methodPreconditionsUnpacked0,
        	LinkedList<String> methodPreconditionsPieces0,
        	String caller,
        	LinkedList<String> params
        		) {
    	String result = "";
    	
    	//First try to obtain the pieces in our method precondition.
    	//We might have to unpack some object propositions to get to them.
    	if (methodPreconditionsPieces0.size() != 0) {
    		for (int i=0;i<methodPreconditionsPieces0.size();i++){
    			if (!GammaPiecesOfObjProp0.contains(methodPreconditionsPieces0.get(i))){
    				//Try to unpack an object proposition and see if it contains this piece.
    				//TODO
    				//Need to update the state of the Gammas after the call to the method below.
    				result = result.concat(
    						unpackToGetPiece(GammaPacked0, methodPreconditionsPieces0.get(i)).getResultString()
    				);
    				
    			}
    		}
    	}
    	
    	//Second try to obtain the unpacked object propositions in our method precondition.
    	//We might have to unpack some object propositions to get to them.
    	if (methodPreconditionsUnpacked0.size() != 0) {
    		for (int i=0;i<methodPreconditionsUnpacked0.size();i++){
    			ObjPropString unpackedObjProp = methodPreconditionsUnpacked0.get(i);
    			unpackedObjProp.setObject(caller);
    			if (!GammaPiecesOfObjProp0.contains(unpackedObjProp)){
    				//Try to unpack an object proposition and see if it contains this 
    				//unpacked object proposition.
    				//TODO
    				//Need to update the state of the Gammas after the call to the method below.
    				result = result.concat(
    						unpackToGetUnpacked(GammaPacked0, unpackedObjProp).getResultString()
    				);
    				
    			}
    		}
    	}
    	
    	//Third try to obtain the packed object propositions in our method precondition.
    	//First try to pack.
    	//If you can't, try to unpack by looking bottom up for what to unpack.
    	//This is the most common case from what I have seen.
    	//We might need to unpack a few times to get to the packed object
    	//proposition that we want.
    	if (methodPreconditionsPacked0.size() != 0) {
    		for (int i=0;i<methodPreconditionsPacked0.size();i++){
    			ObjPropString packedObjProp = methodPreconditionsPacked0.get(i);
    			packedObjProp.setObject(caller);
    			
    			if (!GammaPiecesOfObjProp0.contains(packedObjProp)){

    				//First try to pack to this object proposition.
    				String couldPackString = packPiecesToGetPacked(
    						GammaPiecesOfObjProp0, 
    						packedObjProp
    						).getResultString();
    				
    				if (couldPackString==""){
    				//If packing didn't work, try to unpack repeatedly.
    				
    					StateOfGammas multipleUnpacking = unpackToGetPacked(
    						GammaPacked0,
    			        	GammaUnpacked0,
    			        	GammaPiecesOfObjProp0,
    			        	packedObjProp
    						);
    					
    					result = result.concat(multipleUnpacking.getResultString());

    				}
    				
    				else {
    					result = result.concat(couldPackString);
    					
    				}
    				
    			}
    		}
    	}

        }
    
    //k=1 is for writing nameParam: type
    //k=2 is for writing the current value of the parameters
    //k=3 is for writing the types of the parameters
    void writePredParamsOut(String pred, int k) {
    	try{
    	FieldTypePredbody currentParamsPredicateBody = paramsPredicateBody.get(pred);
		if (currentParamsPredicateBody != null) {
			LinkedList<FieldAndTypePair> formalParamsList =
					currentParamsPredicateBody.getFormalParameters();
			if (!formalParamsList.isEmpty()) {
				for (int i=0;i<formalParamsList.size();i++) {
					FieldAndTypePair f = formalParamsList.get(i);
					switch (k) {
		            case 1:  
		            	out.write(f.getName() + ":"+f.getType()+", ");
		                break; 
		            case 2: 
		            	out.write(f.getName() + ", ");
		            	break;
		            case 3: 
		            	out.write(f.getType() + ", ");
		            	break;
		            default: 
		            	break;
					}
					
				}
			}	
		}
    	}
    	catch (Exception e) {
    		System.err.println("Error: " + e.getMessage());
    	}  	
    }    		
    }