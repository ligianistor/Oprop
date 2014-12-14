package cager.jexpr.visitor;

import java.io.BufferedWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;

import org.apache.bcel.generic.Type;

import cager.jexpr.JExprConstants;
import cager.jexpr.OperatorTypeInfo;
import cager.jexpr.ParseException;
import cager.jexpr.PredicateAndFieldValue;
import cager.jexpr.Types;
import cager.jexpr.ast.AST;
import cager.jexpr.ast.AllocationExpression;
import cager.jexpr.ast.ArgumentList;
import cager.jexpr.ast.BinaryExpression;
import cager.jexpr.ast.Block;
import cager.jexpr.ast.CastExpression;
import cager.jexpr.ast.ClassDeclaration;
import cager.jexpr.ast.CompilationUnit;
import cager.jexpr.ast.CompilationUnits;
import cager.jexpr.ast.ConstructorDeclaration;
import cager.jexpr.ast.DeclarationStatement;
import cager.jexpr.ast.Expression;
import cager.jexpr.ast.FieldAndTypePair;
import cager.jexpr.ast.FieldDeclaration;
import cager.jexpr.ast.FieldSelection;
import cager.jexpr.ast.ForStatement;
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
import cager.jexpr.ast.QuantificationExpression;
import cager.jexpr.ast.QuantifierVariable;
import cager.jexpr.ast.QuantifierVariables;
import cager.jexpr.ast.ReturnStatement;
import cager.jexpr.ast.Statement;
import cager.jexpr.ast.StatementExpression;
import cager.jexpr.ast.TypedAST;
import cager.jexpr.ast.UnaryExpression;
import cager.jexpr.ast.VariableDeclaration;
import cager.jexpr.ast.WhileStatement;

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
	
	//Does this primary expression contain a methodSelection?
	//Are we inside a method selection statement?
	boolean inMethodSelectionStatement = false;
	
	//The name of the latest identifier or field[this].
	//Its use is in visitMethodSelection.
	String currentIdentifier = "";
	
	String lastPrimaryExpressionType = "";
	
	//For each predicate name, this maps it to its body represented as a String.
	HashMap<String, String> predicateBody = new HashMap<String, String>();
	
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
	HashMap<String, String> fieldWhichPredicate = new HashMap<String, String>();
	
	
	//This gathers the fields in each statement. Not in each block, but in each statement.
	//A statement is a child of a block.
	LinkedList<String> fieldsInStatement = new LinkedList<String>();
	
	//Holds the object propositions of a method, starting with 
	//the object propositions in the pre-condition of the method.
	LinkedList<ObjPropString> Gamma = new LinkedList<ObjPropString>();
	
	//This might need to be a hashMap??
	//It should contain the object propositions of a method, but how do we know which method it is?
	LinkedList<ObjPropString> ObjPropPostCondition = new LinkedList<ObjPropString>();
	
	//For each method, this map tells us which are the preconditions for it.  
	HashMap<String, LinkedList<ObjPropString>> methodPreconditions = 
			new HashMap<String, LinkedList<ObjPropString>>();
		
	//For each method, this map tells us which are the preconditions for it. 
	HashMap<String, LinkedList<ObjPropString>> methodPostconditions = 
			new HashMap<String, LinkedList<ObjPropString>>();
	
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
	
	//TODO
	//Make a FractionString class.
	//For each Pack and Unpack procedure, but mainly Pack,
	//have a list of FractionString 's that
	//gets updated in the same way like methodPreconditions.
	//This will let us make updates to fracOK, etc
	//when a Pack procedure is called.
	
	
	//At the beginning of each block, this is made "".
	//This contains the string of each block.
	String statementContent;
	
	boolean inStatement = false;
	
	//The file where the output is written.
	BufferedWriter out;
	
	//The name of the current predicate that we are parsing.
	String namePredicate;
	
	//The name of the current method that we are parsing in Oprop 
	//and translating in Boogie. 
	String currentMethod;
	
	//The set of fields of this Oprop class.
	Set<String> fields = new TreeSet<String>();
	
	//The arraylist of (field, type) pairs of this Oprop class.
	ArrayList<FieldAndTypePair> fieldsTypes = new ArrayList<FieldAndTypePair>();
	
	//The set of predicates of this Oprop class.
	Set<String> predicates = new TreeSet<String>();
	
	//The set of methods in this Oprop class.
	Set<String> methods = new TreeSet<String>();
	
	//For each method, fieldsInMethod(field_i) is false in the beginning.
	//As we parse the method and we encounter field_i, we set fieldsInMethod to true.
	HashMap<String, Boolean> fieldsInMethod = new HashMap<String, Boolean> (); 
	
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
	
	public HashMap<String, LinkedList<ObjPropString>> getMethodPreconditions() {
		return methodPreconditions;
	}

	public HashMap<String, LinkedList<ObjPropString>> getMethodPostconditions() {
		return methodPostconditions;
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
        out.write("type Ref;\n");
        out.write("type PredicateTypes;\n");
        out.write("type FractionType = [Ref] real;\n");
        out.write("type PackedType = [Ref] bool;\n");
        out.write("var packed: PackedType;\n");
        out.write("var frac: FractionType;\n");
        out.write("const null: Ref;\n");
        out.write("\n");
    	}
    	catch (Exception e) {
    		System.err.println("Error: " + e.getMessage());
    	}
        visitChildren(ast );
    }
    
    public void visitFieldDeclaration(FieldDeclaration ast ) throws ParseException 
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
    	visitChildren(ast );  
    	}
    
    public void visitPredicateDeclaration(PredicateDeclaration ast) throws ParseException
    { 
    	namePredicate = ast.getIdentifier().getName();
    	predicates.add(namePredicate);
    	
    			try {
    			    	predicateBody.put(namePredicate, "");
    			    	
    			    	visitChildren(ast ); 
    				}
    			catch (Exception e) {
    						System.err.println("Error: " + e.getMessage());
    			}

    	
    	
    }
    
    public void visitQuantifierVariable(QuantifierVariable ast ) throws ParseException
  	{ 
    	   	    	
    	visitChildren(ast ); 
    }
    
    
 //Since methods are not children of 
 //Predicate, we might not need namePredicate here
    public void visitMethodDeclaration(MethodDeclaration ast ) throws ParseException
    {    	
    	fieldsInMethod = new HashMap<String, Boolean> (); 
    	Gamma.clear();
    	for (String s : fields) {
    		fieldsInMethod.put(s, new Boolean(false));
    	}
    	
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
    	//Write the constructors to out. The constructor that does not pack to anything
    	//and the ones that pack to predicates.
    	makeConstructors(out);
    	
    	Iterator<Entry<String, String>> j = predicateBody.entrySet().iterator(); 
        while(j.hasNext()){
     	   String currentNamePred = j.next().getKey();
     	   
     		String predBodyUnprocessed = predicateBody.get(currentNamePred);
     		//need to do more processing of the predBody
     		//TODO
     		int i=0;
     		while (predBodyUnprocessed.charAt(i)=='&') i++;
     		String predBody = predBodyUnprocessed.substring(i);
     		
     		try {
			//will need to do something about formal parameters
			out.write("procedure Pack"+currentNamePred+"(this:Ref);\n"); 
			out.write("\t requires (packed"+currentNamePred+"[this] == false) && \n");
			out.write("\t \t(" + predBody + ");\n"); 
			out.write("\n");
			out.write("procedure Unpack"+currentNamePred+"(this:Ref);\n");
			out.write("\t requires packed"+currentNamePred+"[this] &&\n");
			out.write("\t \t (frac"+currentNamePred+"[this] > 0.0);\n");
			out.write("\t ensures ("+predBody+");\n");
			out.write("\n");
     		}
     		
    		catch (Exception e) {
    			System.err.println("Error: " + e.getMessage());
    		}
        }
    	}
    	     		
        //Writing the current procedure out.
    		try {
				out.write("procedure "+ast.getIdentifier().getName()+"(this:Ref");
				
				visitChildren(ast);
				
				out.write(methodParams.get(currentMethod));
				
				
				//Need to automatically detect what is being modified, according to the Boogie manual.
				//We do this after we parse and translate the body of the current method.
				String modifies = "\t modifies ";
							
				Iterator<Entry<String, Boolean>> iter = fieldsInMethod.entrySet().iterator(); 
			       while(iter.hasNext()){
			    	   String key = iter.next().getKey();
			           if (fieldsInMethod.get(key).booleanValue())
			           {
			        	   modifies = modifies.concat(key+ ", ");
			           }		   
			       }
			       modifies = modifies.concat("packed, frac;");
			     out.write(modifies+"\n");
				
				out.write(methodSpec.get(currentMethod));
				
				out.write(methodBody.get(currentMethod));
							
			}
		catch (Exception e) {
			System.err.println("Error: " + e.getMessage());
		}
    		
    		isFirstMethod = false;
    }

    public void visitReturnStatement(ReturnStatement ast ) throws ParseException
    {
        visitChildren(ast );

    }

    public void visitFieldSelection(FieldSelection ast) throws ParseException
    {
    	String fieldName = ast.getIdentifier().name+"[this]";
    	currentIdentifier = fieldName;
    	if (insideObjectProposition) {
			  objectPropString = objectPropString.concat(fieldName);
			  }
    	
		  if ((currentMethod != "") && (inStatement) && 
			  !inArgumentList && !inMethodSelectionStatement) {
			  statementContent = statementContent.concat(fieldName);
			  
		  }
		  
		  if ((currentMethod != "") && (inArgumentList) ) {
			  modifyMethodBody(fieldName + ",");
			  
		  }
		  
    	if (!namePredicate.equals("") && !insideObjectProposition){
    		String currentPredicateBody = predicateBody.get(namePredicate);
			 predicateBody.put(namePredicate, currentPredicateBody.concat(fieldName));
			 			 
    	}
    	
    	
    	visitChildren(ast);
    }
    
    public void visitMethodSelection(MethodSelection ast) throws ParseException
    {
    	String methodName = ast.getIdentifier().name;
    	statementContent= statementContent + "\t call "+ methodName + "(";
    	visitChildren(ast);
    	statementContent = statementContent +currentIdentifier+");\n";
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
        			statementContent.concat(fracString.getStatementFracString(true, currentIdentifier));
        	}
    	}
    	
    	currentEnsuresFrac = 
    			ensuresFrac.get(methodName);
    	if (currentEnsuresFrac != null) {
    		for (int pf = 0; pf < currentEnsuresFrac.size(); pf++) {
                FracString fracString = currentEnsuresFrac.get(pf);
        		statementContent = 
        			statementContent.concat(fracString.getStatementFracString(false, currentIdentifier));
        	}
    	}
    	
    	//TODO
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
            			statementContent.concat(fracString.getStatementFracString(true, currentIdentifier));
            	}
        	}
        	
        	currentEnsuresFrac = bv[classOfMethod].getEnsuresFrac().get(methodName);
        	if (currentEnsuresFrac != null) {
        		for (int pf = 0; pf < currentEnsuresFrac.size(); pf++) {
                    FracString fracString = currentEnsuresFrac.get(pf);
            		statementContent = 
            			statementContent.concat(fracString.getStatementFracString(false, currentIdentifier));
            	}
        	}
    		
    	}
    	
    	
    }

    public void visitBinaryExpression(BinaryExpression ast ) throws ParseException
    {
    	if (ast.op.getId() == JExprConstants.KEYACCESS){
    		PrimaryExpression e1 = (PrimaryExpression)ast.E1;
    		PrimaryExpression e2 = (PrimaryExpression)ast.E2;
    		FieldSelection f = (FieldSelection)(e1.getChildren()[1]);
    		String nameField = f.getIdentifier().name;
    		
    		fieldWhichPredicate.put(nameField, namePredicate);
    		IdentifierExpression i = (IdentifierExpression)(e2.getChildren()[0]);
    		String fieldValue = i.getName();
    		
        	PredicateAndFieldValue pv = new PredicateAndFieldValue(namePredicate, fieldValue);
        	//pv.println();
        	quantifiedVars.put(pv, nameField);
        	
    		return;
    	}
    	
    	if (ast.op.getId() == JExprConstants.ASSIGN){
    		helperBinaryExpression(ast , ":=");
    		return;
    	}
    	    		
    	helperBinaryExpression(ast , ast.op.getName());
    	return;
    		
    }
    
    public void helperBinaryExpression(BinaryExpression ast, String operatorSymbol) throws ParseException
    {
    	AST[] children = ast.getChildren();
		
		  children[0].accept(this );
		  if (namePredicate.equals("")) {
		  try{
			  if (currentMethod != "") {
				  statementContent = statementContent.concat(operatorSymbol);
			  }
			  else {
				  if (insideObjectProposition) {
					  objectPropString = objectPropString.concat(operatorSymbol);
				  }
				  else {
				  out.write(operatorSymbol);
				  }
			  }
		  }
	      catch (Exception e) {
	    		System.err.println("Error: " + e.getMessage());
	      }
		  }
		  else {
			 String currentPredicateBody = predicateBody.get(namePredicate);
			 predicateBody.put(namePredicate, currentPredicateBody.concat(operatorSymbol));
			 
		  }
		  
		  
		  children[1].accept(this );
    }
        
    public void visitLiteralExpression(LiteralExpression ast )
  		  throws ParseException
  		  { 
    	String astvalue = ast.value.toString();
    	if (insideObjectProposition) {
			  objectPropString = objectPropString.concat(astvalue);
		  }
    	
		  if ((currentMethod != "") && (inStatement) && !inArgumentList ) {
			  statementContent = statementContent.concat(astvalue);
		  }
		  
		  if ((currentMethod != "") && (inArgumentList) ) {
			  modifyMethodBody(astvalue + ",");
		  }
		  
    	if (!namePredicate.equals("") && !insideObjectProposition){
    		String currentPredicateBody = predicateBody.get(namePredicate);
			 predicateBody.put(namePredicate, currentPredicateBody.concat(astvalue));
    	}
    	
    	visitChildren(ast); }
    
    public void visitObjectProposition(ObjectProposition ast ) throws ParseException
    {
    	insideObjectProposition = true;
    	
    	FracString fracString = new FracString();
    	TypedAST object  = ast.getObject();
    	object.accept(this);
    	String objectString = objectPropString;
    	objectPropString = "";
    	
    	Expression frac = ast.getFraction();
    	
    	frac.accept(this);

    	String fracInObjProp = objectPropString;
    	objectPropString = "";
    	
    	Expression predDecl = ast.getPredicateDeclaration();
    	
    	AST[] childrenPredDecl = predDecl.getChildren();
    	
    	childrenPredDecl[0].accept(this);

    	String identifierPredDecl = objectPropString;
    	String predName = identifierPredDecl;

    	objectPropString = "";
    	childrenPredDecl[1].accept(this);
    	//this is ArgumentList but for this example 
    	//there are no arguments so we set it to empty with new
    	//currentMethod
    	ObjPropString objProp = new ObjPropString(objectString, fracInObjProp, 
    			identifierPredDecl, new LinkedList<String>());
    	    	
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
    				
    	}
    	
    	// The minBound is the same if the fieldName is null or not. 
    	// We do not need to set this inside the if branches.
    	fracString.setMinBound(0);
    	
    	if (currentMethod!="") {
    		modifyMethodSpec(bodyMethodOrPredicate);
 
    		if (insidePrecondition) {
    			Gamma.add(objProp);
    			modifyMethodPreconditions(objProp);
    			modifyRequiresFrac(fracString);
    		} 
    		else {
    			ObjPropPostCondition.add(objProp);
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
    	}
    	insideObjectProposition = false;
    }

   
    public void visitUnaryExpression(UnaryExpression ast ) throws ParseException
    {
        visitChildren(ast );
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
    	lastPrimaryExpressionType = ast.getType().toString();
        visitChildren(ast);
    }
    
    public void visitFormalParameters(FormalParameters ast) throws ParseException 
  		  { 
    	   	
    	visitChildren(ast ); 
    	
    	
    	if (currentMethod != "") {
    		modifyMethodParams(")\n"); 
        	}
    
    	}
    
    public void visitFormalParameter(FormalParameter ast ) throws ParseException
    {
    	
    	
        visitChildren(ast);

    }
    
    public void visitAllocationExpression(AllocationExpression ast ) throws ParseException
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
    
    public void visitDeclarationStatement(DeclarationStatement ast ) 
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
    
    public void visitKeywordExpression(KeywordExpression ast ) throws ParseException
    { 
    	if (insideObjectProposition) {
			  objectPropString = objectPropString.concat(ast.getValue() + "");
		  }
		  else {
			  try {
				  //this is where the error is
				  //seems like keyword is only this
			  //out.write(ast.getValue() + "XCXCX");
			  }
			  catch (Exception e) {
		    		System.err.println("Error: " + e.getMessage());
		      }
			  
		  }
    	visitChildren(ast );

    }
 
    public void visitIdentifierExpression(IdentifierExpression ast) throws ParseException
    {    	
    	currentIdentifier = ast.name;
    try {
      
       String identifierName = currentIdentifier;
       fieldsInStatement.add(identifierName);
       
       if (currentMethod != "") {
    	   if (fieldsInMethod.get(identifierName) != null)
    	   {
    		   fieldsInMethod.put(identifierName, new Boolean(true));
    	   }
       }
       
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
    		   /*
    		   if ((currentMethod != "")  && (fieldsInMethod.get(identifierName) != null)) {
    			   //we must be inside a statement
    			   statementContent = statementContent.concat(identifierName+"[this]");
    	       }
    	       */
    		   
    		   if ((currentMethod != "") && (inArgumentList)) {
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
    	   String currentPredicateBody = predicateBody.get(namePredicate);
    	 
    	   if (!(fieldName == null)) {
    	   
    	   if (insideObjectProposition) {
				  objectPropString = objectPropString.concat(identifierName);
			  }
     	   if (!insideObjectProposition) {
			 predicateBody.put(namePredicate, currentPredicateBody.concat(fieldName+ "[this]"));
     	   }
    	   }
    	   else {
    		   if (insideObjectProposition) {
 				  objectPropString = objectPropString.concat(identifierName);
 			  }
     	   else {
     	   predicateBody.put(namePredicate, currentPredicateBody.concat(identifierName));
     	   }
    	   }
       }
    }
      catch (Exception e) {
    		System.err.println("Error: " + e.getMessage());
      };
      
    	visitChildren(ast);
    }
    
    public void visitIfStatement(IfStatement ast ) throws ParseException
    {
        visitChildren(ast );

    }
    
    public void visitStatementExpression(StatementExpression ast )
  		  throws ParseException
  		  { inStatement = true;
    		visitChildren(ast );
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
    
    public void visitMethodSpecVariable(MethodSpecVariable ast ) 
  		  throws ParseException 
  		  {
    	visitChildren(ast ); 
    	}
    
    public void visitMethodSpecVariables(MethodSpecVariables ast )
  		  throws ParseException 
  		  { 
    	visitChildren(ast ); 
    	}
    
    public void visitMethodSpecExpression(MethodSpecExpression ast ) 
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
    	
    	//need to createObjPropString and add it to Gamma
    	if (postcondition != null) {
    	modifyMethodSpec("\t ensures ");
    	insidePrecondition = false;
    	postcondition.accept(this );
    	modifyMethodSpec(");\n");
    	}
    	
    	}
    
    public void visitBlock(Block ast ) 
  		  throws ParseException 
  		  { 
    	
    	
    	modifyMethodBody("{\n");
    	
    	AST[] children = ast.getChildren();
    	for (int i = 0; i < children.length; i++) {
    		
    		fieldsInStatement.clear();
    		statementContent = "";
    	    children[i].accept(this);
    	    //write what we are packing or unpacking 
    	    //before writing the statement
            for (int fi = 0; fi < fieldsInStatement.size(); fi++) {
                String fieldName = fieldsInStatement.get(fi);
                String predicateOfField = fieldWhichPredicate.get(fieldName); 
                if (predicateOfField != null ) {
                	ObjPropString temp = new ObjPropString("this", "k", 
                			     predicateOfField, new LinkedList<String>());
                	if (Gamma.contains(temp)) {
                		modifyMethodBody("\t call Unpack"+predicateOfField+"(this);\n");
                		modifyMethodBody("\t packed"+predicateOfField+"[this]:=false;\n");
                		Gamma.remove(temp);
                	}
                }
                	
            }
    	    
    	    modifyMethodBody(statementContent);
    	      
    	  }
    	//pack the last object proposition
    	//the one in the postcondition
    	for (int i = 0; i < ObjPropPostCondition.size(); i++) {
    		//need to check if this object proposition is also in Gamma
    		//if it's not, then might throw an error or send a message
    		
    		ObjPropString o = ObjPropPostCondition.get(i);
    		String obj = o.getObject();
    		String name = o.getName();
    		
    		
    		 //need to take care of the OK, ok uppercase issue
    		 modifyMethodBody("\t call Pack"+name+"("+obj+");\n");
    		 modifyMethodBody("\t packed"+name+"["+obj+"]:=true;\n");
    		
    	}
    	
    	modifyMethodBody("}\n ");
     	
  		  }
    
    
    public void visitArgumentList(ArgumentList ast )
  		  throws ParseException 
  		  {  
    	inArgumentList = true;

    	visitChildren(ast); 
    	
    	inArgumentList = false;
    	}
    
    
    public void modifyMethodBody(String s) {
    	String currentMethodBody = methodBody.get(currentMethod);
		currentMethodBody = currentMethodBody.concat(s);
		methodBody.put(currentMethod, currentMethodBody);
    }
    
    public void modifyPredicateBody(String s) {
    	String currentPredicateBody = predicateBody.get(namePredicate);
    	currentPredicateBody = currentPredicateBody.concat(s);
    	predicateBody.put(namePredicate, currentPredicateBody);
    }
        
    public void modifyMethodSpec(String s) {
    	String currentMethodSpec = methodSpec.get(currentMethod);
		currentMethodSpec = currentMethodSpec.concat(s);
		methodSpec.put(currentMethod, currentMethodSpec);
    }
    
    public void modifyMethodParams(String s) {
    	String currentMethodParams = methodParams.get(currentMethod);
		currentMethodParams = currentMethodParams.concat(s);
		methodParams.put(currentMethod, currentMethodParams);
    }
        
    public void modifyMethodPreconditions(ObjPropString s) {
    	LinkedList<ObjPropString> currentMethodPreconditions = 
    			methodPreconditions.get(currentMethod);
    	if (currentMethodPreconditions == null) {
    		currentMethodPreconditions = new LinkedList<ObjPropString>();
    	}
    	currentMethodPreconditions.add(s);
    	methodPreconditions.put(currentMethod, currentMethodPreconditions);
    }
    
    public void modifyMethodPostconditions(ObjPropString s) {
    	LinkedList<ObjPropString> currentMethodPostconditions = 
    			methodPostconditions.get(currentMethod);
    	if (currentMethodPostconditions == null) {
    		currentMethodPostconditions = new LinkedList<ObjPropString>();
    	}
    	currentMethodPostconditions.add(s);
		methodPostconditions.put(currentMethod, currentMethodPostconditions);
    }
    
    //adds a FracString to the requiresFrac map for the currentMethod
    public void modifyRequiresFrac(FracString s) {
    	LinkedList<FracString> currentRequiresFrac = 
    			requiresFrac.get(currentMethod);
    	if (currentRequiresFrac == null) {
    		currentRequiresFrac = new LinkedList<FracString>();
    	}
    	currentRequiresFrac.add(s);
    	requiresFrac.put(currentMethod, currentRequiresFrac);
    }
    
    //adds a FracString to the ensuresFrac map for the currentMethod
    public void modifyEnsuresFrac(FracString s) {
    	LinkedList<FracString> currentEnsuresFrac = 
    			ensuresFrac.get(currentMethod);
    	if (currentEnsuresFrac == null) {
    		currentEnsuresFrac = new LinkedList<FracString>();
    	}
    	currentEnsuresFrac.add(s);
    	ensuresFrac.put(currentMethod, currentEnsuresFrac);
    }
    
    public void modifyPredicateFrac(FracString s) {
    	LinkedList<FracString> currentPredicateFrac = 
    			predicateFrac.get(namePredicate);
    	if (currentPredicateFrac == null) {
    		currentPredicateFrac = new LinkedList<FracString>();
    	}
    	currentPredicateFrac.add(s);
    	predicateFrac.put(namePredicate, currentPredicateFrac);    	
    }
    
    public void makeConstructors(BufferedWriter out) {
    	
    	try {
    		for (String p : predicates) {
        	//write constructors for each predicate
            out.write("procedure Construct" + className + p + "P(");
            for (FieldAndTypePair s : fieldsTypes) {
            	out.write(s.getName() + "1: "+ s.getType() + ", ");
        	}
            out.write("this: Ref);\n");
            out.write("\t ensures ");
            for (FieldAndTypePair s : fieldsTypes) {
            	out.write("(" + s.getName() + "[this] == "+ s.getName() + "1) &&\n \t \t ");
        	}
            out.write("(packed"+p+"[this]) && \n \t \t ");
            out.write("(frac"+p+"[this] == 1.0);\n \n");
        	}
    		
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
    
    public static void printMap(Map mp) {
        Iterator it = mp.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pairs = (Map.Entry)it.next();
            System.out.println(pairs.getKey() + " = " + pairs.getValue());
            it.remove(); // avoids a ConcurrentModificationException
        }
    }
    
    
}
