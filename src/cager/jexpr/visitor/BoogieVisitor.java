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
	HashMap<PredicateAndFieldValue, String> quantifiedVars= new HashMap<PredicateAndFieldValue, String> ();
		
    public void visitCompilationUnits(CompilationUnits ast, BufferedWriter out, String namePredicate) throws ParseException
    {
        visitChildren(ast, out, namePredicate);
    }
    
    public void visitCompilationUnit(CompilationUnit ast, BufferedWriter out, String namePredicate) throws ParseException
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
        visitChildren(ast, out, namePredicate);
    }
    
    public void visitFieldDeclaration(FieldDeclaration ast, BufferedWriter out, String namePredicate) throws ParseException 
    { 
    	try {
    		out.write("var "+ ast.getName()+" [Ref]"+ast.getType()+";\n");
    	}
    	catch (Exception e) {
    		System.err.println("Error: " + e.getMessage());
    	}
    	visitChildren(ast, out, namePredicate);  
    	}
    
    public void visitPredicateDeclaration(PredicateDeclaration ast, BufferedWriter out, String namePredicate1) throws ParseException
    { 
    	String namePredicate = ast.getIdentifier().getName().toLowerCase();
    	try {
    		out.write("const unique "+ namePredicate +"P: PredicateTypes;\n"); 
    	}
    	catch (Exception e) {
    		System.err.println("Error: " + e.getMessage());
    	}
    	
    	visitChildren(ast, out, namePredicate); 
    }
    
    public void visitQuantifierVariable(QuantifierVariable ast, BufferedWriter out, String namePredicate) throws ParseException
  	{ 
    	   	    	
    	visitChildren(ast, out, namePredicate); 
    }
    
    

    public void visitMethodDeclaration(MethodDeclaration ast, BufferedWriter out, String namePredicate) throws ParseException
    {
    	visitChildren(ast, out, namePredicate);
    }

    public void visitReturnStatement(ReturnStatement ast, BufferedWriter out, String namePredicate) throws ParseException
    {
        visitChildren(ast, out, namePredicate);

    }

    public void visitFieldSelection(FieldSelection ast, BufferedWriter out, String namePredicate) throws ParseException
    {
    	visitChildren(ast, out, namePredicate);
    }

    public void visitBinaryExpression(BinaryExpression ast, BufferedWriter out, String namePredicate) throws ParseException
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

        	try{
        	 out.write(" true ");
        	}
        	 catch (Exception e) {
		    		System.err.println("Error: " + e.getMessage());
		      }
    		return;
    	}
    	
    	if (ast.op.getId() == JExprConstants.ASSIGN){
    		helperBinaryExpression(ast, out, namePredicate, ":=");
    	}
    	else {
    		helperBinaryExpression(ast, out, namePredicate, ast.op.getName());
    }
    }
    
    public void helperBinaryExpression(BinaryExpression ast, BufferedWriter out, 
    		     String namePredicate, String operatorSymbol) throws ParseException
    {
    	AST[] children = ast.getChildren();
		
		  children[0].accept(this, out, namePredicate);
		  try{
		  out.write(operatorSymbol);
		  }
	      catch (Exception e) {
	    		System.err.println("Error: " + e.getMessage());
	      }
		  
		  children[1].accept(this, out, namePredicate);
		  
		  return;
    }
        
    public void visitLiteralExpression(LiteralExpression ast, BufferedWriter out, String namePredicate)
  		  throws ParseException
  		  { 
    	 try{
			  out.write(ast.value.toString());
			  }
		      catch (Exception e) {
		    		System.err.println("Error: " + e.getMessage());
		      }
    	
    	visitChildren(ast, out, namePredicate); }
    
    public void visitObjectProposition(ObjectProposition ast, BufferedWriter out, String namePredicate) throws ParseException
    {
        visitChildren(ast, out, namePredicate);
    }

   
    public void visitUnaryExpression(UnaryExpression ast, BufferedWriter out, String namePredicate) throws ParseException
    {
        visitChildren(ast, out, namePredicate);
    }

    public void visitPrimaryExpression(PrimaryExpression ast, BufferedWriter out, String namePredicate) throws ParseException
    {
        visitChildren(ast, out, namePredicate);
    }
    
    public void visitFormalParameter(FormalParameter ast, BufferedWriter out, String namePredicate) throws ParseException
    {
        visitChildren(ast, out, namePredicate);

    }
    
    public void visitLocalVariableDeclaration(LocalVariableDeclaration ast, BufferedWriter out, String namePredicate) throws ParseException
    {
    	visitChildren(ast, out, namePredicate);
    }
    
    public void visitKeywordExpression(KeywordExpression ast, BufferedWriter out, String namePredicate) throws ParseException
    { 
    	visitChildren(ast, out, namePredicate);

    }
 
    public void visitIdentifierExpression(IdentifierExpression ast, BufferedWriter out, String namePredicate) throws ParseException
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
       
       if (!(fieldName.equals("")))
    	   try{
 			  out.write(fieldName+ "[this]");
 			  }
 		      catch (Exception e) {
 		    		System.err.println("Error: " + e.getMessage());
 		      }
       else {
    	   try{
  			  out.write(identifierName);
  			  }
  		      catch (Exception e) {
  		    		System.err.println("Error: " + e.getMessage());
  		      };
       }
    	visitChildren(ast, out, namePredicate);
    }
    
    public void visitIfStatement(IfStatement ast, BufferedWriter out, String namePredicate) throws ParseException
    {
        visitChildren(ast, out, namePredicate);

    }
    
    public void visitStatementExpression(StatementExpression ast, BufferedWriter out, String namePredicate)
  		  throws ParseException
  		  { 
    		visitChildren(ast, out, namePredicate);
    		try {
        		out.write(";\n");
        	}
        	catch (Exception e) {
        		System.err.println("Error: " + e.getMessage());
        	}
  		  }
    
   


}
