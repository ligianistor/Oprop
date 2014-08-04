package cager.jexpr.visitor;

import java.io.BufferedWriter;
import java.util.HashMap;
import java.util.Iterator;
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
import cager.jexpr.ast.BinaryExpression;
import cager.jexpr.ast.Block;
import cager.jexpr.ast.CastExpression;
import cager.jexpr.ast.ClassDeclaration;
import cager.jexpr.ast.CompilationUnit;
import cager.jexpr.ast.CompilationUnits;
import cager.jexpr.ast.ConstructorDeclaration;
import cager.jexpr.ast.DeclarationStatement;
import cager.jexpr.ast.Expression;
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
	HashMap<String, String> predicateBody = new HashMap<String, String>();
	HashMap<String, String> methodBody = new HashMap<String, String>();
	HashMap<String, String> methodSpec = new HashMap<String, String>();
	HashMap<String, String> methodParams = new HashMap<String, String>();
	BufferedWriter out;
	String namePredicate;
	String currentMethod;
	Set<String> fields = new TreeSet<String>();
	HashMap<String, Boolean> fieldsInMethod = new HashMap<String, Boolean> (); 
	boolean insideObjectProposition;
	String objectPropString;
	
	public BoogieVisitor(BufferedWriter boogieFile, String namePredicate_) {
		out = boogieFile;
		namePredicate = namePredicate_;
		currentMethod = ""; 
		insideObjectProposition = false;
		objectPropString = "";
	}
		
    public void visitCompilationUnits(CompilationUnits ast) throws ParseException
    {
        visitChildren(ast);
    }
    
    public void visitCompilationUnit(CompilationUnit ast) throws ParseException
    {
    	try {
        out.write("type Ref;\n");
        out.write("type PredicateTypes;\n");
        out.write("type FractionType = [Ref, PredicateTypes] int;\n");
        out.write("type PackedType = [Ref, PredicateTypes] bool;\n");
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
    	
    	try {
    		out.write("var "+ fieldName +": [Ref]"+ast.getType()+";\n");
    	}
    	catch (Exception e) {
    		System.err.println("Error: " + e.getMessage());
    	}
    	visitChildren(ast );  
    	}
    
    public void visitPredicateDeclaration(PredicateDeclaration ast) throws ParseException
    { 
    	namePredicate = ast.getIdentifier().getName().toLowerCase();
    	
    			try {
    					out.write("const unique "+ namePredicate +"P: PredicateTypes;\n"); 
    					out.write("\n");
    				}
    			catch (Exception e) {
    						System.err.println("Error: " + e.getMessage());
    			}
    	predicateBody.put(namePredicate, "");
    	
    	
    	visitChildren(ast ); 
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
    	for (String s : fields) {
    		fieldsInMethod.put(s, new Boolean(false));
    	}
    	
    	namePredicate = "";
    	String methodName = ast.getIdentifier().name;
    	methodBody.put(methodName, "");
    	methodSpec.put(methodName, "");
    	methodParams.put(methodName, "");
    	currentMethod = methodName;
    	
    	Iterator<Entry<String, String>> j = predicateBody.entrySet().iterator(); 
        while(j.hasNext()){
     	   String currentNamePred = j.next().getKey();
     	   
     		String predBodyUnprocessed = predicateBody.get(currentNamePred);
     		//need to do more processing of the predBody
     		int i=0;
     		while (predBodyUnprocessed.charAt(i)=='&') i++;
     		String predBody = predBodyUnprocessed.substring(i);
     		
    		try {
    			
    			//will need to do something about formal parameters
				out.write("procedure Pack"+currentNamePred.toUpperCase()+"(this:Ref);\n"); 
				out.write("requires ("+predBody+");\n"); 
				out.write("\n");
				out.write("procedure Unpack"+currentNamePred.toUpperCase()+"(this:Ref);\n");
				out.write("requires packed[this, "+currentNamePred+"P];\n");
				out.write("ensures ("+predBody+");\n");
				out.write("\n");
				out.write("procedure "+ast.getIdentifier().getName()+"(this:Ref");
				
				visitChildren(ast);
				
				out.write(methodParams.get(currentMethod));
				
				//do the modifies thing
				String modifies = "modifies ";
							
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
        }
        
         
    	
    	
    }

    public void visitReturnStatement(ReturnStatement ast ) throws ParseException
    {
        visitChildren(ast );

    }

    public void visitFieldSelection(FieldSelection ast ) throws ParseException
    {
    	visitChildren(ast );
    }

    public void visitBinaryExpression(BinaryExpression ast ) throws ParseException
    {
    	if (ast.op.getId() == JExprConstants.KEYACCESS){
    		PrimaryExpression e1 = (PrimaryExpression)ast.E1;
    		PrimaryExpression e2 = (PrimaryExpression)ast.E2;
    		FieldSelection f = (FieldSelection)(e1.getChildren()[1]);
    		String nameField = f.getIdentifier().name;
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
				  modifyMethodBody(operatorSymbol);
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
    	if (namePredicate.equals("")){
    	 try{
			  if (currentMethod != "") {
				  modifyMethodBody(astvalue);
			  }
			  else {
				  if (insideObjectProposition) {
					  objectPropString = objectPropString.concat(astvalue);
				  }
				  else {
					  out.write(astvalue);
				  }
				  
			  }
			  
			  }
		      catch (Exception e) {
		    		System.err.println("Error: " + e.getMessage());
		      }
    	}
    	else
    	{
    		String currentPredicateBody = predicateBody.get(namePredicate);
			 predicateBody.put(namePredicate, currentPredicateBody.concat(astvalue));
    	}
    	
    	visitChildren(ast ); }
    
    public void visitObjectProposition(ObjectProposition ast ) throws ParseException
    {
    	insideObjectProposition = true;
    	
    	TypedAST object  = ast.getObject();
    	
    	object.accept(this);
    	String objectString = objectPropString;

    	objectPropString = "";
    	
    	
    	Expression frac = ast.getFraction();
    	
    	frac.accept(this);

    	String fracString = objectPropString;

    	objectPropString = "";
    	
    	Expression predDecl = ast.getPredicateDeclaration();
    	
    	predDecl.accept(this);

    	String predDeclString = objectPropString;

    	objectPropString = "";
    	String predName = predDeclString.toLowerCase();
    	

    	modifyMethodSpec("packed[" + objectString+","+ 
    			         predName +"P] && (frac["+ objectString+ ","+ predName+"] > 0);\n");
    	insideObjectProposition = false;
    }

   
    public void visitUnaryExpression(UnaryExpression ast ) throws ParseException
    {
        visitChildren(ast );
    }

    public void visitPrimaryExpression(PrimaryExpression ast ) throws ParseException
    {
        visitChildren(ast );
    }
    
    public void visitFormalParameters(FormalParameters ast ) throws ParseException 
  		  { 
    	   	
    	visitChildren(ast ); 
    	
    	if (currentMethod != "") {
    		modifyMethodParams(")\n"); 
        	}
    
    	}
    
    public void visitFormalParameter(FormalParameter ast ) throws ParseException
    {
        visitChildren(ast );

    }
    
    public void visitLocalVariableDeclaration(LocalVariableDeclaration ast ) throws ParseException
    {
    	visitChildren(ast );
    }
    
    public void visitKeywordExpression(KeywordExpression ast ) throws ParseException
    { 
    	if (insideObjectProposition) {
			  objectPropString = objectPropString.concat(ast.getValue() + "");
		  }
		  else {
			  try {
			  out.write(ast.getValue() + "");
			  }
			  catch (Exception e) {
		    		System.err.println("Error: " + e.getMessage());
		      }
			  
		  }
    	visitChildren(ast );

    }
 
    public void visitIdentifierExpression(IdentifierExpression ast ) throws ParseException
    {    	
       String identifierName = ast.name;
       
       if (currentMethod != "") {
    	   if (fieldsInMethod.get(identifierName) != null)
    	   {
    		   fieldsInMethod.put(identifierName, new Boolean(true));
    	   }
       }
       
       PredicateAndFieldValue pv = new PredicateAndFieldValue(namePredicate, identifierName);

       String fieldName = "";
       Iterator<Entry<PredicateAndFieldValue, String>> j = quantifiedVars.entrySet().iterator(); 
       while(j.hasNext()){
    	   PredicateAndFieldValue key = j.next().getKey();
           if (key.equals(pv))
           {
        	   fieldName = quantifiedVars.get(key);
           }
       }
       String currentPredicateBody = predicateBody.get(namePredicate);
       if (!(fieldName.equals(""))) {
    	   if (namePredicate.equals("")){
    	   try{
 			  modifyMethodBody(fieldName+ "[this]");
 			  }
 		      catch (Exception e) {
 		    		System.err.println("Error: " + e.getMessage());
 		      }
    	   }
    	   else {
  			 predicateBody.put(namePredicate, currentPredicateBody.concat(fieldName+ "[this]"));
    	   }
    	   
       }
       else {
    	   if (namePredicate.equals("")){
    	   try {
    		   if ((currentMethod != "")  && (fieldsInMethod.get(identifierName) != null)){
    			   modifyMethodBody(identifierName+"[this]");
    	       }
    		   else {
    			   
    			   //modify object proposition parts
    			   if (insideObjectProposition) {
    					  objectPropString = objectPropString.concat(identifierName);
    				  }

    		   }
    		   
  			  }
  		      catch (Exception e) {
  		    		System.err.println("Error: " + e.getMessage());
  		      };
       }
       else {
    	   predicateBody.put(namePredicate, currentPredicateBody.concat(identifierName));
       }
       }
    	visitChildren(ast );
    }
    
    public void visitIfStatement(IfStatement ast ) throws ParseException
    {
        visitChildren(ast );

    }
    
    public void visitStatementExpression(StatementExpression ast )
  		  throws ParseException
  		  { 
    		visitChildren(ast );
    		try {
    			if (currentMethod != "") {
    				modifyMethodBody(";\n");
    			}
  			  else {
  				  out.write(";\n");
  			  }
        	}
        	catch (Exception e) {
        		System.err.println("Error: " + e.getMessage());
        	}
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

    	modifyMethodSpec("requires ");

    	precondition.accept(this );
    	modifyMethodSpec("ensures ");

    	postcondition.accept(this );
    	}
    
    public void visitBlock(Block ast ) 
  		  throws ParseException 
  		  { 
    	modifyMethodBody("{\n");
    	visitChildren(ast );
    	modifyMethodBody("}\n ");
     	
  		  }
    
    public void modifyMethodBody(String s) {
    	String currentMethodBody = methodBody.get(currentMethod);
		currentMethodBody = currentMethodBody.concat(s);
		methodBody.put(currentMethod, currentMethodBody);
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
    
    
}
