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
import cager.jexpr.ast.FractionAnnotation;
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
import cager.jexpr.ast.PackUnpackAnnotation;
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
	
	boolean inPackUnpackAnnotation = false;
	
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
	
	String stringOfVarDecls = "";
	
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
	
	//Set of predicates for the corresponding frac global variables that are 1
	//in the requires or ensures of the current method.
	Set<String> setFracEq1 = new TreeSet<String>();
	
	
	
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
	HashMap<String, Set<String>> fieldsInMethod = new HashMap<String, Set<String>>();
		
	//For each method, methodsInMethod contains the set of methods called in that method.
	HashMap<String, LinkedList<FieldAndTypePair>> methodsInMethod = 
			new HashMap<String, LinkedList<FieldAndTypePair>>();
	
	//The set of input parameters for the current method.
	Set<String> parametersMethod = new TreeSet<String>();

	//This boolean is true iff we are currently parsing an object proposition. 
	boolean insideObjectProposition;
	
	//This boolean is true iff we are currently inside the pre-condition of a method.
	boolean insidePrecondition;
	
	//This boolean is true iff we are currently inside the post-condition of a method.
	boolean insidePostcondition;
	
	//The string of the object proposition inside which we are at the moment.
	String objectPropString;
	
	String objectObjProp;
	String predicateNameObjProp;
	String fractionObjProp;
	LinkedList<String> argumentsObjProp = new LinkedList<String>();
	
	//The Boogie Visitors of the files that have been translated before this one.
	BoogieVisitor[] bv;
	
	int numberFilesBefore;
	
	public BoogieVisitor(BufferedWriter boogieFile, int n, BoogieVisitor[] bv0) {
		out = boogieFile;
		namePredicate = "";
		currentMethod = ""; 
		insideObjectProposition = false;
		insidePrecondition = false;
		insidePostcondition = false;
		objectPropString = "";
		statementContent = "";
		bv = bv0;
		numberFilesBefore = n;
	}
	
	//public getter functions
	//To use in the subsequent BoogieVisitor classes.
	//Each input Java file is translated using a BoogieVisitor.
	
	public HashMap<String, Set<String>> getFieldsInMethod() {
		return fieldsInMethod;
	}
	
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

    	if (fieldType.equals(className)) {
    		isClass = true;
    	}
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
    	parametersMethod.clear();
    	setFracEq1.clear();
    	stringOfVarDecls = "";
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
     		
     		//processing of the predBody
     		int i=0;
     		while ((predBodyUnprocessed.charAt(i)=='&') ||
     				(predBodyUnprocessed.charAt(i)=='(') ||
     				(predBodyUnprocessed.charAt(i)==')') ) i++;
     		String lastPartPredBody = predBodyUnprocessed.substring(i);
     		String firstPartPredBody = predBodyUnprocessed.substring(0, i);
     		StringBuilder sb = new StringBuilder(firstPartPredBody);
     		int numberOfAmb=0;
     		int k=0;
     		while (k<i) {
     			if (sb.charAt(k)=='&') {
     				numberOfAmb++;
     				sb = sb.deleteCharAt(k);
     				if (k>0) k--;
     				i--;
     			} else {
     				k++;
     			}
     		}
     		
     		int numDeleteParam = (int)(numberOfAmb/2)-1;
     		
     		int numLeftParamToDelete = numDeleteParam;
     		int numRightParamToDelete = numDeleteParam;
     		k=0;
     		while (k<i) {
     			if ((sb.charAt(k) == '(') && (numLeftParamToDelete > 0)) {
     				sb = sb.deleteCharAt(k);
     				numLeftParamToDelete--;
     				if (k>0) k--;
     				i--;
     			}
     			else if ((sb.charAt(k)==')') && (numRightParamToDelete > 0)) {
     				sb = sb.deleteCharAt(k);
     				numRightParamToDelete--;
     				if (k>0) k--;
     				i--;
     			}
     			else {
     				k++;
     			}
     		}
     		
     		
     		String predBody = sb.toString().concat(lastPartPredBody);
     		try {
			//will need to do something about formal parameters
			out.write("procedure Pack"+currentNamePred+"(");
			writePredParamsOutOrToString(currentNamePred, 1, false);
			out.write("this:Ref);\n"); 
			out.write("\t requires packed"+currentNamePred+"[");
			writePredParamsOutOrToString(currentNamePred, 2, false);
			out.write("this]==false &&\n");
			out.write("\t \t" + predBody + ";\n"); 
			out.write("\n");
			out.write("procedure Unpack"+currentNamePred+"(");
			writePredParamsOutOrToString(currentNamePred, 1, false);
			out.write("this:Ref);\n");
			out.write("\t requires packed"+currentNamePred+"[");
			writePredParamsOutOrToString(currentNamePred, 2, false);
			out.write("this] &&\n\t \t ");
			//TODO we don't do this for Pack because 
			//we do it in the code after we call the Pack procedure
			
			out.write("(frac"+currentNamePred+"[");
			writePredParamsOutOrToString(currentNamePred, 2, false);
			out.write("this] > 0.0);\n");
			out.write("\t ensures "+predBody+";\n");
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
    			
				out.write("procedure "+ast.getIdentifier().getName()+"(");
				
				visitChildren(ast);
				
				out.write(methodParams.get(currentMethod));
				out.write("this:Ref)\n");
							
				//Need to automatically detect what is being modified, according to the Boogie manual.
				//We do this after we parse and translate the body of the current method.
				Set<String> localFieldsInMethod = fieldsInMethod.get(currentMethod);
				
        		//Put in modifies the fields that were in the modifies of 
        		//the methods that were called in this method.
        		LinkedList<FieldAndTypePair> currentMethodsInMethod = 
            			methodsInMethod.get(currentMethod);
        		if (currentMethodsInMethod !=null) {
        		for (int i=0;i<currentMethodsInMethod.size();i++) {
        			FieldAndTypePair ft = currentMethodsInMethod.get(i);
        			Set<String> callMethodsSetOfFields = new TreeSet<String>();
        			if (ft.getType().equals(className)) {
        				callMethodsSetOfFields = fieldsInMethod.get(ft.getName());
        			} else {
        				int classOfCallMethod = -1;
        	        	for (int l=0; l < numberFilesBefore; l++) {
        	        		if (bv[l].getClassName().equals(ft.getType()))
        	        			classOfCallMethod = l;
        	        	}
        	        	callMethodsSetOfFields = bv[classOfCallMethod].getFieldsInMethod().get(ft.getName());	
        			}
        			localFieldsInMethod.addAll(callMethodsSetOfFields);
        		}
        		}
 
        		//localFieldsInMethod is the set of elements that are modified.
				String[] fieldsInMethodArray = localFieldsInMethod.toArray(new String[0]);
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
				
				
				//Here I want to say that if there are no unpacked object propositions 
				//which are specified explicitly,
				//then it means that all are packed.
				String requiresPacked = "";
				if (methodPreconditionsUnpacked.isEmpty()) {
					String forallParameter = getNewForallParameter();
					
					//requires (forall x:Ref :: packedOK[x]);
					for (String p : predicates) {
						String formalParamsAndType = getStringPredParams(p, 1);
			        	String formalParamsNoType = getStringPredParams(p, 2);
			        	
			        	if (localFieldsInMethod.contains("packed"+p) && 
			        			!setFracEq1.contains(p)) {
			        		
						requiresPacked = 
						  requiresPacked.concat("requires (forall "+ formalParamsAndType + forallParameter+
								  ":Ref :: packed"+p+"["+formalParamsNoType + forallParameter+"]);\n");
			        	}
					}
					
					//I also write for the predicates of the previous classes that were translated.
			    	for (int i=0; i<numberFilesBefore; i++) {
			    		Set<String> bvPredicates = bv[i].getPredicates();
			    		for (String p : bvPredicates) {
							String formalParamsAndType = getStringPredParams(p, 1);
				        	String formalParamsNoType = getStringPredParams(p, 2);
				        	if (localFieldsInMethod.contains(p) &&
				        			!setFracEq1.contains(p) ) {
							requiresPacked = 
							  requiresPacked.concat("requires (forall "+formalParamsAndType + 
									  forallParameter+":Ref :: packed"+p+
									  "["+formalParamsNoType + forallParameter+"]);\n");
						}
			    		}
			    	}
					
				} else {
					//TODO
					//Need an example with what happens when 
					//there are unpacked object propositions
					//in the precondition.
				}
				out.write(requiresPacked);
				
				//Here I generate 
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
		        	
		        	String forallParameter = getNewForallParameter();
		        	String formalParamsAndType = getStringPredParams(nameOfPredicate, 1);
		        	String formalParamsNoType = getStringPredParams(nameOfPredicate, 2);
		        	String ensuresForall = "";
		        	if (localFieldsInMethod.contains("packed"+nameOfPredicate) &&
		        			!setFracEq1.contains(nameOfPredicate)) {
		        	ensuresForall = ensuresForall.concat(
		        			"\t ensures (forall "+ formalParamsAndType + forallParameter+":Ref:: (");
		        	if (modifiedObjects.isEmpty()) {
		        		ensuresForall = ensuresForall.concat("packed"+nameOfPredicate + 
		        				"["+formalParamsNoType +forallParameter+
		        				"] == old(packed" + nameOfPredicate +"["+formalParamsNoType +
		        				forallParameter+"])));");
		        	} else {
		        		String[] modifiedObjectsArray = modifiedObjects.toArray(new String[0]);
		        		int len = modifiedObjectsArray.length;
		        		if (len > 1) {
		        			ensuresForall = ensuresForall.concat("(");
		        		}
		        		for (int k = 0; k < len - 1; k++) {
		        			ensuresForall = ensuresForall.concat(
		        					"("+forallParameter+"!="+modifiedObjectsArray[k]+") &&");
		        		}
		        		
		        		ensuresForall = ensuresForall.concat(
		        				"("+forallParameter+"!="+modifiedObjectsArray[len-1]+") ==> ");
		        		if (len > 1) {
		        			ensuresForall = ensuresForall.concat("(");
		        		}
		        		
		        		ensuresForall = ensuresForall.concat("(packed"+ nameOfPredicate + 
		        				"["+formalParamsNoType + forallParameter+
		        				"] == old(packed"+nameOfPredicate+"["+
		        				formalParamsNoType + forallParameter+"]))));");
		        	}
		        }
		        	
		        	if (!ensuresForall.equals(""))
		        		out.write(ensuresForall+"\n");
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
				out.write("{\n"+stringOfVarDecls);
				out.write(methodBody.get(currentMethod));
							
			}
		catch (Exception e) {
			System.err.println("Error: " + e.getMessage());
		}
    		
    		isFirstMethod = false;
    }
    
