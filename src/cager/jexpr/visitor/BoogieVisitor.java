package cager.jexpr.visitor;

import java.io.BufferedWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

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
import cager.jexpr.ast.UnaryExpression;
import cager.jexpr.ast.VariableDeclaration;
import cager.jexpr.ast.WhileStatement;

/*
 * This class visits the AST to generate the Boogie code.
 */
public class BoogieVisitor extends NullVisitor {
	HashMap<PredicateAndFieldValue, String> quantifiedVars = new HashMap<PredicateAndFieldValue, String> ();
	HashMap<String, String> predicateBody = new HashMap<String, String>();
	BufferedWriter out;
	
	public BoogieVisitor(BufferedWriter boogieFile) {
		out = boogieFile;
	}
		
    public void visitCompilationUnits(CompilationUnits ast, String namePredicate) throws ParseException
    {
        visitChildren(ast, namePredicate);
    }
    
    public void visitCompilationUnit(CompilationUnit ast, String namePredicate) throws ParseException
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
        visitChildren(ast, namePredicate);
    }
    
    public void visitFieldDeclaration(FieldDeclaration ast, String namePredicate) throws ParseException 
    { 
    	try {
    		out.write("var "+ ast.getName()+" [Ref]"+ast.getType()+";\n");
    	}
    	catch (Exception e) {
    		System.err.println("Error: " + e.getMessage());
    	}
    	visitChildren(ast, namePredicate);  
    	}
    
    public void visitPredicateDeclaration(PredicateDeclaration ast, String namePredicate1) throws ParseException
    { 
    	String namePredicate = ast.getIdentifier().getName().toLowerCase();
    	
    			try {
    					out.write("const unique "+ namePredicate +"P: PredicateTypes;\n"); 
    					out.write("\n");
    				}
    			catch (Exception e) {
    						System.err.println("Error: " + e.getMessage());
    			}
    	predicateBody.put(namePredicate, "");
    	
    	
    	visitChildren(ast, namePredicate); 
    }
    
    public void visitQuantifierVariable(QuantifierVariable ast, String namePredicate) throws ParseException
  	{ 
    	   	    	
    	visitChildren(ast, namePredicate); 
    }
    
    
 //Since methods are not children of 
 //Predicate, we might not need namePredicate here
    public void visitMethodDeclaration(MethodDeclaration ast, String namePredicate) throws ParseException
    {
    	
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
							
			}
		catch (Exception e) {
			System.err.println("Error: " + e.getMessage());
		}
        }
        
         
    	visitChildren(ast, namePredicate);
    }

    public void visitReturnStatement(ReturnStatement ast, String namePredicate) throws ParseException
    {
        visitChildren(ast, namePredicate);

    }

    public void visitFieldSelection(FieldSelection ast, String namePredicate) throws ParseException
    {
    	visitChildren(ast, namePredicate);
    }

    public void visitBinaryExpression(BinaryExpression ast, String namePredicate) throws ParseException
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
    		helperBinaryExpression(ast, namePredicate, ":=");
    		return;
    	}
    	    		
    			helperBinaryExpression(ast, namePredicate, ast.op.getName());
    			return;
    		
    }
    
    public void helperBinaryExpression(BinaryExpression ast, 
    		     String namePredicate, String operatorSymbol) throws ParseException
    {
    	AST[] children = ast.getChildren();
		
		  children[0].accept(this, namePredicate);
		  if (namePredicate.equals("")){
		  try{
		  out.write(operatorSymbol);
		  }
	      catch (Exception e) {
	    		System.err.println("Error: " + e.getMessage());
	      }
		  }
		  else {
			 String currentPredicateBody = predicateBody.get(namePredicate);
			 predicateBody.put(namePredicate, currentPredicateBody.concat(operatorSymbol));
			 
		  }
		  
		  
		  children[1].accept(this, namePredicate);
    }
        
    public void visitLiteralExpression(LiteralExpression ast, String namePredicate)
  		  throws ParseException
  		  { 
    	String astvalue = ast.value.toString();
    	if (namePredicate.equals("")){
    	 try{
			  out.write(astvalue);
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
    	
    	visitChildren(ast, namePredicate); }
    
    public void visitObjectProposition(ObjectProposition ast, String namePredicate) throws ParseException
    {
   
        visitChildren(ast, namePredicate);
        try {
    		out.write(";\n");
    	}
    	catch (Exception e) {
    		System.err.println("Error: " + e.getMessage());
    	}
    }

   
    public void visitUnaryExpression(UnaryExpression ast, String namePredicate) throws ParseException
    {
        visitChildren(ast, namePredicate);
    }

    public void visitPrimaryExpression(PrimaryExpression ast, String namePredicate) throws ParseException
    {
        visitChildren(ast, namePredicate);
    }
    
    public void visitFormalParameters(FormalParameters ast, String namePredicate) throws ParseException 
  		  { 
    	visitChildren(ast, namePredicate); 
    	 try{
			  out.write(")\n");
			  }
		 catch (Exception e) {
		    		System.err.println("Error: " + e.getMessage());
		      }
    	}
    
    public void visitFormalParameter(FormalParameter ast, String namePredicate) throws ParseException
    {
        visitChildren(ast, namePredicate);

    }
    
    public void visitLocalVariableDeclaration(LocalVariableDeclaration ast, String namePredicate) throws ParseException
    {
    	visitChildren(ast, namePredicate);
    }
    
    public void visitKeywordExpression(KeywordExpression ast, String namePredicate) throws ParseException
    { 
    	visitChildren(ast, namePredicate);

    }
 
    public void visitIdentifierExpression(IdentifierExpression ast, String namePredicate) throws ParseException
    {
       String identifierName = ast.name;
       
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
 			  out.write(fieldName+ "[this]");
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
  			  out.write(identifierName);
  			  }
  		      catch (Exception e) {
  		    		System.err.println("Error: " + e.getMessage());
  		      };
       }
       else {
    	   predicateBody.put(namePredicate, currentPredicateBody.concat(identifierName));
       }
       }
    	visitChildren(ast, namePredicate);
    }
    
    public void visitIfStatement(IfStatement ast, String namePredicate) throws ParseException
    {
        visitChildren(ast, namePredicate);

    }
    
    public void visitStatementExpression(StatementExpression ast, String namePredicate)
  		  throws ParseException
  		  { 
    		visitChildren(ast, namePredicate);
    		try {
        		out.write(";\n");
        	}
        	catch (Exception e) {
        		System.err.println("Error: " + e.getMessage());
        	}
  		  }
    
    public void visitMethodSpecVariable(MethodSpecVariable ast, String namePredicate) 
  		  throws ParseException 
  		  {
    	visitChildren(ast, namePredicate); 
    	}
    
    public void visitMethodSpecVariables(MethodSpecVariables ast, String namePredicate)
  		  throws ParseException 
  		  { 
    	visitChildren(ast, namePredicate); 
    	}
    
    public void visitMethodSpecExpression(MethodSpecExpression ast, String namePredicate) 
  		  throws ParseException 
  		  { 
    	Expression precondition = ast.getPrecondition();
    	Expression postcondition = ast.getPostcondition();
    	
    	try {
    		out.write("requires ");
    	}
    	catch (Exception e) {
    		System.err.println("Error: " + e.getMessage());
    	}
    	precondition.accept(this, namePredicate);
    	
    	try {
    		out.write("ensures ");
    	}
    	catch (Exception e) {
    		System.err.println("Error: " + e.getMessage());
    	}
    	postcondition.accept(this, namePredicate);
    	}
    
    public void visitBlock(Block ast,  String namePredicate) 
  		  throws ParseException 
  		  { 
    	
    	try {
    		out.write("{\n");
    		visitChildren(ast, namePredicate);
    		out.write("}\n ");
    	}
    	catch (Exception e) {
    		System.err.println("Error: " + e.getMessage());
    	}
    	
    	
  		  }
    
    
}
