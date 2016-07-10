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
import cager.jexpr.ast.ArgumentAndFieldPair;
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
import cager.jexpr.ast.FractionManipulationStatement;
import cager.jexpr.ast.IdentifierExpression;
import cager.jexpr.ast.IfStatement;
import cager.jexpr.ast.KeywordExpression;
import cager.jexpr.ast.LiteralExpression;
import cager.jexpr.ast.LocalVariableDeclaration;
import cager.jexpr.ast.MethodDeclaration;
import cager.jexpr.ast.MethodSelection;
import cager.jexpr.ast.MethodSpecExpression;
import cager.jexpr.ast.MethodSpecFracVariable;
import cager.jexpr.ast.MethodSpecVariable;
import cager.jexpr.ast.MethodSpecVariables;
import cager.jexpr.ast.ObjectProposition;
import cager.jexpr.ast.PackUnpackAnnotation;
import cager.jexpr.ast.PredicateDeclaration;
import cager.jexpr.ast.PrimaryExpression;
import cager.jexpr.ast.QuantifierVariable;
import cager.jexpr.ast.QuantifierVariables;
import cager.jexpr.ast.ReturnStatement;
import cager.jexpr.ast.StatementExpression;
import cager.jexpr.ast.TypedAST;
import cager.jexpr.ast.UnaryExpression;
import cager.jexpr.ast.WriteOut;

/*
 * This class visits the AST to generate the Boogie code.
 */
public class BoogieVisitor extends NullVisitor {
	
	// For each "packed" in the current method, this map
	// points to a set that holds all the objects
	// that are modified through "packed" in the body of the method.
	HashMap<String, Set<String>> packedModifiedArgsInMethod =
			new HashMap<String, Set<String>>();
		
	// For each "packed" in the current method, this map
	// points to a set that holds all the objects
	// of packedPred[object] (even if it's == false)
	// that are mentioned in the precondition of the method.
	// The above 3 maps will tell us what to write in the 
	// "requires forall" for the packed variables
	// in the pre-conditions of methods.
	HashMap<String, Set<String>> packedModifiedArgsInPrecondition =
			new HashMap<String, Set<String>>();	
	
	// For each method, this map has pairs of (predicate, object)
	// that are mentioned in the precondition but not in the postcondition.
	HashMap<String, Set<FieldAndTypePair>> predObjNotMentionedPostcond =
			new HashMap<String, Set<FieldAndTypePair>>();
	
	// This maps the (predicate, identifier) to the field that the
	// identifier represents.
	HashMap<PredicateAndFieldValue, String> quantifiedVars = 
			new HashMap<PredicateAndFieldValue, String> ();
	
	//The name of the Java class.
	String className;
	
	// For the pre-, or post-conditions of a method,
	// or for the translation body of a perdicate,
	// this is the number of the disjunction member
	// in the list of conjunctions making up the above.
	int disjunctionNumber = -1;
	
	//The name of the current local variable
	String localVariableName = "";
	
	//Is this the first method that is being translated 
	boolean isFirstMethod = true;
	
	//Are we in an argument list?
	boolean inArgumentList = false;
	
	//Are we in a binary expression?
	boolean inBinaryExpression = false;
	
	// Are we before child 0 in a binary expression?
	boolean beforeChild0 = false;
	
	// Are we in the left part of the ~=> implies?
	// This is for setting FractionManipulationStatement.
	boolean inChild1OfImplies = false;
	
	// Are we in the right part of the ~=> implies?
	// This is for setting FractionManipulationStatement.
	boolean inChild2OfImplies = false;
	
	// The 2 variables needed for the fractionManipulation statements
	// that I add after a call to a pack/unpack of a predicate
	// and after a call to a method.
	// If the fraction manipulation is not surrounded by an if statement
	// the first variable "ifConditionFractionManipulation" will be null.
	// Depending on the situation, the other variable might be null.
	String ifConditionFractionManipulation = "";
	
	boolean inPackUnpackAnnotation = false;
	
	//True iff this program contains the modulo operator.
	boolean programContainsModulo = false;
	boolean writtenModuloFunction = false;
	
	//Does this primary expression contain a methodSelection?
	//Are we inside a method selection statement?
	boolean inMethodSelectionStatement = false;
	
	//Are we inside a field selection statement?
	boolean inFieldSelection = false;
		
	//The name of the last identifier or keyword expression (for "this").
	//It is used in visitFieldSelection.
	//It also can be of the form fieldName[object].
	String lastIdentifierOrKeyword = "";	
		
	String lastPrimaryExpressionType = "";
	
	// all the local variables declared in a method
	String stringOfVarDecls = "";
	
	//For each predicate name, this maps to a list of PackObjMods. 
	//This map needs to be reset in the beginning of each method.
	//The first String represents the name of the predicate.
	//See class PackObjMods for more insight.
	HashMap<String, LinkedList<PackObjMods>> packedMods = 
			new HashMap<String, LinkedList<PackObjMods>>();
	
	// For each predicate, this gives the list of the field that each 
	// argument of the predicate represents. If the argument does not 
	// represent the current value of a 
	// field, then I put "" in this list.
	HashMap<String, LinkedList<ArgumentAndFieldPair>> predArgWhichField = 
			new HashMap<String, LinkedList<ArgumentAndFieldPair>>();
	
	// For each predicate name, this maps it to its body represented 
	// as a String.
	HashMap<String, FieldTypePredbody> paramsPredicateBody = 
			new HashMap<String, FieldTypePredbody>();
	
	// For each predicate or method, I have a list of 
	// (if condition) (fracPredicate[object], fractionAmount) 
	// representing the fraction annotations that will be added 
	// to the code right after packing/unpacking a predicate
	// and right after calling a method.
	// The String key in the HashMap is the name of the predicate or 
	// method.
	HashMap<String, LinkedList<FractionManipulationStatement>>  fractionManipulationsListPredicate = 
			new HashMap<String, LinkedList<FractionManipulationStatement>>();
	HashMap<String, LinkedList<FractionManipulationStatement>>  fractionManipulationsListMethodPre = 
			new HashMap<String, LinkedList<FractionManipulationStatement>>();
	HashMap<String, LinkedList<FractionManipulationStatement>>  fractionManipulationsListMethodPost = 
			new HashMap<String, LinkedList<FractionManipulationStatement>>();
	
	// This maps each method name to its String method body.
	HashMap<String, String> methodBody = new HashMap<String, String>();
	
	// This maps each method name to its String method specification
	// (pre- and post-condition).
	HashMap<String, String> methodSpec = new HashMap<String, String>();
	
	//This maps each method name to the parameters of that method.
	HashMap<String, LinkedList<FieldAndTypePair>> methodParams = 
			new HashMap<String, LinkedList<FieldAndTypePair>>();

	//This maps each method name to the existential variables of that method.
	//For each variable there is a part  name+ ":" + type +",".
	HashMap<String, String> methodExistentialParams = 
			new HashMap<String, String>();
	
	//For each name of a field, this map tells us which is the predicate that 
	//has the permission to the field.
	//There might be more than one predicate for the same
	//field, but they don't exist at the same time in a method. 
	HashMap<String, LinkedList<String>> fieldWhichPredicates = 
			new HashMap<String, LinkedList<String>>();	
	
	// This gathers the fields in each statement. 
	// Not in each block, but in each statement.
	// A statement is a child of a block.
	Set<String> fieldsInStatement = new TreeSet<String>();
	
	//Set of predicates for the corresponding frac global variables that are 1
	//in the requires or ensures of the current method.
	//TODO I think this should only contain the predicates for which the fraction is 1 in the 
	// postcondition only, it does not matter if it is 1 in the precondition.
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
	
	//For each method, this map
	//tells us which fracPred's are in the body of that method.
	//This is to know for what frac fields to write 
	//assume (forall y:Ref :: (fracRight[y] >= 0.0) );
	HashMap<String, LinkedList<String>>  methodBodyFrac = 
			new HashMap<String, LinkedList<String>>();
	
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
	//If we are not inside a predicate, this variable will be "".
	String namePredicate;
	
	//The name of the current method that we are parsing in Oprop 
	//and translating in Boogie. 
	String currentMethod;
	
	//The set of fields of this Oprop class.
	//This includes packed and frac, but fieldsTypes does not.
	Set<String> fields = new TreeSet<String>();
	
	//The arraylist of (field, type) pairs of this Oprop class.
	ArrayList<FieldAndTypePair> fieldsTypes = 
			new ArrayList<FieldAndTypePair>();
	
	//The set of predicates of this Oprop class.
	Set<String> predicates = new TreeSet<String>();
	
	//The set of methods in this Oprop class.
	Set<String> methods = new TreeSet<String>();
	
	//For each method, fieldsInMethod contains 
	//the set of fields in that method. 
	//This structure only contains the fields, including packedPred and fracPred,
	//on the left of :=.
	// XXXX This is what I need, it represents
	// what gets put in the "modifies" clause of each method.
	HashMap<String, Set<String>> fieldsInMethod = 
			new HashMap<String, Set<String>>();
	
	//For each method, unpackedPredicatesInPrecondition contains the
	//set of (predicate, object) for which we have unpacked(object#.. Predicate(..)
	//in the precondition of that method.
	HashMap<String, LinkedList<PredicateAndFieldValue>> unpackedPredicatesInPrecondition = 
			new HashMap<String, LinkedList<PredicateAndFieldValue>>();
	
	//For each method, methodsInMethod contains 
	//the set of methods called in that method.
	HashMap<String, LinkedList<FieldAndTypePair>> methodsInMethod = 
			new HashMap<String, LinkedList<FieldAndTypePair>>();
	
	//The set of input parameters for the current method.
	Set<String> parametersMethod = new TreeSet<String>();

	//This boolean is true iff we are currently parsing 
	//an object proposition. 
	boolean insideObjectProposition;
	
	//This boolean is true iff we are currently inside 
	//the pre-condition of a method.
	boolean insidePrecondition;
	
	//This boolean is true iff we are currently inside 
	//the post-condition of a method.
	boolean insidePostcondition;
	
	//The string of the object proposition inside 
	//which we are at the moment.
	//This can also represent just components of 
	//an object proposition.
	String objectPropString = "";
	
	String objectObjProp;
	String predicateNameObjProp;
	String fractionObjProp;
	//The actual arguments of the current object proposition.
	LinkedList<String> argumentsObjProp = new LinkedList<String>();
	
	//The actual existential arguments of the current object proposition.
	LinkedList<String> existentialArgsObjProp = new LinkedList<String>();
	
	//The actual arguments for the constructor that is currently called.
	LinkedList<String> argumentsConstructor = new LinkedList<String>();
	
	//The actual arguments for the predicate in the constructor 
	//that is currently called.
	LinkedList<String> argumentsPredicate = new LinkedList<String>();
	
	// The actual arguments for the current method.
	LinkedList<String> actualArgumentsMethod = new LinkedList<String>();
	
	//The Boogie Visitors of the files that have been translated before this one.
	BoogieVisitor[] bv;
	
	int numberFilesBefore;
	
	public BoogieVisitor(
			BufferedWriter boogieFile, 
			int n, 
			BoogieVisitor[] bv0
	) {
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
	
	public HashMap<String, LinkedList<ObjPropString>> 
	getMethodPreconditionsPacked() {
		return methodPreconditionsPacked;
	}

	public HashMap<String, LinkedList<ObjPropString>> 
	getMethodPostconditionsPacked() {
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
	
    public void visitCompilationUnits(CompilationUnits ast) 
    		throws ParseException
    {
        visitChildren(ast);
    }
    
    public void visitCompilationUnit(CompilationUnit ast) 
    		throws ParseException
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
    		System.err.println("Error in visitCompilationUnit: " + e.getMessage());
    	}
        visitChildren(ast);
    }
    
    public void visitFieldDeclaration(FieldDeclaration ast) 
    		throws ParseException 
  { 
    	try {
    		String fieldName = ast.getName();
    		fields.add(fieldName);
    		fieldsTypes.add(
    			new FieldAndTypePair(fieldName, ast.getType().toString()));
    	
    		String fieldType = ast.getType().toString();
    		boolean isClass = false;

    		if (fieldType.equals(className)) {
    			isClass = true;
    		}
    		for (int i=0; i<numberFilesBefore; i++) {
    			if (bv[i].getClassName().equals(fieldType) )
    				isClass = true;
    		}
    	
    		if (isClass) {
    			out.write("var "+ fieldName +": [Ref]Ref;\n");
    		}
    		else 
    			out.write("var "+ fieldName +": [Ref]"+fieldType+";\n");
    	}
    	catch (Exception e) {
    		System.err.println("Error in visitFieldDeclaration: " + e.getMessage());
    	}
    	visitChildren(ast);  
    	}
    
    public void visitPredicateDeclaration(PredicateDeclaration ast) 
    		throws ParseException
    { 
    try {
    	namePredicate = ast.getIdentifier().getName();
    	predicates.add(namePredicate);
    	paramsPredicateBody.put(namePredicate, new FieldTypePredbody());
    	predArgWhichField.put(namePredicate, null);
    	fractionManipulationsListPredicate.put(namePredicate, new LinkedList<FractionManipulationStatement>());
    }     	
    catch (Exception e) {
		System.err.println("Error in visitPredicateDeclaration: " + e.getMessage());
	}
    	AST[] children = ast.getChildren();
    	//Visit formal parameters.
    	children[0].accept(this);
    	//TODO
    	//need to take care of formal parameters
    	//Visit expression.
    	disjunctionNumber = -1;
    	children[1].accept(this);
    }
    
    public void visitQuantifierVariables(QuantifierVariables ast) 
    		throws ParseException
  	{  
    	visitChildren(ast);
  	}
    
    public void visitQuantifierVariable(QuantifierVariable ast) 
    		throws ParseException
  	{    		
    	if (ast!=null) {
    	  try { 
    		String name = ast.getName();
    	
    		if (currentMethod != "") {
    			parametersMethod.add(name);
    		}
    		String type = ast.getType().toString();
    		if (namePredicate!="") {
    			// Only fractions are of type double
    			// and we don't need to keep track of them.
    			if(! (type.equals("double")) ) {
    				modifyExistentialParams(name, type); 
    				addArgToPredArgWhichField(name);
    				// TODO need to write a similar method for methods
    			}
    		}
    		else if (currentMethod !="") {
    			modifyMethodExistentialParams(name+ ":" + type +",");
    		}
    	    }     	
    	    catch (Exception e) {
    			System.err.println("Error in visitQuantifierVariable: " + e.getMessage());
    		}
    		visitChildren(ast);
    	}
    }
    
    String writeAllPredArgWhichField(String namePred, String predBody) 
    {
    	try {
    	//As I modify predBody with the new additions,
    	//I keep count of the length of the strings that I add.
    	int offset=0;
    			
    	// Now we modify predBody with the connections
		// between the arguments of this predicate and
		// the arguments of other object propositions that are
		// contained in this predicate.
    	// The list of object propositions in the predicate
    	// named "namePred".
    	LinkedList<ObjPropString>  listObjProp = 
    			predicateObjProp.get(namePred);
    	
    	if (listObjProp != null) {

    		for (int i = 0; i < listObjProp.size(); i++) {
    			ObjPropString objProp = listObjProp.get(i);
    		
    			// The list of which field each argument of 
    			// this predicate represents.
    			LinkedList<ArgumentAndFieldPair> listArgsToFields =
    					predArgWhichField.get(objProp.getName());
        
    			String oneObjProp = writePredArgWhichField(
    					listArgsToFields, 
    					objProp.getParams(), 
    					objProp.getObject()
    					);
    		
    			int location = objProp.getLocation();
    			String firstHalf = predBody.substring(0, location+ offset+1);
    			String secondHalf = predBody.substring(
    					location+offset+1, predBody.length());
    		
    			predBody = firstHalf.concat(oneObjProp).concat(secondHalf);
    			offset += oneObjProp.length();	
    		}	
    	}
    	
    	// Second modify predBody with parameters representing the values
    	// of fields in the current predicate.	
    	// The list of which field each argument of 
		// this predicate represents.
		LinkedList<ArgumentAndFieldPair> listArgsToFieldsThisPred =
				predArgWhichField.get(namePred);
			if (listArgsToFieldsThisPred != null) {
				String argsToFieldsString = "";
				for (int i = 0; i < listArgsToFieldsThisPred.size(); i++) {
					ArgumentAndFieldPair argField = listArgsToFieldsThisPred.get(i);
					String field = argField.getField();
					// The second part of this condition says that this is a proper field
					// of this predicate,
					// that it is not a field of another object proposition.
					if (!field.equals("") && (!field.contains(" ")) ) {
						argsToFieldsString = argsToFieldsString.concat(
								" && ("+field+ 
								"[this]=="+argField.getArgument()+")");
					}
				}
				predBody = predBody.concat(argsToFieldsString);
		}

    	}
	    catch (Exception e) {
			System.err.println("Error in writeAllPredArgWhichField: " + e.getMessage());
		}
    	
    	return predBody;
	
    }
    