String getNewForallParameter() {
    String forallParameter = "x";
	int i=0;
	while (parametersMethod.contains(forallParameter)) {
		forallParameter = forallParameter.concat(i+"");
		i++;		
	}
	return forallParameter;
    }

    public void visitReturnStatement(ReturnStatement ast) throws ParseException
    {
        visitChildren(ast);
    }

    public void visitFieldSelection(FieldSelection ast) throws ParseException
    {
    	String identifierName = ast.getIdentifier().name;
    	
        if (currentMethod != "") {
     		   modifyFieldsInMethod(identifierName);   
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
    	modifyMethodsInMethod(new FieldAndTypePair(methodName, lastPrimaryExpressionType));
    	   	
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
        			statementContent.concat(
        					fracString.getStatementFracString(true, identifierBeforeMethSel));
        		modifyFieldsInMethod(fracString.getNameFrac());
        	}
    	}
    	
    	currentEnsuresFrac = 
    			ensuresFrac.get(methodName);
    	if (currentEnsuresFrac != null) {
    		for (int pf = 0; pf < currentEnsuresFrac.size(); pf++) {
                FracString fracString = currentEnsuresFrac.get(pf);
        		statementContent = 
        			statementContent.concat(fracString.getStatementFracString(false, identifierBeforeMethSel));
        		modifyFieldsInMethod(fracString.getNameFrac());
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
            		modifyFieldsInMethod(fracString.getNameFrac());
            	}
        	}
        	
        	currentEnsuresFrac = bv[classOfMethod].getEnsuresFrac().get(methodName);
        	if (currentEnsuresFrac != null) {
        		for (int pf = 0; pf < currentEnsuresFrac.size(); pf++) {
                    FracString fracString = currentEnsuresFrac.get(pf);
            		statementContent = 
            			statementContent.concat(fracString.getStatementFracString(false, identifierBeforeMethSel));
            		modifyFieldsInMethod(fracString.getNameFrac());
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
  			 if (insideObjectProposition && (currentMethod != "")) {
				  objectPropString = objectPropString.concat(symbol);
			  } else if (!insideObjectProposition && (currentMethod != "") && !insidePrecondition) {
  				  statementContent = statementContent.concat(symbol);  				
  			           } else if(!insideObjectProposition && (currentMethod != "") && insidePrecondition) {
  			        	   modifyMethodSpec(symbol);
  			           } 
  			           else
  			           {
  			             out.write(symbol);
  			       }
  			  
  		  }
  	      catch (Exception e) {
  	    		System.err.println("Error: " + e.getMessage());
  	      }
  		  }
  		  else {		
  			  FieldTypePredbody currentParamsPredicateBody = 
  					  paramsPredicateBody.get(namePredicate);
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
    	 
    	if (!namePredicate.equals("") || insidePrecondition) {
    		concatToStatementObjProp("(");
    	}
    	
		  children[0].accept(this );
		  concatToStatementObjProp(operatorSymbol);
		  children[1].accept(this );
	    if (!namePredicate.equals("") || insidePrecondition) {
	    	concatToStatementObjProp(")");
	    	}
		  
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
    	if (!inPackUnpackAnnotation) {
		  if ((currentMethod != "") && (inStatement) && !inArgumentList ) {
			  statementContent = statementContent.concat(astvalue);
		  }
		  
		  if ((currentMethod != "") && (inStatement) && (inArgumentList) ) {
			  statementContent = statementContent.concat(astvalue + ",");
		  }
		  
		  if ((currentMethod != "") && (!inStatement) && (inArgumentList) ) {
			 modifyMethodBody(astvalue + ",");
		  }
		  
		   if ((currentMethod != "") && !insideObjectProposition && insidePrecondition) {
			   modifyMethodSpec(astvalue);
		   }
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
    	
    	argumentsObjProp = args;
    	objectObjProp = objectString;
    	predicateNameObjProp = predName;
    	fractionObjProp = fracInObjProp;
    	if (fracInObjProp.equals("1")) {
    		fracInObjProp = fracInObjProp.concat(".0");
    		if (insidePrecondition || insidePostcondition) {
    			setFracEq1.add(predName);
    		}
    	}
    	
    	ObjPropString objProp =
    			new ObjPropString(objectString, fracInObjProp, 
    	    			predName, args);
    	if (isNumeric(fracInObjProp)) {
    		double d = Double.parseDouble(fracInObjProp); 
    		objProp.setExactFrac(d);
    	}
    	    	
    	PredicateAndFieldValue pv = new PredicateAndFieldValue(namePredicate, objectString);
    	String fieldName = quantifiedVars.get(pv);
        String bodyMethodOrPredicate = "";
        
        String bodyPredicate = "";
        fracString.setNameFrac("frac"+predName);
        fracString.setParameters(args);
       //TODO
        if (isNumeric(fracInObjProp)) {
    	if (fieldName == null){
    		
    		// This is where FracString is updated
    		// but only when we are inside a predicate.
    	bodyMethodOrPredicate = "packed"+predName+"[";
    	bodyMethodOrPredicate = bodyMethodOrPredicate.concat(writeActualParams(argumentsObjProp));
    	bodyMethodOrPredicate = bodyMethodOrPredicate.concat(objectString+
    			          "] && \n \t \t(frac"+predName+"[");
    	bodyMethodOrPredicate = bodyMethodOrPredicate.concat(writeActualParams(argumentsObjProp));
    	bodyMethodOrPredicate = bodyMethodOrPredicate.concat(objectString+ "] == " + fracInObjProp+")");
    	bodyPredicate = "frac"+predName+"[";
    	bodyPredicate = bodyPredicate.concat(writeActualParams(argumentsObjProp));
    	bodyPredicate = bodyPredicate.concat(objectString+ "] == " + fracInObjProp);
    	}
    	else {
    		
        	bodyMethodOrPredicate = "packed"+predName+"[";
        	bodyMethodOrPredicate = bodyMethodOrPredicate.concat(writeActualParams(argumentsObjProp));
        	bodyMethodOrPredicate = bodyMethodOrPredicate.concat(fieldName +
			          "[this]] && \n \t \t(frac"+predName+"[");
        	bodyMethodOrPredicate = bodyMethodOrPredicate.concat(writeActualParams(argumentsObjProp));
        	bodyMethodOrPredicate = bodyMethodOrPredicate.concat(fieldName + "[this]] == " + fracInObjProp+")");

        	bodyPredicate = "frac"+predName+"[";
        	bodyPredicate = bodyPredicate.concat(writeActualParams(argumentsObjProp));
        	bodyPredicate = bodyPredicate.concat(fieldName + "[this]] == " + fracInObjProp);
    		fracString.setField(fieldName);
    		objProp.setObject(fieldName+"[this]");
    				
    	} 
    	} else {
        	if (fieldName == null){
        		
        		// This is where FracString is updated
        		// but only when we are inside a predicate.       	
        	bodyMethodOrPredicate = "packed"+predName+"[";
        	bodyMethodOrPredicate = bodyMethodOrPredicate.concat(writeActualParams(argumentsObjProp));
        	bodyMethodOrPredicate = bodyMethodOrPredicate.concat(objectString+
        			          "] && \n \t \t(frac"+predName+"[");
        	bodyMethodOrPredicate = bodyMethodOrPredicate.concat(writeActualParams(argumentsObjProp));
        	bodyMethodOrPredicate = bodyMethodOrPredicate.concat(objectString+ "] > 0.0)");
        	bodyPredicate = "frac"+predName+"[";
        	bodyPredicate = bodyPredicate.concat(writePredParamsOutOrToString(predName, 2, true));
        	bodyPredicate = bodyPredicate.concat(objectString+ "] > 0.0)");
        	}
        	else {
        		
            	bodyMethodOrPredicate = "packed"+predName+"[";
            	bodyMethodOrPredicate = bodyMethodOrPredicate.concat(writeActualParams(argumentsObjProp));
            	bodyMethodOrPredicate = bodyMethodOrPredicate.concat(fieldName +
    			          "[this]] && \n \t \t(frac"+predName+"[");
            	bodyMethodOrPredicate = bodyMethodOrPredicate.concat(writeActualParams(argumentsObjProp));
            	bodyMethodOrPredicate = bodyMethodOrPredicate.concat(fieldName + "[this]] > 0.0)");

            	bodyPredicate = "frac"+predName+"[";
            	bodyPredicate = bodyPredicate.concat(writeActualParams(argumentsObjProp));
            	bodyPredicate = bodyPredicate.concat(fieldName + "[this]] > 0.0)");
        		fracString.setField(fieldName);
        		objProp.setObject(fieldName+"[this]");
        				
        	} 
    	}
    	
    	
    	// The minBound is the same if the fieldName is null or not. 
    	// We do not need to set this inside the if branches.
    	fracString.setMinBound(0);
    	
    	if ((currentMethod != "") && !inPackUnpackAnnotation) {
    		modifyMethodSpec(bodyMethodOrPredicate);
 
    		if (insidePrecondition) {
    			modifyMethodPreconditions(objProp);
    			modifyRequiresFrac(fracString);
    		} 
    		else {
    			modifyMethodPostconditions(objProp);
    			modifyEnsuresFrac(fracString);
    		}
    	}
    	else if (currentMethod == "") {
    		modifyPredicateBody(bodyPredicate);
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
    
    	}
    
    public void visitFormalParameter(FormalParameter ast) throws ParseException
    {
    	if (ast!=null) {
    		String name = ast.getName();
    		parametersMethod.add(name);
    		String type = ast.getType().toString();
    	if (namePredicate!="") {
    		modifyFormalParams(name, type); 
    	}
    	else if (currentMethod !="") {
    		modifyMethodParams(name+ ":" + type +",");
    	}
    		//I don't think this node has any children.
    		visitChildren(ast);
    	}
    }
    
    public void visitAllocationExpression(AllocationExpression ast) throws ParseException
    {
    	String predicateOfConstruct = ast.getPredicate();
    	
    	modifyMethodBody("\t call Construct" + ast.getAlloc_func()+"(");
    	AST[] children = ast.getChildren();
    	//This is the ArgumentList that contains the arguments
    	//for the fields.
        children[1].accept(this);
       
        modifyMethodBody(localVariableName + ");\n");
        modifyMethodBody("packed" +predicateOfConstruct+"[");
        
        children[0].accept(this);
        
        modifyMethodBody(localVariableName + "] := true;\n");
        
        modifyMethodBody("frac" +predicateOfConstruct+"[");
        
        children[0].accept(this);
        
        modifyMethodBody(localVariableName + "] := 1.0;\n");
        
        modifyFieldsInMethod("packed" +predicateOfConstruct);
        modifyFieldsInMethod("frac" +predicateOfConstruct);
        
        
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
    			stringOfVarDecls = stringOfVarDecls.concat("\t var " + localVariableName +":Ref;\n");
    		}
    		else 
    			stringOfVarDecls = stringOfVarDecls.concat("\t var " + localVariableName +":"+ fieldType+";\n");
    	
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
     		   if (!inPackUnpackAnnotation) {
     		   if ((currentMethod != "") && (inArgumentList)) {
     			   modifyMethodBody(keywordString + ",");
     		   }
     		   
     		   if ((currentMethod != "") && (inStatement) && 
     			   !inArgumentList && !inMethodSelectionStatement ) {
     				  statementContent = statementContent.concat(keywordString);
     			  }
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
    
    public void visitFractionAnnotation(FractionAnnotation ast) 
  		  throws ParseException
  		  { 
    	
    	String localObjectObjProp;
    	String localPredicateNameObjProp;
    	
    	//LinkedList<String> localArgumentsObjProp = new LinkedList<String>();
    	
    	String annotationName = ast.getName();
    	String toWrite = "";
    	inPackUnpackAnnotation = true;
    	AST[] children0 = ast.getChildren();
    	ArgumentList argAst = (ArgumentList)(children0[0]);
    	AST[] children = argAst.getChildren();
    	

    	objectObjProp = "";
    	predicateNameObjProp = "";
    	argumentsObjProp.clear();
    	fractionObjProp = "";
    	children[0].accept(this);
    	localObjectObjProp = objectObjProp;
    	localPredicateNameObjProp = predicateNameObjProp;
    	//localFractionObjProp = fractionObjProp;
    	//localArgumentsObjProp = argumentsObjProp;
    
    	toWrite = toWrite.concat("frac"+predicateNameObjProp+"[");
    	for (int i=0;i<argumentsObjProp.size();i++) {
    		toWrite = toWrite.concat(argumentsObjProp.get(i)+", ");
    	}
    	toWrite = toWrite.concat(objectObjProp + "] := ");
    	//Same code as above
    	//TODO remove duplicate code
    	toWrite = toWrite.concat("frac"+predicateNameObjProp+"[");
    	for (int i=0;i<argumentsObjProp.size();i++) {
    		toWrite = toWrite.concat(argumentsObjProp.get(i)+", ");
    	}
    	toWrite = toWrite.concat(objectObjProp + "]");
    	
  	  for (int i = 1; i < children.length; i++) {
  		objectObjProp = "";
    	predicateNameObjProp = "";
    	argumentsObjProp.clear();
    	fractionObjProp = "";
		children[i].accept(this);
		assert(localObjectObjProp == objectObjProp);
		assert(localPredicateNameObjProp == predicateNameObjProp);
		if (annotationName.equals("addFrac")) {
		toWrite = toWrite.concat("+" + fractionObjProp);
		} else {
			
		}
		  //TODO
		  //need to add assert for LinkedList too
	  }
    	
  	modifyMethodBody(toWrite);
  	inPackUnpackAnnotation = false;
  		  }
    
    
    public void visitPackUnpackAnnotation(PackUnpackAnnotation ast) 
  		  throws ParseException
  		  { 
    	String annotationName = ast.getName();
    	String toWrite = "";
    	inPackUnpackAnnotation = true;
    	
    	objectObjProp = "";
    	predicateNameObjProp = "";
    	argumentsObjProp.clear();
    	visitChildren(ast); 
    	
    	if (annotationName.equals("pack")) {
    		toWrite = toWrite.concat("call Pack"); 
    	}
    	else {
    		toWrite = toWrite.concat("call Unpack"); 
    	}
    	
    	toWrite = toWrite.concat(predicateNameObjProp + "("); 
    	
    	for (int i=0;i<argumentsObjProp.size();i++) {
    		toWrite = toWrite.concat(argumentsObjProp.get(i)+", ");
    	}
    	
    	toWrite = toWrite.concat(objectObjProp + ");\n"); 
    	
    	toWrite = toWrite.concat("packed" + predicateNameObjProp+"[");
    	for (int i=0;i<argumentsObjProp.size();i++) {
    		toWrite = toWrite.concat(argumentsObjProp.get(i)+", ");
    	}
    	toWrite = toWrite.concat(objectObjProp + "] := "); 
    	
       	if (annotationName.equals("pack")) {
    		toWrite = toWrite.concat("true"); 
    	}
    	else {
    		toWrite = toWrite.concat("false"); 
    	}
       	modifyFieldsInMethod("packed"+predicateNameObjProp);
    	
    	modifyMethodBody(toWrite);
    	inPackUnpackAnnotation = false;
    	
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
    		   if (!inPackUnpackAnnotation) {
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
    		   
    		   if ((currentMethod != "") && !insideObjectProposition && insidePrecondition) {
    			   modifyMethodSpec(identifierName);
    		   }

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
    	precondition.accept(this);
    	insidePrecondition = false;
    	modifyMethodSpec(";\n");
    	}

    	if (postcondition != null) {
    	modifyMethodSpec("\t ensures ");
    	insidePostcondition = true;
    	postcondition.accept(this);
    	insidePostcondition = false;
    	modifyMethodSpec(";\n");
    	}
    	
    	}
    
    public void visitBlock(Block ast) 
  		  throws ParseException 
  		  { 
    	if (inIfStatement) {
    	modifyMethodBody("{\n");
    	}
    	
    	AST[] children = ast.getChildren();
    	for (int i = 0; i < children.length; i++) {  		
    		fieldsInStatement.clear();
    		statementContent = "";
    		visitedMethSel = false;
    		inMethodSelectionStatement = false;
    	    children[i].accept(this);

    	    modifyMethodBody(statementContent);
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
		//For the else branch, it means this predicate does not exist.
		//We should not end up on this branch.
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
    
    void modifyFieldsInMethod(String s) {
    	Set<String> currentFieldsInMethod = 
    			fieldsInMethod.get(currentMethod);
    	if (currentFieldsInMethod == null) {
    		currentFieldsInMethod = new TreeSet<String>();
    	}
    	currentFieldsInMethod.add(s);
    	fieldsInMethod.put(currentMethod, currentFieldsInMethod);    	
    }
    
    void modifyMethodsInMethod(FieldAndTypePair s) {
    	LinkedList<FieldAndTypePair> currentMethodsInMethod = 
    			methodsInMethod.get(currentMethod);
    	if (currentMethodsInMethod == null) {
    		currentMethodsInMethod = new LinkedList<FieldAndTypePair>();
    	}
    	currentMethodsInMethod.add(s);
    	methodsInMethod.put(currentMethod, currentMethodsInMethod);    	
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

    		for (String p : predicates) {
    			out.write("var packed" + p + ": [");
    			writePredParamsOutOrToString(p, 3, false);
    			 out.write("Ref] bool;\n");
    			 out.write("var frac" + p + ": [");
    			 writePredParamsOutOrToString(p, 3, false);
    			 out.write("Ref] real;\n");
    			
    			 /*
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
    	  	   	*/
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
            	String type = s.getType();
            	if (type.equals("int") || type.equals("double") || type.equals("boolean")) {
            	out.write(s.getName() + "1 :"+ type + ", ");
            	}
            	else {
            		out.write(s.getName() + "1 : Ref, ");
            	}
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
	//TODO
	//What does this method do?
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

//I am not inferring the calling of pack/unpack.
//The programmer now puts the annotations in the body of the Java files.
//The inference of the pack/unpack can be done as a side project.
//That would be a search problem + heuristics.
	
	String writeActualParams(LinkedList<String> argsObjProp) {
		String result = "";
		for (int i=0;i<argsObjProp.size();i++){
			result = result.concat(argsObjProp.get(i)+",");
		}
		return result;
	}
    
    //k=1 is for writing nameParam: type
    //k=2 is for writing the current value of the parameters
    //k=3 is for writing the types of the parameters
	//toStrinf is false if we want to write to out or true if we need to return 
	//the string that contains the modifications.
    String writePredParamsOutOrToString(String pred, int k, boolean toString) {
    	String result = "";
    	if (toString) {
        	FieldTypePredbody currentParamsPredicateBody = paramsPredicateBody.get(pred);
    		if (currentParamsPredicateBody != null) {
    			LinkedList<FieldAndTypePair> formalParamsList =
    					currentParamsPredicateBody.getFormalParameters();
    			if (!formalParamsList.isEmpty()) {
    				for (int i=0;i<formalParamsList.size();i++) {
    					FieldAndTypePair f = formalParamsList.get(i);
    					switch (k) {
    		            case 1:  
    		            	result = result.concat(f.getName() + ":"+f.getType()+", ");
    		                break; 
    		            case 2: 
    		            	result = result.concat(f.getName() + ", ");
    		            	break;
    		            case 3: 
    		            	result = result.concat(f.getType() + ", ");
    		            	break;
    		            default: 
    		            	break;
    					}
    					
    				}
    			}	
    		}
    		
    	} else {
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
    	
    	return result;
    }    
    
    //k=1 is for writing nameParam: type
    //k=2 is for writing the current value of the parameters
    //k=3 is for writing the types of the parameters
    String getStringPredParams(String pred, int k) { 
    	String result = "";
    	FieldTypePredbody currentParamsPredicateBody = paramsPredicateBody.get(pred);
		if (currentParamsPredicateBody != null) {
			LinkedList<FieldAndTypePair> formalParamsList =
					currentParamsPredicateBody.getFormalParameters();
			if (!formalParamsList.isEmpty()) {
				for (int i=0;i<formalParamsList.size();i++) {
					FieldAndTypePair f = formalParamsList.get(i);
					switch (k) {
		            case 1:  
		            	result = result.concat(f.getName() + ":"+f.getType()+", ");
		                break; 
		            case 2: 
		            	result = result.concat(f.getName() + ", ");
		            	break;
		            case 3: 
		            	result = result.concat(f.getType() + ", ");
		            	break;
		            default: 
		            	break;
					}	
				}
			}	
		} 
		return result;
    }
    
    
    }