package cager.jexpr.visitor;

import java.io.BufferedWriter;
import java.util.HashMap;
import java.util.Map;

import org.apache.bcel.generic.Type;

import cager.jexpr.JExprConstants;
import cager.jexpr.OperatorTypeInfo;
import cager.jexpr.ParseException;
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
import cager.jexpr.ast.UnaryExpression;
import cager.jexpr.ast.VariableDeclaration;
import cager.jexpr.ast.WhileStatement;

/*
 * This class visits the AST to generate the Boogie code.
 */
public class BoogieVisitor extends NullVisitor {
	HashMap<String, HashMap<String, String>> quantifiedVars= new HashMap<String, HashMap<String, String>> ();
		
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
    	HashMap<String,String> emptyParams = new HashMap<String,String>();
    	quantifiedVars.put(namePredicate, emptyParams);
    	
    	visitChildren(ast, out, namePredicate); 
    }
    
    public void visitQuantifierVariable(QuantifierVariable ast, BufferedWriter out, String namePredicate) throws ParseException
  	{ 
    	HashMap<String,String> params = new HashMap<String,String>();
    	params.put(ast.getName(), "");
    	quantifiedVars.put(namePredicate, params);
    	
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
    		
    		HashMap<String,String> params = new HashMap<String,String>();
        	params.put(fieldValue, nameField);
        	quantifiedVars.put(namePredicate, params);
        	try{
        	 out.write(" true ");
        	}
        	 catch (Exception e) {
		    		System.err.println("Error: " + e.getMessage());
		      }
    		return;
    	}
    	
    	if (ast.op.getId() == JExprConstants.SC_AND){
    		
    		  AST[] children = ast.getChildren();
    		
    			  children[0].accept(this, out, namePredicate);
    			
    			  
    			  try{
    			  out.write(" && ");
    			  }
    		      catch (Exception e) {
    		    		System.err.println("Error: " + e.getMessage());
    		      }
    			  
    			  children[1].accept(this, out, namePredicate);
    		  
    		return;
    	}
    	
    	
    	
    	
    	
           

    }
    
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
       
    	visitChildren(ast, out, namePredicate);
    }
    
    public void visitIfStatement(IfStatement ast, BufferedWriter out, String namePredicate) throws ParseException
    {
        visitChildren(ast, out, namePredicate);

    }
    
    public void visitWhileStatement(WhileStatement ast, BufferedWriter out, String namePredicate) throws ParseException
    {
        visitChildren(ast, out, namePredicate);

    }


}