    boolean areEqualSetsPacked(
    		Set<FractionManipulationStatement> setPre,
    		Set<FractionManipulationStatement> setPost 		
    ) {
    	// copy constructor
    	Set<FractionManipulationStatement> setPreCopy =
    			new TreeSet<FractionManipulationStatement>(setPre);
    	//1. check if setPre and setPost have the same number of elements
    	//2. go through all the elements in setPre and for each look in setPost.
    	//3. if you find it remove it from setPre
    	//4. at the end, setPre should be empty and then the sets are equal
    	if (setPre.size() == setPost.size()) {
    		Iterator<FractionManipulationStatement> iterPre = setPre.iterator();
    		 while (iterPre.hasNext()) {
    			 FractionManipulationStatement elementPre = iterPre.next();
    			 Iterator<FractionManipulationStatement> iterPost = setPost.iterator();
    			 while (iterPost.hasNext()) {
        			 FractionManipulationStatement elementPost = iterPost.next();
        			 if (elementPre.equalsForPacked(elementPost)) {
        				 // remove the element from setPreCopy
        				 setPreCopy.remove(elementPre);
        				 // break out of the interior while
        				 break;
        			 }
    		    }	
    	}
    	}
    	
    	if (setPreCopy.isEmpty()) return true;
    	return false;
    }
    
    // TODO need to write a single function where I pass 
    // the function equalsForPacked or equalsForFractions as parameter
    boolean areEqualSetsFractions(
    	    		Set<FractionManipulationStatement> setPre,
    	    		Set<FractionManipulationStatement> setPost 		
    	    ) {
    	    	// copy constructor
    	    	Set<FractionManipulationStatement> setPreCopy =
    	    			new TreeSet<FractionManipulationStatement>(setPre);
    	    	//1. check if setPre and setPost have the same number of elements
    	    	//2. go through all the elements in setPre and for each look in setPost.
    	    	//3. if you find it remove it from setPre
    	    	//4. at the end, setPre should be empty and then the sets are equal
    	    	if (setPre.size() == setPost.size()) {
    	    		Iterator<FractionManipulationStatement> iterPre = setPre.iterator();
    	    		 while (iterPre.hasNext()) {
    	    			 FractionManipulationStatement elementPre = iterPre.next();
    	    			 Iterator<FractionManipulationStatement> iterPost = setPost.iterator();
    	    			 while (iterPost.hasNext()) {
    	        			 FractionManipulationStatement elementPost = iterPost.next();
    	        			 if (elementPre.equalsForFractions(elementPost)) {
    	        				 // remove the element from setPreCopy
    	        				 setPreCopy.remove(elementPre);
    	        				 // break out of the interior while
    	        				 break;
    	        			 }
    	    		    }	
    	    	}
    	    	}
    	    	
    	    	if (setPreCopy.isEmpty()) return true;
    	    	return false;
    	    }
    
    // Check if fracMan exists in setPre. Comparison function is not straight equality
    // it only checks if a FractionManipulationStatement exists with the same predicate and object
    // in setPost.
    boolean doesFractionManipulationExist(
    		 LinkedList<FractionManipulationStatement> listPost,
    		 FractionManipulationStatement fracMan	 
    ) {
    	boolean result = false;
		Iterator<FractionManipulationStatement> iterPost = listPost.iterator();
		 while (iterPost.hasNext()) {
			 FractionManipulationStatement elementPost = iterPost.next();
			 if (
					 fracMan.getPredName().equals(elementPost.getPredName()) &&
					 fracMan.getFractionObject().equals(elementPost.getFractionObject())
			 ) {
				 result = true;
				 break;
			 }
		 }
    	return result;
    }
    
	void addToPackedModifiedObjects(
			HashMap<String, Set<String>> packedModifiedObjects,
			String predName, 
			String fractionObject
	 ) {
		Set<String> currentSetOfObjects = packedModifiedObjects.get(predName);
		if (currentSetOfObjects == null) {
			currentSetOfObjects = new TreeSet<String>();
		}
		currentSetOfObjects.add(fractionObject);
		packedModifiedObjects.put(predName, currentSetOfObjects);		
	}
	
	void addToFractionsModifiedObjects(
			HashMap<String, Set<String>> fractionsModifiedObjects,
			String predName, 
			String fractionObject
	 ) {
		Set<String> currentSetOfObjects = fractionsModifiedObjects.get(predName);
		if (currentSetOfObjects == null) {
			currentSetOfObjects = new TreeSet<String>();
		}
		currentSetOfObjects.add(fractionObject);
		fractionsModifiedObjects.put(predName, currentSetOfObjects);		
	}
    
    // First look at one obj prop, say objProp1, element in the precondition and what is its disjunction number.
    // Then put in a set all the obj props elements that have the same disjunction number in the precondition.
    // Then look for the obj props in the postcondition that 
    // are equal to objProp1, but don't include in the equality 
    // the disjunction number.
    // From the one that is equal to this one (assume there is only one for now, if there are more, 
    // it is left for future work),
    // construct the set that represents the whole disjunction
    // and see if it is equal to the set got from the pre-condition.
    // In this way I get everything that is the same in the pre and postconditions.
    // I also need to keep track of what is not the same, because they
    // will also tell me what I need to write in the (...)==> part of the 
    // ensures forall.
    // If what is in the precondition is exactly the same as what is in the postcondition,
    // then I can move on.
    // If what I have in the precondition cannot be found in the postcondition
    // then I take the object proposition of the precondition
    // and look for an object proposition in the postcondition that has the same object
    // and the same predicate. If it exists it means that it changed and it needs to go
    // in the map that keeps track of which objects changed for each predicate. 
	// For this last case, when I look if something changed, I
    // can disregard the disjunction number because I need an over approximation
    // of the packed/unpacked (and fractions for below?) that changed.
    // Also, if in the precondition it is surrounded by an ifCondition,
    // but in the postcondition it is not, I consider it changed.
    // If an object proposition with the same object and predicate cannot be found in the 
    // postcondition, this means that I cannot write an ensures forall for that predicate,
    // both for packed and for frac.
    // This is because the programmer did not have enough information to write anything
    // about that object proposition that we are looking for and thus they did 
    // not know how that predicate changed.
	// The above algorithm is only for disjunctions, but it should be similar 
	// for simple fractionManipulationStatements.
	// This returns void because all these parameters are
	// passed by value and thus modified by the end of this method.
	// forPacked is true here
    void constructStructuresForInferEnsuresForallPacked(
    		String methodName_, 
    		HashMap<String, Set<String>> packedModifiedObjects,
    		HashMap<String, Boolean> predicateIsMentioned,
    		Set<String> unpackedInPostcondition   		
    ) {
  		 Set<Integer> setDisjunctionNumbersPrecond = new TreeSet<Integer>();
  		 
        LinkedList<FractionManipulationStatement> fractionManipulationsListPre = 
        		 fractionManipulationsListMethodPre.get(methodName_);
         	 
        LinkedList<FractionManipulationStatement> fractionManipulationsListPost = 
             	 fractionManipulationsListMethodPost.get(methodName_);
  	 	
     	 // It is simpler for the map above to be populated right now
     	 // as it only requires a pass over the postconditions.
    	 for (int i=0; i<fractionManipulationsListPost.size(); i++) {
    		 FractionManipulationStatement fracMan = fractionManipulationsListPost.get(i);
    		 boolean isPacked = fracMan.getIsPacked();
    		 if (!isPacked) {
    			 String pred = fracMan.getPredName();
    			 unpackedInPostcondition.add(pred);
    		 } 
    	 }
    	  
    	 // This has to be done before the main while below because
    	 // otherwise the true for a predicate that was found before to have false
    	 // overwrites the false.
     	 // I need to put True for all predicates that are mentioned in the "modifies" 
     	 // to start with. 
    	 for (int i=0; i<fractionManipulationsListPre.size(); i++) {		 		 
    		 FractionManipulationStatement fracMan = fractionManipulationsListPre.get(i);
    		 // Initialize all the entries in maps to default values.
    		 predicateIsMentioned.put(fracMan.getPredName(), true);
    	 }
       	 
    	 for (int i=0; i<fractionManipulationsListPre.size(); i++) {		 		 
    		 FractionManipulationStatement fracMan = fractionManipulationsListPre.get(i);
    		 
    		 // I need to reconstruct the disjunction if fracMan has a disjunction number.
    		 int disjNumber = fracMan.getDisjunctionNumber();
    		 // disjNumber does not need to be specifically wrapped in an Integer
    		 // because Java does it automatically if need be.
    		 if ((disjNumber != -1) && (!setDisjunctionNumbersPrecond.contains(disjNumber))) {
    			 setDisjunctionNumbersPrecond.add(disjNumber);
    			 // TODO next !!!! remember lastDisjunctionNumber
    			 // so that I can start from it after I reset it to -1.
    			 // The set of object propositions in the precondition
    			 // that are in the same disjunction with the current object proposition.
    			 Set<FractionManipulationStatement> setPrecondDisjunctionFracMan = 
    					 new TreeSet<FractionManipulationStatement>();
    			 // I added compareTo() for the line below to work.
    			 setPrecondDisjunctionFracMan.add(fracMan);
    			 for (int j=i+1; j<fractionManipulationsListPre.size(); j++) {
    				 FractionManipulationStatement fracManAfter = fractionManipulationsListPre.get(j);
    				 if (fracManAfter.getDisjunctionNumber() == disjNumber) {
    					 setPrecondDisjunctionFracMan.add(fracManAfter);
    				 }
    			 }
    			 
    			 // Now construct the set of object propositions from the postcondition
    			 // that might be equal to the set setDisjunctionFracMan above.
    			 // The following part is all about the postconditions.
    			 // I start the process, the same one as above, but for postconditions.
    			 // The set of object propositions in the postcondition
    			 // that are in the same disjunction with fracMan.
    			 Set<FractionManipulationStatement> setPostcondDisjunctionFracMan = 
    					 new TreeSet<FractionManipulationStatement>();
    			  
    			 int j;
    			 int postDisjunctionNumber = -1;
    			 for (j=0; j<fractionManipulationsListPost.size(); j++) {
    				 FractionManipulationStatement fracManPost = fractionManipulationsListPost.get(j);
    				 if (fracManPost.getFractionObject().equals(fracMan.getFractionObject())
    						&&
    					(fracManPost.getPredName().equals(fracMan.getPredName()))
    						&&
    					(fracManPost.getIfCondition().equals(fracMan.getIfCondition()))
    						 ) {
    					 
    					 setPostcondDisjunctionFracMan.add(fracManPost);
    					 postDisjunctionNumber = fracManPost.getDisjunctionNumber();
    					 break;
    				 } 				
    			 }
    			 
    			 // Add all the fractionManipulations that have the same disjunctionNumber as 
    			 //fracManAfter above.
    			 if (postDisjunctionNumber!=-1) {				 
    				 for (int k=j+1; k<fractionManipulationsListPost.size(); k++) {
    					 FractionManipulationStatement fracManPost1 = fractionManipulationsListPost.get(k);
    					 if (fracManPost1.getDisjunctionNumber() == postDisjunctionNumber) {					 
    						 setPostcondDisjunctionFracMan.add(fracManPost1);
    					 }
    				 }
    			 }
    			 
    			 // Compare setPostcondDisjunctionFracMan and setPrecondDisjunctionFracMan.
    			 // If they are the same then I don't need to do anything.
    			 // If they are not the same, if there is an ifCondition,
    			 // I need to look at all the individual object propositions in the disjunction
    			 // for the precondition
    			 // and see how each changed in the postcondition.
    			 // If there is no ifCondition, I still need to look at all the 
    			 // object propositions in the postcondition and see if there is 
    			 // one that is like this one from the precondition.
    			 
    			 // For the composite example, we don't go inside this if.
    			 boolean areSetsEqual;
   			 
    			 areSetsEqual = areEqualSetsPacked(setPrecondDisjunctionFracMan, setPostcondDisjunctionFracMan);
    			     			 
    			 if (!areSetsEqual) {
    				 // Any of the objects in this disjunction of the precondition
    				 // that is not equal to one in the postcondition
    				 // means that they could change and cannot be included in the
    				 // ensures forall. Only if it was the exact same as the disjunction in the postcondition
    				 // it could have been included in the ensures forall.
    				 // The objects of the object propositions in the pre set could
    				 // be put straight in packedModifiedObjects, but it could be that
    				 // they are not even mentioned in the postcondition and in that case
    				 // packedIsMentioned needs to be modified for that predicate.
    				 // So we do need to find at least one object proposition in the postcondition
    				 // that has the same object and predicate as the current one 
    				 // taken from setPrecondDisjunctionFracMan.
    				Iterator<FractionManipulationStatement> iterPrecond = setPrecondDisjunctionFracMan.iterator();
    				while (iterPrecond.hasNext()) {
    						 FractionManipulationStatement elementPrecond = iterPrecond.next();
    						 
    						 if (!doesFractionManipulationExist(fractionManipulationsListPost, elementPrecond)) {
    							 modifyPredObjNotMentionedPostcond(
    									 methodName_, 
    									 new FieldAndTypePair(elementPrecond.getPredName(), elementPrecond.getFractionObject())
    							 );
    							 
    							 predicateIsMentioned.put(elementPrecond.getPredName(), false);
    						 } else {			 
    							 addToPackedModifiedObjects(
    									 packedModifiedObjects,
    									 elementPrecond.getPredName(), 
    									 elementPrecond.getFractionObject()
    							 );							 
    						 }
    						 
    					 }

    			 // This is for the object propositions that are not part of a disjunction.
				 // Compare all the object propositions in the precondition set above
				 // one by one, with *all* the object propositions in the postcondition
				 // and retain for each the name of the predicate and the object if they are
				 // different.
    			 
    			 // Future work.
    			 // If I do need to write fraction statements about a disjunction
    			 // I'll have to write for both disjunction elements 
    			 // because Boogie does not know which one will hold.	 
    		 }	 
    	 } else if (disjNumber == -1) {
    		 // This is a FractionManipulationStatement that is not part of a disjunction.
    		 // If there is an ifCondition or not does not influence what I do.
    		 // I am comparing the current fracMan with all the fracMan's in the postcondition
    		 // and if there is one that has the same object and predicate, and the "isPacked"
    		 // changed, I add that object to packedModifiedObjects.
    		 if (!doesFractionManipulationExist(fractionManipulationsListPost, fracMan)) {	
    			 	modifyPredObjNotMentionedPostcond(
						 methodName_, 
						 new FieldAndTypePair(fracMan.getPredName(), fracMan.getFractionObject())
    				);
				 	predicateIsMentioned.put(fracMan.getPredName(), false);
			 }    		 
        	 for (int k=0; k<fractionManipulationsListPost.size(); k++) {
        		 FractionManipulationStatement fracManPost = fractionManipulationsListPost.get(k);
        			if (
        				 fracMan.getPredName().equals(fracManPost.getPredName()) &&
        				 fracMan.getFractionObject().equals(fracManPost.getFractionObject()) &&
        				 (fracMan.getIsPacked()!=fracManPost.getIsPacked())
        				 //TODO should I compare ifConditions?
        		 ) {
        			 addToPackedModifiedObjects(
        					 packedModifiedObjects,
        					 fracManPost.getPredName(),
        					 fracManPost.getFractionObject()
        			);
        			 // packedModifiedObjects passed by value and thus I don't need to do anything else
        		 }
        		
        	 }
    	 }
 
    }
    }
    
    // forPacked is false here
    // I need to compute fractionsModifiedObjects separately from packedModifiedObjects
    // in this method because it can happen that the same object proposition is packed in
    // the pre- and postcondition but the fraction amount has changed.
    void constructStructuresForInferEnsuresForallFractions(
    		String methodName_, 
    		HashMap<String, Set<String>> fractionsModifiedObjects
    ) {
  		Set<Integer> setDisjunctionNumbersPrecond = new TreeSet<Integer>();
       
  		LinkedList<FractionManipulationStatement> fractionManipulationsListPre = 
        		 fractionManipulationsListMethodPre.get(methodName_);
         	 
        LinkedList<FractionManipulationStatement> fractionManipulationsListPost = 
             	 fractionManipulationsListMethodPost.get(methodName_);
    	       	 
    	 for (int i=0; i<fractionManipulationsListPre.size(); i++) {		 		 
    		 FractionManipulationStatement fracMan = fractionManipulationsListPre.get(i);
    		 
    		 // I need to reconstruct the disjunction if fracMan has a disjunction number.
    		 int disjNumber = fracMan.getDisjunctionNumber();
    		 // disjNumber does not need to be specifically wrapped in an Integer
    		 // because Java does it automatically if need be.
    		 if ((disjNumber != -1) && (!setDisjunctionNumbersPrecond.contains(disjNumber))) {
    			 setDisjunctionNumbersPrecond.add(disjNumber);
    			 // The set of object propositions in the precondition
    			 // that are in the same disjunction with the current object proposition.
    			 Set<FractionManipulationStatement> setPrecondDisjunctionFracMan = 
    					 new TreeSet<FractionManipulationStatement>();
    			 // I added compareTo() for the line below to work.
    			 setPrecondDisjunctionFracMan.add(fracMan);
    			 for (int j=i+1; j<fractionManipulationsListPre.size(); j++) {
    				 FractionManipulationStatement fracManAfter = fractionManipulationsListPre.get(j);
    				 if (fracManAfter.getDisjunctionNumber() == disjNumber) {
    					 setPrecondDisjunctionFracMan.add(fracManAfter);
    				 }
    			 }
    			 
    			 // Now construct the set of object propositions from the postcondition
    			 // that might be equal to the set setDisjunctionFracMan.
    			 // The following part is all about the postconditions.
    			 // I start the process, the same one as above, but for postconditions.
    			 // The set of object propositions in the postcondition
    			 // that are in the same disjunction with fracMan.
    			 int postDisjunctionNumber = -1;
    			 int j;
    			 Set<FractionManipulationStatement> setPostcondDisjunctionFracMan = 
    					 new TreeSet<FractionManipulationStatement>();
    			 for (j=0; j<fractionManipulationsListPost.size(); j++) {
    				 FractionManipulationStatement fracManPost = fractionManipulationsListPost.get(j);
    				 if (fracManPost.getFractionObject().equals(fracMan.getFractionObject())
    						&&
    					(fracManPost.getPredName().equals(fracMan.getPredName()))
    						&&
    					(fracManPost.getIfCondition().equals(fracMan.getIfCondition()))
    						 ) {
    					 setPostcondDisjunctionFracMan.add(fracManPost); 					 
    					 postDisjunctionNumber = fracManPost.getDisjunctionNumber();
    					 break;
    				 } 				
    			 }
    			 
    			 // Add all the fractionManipulations that have the same disjunctionNumber as 
    			 //fracManAfter above.
    			 if (postDisjunctionNumber!=-1) {
    				 for (int k=j+1; k<fractionManipulationsListPost.size(); k++) {
    					 FractionManipulationStatement fracManPost1 = fractionManipulationsListPost.get(k);
    					 if (fracManPost1.getDisjunctionNumber() == postDisjunctionNumber) {
    						 setPostcondDisjunctionFracMan.add(fracManPost1);
    					 }
    				 }
    			 }
    			 
    			 // Compare setPostcondDisjunctionFracMan and setPrecondDisjunctionFracMan.
    			 // If they are the same then I don't need to do anything.
    			 // If they are not the same, if there is an ifCondition,
    			 // I need to look at all the individual object propositions in the disjunction
    			 // for the precondition
    			 // and see how each changed in the postcondition.
    			 // If there is no ifCondition, I still need to look at all the 
    			 // object propositions in the postcondition and see if there is 
    			 // one that is like this one from the precondition.
    			 
    			 // For the composite example, we don't go inside the if branch below.
    			 boolean areSetsEqual;
    			
    			 areSetsEqual = areEqualSetsFractions(setPrecondDisjunctionFracMan, setPostcondDisjunctionFracMan); 
    			 
    			 
    			 if (!areSetsEqual) {
    				 // Any of the objects in this disjunction of the precondition
    				 // that is not equal to one in the postcondition
    				 // means that they could change and cannot be included in the
    				 // ensures forall. Only if it was the exact same as the disjunction in the postcondition
    				 // it could have been included in the ensures forall.
    				 // The objects of the object propositions in the pre set could
    				 // be put straight in packedModifiedObjects, but it could be that
    				 // they are not even mentioned in the postcondition and in that case
    				 // packedIsMentioned needs to be modified for that predicate.
    				 // So we do need to find at least one object proposition in the postcondition
    				 // that has the same object and predicate as the current one 
    				 // taken from setPrecondDisjunctionFracMan.
    				Iterator<FractionManipulationStatement> iterPrecond = setPrecondDisjunctionFracMan.iterator();
    				while (iterPrecond.hasNext()) {
    						 FractionManipulationStatement elementPrecond = iterPrecond.next();
    						 
    						 if (!doesFractionManipulationExist(fractionManipulationsListPost, elementPrecond)) {
    							 modifyPredObjNotMentionedPostcond(
    									 methodName_, 
    									 new FieldAndTypePair(elementPrecond.getPredName(), elementPrecond.getFractionObject())
    							 );
    							 
    						 } else {
    							 addToFractionsModifiedObjects(
    									 fractionsModifiedObjects,
    									 elementPrecond.getPredName(), 
    									 elementPrecond.getFractionObject()
    							 ); 							 
    						 }	 
    					 }

    			 // This is for the object propositions that are not part of a disjunction.
				 // Compare all the object propositions in the precondition set above
				 // one by one, with *all* the object propositions in the postcondition
				 // and retain for each the name of the predicate and the object if they are
				 // different.
    			 
    			 // Future work.
    			 // If I do need to write fraction statements about a disjunction
    			 // I'll have to write for both disjunction elements 
    			 // because Boogie does not know which one will hold.	 
    		 }	 
    	 } else if (disjNumber == -1) {
    		 // This is a FractionManipulationStatement that is not part of a disjunction.
    		 // If there is an ifCondition or not does not influence what I do.
    		 // I am comparing the current fracMan with all the fracMan's in the postcondition
    		 // and if there is one that has the same object and predicate, and the "isPacked"
    		 // changed, I add that object to packedModifiedObjects.
    		 
    		 // TODO I also need to change isModified in this branch.
    		 
        	 for (int k=0; k<fractionManipulationsListPost.size(); k++) {
        		 FractionManipulationStatement fracManPost = fractionManipulationsListPost.get(k);
        			// this is same as the body of the if above, but for fractions
        			if (
           				 fracMan.getPredName().equals(fracManPost.getPredName()) &&
           				 fracMan.getFractionObject().equals(fracManPost.getFractionObject()) &&
           				 (!fracMan.getFraction().equals(fracManPost.getFraction()))
           				 // TODO need to see how to deal with constants k
           				 // they are the same but might have different names in the pre- and
           				 // postconditions.
           				 //TODO should I compare ifConditions?
           		 ) {
           			 addToFractionsModifiedObjects(
           					 fractionsModifiedObjects,
           					 fracManPost.getPredName(),
           					 fracManPost.getFractionObject()
           			);
           			 //fractionsModifiedObjects passed by value and thus I don't need to do anything else
           		 }
        		
        	 }
    	 }
 
    }
    }
    
    PairOfStrings inferEnsuresForall(
    		String methodName_, 
    		boolean forPacked, 
    		String forallParameter,
    		HashMap<String, Set<String>> packedModifiedObjects,
    		HashMap<String, Set<String>> fractionsModifiedObjects,
    		HashMap<String, Boolean> predicateIsMentioned,
    		Set<String> unpackedInPostcondition 		
   ) {
    PairOfStrings pairOfStrings = new PairOfStrings();
    // Here I write the "ensures forall" for packed as we have the infrastructure in place.
    Iterator<Entry<String, Boolean>> j = 
    		predicateIsMentioned.entrySet().iterator(); 
    while(j.hasNext()) {
    	    Entry<String, Boolean> pair = (Entry<String, Boolean>)j.next();
    		Boolean currentIsMentioned = pair.getValue();
    		if (currentIsMentioned.booleanValue()) {
        		String currentNamePred = pair.getKey();
        		if (forPacked) {
        			// This is the generation of the "ensures forall" string for packed.
        			if (unpackedInPostcondition.contains(currentNamePred)) {
        				// We need to add
        				// "ensures (forall y:Ref :: ( (this!=y)  ==> 
        				// (packedPred[y] == old(packedPred[y])) ) );"
        				// to result.
        				Set<String> currentModifiedObjects = packedModifiedObjects.get(currentNamePred);
        				if (currentModifiedObjects!=null) {
        					pairOfStrings.concatPacked("\t ensures (forall ");
        					pairOfStrings.concatPacked(forallParameter);
        					pairOfStrings.concatPacked(":Ref :: (");
        				
        					String[] currentModifiedObjectsArray = currentModifiedObjects.toArray(new String[0]);
        					for (int z=0; z <currentModifiedObjectsArray.length-1; z++ ) {
        						pairOfStrings.concatPacked("("+currentModifiedObjectsArray[z]+"!="+forallParameter + ") &&");
        					}
        					pairOfStrings.concatPacked("("+
        						currentModifiedObjectsArray[currentModifiedObjectsArray.length-1]+
        						"!="+forallParameter + ")) ==> packed"+
        						upperCaseFirstLetter(currentNamePred)+"["+forallParameter+"]==old(packed"+
        						upperCaseFirstLetter(currentNamePred)+"["+forallParameter+"]));\n");
        				} else {
        					pairOfStrings.concatPacked("\t ensures (forall ");
        					pairOfStrings.concatPacked(forallParameter);
        					pairOfStrings.concatPacked(":Ref :: (packed"+
        						upperCaseFirstLetter(currentNamePred)+"["+forallParameter+"]==old(packed"+
        						upperCaseFirstLetter(currentNamePred)+"["+forallParameter+"])));\n");      					
        				}
        			} else {
        				// We need to add "ensures (forall y:Ref :: packedParent[y]);"
        				// to result.
        				//TODO need to add an if here!!
        				pairOfStrings.concatPacked("\t ensures (forall "+ forallParameter +":Ref :: packed"+
        					upperCaseFirstLetter(currentNamePred)+"["+forallParameter+"]);\n");
        			}
        		} else {
        			// This is the generation of the "ensures forall" string for fractions.
    				
    				LinkedList<FieldAndTypePair> methodsCalledInThisMethod =
    						methodsInMethod.get(methodName_);
    				
        			if (methodsCalledInThisMethod == null) {
        				// We need to add
        				// "ensures (forall y:Ref :: ( (this!=y)  ==> 
        				// (packedPred[y] == old(packedPred[y])) ) );"
        				// to result.
        				Set<String> currentModifiedObjects = fractionsModifiedObjects.get(currentNamePred);
        				if (currentModifiedObjects!=null) {
        					pairOfStrings.concatFractions("\t ensures (forall ");
        					pairOfStrings.concatFractions(forallParameter);
        					pairOfStrings.concatFractions(":Ref :: (");
        				
        					String[] currentModifiedObjectsArray = 
        							currentModifiedObjects.toArray(new String[0]);
        					for (int z=0; z < currentModifiedObjectsArray.length-1; z++ ) {
        						pairOfStrings.concatFractions("("+
        								currentModifiedObjectsArray[z]+"!="+forallParameter + ") &&");
        					}
        					pairOfStrings.concatFractions("("+
        						currentModifiedObjectsArray[currentModifiedObjectsArray.length-1]+
        						"!="+forallParameter + ")) ==> frac"+
        						upperCaseFirstLetter(currentNamePred)+"["+forallParameter+"]==old(frac"+
        						upperCaseFirstLetter(currentNamePred)+"["+forallParameter+"]));\n");
        				}  else {
        					pairOfStrings.concatFractions("\t ensures (forall ");
        					pairOfStrings.concatFractions(forallParameter);
        					pairOfStrings.concatFractions(":Ref :: (frac"+
        						upperCaseFirstLetter(currentNamePred)+"["+forallParameter+"]==old(frac"+
        						upperCaseFirstLetter(currentNamePred)+"["+forallParameter+"])));\n");      					
        				}
        			} else {
        				// TODO need to refine the below a bit, maybe check a few more things

        				// TODO I need to check for which objects the fractions were > 0.0
        				// in the precondition
        				// but >= 0.0 in the postcondition.
        				// The resulting string should be similar to:
        				// ensures (forall y:Ref :: (old(fracParent[y]) > 0.0) ==> (fracParent[y] > 0.0));
        				// but using forallParameter instead of y.
    					pairOfStrings.concatFractions("\t ensures (forall "+forallParameter+":Ref :: (old(frac"+
        						upperCaseFirstLetter(currentNamePred)+"["+forallParameter+"]) > 0.0) ==> (frac" +
        						upperCaseFirstLetter(currentNamePred)+"["+forallParameter+"] > 0.0 ));\n");	
        			}
        			
        		}
        		    			
    		}
    }  	
    	return pairOfStrings;			
    }
        
    //Since methods are not children of 
    //Predicate, we might not need namePredicate here.
    public void visitMethodDeclaration(MethodDeclaration ast) 
    		throws ParseException {    	
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
    	
    	packedModifiedArgsInMethod.clear();
    	packedModifiedArgsInPrecondition.clear();
    	
    	methodBody.put(methodName, "");
    	methodSpec.put(methodName, "");
    	methodParams.put(methodName, new LinkedList<FieldAndTypePair>());
    	fractionManipulationsListMethodPre.put(methodName, 
    			new LinkedList<FractionManipulationStatement>());
    	fractionManipulationsListMethodPost.put(methodName, 
    			new LinkedList<FractionManipulationStatement>());
    	currentMethod = methodName;
    	
    	//When we hit the first method, we write out the constructors for this 
    	//class and the Pack and Unpack procedures. 
    	if (isFirstMethod) {   	
    		//This if is for when the modulo symbol is in the body of a predicate.
    		if (programContainsModulo && !writtenModuloFunction) {
    			try{
    				out.write(moduloTranslation);
    			}
    			catch(Exception e) {
    				System.err.println("Error in visitMethodDeclaration first method: " 
    						+ e.getMessage());
    			}
    			//We only want to write out the modulo function once.
    			writtenModuloFunction = true;
    		}
		
    		//Write the constructors to out. The constructor 
    		//that does not pack to anything
    		//and the ones that pack to predicates.
    		makeConstructors(out);
    	
    		Iterator<Entry<String, FieldTypePredbody>> j = 
    			paramsPredicateBody.entrySet().iterator(); 
    
    		while(j.hasNext()) {
    			String currentNamePred = j.next().getKey();
    			FieldTypePredbody paramsPred = 
    					paramsPredicateBody.get(currentNamePred);
    			String predBodyUnprocessed = paramsPred.getPredicateBody();
    			predBodyUnprocessed = writeAllPredArgWhichField(
    					currentNamePred, 
    					predBodyUnprocessed
    			);
     		
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
    			//int totalDeletedChars = numberOfAmb + numDeleteParam;
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
    				else 
    					if ((sb.charAt(k)==')') && (numRightParamToDelete > 0)) {
    						sb = sb.deleteCharAt(k);
    						numRightParamToDelete--;
    						if (k>0) k--;
    						i--;
    					} else {
    						k++;
    					}
    			}
     		
    			String predBody = sb.toString().concat(lastPartPredBody);
    			try {
    				//will need to do something about formal parameters
    				out.write("procedure Pack"+
    						upperCaseFirstLetter(currentNamePred)+"(");
    				writePredParamsOutOrToString(currentNamePred, 1, false);
    				out.write("this:Ref);\n"); 
    				out.write("\t requires (packed"+
    						upperCaseFirstLetter(currentNamePred)+"[");
    				out.write("this]==false) &&\n");
			
    				out.write("\t \t" + predBody + "; \n \n"); 
			
    				out.write("procedure Unpack"+
    						upperCaseFirstLetter(currentNamePred)+"(");
    				writePredParamsOutOrToString(currentNamePred, 1, false);
    				out.write("this:Ref);\n");
    				out.write("\t requires packed"+
    						upperCaseFirstLetter(currentNamePred)+"[");
    				out.write("this] &&\n\t \t ");
    				//TODO we don't do this for Pack because 
    				//we do it in the code after we call the Pack procedure
			
    				out.write("(frac"+
    						upperCaseFirstLetter(currentNamePred)+"[");
    				out.write("this] > 0.0);\n");
			
    				//TODO need to update predBody in the appropriate map.
    				out.write("\t ensures "+predBody+";\n");
    				out.write("\n");
    			}
    			catch (Exception e) {
    				System.err.println("Error in visitMethodDeclaration writing specifications: " + e.getMessage());
    			}
    			//We are initializing packedMods here, with the names of all predicates.
    			packedMods.put(currentNamePred, new LinkedList<PackObjMods>());   
    		}
        
    		// We have to initialize packedMods with the names of the predicates in the 
    		// previous classes too.
    		for (int i=0; i<numberFilesBefore; i++) {
    			Set<String> predicatesOfi = bv[i].getPredicates();
    			for (String namePredicateOfi : predicatesOfi) {
    				packedMods.put(	
    						namePredicateOfi, 
    						new LinkedList<PackObjMods>()
    				);   
    			}	
    		}
    	}
    	     		
        //Writing the current procedure out.
    	try {		
    		// We reset packedMods here.
    		// Before we enter the body of the current method.
    		Iterator<Entry<String, FieldTypePredbody>> j = 
    				paramsPredicateBody.entrySet().iterator(); 
    	    	
    	    while(j.hasNext()) {
    	    	String currentNamePred = j.next().getKey();
    	     	packedMods.put(currentNamePred, new LinkedList<PackObjMods>()); 
    	    }
    			
    	    out.write("\nprocedure "+ast.getIdentifier().getName()+"(");
    	}
	    catch (Exception e) {
			System.err.println("Error in visitMethodDeclaration writing this method out, " +
					"before visitChildren: " + e.getMessage());
		}

    	// I am visiting everything except the method block.
    	AST[] children = ast.getChildren();
    	int sizeOfChildren = children.length;
    	for (int h=0;h<sizeOfChildren-1;h++) {
    		children[h].accept(this);
    	}
    	
    	////////////////////////////////////////////////////
    	// The following variables are used for inferring the ensures forall for this method.
          	 
          	 // For each predicate this map contains which are the 
          	 // objects for which the object propositions are packed in the
          	 // precondition and unpacked in the postcondition 
          	 // or vice versa, only if they are mentioned both in the pre- and postcondition.
        	 HashMap<String, Set<String>> packedModifiedObjects =
       			new HashMap<String, Set<String>>();
        	 // Same map as above but says for which objects the fractions
        	 // were modified between the pre and postconditions.
        	 HashMap<String, Set<String>> fractionsModifiedObjects =
       			new HashMap<String, Set<String>>();
        	 
        	 // For each predicate this map contains whether
        	 // that predicate (for all objects to which it is bound in the precondition)
        	 // which was mentioned in the precondition
        	 // was also mentioned in the postcondition.
        	 // If it was not mentioned for at least one object for which it was mentioned in the precondition
        	 // it means that we cannot write an "ensures forall" for that predicate.
        	 HashMap<String, Boolean> predicateIsMentioned =
       			new HashMap<String, Boolean>();
        	  	 
        	 // This set contains the predicates for which
        	 // there exists an "unpacked" in the postcondition.
        	 Set<String> unpackedInPostcondition =
       			new TreeSet<String>();
        	 
        	 constructStructuresForInferEnsuresForallPacked(
        	    		methodName,       	    		
        	    		packedModifiedObjects,
        	    		predicateIsMentioned,
        	    		unpackedInPostcondition
        	  );
        	 constructStructuresForInferEnsuresForallFractions(
     	    		methodName, 
     	    		fractionsModifiedObjects
     	  );
        	 
        	////////////////////////////////////////

			// Now I am visiting the method block.
        	children[children.length-1].accept(this);
    	    
    	    try {
    	    //TODO add all parameters when calling function
    	    LinkedList<FieldAndTypePair> currentMethodParams = methodParams.get(currentMethod);
    	    for (int i=0; i<currentMethodParams.size(); i++) {
    	    	out.write(currentMethodParams.get(i).getName()+":");
    	    	String type = currentMethodParams.get(i).getType();
    	    	if (type.equals("int") || type.equals("bool") ||type.equals("real") ) {
    	    		out.write(currentMethodParams.get(i).getType() + ", ");
    	    	} else {
    	    		out.write("Ref, ");
    	    	}
    	    }
    	    out.write("this:Ref)\n");
							
    	    //Need to automatically detect what is being modified, 
    	    //according to the Boogie manual.
    	    //We do this after we parse and translate the body of the current method.
    	    Set<String> localFieldsInMethod = 
    	    		fieldsInMethod.get(currentMethod);
				
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
        	        	callMethodsSetOfFields = 
        	        		bv[classOfCallMethod].getFieldsInMethod().get(ft.getName());	
        			}
        			localFieldsInMethod.addAll(callMethodsSetOfFields);
        		}
        	}
        	
        	//localFieldsInMethod is the set of elements that are modified.
        	if (localFieldsInMethod!=null) {       	
        		String[] fieldsInMethodArray = 
					localFieldsInMethod.toArray(new String[0]);
        		
        		int leng = fieldsInMethodArray.length;
        		if (leng > 0) {
        			String modifies = "\t modifies ";
				
        			for (int k = 0; k < leng - 1; k++) {
        				modifies = modifies.concat(fieldsInMethodArray[k]+",");
        			}
        			modifies = modifies.concat(fieldsInMethodArray[leng-1]+";\n");
      			
        			out.write(modifies);
        		}
        	}		
		out.write(methodSpec.get(currentMethod));
				
		//Here I want to say that if there are 
		//no unpacked object propositions 
		//which are specified explicitly,
		//then it means that all are packed.
		String requiresPacked = "";

		if (methodPreconditionsUnpacked.isEmpty()) {
			
			String forallParameter = getNewForallParameter();		
			
			//requires (forall x:Ref :: packedOK[x]);
			// Not everything is packed, only the ones that are 
			// not specifically unpacked.
			if (localFieldsInMethod != null) {
				
				for (String p : predicates) {					        	
					if (localFieldsInMethod.contains("packed"+upperCaseFirstLetter(p)) 
							&& 
							!setFracEq1.contains(p)) {
						
						LinkedList<PredicateAndFieldValue> unpackedPredicatesThisMethod =
								unpackedPredicatesInPrecondition.get(currentMethod);
						
						Set<String> unpackedObjectsSet = new TreeSet<String>();
						
						// here I compute the objectStrings 
						// that actually appear in unpacked(objectString#..Pred(())
						// and I put them in unpackedObjects.
						if (unpackedPredicatesThisMethod != null) {
							for (int l=0; l<unpackedPredicatesThisMethod.size(); l++) {
								if ((unpackedPredicatesThisMethod.get(l)).
										getPredicate().equals(p)) {
									unpackedObjectsSet.add(
											(
											unpackedPredicatesThisMethod.get(l)).
											getFieldValue()
											);
								}
							}
						}

						boolean areEqual = true;
						Set<String> argsInMethod = packedModifiedArgsInMethod.get(p);
						Set<String> argsInPrecondition = packedModifiedArgsInPrecondition.get(p);
						if ((argsInMethod == null) || (argsInPrecondition==null)) {
							areEqual = false;
							//TODO what happens when both are null?
						} else {
							if (argsInMethod != argsInPrecondition) {
								areEqual = false;
							}
						}
						LinkedList<String> unpackedObjects = new LinkedList<String>();
						unpackedObjects.addAll(unpackedObjectsSet);
						
						if (unpackedObjects.isEmpty()
							&&
							!areEqual
						) {
							requiresPacked = 
								requiresPacked.concat("\t requires (forall " + 
										forallParameter+
										":Ref :: packed"+
										upperCaseFirstLetter(p)+"[" +
										forallParameter+"]);\n"
										);
						} else if  (!unpackedObjects.isEmpty()){
							// create something like 
							// ( ( (y!=this) && (y!=op) )
							String notEquals = "";
						
							for (int m=0; m<unpackedObjects.size() - 1; m++) {
								notEquals = notEquals.concat("("+forallParameter+"!="+unpackedObjects.get(m) +") && ");
							}
							notEquals = notEquals.concat("("+forallParameter+"!="+
									unpackedObjects.get(
											unpackedObjects.size() - 1) +") ==> ");
							requiresPacked = 
								requiresPacked.concat("\t requires (forall " + 
										forallParameter+
										":Ref :: (" + notEquals +" packed"+
										upperCaseFirstLetter(p)+"[" +
										forallParameter+"]));\n"
										);
							
						}
					}
				}
								
				//I also write for the predicates of the 
				//previous classes that were translated.
				for (int i=0; i<numberFilesBefore; i++) {
					Set<String> bvPredicates = bv[i].getPredicates();
					for (String p : bvPredicates) {							        	
						if (localFieldsInMethod.contains("packed"+upperCaseFirstLetter(p)) &&
				        				!setFracEq1.contains(p) ) {	
							requiresPacked = requiresPacked.concat("\t requires (forall "+ 
									forallParameter+":Ref :: packed"+upperCaseFirstLetter(p)+
									"["+ forallParameter+"]);\n");
							}
					}
				}
			}
		
		} else {			
			//TODO
			//Need to write what happens when 
			//there are unpacked object propositions
			//in the precondition.
		}
		out.write(requiresPacked);
		//Here I generate 
		//"ensures (forall x:Ref :: (packedOK[x] == old(packedOK[x])));"
		// or "ensures (forall x:Ref :: ( ((x!=this) && (x!=that) ) 
		//==> (packedOK[x] == old(packedOK[x]))));"
		// and the others, related to fracPredicate.
		
		// I look which are the ifCondition ==> expression that are the exact same in
		// the pre and postcondition. 
		// The fractionManipulation structures are exactly what I need, 
		// especially fractionManipulationsListPredicate and the likes.
		// I can look at them and decide if I have the same thing in the pre- and post-conditions and
		// decide that they cancel each other.
		// Each item is one part of the &&, which includes the ifCondition and the right part
		// of it, that might include ||.
		// Those can be disregarded in the ensures forall, and also I don't need to write 
		// anything related to them after the method is called.
		
		// Only infer the ensures forall from the pre and post-condition. They 
		// will show what are the packed and frac that changed.
		// Keep track of which if-conditions are exactly the same in the pre and postconditions.
		// Also keep track of the other elements that make up the pre and postconditions.
		// Only look at those elements which are not equal in the pre and post-conditions. 
		// From those extract the objects that changed for packed, for each predicate.
		
		// For the fractions, also only look at pre and post-conditions.
		// Only try to infer the ensures forall for fractions if there is a mention of a frac in the postcondition.
		// If there is no mention, it means that the programmer did not know what happens to the frac and we 
		// cannot infer the ensures forall.
		
		// There are 2 kinds of ensures forall that can be inferred for fractions: 
		// ensures (forall y:Ref :: ( (y!=this) ==> (fracLeft[y] == old(fracLeft[y]) ) ) );
		// and 
		// ensures (forall y:Ref :: (old(fracParent[y]) > 0.0) ==> (fracParent[y] > 0.0));  
		
		// The second one is more relaxed. If there are calls to any methods in this method,
		// then infer the second ensures forall. 
		// The first one can be inferred for methods that have no calls to other methods inside. 
		
		// For the "ensures forall" for packed, if there is an unpacked in the postcondition
		// then infer the "ensures forall" that has the "old() == ..".
		// If there is no "unpacked" in the postcondition then 
		// infer the one that says "ensures forall y:: packedPred[y]".
		    
		// This is where I write the "ensures (forall .." out.
	    String forallParameter = getNewForallParameter();
	    String ensuresForall = "";
	    // We only need to add "ensures forall" and "requires forall" for the
	    // other procedures that are not main.
	    // "main" is the client code.
	    String thisMethodName = ast.getIdentifier().getName();
	    if (!thisMethodName.equals("main")) {
	       	// This is for writing "ensures forall for packed.
	    	ensuresForall = ensuresForall.concat(
	    			inferEnsuresForall(
	    					thisMethodName, 
	    					true, 
	    					forallParameter,
	    		    		packedModifiedObjects,
	    		    		fractionsModifiedObjects,
	    		    		predicateIsMentioned,
	    		    		unpackedInPostcondition		
	    			).getEnsuresForallPacked());
	    	
	        //This is for writing "ensures forall for frac.
	    	ensuresForall = ensuresForall.concat(
	    			inferEnsuresForall(
	    					thisMethodName, 
	    					false, 
	    					forallParameter,
	    		    		packedModifiedObjects,
	    		    		fractionsModifiedObjects,
	    		    		predicateIsMentioned,
	    		    		unpackedInPostcondition		
	    			).getEnsuresForallFractions());
	        	
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
			out.write(writeAssumeForall(currentMethod));
			out.write(methodBody.get(currentMethod));				
		}
		catch (Exception e) {
			System.err.println("Error in visitMethodDeclaration writing this method out, " +
					"after visitChildren: "
					+ e.getMessage());
		}
    	isFirstMethod = false;
    }
    
    void addArgToPackedModifiedArgsInMethod(String predName, String object) {
    	Set<String> currentArguments = 
    			packedModifiedArgsInMethod.get(predName);
    	if (currentArguments == null) {
    		currentArguments = new TreeSet<String>();
    	}
    	currentArguments.add(object);
    	packedModifiedArgsInMethod.put(predName, currentArguments);
    }
    
    void addArgToPackedModifiedArgsInPrecondition(String predName, String object) {
    	Set<String> currentArguments = 
    			packedModifiedArgsInPrecondition.get(predName);
    	if (currentArguments == null) {
    		currentArguments = new TreeSet<String>();
    	}
    	currentArguments.add(object);
    	packedModifiedArgsInPrecondition.put(predName, currentArguments);
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

    public void visitReturnStatement(ReturnStatement ast) 
    		throws ParseException 
    {
        visitChildren(ast);
    }
    
    String addDelimitersToString(String initial) {
    	int index = initial.indexOf('[');
    	if (index==-1) {
    		return initial;
    	} else {
    		String result = initial.substring(0, index+1);
    		result = result.concat("$");
    		String leftover = initial.substring(index+1, initial.length());
    		index = leftover.indexOf(']');
    		String first = leftover.substring(0, index);
    		String last = leftover.substring(index, leftover.length());
    		result = result.concat(first+"@"+last);
    		return result;
    	}
    	
    }
    
    // This is instead of visitFieldSelection.
    // We  do not end up in the visitFieldSelection() method
    // so I removed it.
    public void helperFieldSelection(String identifierName) {
    	try {
    	// TODO add additional checks for lastIdentifierOrKeyword
    	String fieldName = identifierName +"["+ lastIdentifierOrKeyword +"]";
    	lastIdentifierOrKeyword = fieldName;

    	if (identifierName.contains("[")) fieldName = fieldName.concat("]");
    	fieldsInStatement.add(identifierName);
    	if (!inPackUnpackAnnotation) {
    		if (insideObjectProposition) {
    			objectPropString = objectPropString.concat(fieldName);
    		}
    		
    		if ((currentMethod != "") && (inStatement) && 
    				!inArgumentList && !inMethodSelectionStatement) {  
			  		statementContent = statementContent.concat(fieldName);  
    		}
		  
    		  if ((currentMethod != "") && (inArgumentList) && inStatement 
    				  && inMethodSelectionStatement) {
    			  statementContent = statementContent.concat(fieldName + ", ");  	  
    		 }
		  
    		// TODO maybe I should add something to fieldName here
    		  // I need to add the !insideObjectProposition part because
    		  // that is taken care of when I write which arguments belong to which fields.
    		if ((insidePrecondition || insidePostcondition ) && !insideObjectProposition)  {
    			modifyMethodSpec(fieldName);  
    		}
  
    		if (!namePredicate.equals("") && !insideObjectProposition){
    			FieldTypePredbody currentParamsPredicateBody = 
    					paramsPredicateBody.get(namePredicate);
    			paramsPredicateBody.put(
					namePredicate, 
					currentParamsPredicateBody.concatToPredicateBody(fieldName)
    			); 		
    		}
    	} else {
    		
    		// We are inside a pack/unpack annotation.
    		if (inArgumentList) {
    			objectPropString = objectPropString.concat(fieldName);
    		}	
    	}
    	
    	// TODO if it contains [] I have to surround what is inside by $@.
    	
		  if (inChild1OfImplies) {
  			  ifConditionFractionManipulation = 
  					  ifConditionFractionManipulation.concat(addDelimitersToString(fieldName));
  		  }
		}
		catch (Exception e) {
			System.err.println("Error in  helperFieldSelection"	+ e.getMessage());
		}	
    }
    
    // The String full is made of Strings separated by the character ch
    LinkedList<String> separateParameters(String full, int ch) {
    	 LinkedList<String> result = new  LinkedList<String>();
    	 int firstIndex = full.indexOf(ch);
    	 String firstSegment;
    	 
    	 while(firstIndex != -1) {
    		 firstSegment = full.substring(0, firstIndex);
    		 result.add(firstSegment);
    		 full = full.substring(firstIndex + 1, full.length());
    		 firstIndex = full.indexOf(ch);
    	 }
    	 result.add(full);
    	 return result;
    }
    

    public void visitMethodSelection(MethodSelection ast) 
    		throws ParseException
    {
    	String identifierBeforeMethSel = lastIdentifierOrKeyword;
    	visitedMethSel = true;
    	// It might be that some object propositions in the "requires" of the call procedure
    	// are already packed and we might not need to pack them. I need to check for that.
    	String methodName = ast.getIdentifier().name;
    	
    	modifyMethodsInMethod(
    			new FieldAndTypePair(methodName, lastPrimaryExpressionType));
    	   	
    	LinkedList<ObjPropString> callMethodPreconditions = 
    			methodPreconditionsPacked.get(methodName);
    	if (callMethodPreconditions == null) {
    		int classOfCallMethod = -1;
        	for (int i=0; i < numberFilesBefore; i++) {
        		if (bv[i].getClassName().equals(lastPrimaryExpressionType))
        			classOfCallMethod = i;
        	}
        	callMethodPreconditions = 
        		bv[classOfCallMethod].getMethodPreconditionsPacked().get(methodName);		
    	}

    	statementContent = statementContent + "\t call "+ methodName + "(";
    	String statementContentBefore = statementContent;
    	visitChildren(ast);
    	String statementContentAfter = statementContent;
    	String listOfActualParams = statementContentAfter.
    			substring(statementContentBefore.length(), statementContentAfter.length());
    	// TODO need to add a new LinkedList here to hold the actual params
    	// calculated from the string that I get from the visitChildren() above
    	statementContent = statementContent +identifierBeforeMethSel+");\n";
    	listOfActualParams = listOfActualParams.concat(identifierBeforeMethSel);
    	
    	actualArgumentsMethod = separateParameters(listOfActualParams, ',');
    	
    	// Add fractionManipulationStatements
    	// First write the fraction manipulations of the preconditions
    	statementContent = statementContent.concat(writeFractionManipulation(
    			methodName, false, false, identifierBeforeMethSel));
  
    	//If the last 2 characters are ";\n" we need to delete them because
    	//they are going to be added at the end of visitStatement.
    	statementContent = statementContent.substring(0, statementContent.length() - 2); 	
    }

    public void visitBinaryExpression(BinaryExpression ast) 
    		throws ParseException
    {
    	inBinaryExpression = true;

    	if (ast.op.getId() == JExprConstants.SC_OR) {
    		disjunctionNumber++;
    	} else {
    		disjunctionNumber = -1;
    	}
    	// The above only works if the precondition is
    	// (a||b) && c && (e||d)
    	// so if there are only 2 elements in a disjunction.
    	if (ast.op.getId() == JExprConstants.KEYACCESS) {
    		PrimaryExpression e1 = (PrimaryExpression)ast.E1;
    		PrimaryExpression e2 = (PrimaryExpression)ast.E2;
    		FieldSelection f = (FieldSelection)(e1.getChildren()[1]);
    		String nameField = f.getIdentifier().name;
    		
    		// Here we put in the fieldWhichPredicate 
    		// only if we are in the definition of a predicate.
    		if (namePredicate != "" ) {
    			addToFieldWhichPredicates(nameField, namePredicate);
    		}

    		String fieldValue="";
    		if (e2.getChildren()[0] instanceof IdentifierExpression) {
    			IdentifierExpression i = (IdentifierExpression)(e2.getChildren()[0]);
    			fieldValue = i.getName();
    			if (namePredicate != "") {
    				//This only has a side effect if fieldValue is 
    				//an argument of the predicate.
    				addFieldToPredArgWhichField(fieldValue, nameField);
    			}
    		} else if (e2.getChildren()[0] instanceof LiteralExpression) {
    			LiteralExpression i = (LiteralExpression)(e2.getChildren()[0]);
    			fieldValue = i.getValue() + "";
    		}
    		
        	PredicateAndFieldValue pv = new PredicateAndFieldValue(namePredicate, fieldValue);
        	quantifiedVars.put(pv, nameField); 
        	
        	if (namePredicate.equals("")){
        		helperBinaryExpression(ast , "==");
        	}
        	inBinaryExpression = false;
    		return;
    	}
    	
    	if (ast.op.getId() == JExprConstants.ASSIGN){
    		helperBinaryExpression(ast , ":=");
    		inBinaryExpression = false;
    		return;
    	}
    	
    	//This is for modulo == REM 
    	if (ast.op.getId() == JExprConstants.REM){
    		programContainsModulo = true;
    		//The , is like the operator that gets put between operand 1 and 2.
    		helperBinaryExpression(ast, ",");
    		inBinaryExpression = false;
    		return;
    	}
    	    		
    	helperBinaryExpression(ast, ast.op.getName());
    	inBinaryExpression = false;
    	return; 		
    }
    
    void concatToStatementObjProp(String symbol) {
    	if (namePredicate.equals("")) {
    		// We are not inside a predicate.
    		try {
    			if ((insideObjectProposition && (currentMethod != "")) ||
    			   (inPackUnpackAnnotation && inArgumentList) )
    			{
    				objectPropString = objectPropString.concat(symbol);
    			} else 
    				if (!insideObjectProposition && 
    						(currentMethod != "") && 
    						!insidePrecondition &&
    						!insidePostcondition) {
    					statementContent = statementContent.concat(symbol);  				
    				} else if(!insideObjectProposition && (currentMethod != "") && 
    						(insidePrecondition || insidePostcondition)) {
  			        	 		modifyMethodSpec(symbol);
  			           		} else {
  			           			out.write(symbol);
  			           		}
  		  	}
  	      	catch (Exception e) {
  	      		System.err.println("Error in concatToStatementObjProp: " + e.getMessage());
  	      	}
    	} else {
    		  // We are inside a predicate.
  			  FieldTypePredbody currentParamsPredicateBody = 
  					  paramsPredicateBody.get(namePredicate);
  			  paramsPredicateBody.put(
  					  namePredicate, 
  					  currentParamsPredicateBody.concatToPredicateBody(symbol)
  			  );
  		  }
    }
    
    void helperBinaryExpression(BinaryExpression ast, String operatorSymbol) 
    		throws ParseException 
    {	
    	try {
    	String localOperatorSymbol = operatorSymbol;
    	// We replace the linear implies ~=> with ==>.
    	if (operatorSymbol.equals("~=>")) {
    		operatorSymbol = "==>";
    		localOperatorSymbol = "==>";
    	}
    	int initialStatementLength = statementContent.length();
    	if (operatorSymbol.equals(",")) {
    		//TODO this is a modulo binary expression
    		concatToStatementObjProp("modulo(");
    	}

    	if (!namePredicate.equals("") || insidePrecondition || insidePostcondition) {
    		concatToStatementObjProp("(");
    	}
    	
    	if (localOperatorSymbol.equals("==>")) {
    		inChild1OfImplies = true;
    		ifConditionFractionManipulation = "";
    	}
    	if (operatorSymbol.equals(":=")) { beforeChild0 = true; }
		if (inChild1OfImplies && ((operatorSymbol.equals("&&")) || (operatorSymbol.equals("||")))) {
			ifConditionFractionManipulation = 
					ifConditionFractionManipulation.concat("(");
		}
		
    	AST[] children = ast.getChildren();
		children[0].accept(this );
		if (inChild1OfImplies && ((operatorSymbol.equals("&&")) || (operatorSymbol.equals("||")))) {
			ifConditionFractionManipulation = 
					ifConditionFractionManipulation.concat(")");
		}

		beforeChild0 = false;
		  if (localOperatorSymbol.equals("==>")) {
			  inChild1OfImplies = false;
		  }
		  
		  if (inChild1OfImplies) {
			  ifConditionFractionManipulation = 
					  ifConditionFractionManipulation.concat(operatorSymbol);
		  }
		  
		  concatToStatementObjProp(operatorSymbol);
		  if (localOperatorSymbol.equals("==>")) {
			  inChild2OfImplies = true;
		  }
		  
		  if (inChild1OfImplies && ((operatorSymbol.equals("&&")) || (operatorSymbol.equals("||")))) {
				ifConditionFractionManipulation = 
						ifConditionFractionManipulation.concat("(");
		  }
		  children[1].accept(this );
		  if (inChild1OfImplies && ((operatorSymbol.equals("&&")) || (operatorSymbol.equals("||")))) {
				ifConditionFractionManipulation = 
						ifConditionFractionManipulation.concat(")");
		  }
		  if (localOperatorSymbol.equals("==>")) {
			  inChild2OfImplies = false;
			  ifConditionFractionManipulation = "";
		  }
	    if (!namePredicate.equals("") || insidePrecondition || insidePostcondition) {
	    	concatToStatementObjProp(")");
	    }
		  
	    if (operatorSymbol.equals(",")) {
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
		catch (Exception e) {
			System.err.println("Error in  helperBinaryExpression"	+ e.getMessage());
		}
    }
      
    public void visitLiteralExpression(LiteralExpression ast) 
    		throws ParseException 
    { 
    	try {
    	String astvalue = ast.value.toString();
    	if (insideObjectProposition) {
			  objectPropString = objectPropString.concat(astvalue);
		}
    	if (!inPackUnpackAnnotation) {
    		if ((currentMethod != "") && (inStatement) && !inArgumentList ) {
    			statementContent = statementContent.concat(astvalue);
    		}
		  
    		if ((currentMethod != "") && (inStatement) && (inArgumentList) ) {
    			//This is a special case when an argument is of the form a+b+c.
    			if (inBinaryExpression) {
    				statementContent = statementContent.concat(astvalue);
    			} else {
    				statementContent = statementContent.concat(astvalue + ",");
    			}
    		}
		  
    		if ((currentMethod != "") && !insideObjectProposition 
    				&& (insidePrecondition || insidePostcondition)) {
    			modifyMethodSpec(astvalue);
    		}
    	} else {
    		// We are inside a pack/unpack annotation.
    		if (!insideObjectProposition && inArgumentList) {
    			objectPropString = objectPropString.concat(astvalue);
    		}
    	}
		  
    	if (!namePredicate.equals("") && !insideObjectProposition){
    		FieldTypePredbody currentParamsPredicateBody = paramsPredicateBody.get(namePredicate);
    		paramsPredicateBody.put(
    				namePredicate, 
    				currentParamsPredicateBody.concatToPredicateBody(astvalue)
			);
    	}
    	
        if (inArgumentList) {
 		   	argumentsConstructor.add(astvalue);
 		   	argumentsPredicate.add(astvalue);
        }
        
		  if (inChild1OfImplies) {
  			  ifConditionFractionManipulation = 
  					  ifConditionFractionManipulation.concat(astvalue);
  		  }
		  
	}
	catch (Exception e) {
		System.err.println("Error in  visitLiteralExpression"	+ e.getMessage());
	}
    	
    	visitChildren(ast); 
    }
    
    // Write the fields corresponding to each argument
    // for one object proposition, which has the arguments args
    // and has the object objectString.
    String writePredArgWhichField(
    		LinkedList<ArgumentAndFieldPair> listArgsToFields, 
    		LinkedList<String> args, 
    		String objectString) {
    	String result = "";
    try {
    	for (int i=0;i<args.size();i++) {
    		ArgumentAndFieldPair argField = listArgsToFields.get(i);
    		//This can be null for predicates that have no arguments.
    		if (argField!=null){
    			if (!(argField.equals(""))) {
    				String localField = argField.getField();
    				if (localField.contains(" ")) {
    					//If the field contains " " it means
    					//it is of the form 
    					//name_predicate object number 
    					//and it needs to be deconstructed.
    					// The name_predicate object number is for
    					// when an argument is itself the argument of another 
    					//object proposition inclosed in this predicate.

    					LinkedList<String> localFieldParts = separateParameters(localField, ' ');
    					String localNamePred = localFieldParts.get(0);  					
    					String localObject = localFieldParts.get(1);  					
    					int localNumber = Integer.parseInt(localFieldParts.get(2));
    		        	// The list of which field each argument of 
    		        	// this predicate represents.
    					// This recursive idea only works for one level down, 
    					// it is not generally recursive. 
    					// It should be made generally recursive in future work.
    		        	LinkedList<ArgumentAndFieldPair> listArgsToFieldsRecursive =
    		        			predArgWhichField.get(localNamePred);
    		            // localObject is of the form field[this], but I need to 
    		        	// transform it to field[objectString]
    		        	localObject = localObject.replaceFirst("this", objectString);
    					result = result.concat(" && ("+ 
    							listArgsToFieldsRecursive.get(localNumber).getField()+
    							"["+localObject+"]=="+args.get(i)+")"
    							);	
    				} else {
    					result = result.concat(" && ("+argField.getField() + 
    							"["+objectString+"]=="+args.get(i)+")");
    				}
    			}
    		}
    	}
    	
	}
	catch (Exception e) {
		System.err.println("Error in  writePredArgWhichField"	+ e.getMessage());
	}
    	return result;
    }
    
    public void visitObjectProposition(ObjectProposition ast) 
    		throws ParseException
    {    
    	try {
    	insideObjectProposition = true;
    	
        String packedOrUnpacked = "";
        boolean isPacked = true;
        if (lastIdentifierOrKeyword.equals("unpacked")) {
        	packedOrUnpacked = " == false";
        	isPacked = false;
        }
    	
        //TODO could make == 1.0 instead of (fracCount[this] >= 1.0)
        //as a special case. It's not in this method.
        // XXXX TODO this next
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
    	
    	// The list of which field each argument of 
    	// this predicate represents.

    	/*LinkedList<ArgumentAndFieldPair> listArgsToFields =
    			predArgWhichField.get(predName);*/
    	
    	objectPropString = "";
    	ArgumentList argList = (ArgumentList)childrenPredDecl[1];
    	
    	LinkedList<String> args = new LinkedList<String>();
    	if (!argList.isEmpty()) {
    	// This is ArgumentList.
    		inArgumentList = true;
    		AST[] childrenArgList = argList.getChildren();
    		for (int i=0; i<childrenArgList.length; i++){
    			objectPropString = "";
    			childrenArgList[i].accept(this);
    			args.add(objectPropString);
    		}
    		inArgumentList = false;
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
        fracString.setNameFrac("frac"+upperCaseFirstLetter(predName));
        
        //TODO does fracString need parameters?
        fracString.setParameters(argumentsObjProp);       

        if (isNumeric(fracInObjProp)) {
        	if (fieldName == null){
        		// This is where FracString is updated
        		// but only when we are inside a predicate.
        		bodyMethodOrPredicate = "((packed"+upperCaseFirstLetter(predName)+"[";
        		bodyMethodOrPredicate = bodyMethodOrPredicate.concat(objectString+
        				"]"+ packedOrUnpacked+") && \n \t \t(frac"+
        				upperCaseFirstLetter(predName)+"[");
        		bodyMethodOrPredicate = bodyMethodOrPredicate.concat(objectString+ "] >= " 
        				+ fracInObjProp+")");
        		bodyPredicate = "(frac"+upperCaseFirstLetter(predName)+"[";
        		bodyPredicate = bodyPredicate.concat(objectString+ "] >= " + fracInObjProp + ")");
        	} else {
        		bodyMethodOrPredicate = "((packed"+upperCaseFirstLetter(predName)+"[";
        		bodyMethodOrPredicate = bodyMethodOrPredicate.concat(fieldName +
        				"[this]]"+ packedOrUnpacked+") && \n \t \t(frac"+upperCaseFirstLetter(predName)+"[");
        		bodyMethodOrPredicate = bodyMethodOrPredicate.concat(fieldName + 
        				"[this]] >= " + fracInObjProp+")");

        		bodyPredicate = "(frac"+upperCaseFirstLetter(predName)+"[";
        		bodyPredicate = bodyPredicate.concat(fieldName + 
        				"[this]] >= " + fracInObjProp + ")");
        		fracString.setField(fieldName);
        		objProp.setObject(fieldName+"[this]");	
        	} 
    	} else {
        	if (fieldName == null){
        		// This is where FracString is updated
        		// but only when we are inside a predicate.       	
        		bodyMethodOrPredicate = "((packed"+upperCaseFirstLetter(predName)+"[";
        		bodyMethodOrPredicate = bodyMethodOrPredicate.concat(objectString+
        				"] "+ packedOrUnpacked+") && \n \t \t(frac"+upperCaseFirstLetter(predName)+"[");
        		bodyMethodOrPredicate = bodyMethodOrPredicate.concat(objectString+ "] > 0.0)");
        		bodyPredicate = "(frac"+upperCaseFirstLetter(predName)+"[";
        		bodyPredicate = bodyPredicate.concat(writePredParamsOutOrToString(predName, 2, true));
        		bodyPredicate = bodyPredicate.concat(objectString+ "] > 0.0)");
        	} else {
            	bodyMethodOrPredicate = "((packed"+upperCaseFirstLetter(predName)+"[";
            	bodyMethodOrPredicate = bodyMethodOrPredicate.concat(fieldName +
    			          "[this]]"+ packedOrUnpacked+") && \n \t \t(frac"+
            			  upperCaseFirstLetter(predName)+"[");
            	bodyMethodOrPredicate = bodyMethodOrPredicate.concat(fieldName + "[this]] > 0.0)");
            	bodyPredicate = "(frac"+upperCaseFirstLetter(predName)+"[";
            	bodyPredicate = bodyPredicate.concat(fieldName + "[this]] > 0.0)");
        		fracString.setField(fieldName);
        		objProp.setObject(fieldName+"[this]");		
        	} 
    	}
        
    	// The minBound is the same if the fieldName is null or not. 
    	// We do not need to set this inside the if branches.
    	fracString.setMinBound(0);
    	
    	if ( (currentMethod != "") && !insidePrecondition && !insidePostcondition) {
    		modifyMethodBodyFrac(fracString.getNameFrac());
        	addArgToPackedModifiedArgsInMethod(predName, objectString);
    	}
    	
    	// TODO don't I need to add if I am in inPackUnpackAnnotation also???
    	if ((currentMethod != "") && !inPackUnpackAnnotation) {
    		if (insidePrecondition || insidePostcondition) {
    			modifyMethodSpec(bodyMethodOrPredicate);
    			// The list of which field each argument of 
            	// this predicate (the predicate of this object proposition) represents.
            	LinkedList<ArgumentAndFieldPair> listArgsToFields =
            			predArgWhichField.get(predName);
    			String oneObjProp = writePredArgWhichField(
    					listArgsToFields,
    					args,
    					objectString
    			);
    			modifyMethodSpec(oneObjProp+")");
    		}
 
    		if (insidePrecondition) {
    			addArgToPackedModifiedArgsInPrecondition(predName, objectString);
    			modifyMethodPreconditions(objProp);
    			modifyRequiresFrac(fracString); 
    			
    		    addToFractionManipulationsList(
    		    		1, // 1 for method precondition
    		    		currentMethod, 
    		    		ifConditionFractionManipulation,
    		    		predName,
    		    		objectString,
    		    		fracInObjProp,
    		    		isPacked
    		    );
    		    
    		    // add the (predicate, object) to 
    		    // unpackedPredicatesInPrecondition
    		    if (packedOrUnpacked.equals(" == false")) {
    		    modifyUnpackedPredicatesInPrecondition(predName, objectString);
    		    }
    				
    		} else if (insidePostcondition) {
    			modifyMethodPostconditions(objProp);
    			modifyEnsuresFrac(fracString);
    		    addToFractionManipulationsList(
 		    			2, // 2 for method postcondition
 		    			currentMethod, 
 		    			ifConditionFractionManipulation,
    		    		predName,
    		    		objectString,
    		    		fracInObjProp,
    		    		isPacked
    		    );
    		}
    	} else if (currentMethod == "") {
    		int locationEndObjProp = modifyPredicateBody(bodyPredicate);
    		// This is where I write
    		// add count_ol_2 to the map
    		// to be searched for recursively.
    		// TODO need to add a much bigger comment here
    		addObjPropToPredArgWhichField(objProp);
    		
    		//We want to add the FracString to predicateFrac for
    		//the current predicate and the FracString 
    		//frac+predName[fieldname] >0.0.
    		modifyPredicateFrac(fracString);
    		//We set the location of where this object proposition ends in the 
    		//string of the body of this predicate.
    		// We subtract 2 because of the "(" and ")" that surround this 
    		// object proposition.
    		objProp.setLocation(locationEndObjProp-2);
    		modifyPredicateObjProp(objProp);
    		
		    addToFractionManipulationsList(
		    		0, // this is 0 for predicate 
		    		namePredicate, 
		    		ifConditionFractionManipulation,
		    		predName,
		    		objectString,
		    		fracInObjProp,
		    		isPacked
		    );
    		
    	}
    	insideObjectProposition = false;
    	}
    	catch (Exception e) {
    		System.err.println("Error in  visitObjectProposition "+ e.getMessage());
    	}
    }

   
    public void visitUnaryExpression(UnaryExpression ast) 
    		throws ParseException 
    {
        visitChildren(ast);
    }

    public void visitPrimaryExpression(PrimaryExpression ast) 
    		throws ParseException 
    { 
    	Expression[] children = (Expression[])ast.getChildren();
    	if (children.length == 2) {
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
    	
    	// If the second child is FieldSelection
    	// we treat this case separately.
    	if (children.length == 2) {
    		if (children[1] instanceof FieldSelection) {
    			FieldSelection fieldSel1 = (FieldSelection)children[1];
    			String stringField1 = fieldSel1.getIdentifier().getName();
                if ((currentMethod != "") && beforeChild0) {
            		   modifyFieldsInMethod(stringField1);     
                }
                // We need to signal that we are 
                // inside a field selection because we do not want
                // the string of children[0] (usually a keyword or an identifier) 
                // to be written out twice.
                inFieldSelection = true;
                children[0].accept(this);
                inFieldSelection = false;
         		helperFieldSelection(stringField1);
         		
         		return;
    		}
    	}
    	
    	// Because FieldSelection has many special cases, each
    	// of these cases is treated separately here.
    	// Future work: include here the case 
    	// when we have more than 2 FieldSelection nodes in a row.
    	if (children.length == 3) {
    	if ((children[1] instanceof FieldSelection) && 
    		(children[2] instanceof FieldSelection)) {
    		FieldSelection fieldSel1 = (FieldSelection)children[1];
    		FieldSelection fieldSel2 = (FieldSelection)children[2];
    		String stringField1 = fieldSel1.getIdentifier().getName();
    		String stringField2 = fieldSel2.getIdentifier().getName();
    		
    		String identifierName = stringField2 + "[" + stringField1;
            if ((currentMethod != "") && beforeChild0) {
       		   modifyFieldsInMethod(stringField1);   
       		   modifyFieldsInMethod(stringField2);   
          }
            inFieldSelection = true;
            children[0].accept(this);
            inFieldSelection = false;
    		helperFieldSelection(identifierName);
    		
    		return;
    	} else 
        	// If the second child is FieldSelection
        	// and the third is MethodSelection 
        	// we treat this case separately.
        	if (children[1] instanceof FieldSelection &&
        		children[2] instanceof MethodSelection) {
        		FieldSelection fieldSel1 = (FieldSelection)children[1];
        		String stringField1 = fieldSel1.getIdentifier().getName();
        		if ((currentMethod != "") && beforeChild0) {
        			modifyFieldsInMethod(stringField1);     
                }
        		// We need to signal that we are 
        		// inside a field selection because we do not want
        		// the string of children[0] (usually a keyword or an identifier) 
        		// to be written out twice.
        		inFieldSelection = true;
        		children[0].accept(this);
        		inFieldSelection = false;
        		helperFieldSelection(stringField1);	
             	children[2].accept(this);	
             	return;
        	}
    	}
        visitChildren(ast);
    }
    
    public void visitFormalParameters(FormalParameters ast) 
    		throws ParseException 
    { 
    	visitChildren(ast);
    }
    
    public void visitFormalParameter(FormalParameter ast) 
    		throws ParseException 
    {
    	if (ast!=null) {
    		String name = ast.getName();
    		parametersMethod.add(name);
    		String type = ast.getType().toString();
    		if (namePredicate!="") {
    			modifyFormalParams(name, type); 
    			addArgToPredArgWhichField(name);
    		} else if (currentMethod !="") {
    			modifyMethodParams(name, type);
    		}
    		//I don't think this node has any children.
    		visitChildren(ast);
    	}
    }
    
    public void visitAllocationExpression(AllocationExpression ast) 
    		throws ParseException 
    {
    	String predicateOfConstruct = ast.getPredicate();
    	LinkedList<String> localArgumentsConstructor = new LinkedList<String>();
    	LinkedList<String> localArgumentsPredicate = new LinkedList<String>();
    	
    	modifyMethodBody("\t call Construct" + ast.getAlloc_func()+"(");
    	AST[] children = ast.getChildren();
    	//This is the ArgumentList that contains the arguments
    	//for the fields.
    	argumentsConstructor.clear();
        children[1].accept(this);
        for (int i=0;i<argumentsConstructor.size();i++) {
        	localArgumentsConstructor.add(argumentsConstructor.get(i));
        }
              
        modifyMethodBody(localVariableName + ");\n");
        modifyMethodBody("packed" +predicateOfConstruct+"[");
        argumentsPredicate.clear();
        children[0].accept(this);
        for (int i=0;i<argumentsPredicate.size();i++) {
        	localArgumentsPredicate.add(argumentsPredicate.get(i));
        }
        
        modifyMethodBody(localVariableName + "] := true;\n");
        modifyMethodBody("frac" +upperCaseFirstLetter(predicateOfConstruct)+"[");
        children[0].accept(this);
        modifyMethodBody(localVariableName + "] := 1.0;\n");
        modifyFieldsInMethod("packed" +upperCaseFirstLetter(predicateOfConstruct));
        modifyFieldsInMethod("frac" +upperCaseFirstLetter(predicateOfConstruct));
        
        LinkedList<FracString> currentPredicateFrac = 
    			predicateFrac.get(predicateOfConstruct);
        if (currentPredicateFrac != null) {	
        	for (int pf = 0; pf < currentPredicateFrac.size(); pf++) {
                FracString fracString = currentPredicateFrac.get(pf);
                //replace the formal parameters with the actual ones
                String field = fracString.getField();
            
                int positionInListOfFields=-1;
                if (field!=null) {
                	for (int i=0;i<fieldsTypes.size();i++) {
                		if (fieldsTypes.get(i).getName().equals(field)) {
                			positionInListOfFields = i;
                		}
                	}
                }
                LinkedList<String> initialParameters = new LinkedList<String>();
                initialParameters = fracString.getParameters();
                if (localArgumentsPredicate!=null) {
                	fracString.setParameters(localArgumentsPredicate);
                }
                
                if (positionInListOfFields == -1){
                	modifyMethodBody(fracString.getStatementFracString(true, "this"));
                } else {
                	String actualField = localArgumentsConstructor.get(positionInListOfFields);
                	if (!actualField.equals("null")) {
                		//I only need to set the field to null temporarily,
                		//then I set it back to the original field.
                		fracString.setField(null);
                		modifyMethodBody(fracString.getStatementFracString(true, actualField));
                		fracString.setField(field);
                	}
                    		
                }
                fracString.setParameters(initialParameters);
        	}
        }
    }
    
    public void visitDeclarationStatement(DeclarationStatement ast) 
    		throws ParseException 
    { 
    	visitChildren(ast); 
    	localVariableName = "";
    }
    
    public void visitLocalVariableDeclaration(LocalVariableDeclaration ast) 
    		throws ParseException
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
    			stringOfVarDecls = stringOfVarDecls.concat("\t var " +
    						localVariableName +":"+ fieldType+";\n");
    	
    	visitChildren(ast);
      }
    
    public void visitKeywordExpression(KeywordExpression ast) 
    		throws ParseException
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
    	
    	lastIdentifierOrKeyword = keywordString;
    		
        if (namePredicate.equals("")) {
     	   //we are not inside a predicate
        	if (!inPackUnpackAnnotation) {
        		if ((currentMethod != "") && inArgumentList && 
         			   	!inFieldSelection) {
        			statementContent = statementContent.concat(keywordString + ", ");
     		   }
     		   
     		   if ((currentMethod != "") && (inStatement) && 
     				   !inArgumentList && !inMethodSelectionStatement &&
     				   !inFieldSelection) {
     				  	statementContent = statementContent.concat(keywordString);
     		   }
     		   
     		   // We put in all the cases when "this" should be written down.
     		   if ((insidePrecondition || insidePostcondition) && 
     				  ((keywordString.equals("null") 
     						  || 
     				  (keywordString.equals("this") && 
     						  !inFieldSelection && !insideObjectProposition ) 
     						  ) ) ) {
     			   modifyMethodSpec(keywordString);
     		   }
        	}  else {
        		// We are inside a pack/unpack annotation.
        		if (!insideObjectProposition && inArgumentList && !inFieldSelection) {
        			objectPropString = objectPropString.concat(keywordString);
        		}
        	}
     	
        	//modify object proposition parts
        	if (insideObjectProposition && !inFieldSelection) {
        		objectPropString = objectPropString.concat(keywordString);
        	}	   
        } else { 
     	   	//we are inside a predicate	  
 		   	FieldTypePredbody currentParamsPredicateBody = paramsPredicateBody.get(namePredicate);
     	      	 
 		   	if (insideObjectProposition) {
 		   		objectPropString = objectPropString.concat(keywordString);
  			} else {
  				paramsPredicateBody.put(
  						namePredicate, 
  						currentParamsPredicateBody.concatToPredicateBody(keywordString)
  				); 
  			}
        }
        
        if (inArgumentList) {
        	argumentsConstructor.add(keywordString);
        	argumentsPredicate.add(keywordString);
        }
        
		  if (inChild1OfImplies) {
			  // Although "this" is a keyword,
			  // it should be treated like a formal parameter.
			  // That is why it is surrounded by $ and @.
			  if (keywordString.equals("this")) {
				  ifConditionFractionManipulation = 
	  					  ifConditionFractionManipulation.concat("$"+keywordString+"@");
			  } else {
				  // The keywordString does not need to be replaced by
				  // the actual parameter.
				  ifConditionFractionManipulation = 
  					  ifConditionFractionManipulation.concat(keywordString);
			  }
  		  }
    }
    
    public void visitFractionAnnotation(FractionAnnotation ast) 
    		throws ParseException 
    { 
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
    	inArgumentList = true;
    	children[0].accept(this);
    
    	toWrite = toWrite.concat("frac"+upperCaseFirstLetter(predicateNameObjProp)+"[");
    	toWrite = toWrite.concat(objectObjProp + "] := ");
    	//Same code as above
    	//TODO remove duplicate code
    	toWrite = toWrite.concat("frac"+upperCaseFirstLetter(predicateNameObjProp)+"[");
    	toWrite = toWrite.concat(objectObjProp + "]");
    	
  	  	for (int i = 1; i < children.length; i++) {
  	  		objectObjProp = "";
  	  		predicateNameObjProp = "";
  	  		argumentsObjProp.clear();
  	  		fractionObjProp = "";
  	  		children[i].accept(this);
  	  		if (annotationName.equals("addFrac")) {
  	  			toWrite = toWrite.concat("+" + fractionObjProp);
  	  		} else {
  	  			//TODO need to write something here
  	  		}
  	  	}
  	    inArgumentList = false;
    	
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
    	
    	AST[] children = ast.getChildren();
    	children[0].accept(this);
    	
    	existentialArgsObjProp.clear();
    	ArgumentList argList = (ArgumentList)children[1];
    	inArgumentList = true;
    	AST[] childrenArgList = argList.getChildren();
    	for (int i=0;i<childrenArgList.length; i++){
    		objectPropString = "";
    		childrenArgList[i].accept(this);
    		existentialArgsObjProp.add(objectPropString);
    	}
    	inArgumentList = false;
    	
    	if (annotationName.equals("pack")) {
    		toWrite = toWrite.concat("call Pack"); 
    	} else {
    		toWrite = toWrite.concat("call Unpack"); 
    	}
    	toWrite = toWrite.concat(upperCaseFirstLetter(predicateNameObjProp) + "("); 
    	
    	for (int i=0;i<argumentsObjProp.size();i++) {
    		toWrite = toWrite.concat(argumentsObjProp.get(i)+", ");
    	}
    	
    	for (int i=0;i<existentialArgsObjProp.size();i++) {
    		toWrite = toWrite.concat(existentialArgsObjProp.get(i)+", ");
    	}
    	
    	toWrite = toWrite.concat(objectObjProp + ");\n"); 
       	if (annotationName.equals("pack")) {
    	 	// Add the fraction manipulations statements
    		toWrite = toWrite.concat(writeFractionManipulation(
    				predicateNameObjProp, true, true, objectObjProp));
    	} else {
    	 	// Add the fraction manipulations statements
    		toWrite = toWrite.concat(writeFractionManipulation(
    				predicateNameObjProp, true, false, objectObjProp));
    	}
    	
    	toWrite = toWrite.concat("packed" + upperCaseFirstLetter(predicateNameObjProp)+"[");
    	toWrite = toWrite.concat(objectObjProp + "] := "); 
    	
       	if (annotationName.equals("pack")) {
    		toWrite = toWrite.concat("true"); 
    	} else {
    		toWrite = toWrite.concat("false"); 
    	}
       	modifyFieldsInMethod("packed"+upperCaseFirstLetter(predicateNameObjProp));
    
    	modifyMethodBody(toWrite);
    	inPackUnpackAnnotation = false;    	
  	}
    
    String findActualParamInFormals(
        	LinkedList<FieldAndTypePair> formalParams,
        	LinkedList<String> actualParams,
        	String formalParameter) {
    	String result = "";
    	
    	int indexOfCurrentParam = -1;
		for (int i=0; i<formalParams.size(); i++) {
			if (formalParams.get(i).getName().equals(formalParameter)) {
				indexOfCurrentParam = i;
			}
		}

		// This should always be different than -1.
		if ((indexOfCurrentParam != -1)) {
			result = actualParams.get(indexOfCurrentParam);
		}	
		return result;
    	
    }
    
    // Each formal parameter should be!!! surrounded by $ and @ in oldString
    String replaceFormalArgsWithActual(
    	LinkedList<FieldAndTypePair> formalParams,
    	LinkedList<String> actualParams,
    	String oldString
    ) {
    	String result = "";
    	int indexOfLeftParen = oldString.indexOf('$');
    	int indexOfRightParen = oldString.indexOf('@');

    	while (indexOfLeftParen != -1) {
    		
    		String currentParam = oldString.substring(indexOfLeftParen+1, indexOfRightParen);
    	
    		int indexOfCurrentParam = -1;
    		for (int i=0; i<formalParams.size(); i++) {
    			if (formalParams.get(i).getName().equals(currentParam)) {
    				indexOfCurrentParam = i;
    			}
    		}
    		
    		// This should always be different than -1.
    		if (indexOfCurrentParam != -1) {
    			result = result.concat(oldString.substring(0, indexOfLeftParen));
    			result = result.concat(actualParams.get(indexOfCurrentParam));
    		}
    		
    		oldString = oldString.substring(indexOfRightParen+1, oldString.length());
    		
    		indexOfLeftParen = oldString.indexOf('$');
        	indexOfRightParen = oldString.indexOf('@');
    		
    	}
    	result = result.concat(oldString);
    	return result;
    }
    
    public <K> LinkedList<K> copyLinkedList(LinkedList<K> l) {
    	 LinkedList<K> result = new LinkedList<K>();
    	 for (int i = 0; i< l.size(); i++) {
    		 result.add(l.get(i));
    	 }
    	 return result;
    }
         
    String writeFractionManipulation(
    		String namePredOrMethod, 
    		boolean isPredicate, 
    		boolean isPack, 
    		String callingObject
    ) {
    	 String result = "";
    	 LinkedList<FractionManipulationStatement> fractionManipulationsList;
    	 // First I need to get the formal parameters list corresponding to this method or 
    	 // predicate and also the actual parameters list. 
    	 // In condition and in the fraction object I need to replace the formal parameters with
    	 // the actual parameters.
    	 // The order is that the formal params are written out is:
    	 // formal params, existential params, this.
    	 // I only need the name of all these parameters.
    	 LinkedList<FieldAndTypePair> formalParams = new LinkedList<FieldAndTypePair>();
    	 LinkedList<String> actualParams = new LinkedList<String>();
    	 
    	 if (isPredicate) {
    		 fractionManipulationsList = fractionManipulationsListPredicate.get(namePredOrMethod);
    		 // use paramsPredicateBody for formal params.
    		 // for actual params use argumentsObjProp and existentialArgsObjProp
    		 FieldTypePredbody args = paramsPredicateBody.get(namePredOrMethod);
    		 // Here I am making a copy of the actual list of formal paramters
    		 // because I do not want to add elements to the original list.
    		 formalParams = copyLinkedList(args.getFormalParameters());

    		 formalParams.addAll(args.getExistentialParameters());
    		 formalParams.add(new FieldAndTypePair("this", "Ref"));
    		 
    		 // These are the actual parameters of the current object proposition.
    		 // This is right because for the predicate case we are always inside an object proposition
    		 // and argumentsObjProp are accurate.
    		 actualParams = copyLinkedList(argumentsObjProp);
    		 actualParams.addAll(existentialArgsObjProp);
    		 actualParams.add(callingObject);
    		 
    		 
        	 for (int i=0; i<fractionManipulationsList.size(); i++) {
        		 FractionManipulationStatement fracMan = fractionManipulationsList.get(i);
        		
        		 if (!fracMan.getIfCondition().equals("")) {
        			 result = result.concat("if (" + 
        					 replaceFormalArgsWithActual(
        							 formalParams,
        							 actualParams,
        							 fracMan.getIfCondition()
        							 )
        							 + ") {\n ");
        		 }
        		 
        		 String actualObject = findActualParamInFormals(
        		        	formalParams,
        		        	actualParams,
        		        	fracMan.getFractionObject());
        		 result = result.concat("frac" + upperCaseFirstLetter(fracMan.getPredName()) + 
        				 "[" + actualObject
        				 +"] := frac" + upperCaseFirstLetter(fracMan.getPredName()) + "[" + 
        				 actualObject
        				 +"]"); 
        		 String fractionString = fracMan.getFraction();
        		 modifyFieldsInMethod("frac" + upperCaseFirstLetter(fracMan.getPredName()));
        		 if (isNumeric(fractionString)) {
        			 if (isPredicate) {
        				 if (isPack) {
        					 result = result.concat(" - ");
        				 } else {
        					 result = result.concat(" + ");
        				 }
        			 } 
        			 result = result.concat(fractionString);
        		 } else {
        			 // If we are in this branch
        			 // it means that the fraction is "k" or similar
        			 if (isPredicate) {
        				 if (isPack) {
        					 result = result.concat(" / 2.0");
        				 } else {
        					 result = result.concat(" + 0.001");
        				 }
        			 } 
        			 
        		 }
        		 result = result.concat(";\n");
        		 if (!fracMan.getIfCondition().equals("")) {
        			 result = result.concat("}\n");
        		 }
        	 }

    	 } else { 
    		 // look in methodParams for formal params
    		 // need to separate the comma separated params, then add "this" as it's not added yet.
    		 // for actual params need to add a new LinkedList in visitMethodSelection
    		 formalParams = copyLinkedList(methodParams.get(namePredOrMethod));
    		 formalParams.add(new FieldAndTypePair("this", "Ref"));
    		 // This is the list of actual arguments for the current method 
    		 actualParams = actualArgumentsMethod;
    		 for (int kk=0; kk<actualArgumentsMethod.size(); kk++) {
    			 System.out.println(namePredOrMethod +" " + actualArgumentsMethod.get(kk));
    		 }
    		 
    		 Set<FieldAndTypePair> predObjs = 
    	    			predObjNotMentionedPostcond.get(namePredOrMethod);
    	     if (predObjs != null) {
    	    	 for (FieldAndTypePair p : predObjs) {
        	    	System.out.println(p.getType());
    	    		result = result.concat("\t frac"+upperCaseFirstLetter(p.getName())+"["+
        			replaceFormalArgsWithActual(
        					 formalParams,
        					 actualParams,
        					 '$'+p.getType()+'@' // this is not actually the type but the object
        					 // and it should be surrounded by $ and @
        					 ) +"] := 0.0;\n"
        			);			
    	    	 }  		
    	    }	 
    	 }
    	
    	return result;
    }
     
    public void visitIdentifierExpression(IdentifierExpression ast) 
    		throws ParseException
    {    	
    	lastIdentifierOrKeyword = ast.name;
    	if (lastIdentifierOrKeyword.equals("unpacked")) 
    		return;
    	try {
    		String identifierName = lastIdentifierOrKeyword;  
    		if (namePredicate.equals("")) {
    			//we are not inside a predicate
    				if (!inPackUnpackAnnotation) {
    					// If we are in FieldSelection, this already 
    					// is written to the statementContent in another place.
    					if ((currentMethod != "") && (inArgumentList) 
    							&& (inStatement) && !inFieldSelection) {
    						if (inBinaryExpression) {
    							statementContent = statementContent.concat(identifierName);
    						} else {
    							statementContent = statementContent.concat(identifierName + ", ");
    						}
    					}
    		   
    					if ((currentMethod != "") && (inStatement) && 
    							!inArgumentList && !inMethodSelectionStatement &&
    							!inFieldSelection) {
    						statementContent = statementContent.concat(identifierName);
    					}
    		   
    					if (!insideObjectProposition && !inFieldSelection &&
    							(insidePrecondition || insidePostcondition)) {
    						modifyMethodSpec(identifierName);
    					}
    				} else {
    					// We are in pack/unpack annotation
    					if (!insideObjectProposition && inArgumentList && !inFieldSelection){
    						objectPropString = objectPropString.concat(identifierName);
    					}
    				}
    				//modify object proposition parts
    				if (insideObjectProposition && !inFieldSelection) {
    					objectPropString = objectPropString.concat(identifierName);
    				}	
    			
    		} else {
    			//we are inside a predicate
    			FieldTypePredbody currentParamsPredicateBody = paramsPredicateBody.get(namePredicate);
    	      	
    				if (insideObjectProposition && !inFieldSelection) {
    					objectPropString = objectPropString.concat(identifierName);
    				} else {
    					if (!inFieldSelection) {
    						paramsPredicateBody.put(
    								namePredicate, 
    								currentParamsPredicateBody.concatToPredicateBody(identifierName)
    						); 	
    					}
    				}	
    		}
    		//TODO put more comments
    		// and realign the code.
       
    		if (inArgumentList) {
    			argumentsConstructor.add(identifierName);
    			argumentsPredicate.add(identifierName);
    		}
        	
    		// If we are in field selection, 
    		// this identifier will be written out in helperFieldSelection().
  		  if (inChild1OfImplies && !inFieldSelection) {
  			  ifConditionFractionManipulation = 
  					  ifConditionFractionManipulation.concat("$"+identifierName+"@");
  		  }
    	}
    	catch (Exception e) {
    		System.err.println("Error in visitIdentifierExpression: " + e.getMessage());
    	};

    	visitChildren(ast);
    }
    
    public void visitIfStatement(IfStatement ast) 
    		throws ParseException
    {
    	inMethodSelectionStatement = false;
    	//An if statement can be only inside a method statement.
    	inStatement = true;
    	AST[] children = ast.getChildren();
    	int size = children.length;
    	statementContent = statementContent.concat("if (");    	
    	children[0].accept(this);
    	statementContent = statementContent.concat(") {\n");
    	
    	modifyMethodBody(statementContent);
    	//We make it "" because we have just written it to methodBody.
    	statementContent = "";
    	//children[1] and [2] are Blocks.
    	children[1].accept(this);
    	statementContent = "";
    	if (size == 3) { 
    		modifyMethodBody("else {\n");
        	children[2].accept(this);
        	statementContent = "";
    	}
    	inStatement = false;
    }
    
    public void visitStatementExpression(StatementExpression ast)
  		  throws ParseException
  	{ 
    	inStatement = true;
    	visitChildren(ast);
    	try {
    		if (currentMethod != "") {
    			statementContent = statementContent.concat(";\n");
    		} else {
    			out.write(";\n");	
  			}
        }
    	catch (Exception e) {
    		System.err.println("Error in visitStatementExpression: " + e.getMessage());
        }
    	inStatement = false;
  	}
    
    public void visitMethodSpecVariable(MethodSpecVariable ast) 
  		  throws ParseException 
  	{
    	if (ast!=null) {
    		String name = ast.getName();
    		parametersMethod.add(name);
    		String type = ast.getType().toString();
    	if (currentMethod !="") {
    		modifyMethodParams(name,type);
    	}
    		//I don't think this node has any children.
    		visitChildren(ast);
    	} 
    }
    
    public void visitMethodSpecVariables(MethodSpecVariables ast)
  		  throws ParseException 
  	{ 
    	visitChildren(ast); 
    }
    
    public void visitMethodSpecFracVariable(MethodSpecFracVariable ast) 
    		throws ParseException 
    { 
    	visitChildren(ast); 
    }
    
    public void visitMethodSpecExpression(MethodSpecExpression ast) 
  		  throws ParseException 
  	{ 
    	MethodSpecVariables methodVariables = ast.getMethodSpecVariables();
    	Expression precondition = ast.getPrecondition();
    	Expression postcondition = ast.getPostcondition();
    	
    	methodVariables.accept(this);
    	
    	if (precondition != null) {
    		modifyMethodSpec("\t requires (this != null) && ");
    		insidePrecondition = true;
    		disjunctionNumber = 0;
    		precondition.accept(this);
    		insidePrecondition = false;
    		modifyMethodSpec(";\n");
    	}

    	if (postcondition != null) {
    		modifyMethodSpec("\t ensures ");
    		insidePostcondition = true;
    		disjunctionNumber = 0;
    		postcondition.accept(this);
    		insidePostcondition = false;
    		modifyMethodSpec(";\n");
    	}
    }
    
    public void visitBlock(Block ast) 
    		throws ParseException 
  	{ 
    	
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
    	// If we are inside unpacked(...)
    	// then this is not really an argument list.
    	// It is actually only a single object proposition.
    	if (!lastIdentifierOrKeyword.equals("unpacked")) {
    		inArgumentList = true;
    	}

    	visitChildren(ast); 
    	inArgumentList = false;
    }
    
    void modifyMethodBody(String s) {
    	String currentMethodBody = methodBody.get(currentMethod);
		currentMethodBody = currentMethodBody.concat(s);
		methodBody.put(currentMethod, currentMethodBody);
    }
    
    int modifyPredicateBody(String s) {
		FieldTypePredbody currentParamsPredicateBody = paramsPredicateBody.get(namePredicate);
		paramsPredicateBody.put(
				namePredicate, 
				currentParamsPredicateBody.concatToPredicateBody(s)
		);
		return currentParamsPredicateBody.getPredicateBody().length();
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
    
    //Adds the pair (argument name, "") to 
    //the list of pairs (argument, corresponding field)
    void addArgToPredArgWhichField(String arg) {
    	LinkedList<ArgumentAndFieldPair> list =
    			predArgWhichField.get(namePredicate);
    	if (list==null) {
    		list = new LinkedList<ArgumentAndFieldPair>();
    	}
		list.add(new ArgumentAndFieldPair(arg));
    	predArgWhichField.put(
    			namePredicate,
    			list
    	);
    }
    
    //Adds the field corresponding to 
    //the argument 
    void addFieldToPredArgWhichField(String arg, String field) {
    	LinkedList<ArgumentAndFieldPair> list =
    			predArgWhichField.get(namePredicate);
    	if (list!=null) {
    		for (int i=0; i < list.size(); i++) {
    			ArgumentAndFieldPair o = list.get(i);
    			if (o.getArgument().equals(arg)) {
    				o.setField(field);
    			}
    		}
    	}
    }
    
    //Adds the object proposition corresponding to 
    //the argument. An argument of a predicate 
    //can only be the right side of -> or used as a parameter in
    //another predicate.
    void addObjPropToPredArgWhichField(ObjPropString ops) {
    	LinkedList<ArgumentAndFieldPair> listArgField =
    			predArgWhichField.get(namePredicate);
    	LinkedList<String> listParams = ops.getParams();
    	if (listParams != null) {
    		for (int i=0; i<listParams.size(); i++) {
    			String name = listParams.get(i);
    			if (listArgField != null) {
    				for (int j=0; j<listArgField.size(); j++) {
    					ArgumentAndFieldPair o = listArgField.get(j);
    					if (name.equals(o.getArgument())) {
    						// This is the compact representation of
    						// an object proposition that has the argument as one of its
    						// parameters.
    						String compactObjProp = ops.getName()+" "+ops.getObject()+" "+i;   				
    						o.setField(compactObjProp);
    						break;
    					}
    				}
    			}
    		}
    	}
    }
    
    void modifyExistentialParams(String name, String type) {
		FieldTypePredbody currentParamsPredicateBody = 
				paramsPredicateBody.get(namePredicate);
		if (currentParamsPredicateBody != null) {
			paramsPredicateBody.put(
					namePredicate, 
					currentParamsPredicateBody.addExistentialParam(name, type)
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
    
    void modifyMethodParams(String field, String type) {
    	LinkedList<FieldAndTypePair> currentMethodParams = methodParams.get(currentMethod);
    	if (currentMethodParams!=null) {
    		currentMethodParams.add(new FieldAndTypePair(field, type));
    	}
		methodParams.put(currentMethod, currentMethodParams);
    }
    
    // Adds name+ ":" + type +",", representing the name of
    // the existential parameter and its type to the list 
    // of existential parameters of each method.
    // This method works only on currentMethod.
    void modifyMethodExistentialParams(String s) {
    	String currentMethodExistentialParams = methodExistentialParams.get(currentMethod);
    	currentMethodExistentialParams = currentMethodExistentialParams.concat(s);
		methodExistentialParams.put(currentMethod, currentMethodExistentialParams);
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
    
    //adds a FracString to the methodBodyFrac map for the currentMethod
    void modifyMethodBodyFrac(String s) {
    	LinkedList<String> currentMethodBodyFrac = 
    			methodBodyFrac.get(currentMethod);
    	if (currentMethodBodyFrac == null) {
    		currentMethodBodyFrac = new LinkedList<String>();
    	}
    	currentMethodBodyFrac.add(s);
    	methodBodyFrac.put(currentMethod, currentMethodBodyFrac);
    	
    }
    
    // write assume (forall y:Ref :: (fracRight[y] >= 0.0) );
    // to a string.
    String writeAssumeForall(String methodName) {
    	String result = "";
    	
    	Set<String> fracInMethodBodyAndSpec = new TreeSet<String>();
    	LinkedList<FracString> requiresFracInMethod = requiresFrac.get(methodName);
    	LinkedList<FracString> ensuresFracInMethod = ensuresFrac.get(methodName);
    	LinkedList<String> methodBodyFracInMethod = methodBodyFrac.get(methodName);
    	
    	if (requiresFracInMethod!=null) {
    		for (int i=0; i<requiresFracInMethod.size(); i++) {
    			fracInMethodBodyAndSpec.add(requiresFracInMethod.get(i).getNameFrac());
    		}
    	}
    	
    	if (ensuresFracInMethod != null) {
    		for (int i=0; i<ensuresFracInMethod.size(); i++) {
    			fracInMethodBodyAndSpec.add(ensuresFracInMethod.get(i).getNameFrac());
    		}
    	}
    	
    	if (methodBodyFracInMethod != null) {
    		for (int i=0; i<methodBodyFracInMethod.size(); i++) {
    			fracInMethodBodyAndSpec.add(methodBodyFracInMethod.get(i));
    		}
    	}

    	if (!fracInMethodBodyAndSpec.isEmpty()) {
    		for (String temp : fracInMethodBodyAndSpec) {
    			result = result.concat("\t assume (forall y:Ref :: ("+ 
    				temp +
    				"[y] >= 0.0) );\n");
    		}
    	}
    	return result;
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
    
    
    void addToFractionManipulationsList(
    		int flag, // this is 0 for predicate 
    			      // 1 for method precondition
    				 // 2 for method postcondition
    		String predOrMethod, 
    		String ifCondition,
    		String predName,
    		String fractionObject,
    		String fraction,
    		boolean isPacked
    ) {
    	LinkedList<FractionManipulationStatement> currentPredOrMethodFracManipulation = null;
				
    	switch (flag) {
         	case 0: currentPredOrMethodFracManipulation = 
         				fractionManipulationsListPredicate.get(predOrMethod);
                  	 break;
         	case 1: currentPredOrMethodFracManipulation = 
     					fractionManipulationsListMethodPre.get(predOrMethod);
         	 		break;
         	case 2: currentPredOrMethodFracManipulation = 
 					    fractionManipulationsListMethodPost.get(predOrMethod);
 	 				break;        
    	}
    	FractionManipulationStatement newFracMani =
    			new FractionManipulationStatement(
    					ifCondition,
    					predName,
    					fractionObject,
    					fraction	
    			);
    	newFracMani.setDisjunctionNumber(disjunctionNumber);
    	newFracMani.setIsPacked(isPacked);
    	
    	if (currentPredOrMethodFracManipulation == null) {
    		currentPredOrMethodFracManipulation = new LinkedList<FractionManipulationStatement>();
    	}
    	currentPredOrMethodFracManipulation.add(newFracMani);
    	
    	switch (flag) {
     		case 0: fractionManipulationsListPredicate.put(predOrMethod, 
     					currentPredOrMethodFracManipulation);	 
              	 	break;
     		case 1: fractionManipulationsListMethodPre.put(predOrMethod, 
 						currentPredOrMethodFracManipulation);	 
      	 			break;
     		case 2: fractionManipulationsListMethodPost.put(predOrMethod, 
						currentPredOrMethodFracManipulation);	 
	 				break;       
	    } 
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
   
    void modifyUnpackedPredicatesInPrecondition(String predicate, String object) {
    	 LinkedList<PredicateAndFieldValue> currentUnpackedPredicatesInPrecondition = 
    			unpackedPredicatesInPrecondition.get(currentMethod);
    	if (currentUnpackedPredicatesInPrecondition == null) {
    		currentUnpackedPredicatesInPrecondition = new LinkedList<PredicateAndFieldValue>();
    	}
    	currentUnpackedPredicatesInPrecondition.add(new PredicateAndFieldValue(predicate, object));
    	unpackedPredicatesInPrecondition.put(currentMethod, currentUnpackedPredicatesInPrecondition);    	
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
    
    void modifyPredObjNotMentionedPostcond(String methodName, FieldAndTypePair s) {
    	Set<FieldAndTypePair> currentPredObjNotMentionedPostcond = 
    			predObjNotMentionedPostcond.get(methodName);
    	if (currentPredObjNotMentionedPostcond == null) {
    		currentPredObjNotMentionedPostcond = new TreeSet<FieldAndTypePair>();
    	}
    	currentPredObjNotMentionedPostcond.add(s);
    	predObjNotMentionedPostcond.put(methodName, currentPredObjNotMentionedPostcond);    	
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
    
    String upperCaseFirstLetter(String input) {
    	return input.substring(0, 1).toUpperCase() + input.substring(1);
    }
   
    void makeConstructors(BufferedWriter out) {
    	//I also declare the packed and frac global variables for this class.
    	try {
    		for (String p : predicates) {
    			out.write("var packed" + upperCaseFirstLetter(p) + ": [Ref] bool;\n");
    			out.write("var frac" + upperCaseFirstLetter(p) + ": [Ref] real;\n");
    			
    		}
    		out.write("\n");
    	}
    	catch (Exception e) {
    		System.err.println("Error in makeConstructors declaration of packed and frac: " +
    					e.getMessage());
    	}
    	    	
    	try {
    		//write a constructor that doesn't pack to any predicate
    		out.write("procedure Construct" + className + "(");
            for (FieldAndTypePair s : fieldsTypes) {
            	String type = s.getType();
            		if (type.equals("int") || type.equals("double") || type.equals("boolean")) {
            			out.write(s.getName() + "1 :"+ type + ", ");
            		} else {
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
        		System.err.println("Error in makeConstructors writing of procedure Construct: " + 
        					e.getMessage());
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
	
	// This is a helper function for the function
	// writePredParamsOutOrToString()
	String helperWriteToResult(LinkedList<FieldAndTypePair> paramsList, int k, String result) {
		if (!paramsList.isEmpty()) {
			for (int i=0;i<paramsList.size();i++) {
				FieldAndTypePair f = paramsList.get(i);
				switch (k) {
	            case 1:  
	            	if ((f.getType()).equals("int") || (f.getType()).equals("boolean") ){
	            		result = result.concat(f.getName() + ":"+f.getType()+", ");
	            	} else {
	            		// The types of our parameters can only be int, boolean or 
	            		// object of a certain class. 
	            		// If we are in the "else" branch then the type was not "int"
	            		// or "boolean".
	            		result = result.concat(f.getName() + ": Ref, ");
	            	}
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
    			//Write formal parameters out.
    			LinkedList<FieldAndTypePair> formalParamsList =
    					currentParamsPredicateBody.getFormalParameters();
    			 helperWriteToResult(formalParamsList, k, result);
    			
    			//Write existential parameters out.
    			LinkedList<FieldAndTypePair> existentialParamsList =
    					currentParamsPredicateBody.getExistentialParameters();
    			helperWriteToResult(existentialParamsList, k, result);
    		}
    		
    	} else {
    		try {
    			FieldTypePredbody currentParamsPredicateBody = 
    					paramsPredicateBody.get(pred);
    			if (currentParamsPredicateBody != null) {
    				//Write formal parameters out.
    				LinkedList<FieldAndTypePair> formalParamsList =
    						currentParamsPredicateBody.getFormalParameters();
    				out.write(helperWriteToResult(formalParamsList, k, ""));	
			
    				//Write existential parameters out.
    				LinkedList<FieldAndTypePair> existentialParamsList =
    						currentParamsPredicateBody.getExistentialParameters();
    				out.write(helperWriteToResult(existentialParamsList, k, ""));	
    			}
    		}
    		catch (Exception e) {
    			System.err.println("Error in writePredParamsOutOrToString: " + e.getMessage());
    		}  
    	}
    	return result;
    }    
    
    //k=1 is for writing nameParam: type
    //k=2 is for writing the current value of the parameters
    //k=3 is for writing the types of the parameters
    //If alterName is true then I add "497" to the end of getName(). 
    //I chose "497" because it is unlikely that the programmer 
    //will write an input parameter of this form.
    // TODO this is not used, why did I write it???
    String getStringPredParams(String pred, int k, boolean alterName) { 
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
		            	if (alterName) {
		            	result = result.concat(f.getName() + "497:"+f.getType()+", ");
		            	} else {
		            	result = result.concat(f.getName() + ":"+f.getType()+", ");
		            	}
		                break; 
		            case 2: 
		            	if (alterName) {
		            	result = result.concat(f.getName() + "497, ");
		            	} else {
		            	result = result.concat(f.getName() + ", ");
		            	}
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
 
 // For use in debugging:
 // write a method that iterates through a hashmap that has the key string and the 
 // value a LinkedList of elements. The only requirement is for the elements
 // in the linked list to have a function "writeOut".
 // Using generics.
    
    public static <E extends WriteOut> void writeHashMap(HashMap<String, LinkedList<E>> hashMap) {
    	for (Entry<String, LinkedList<E>> entry : hashMap.entrySet()) {
    	    String key = entry.getKey();
    	    LinkedList<E> value = entry.getValue();
    	    System.out.println("key: "+ key);
    	    for (int i=0; i<value.size();i++) {
    	    	value.get(i).writeOut();
    	    }
    }
    	System.out.println("-----------------");
    }
    
    public static <E extends WriteOut> void writeLinkedList(LinkedList<E> linkedList) {
    	    for (int i=0; i<linkedList.size();i++) {
    	    	linkedList.get(i).writeOut();
    	    }
    	    System.out.println("-----------------");    
    }
    
    public static <E extends WriteOut> void writeSet(Set<E> set) {
    	  Iterator<E> iterator = set.iterator();
    	    while(iterator.hasNext()) {
    	        E setElement = iterator.next();
    	        setElement.writeOut();
    	    }
    	    System.out.println("-----------------");
    }
  
    
    
}



