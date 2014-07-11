package cager.jexpr.visitor;

import java.io.BufferedWriter;

import org.apache.bcel.generic.Type;

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
	
	BufferedWriter out;
	
    public void visitCompilationUnits(CompilationUnits ast, BufferedWriter out) throws ParseException
    {
        visitChildren(ast, out);
    }
    
    public void visitCompilationUnit(CompilationUnit ast, BufferedWriter out) throws ParseException
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
        visitChildren(ast, out);
    }
    
    public void visitFieldDeclaration(FieldDeclaration ast, BufferedWriter out) throws ParseException 
    { 
    	try {
    		out.write("var "+ ast.getName()+" [Ref]"+ast.getType()+";\n");
    	}
    	catch (Exception e) {
    		System.err.println("Error: " + e.getMessage());
    	}
    	visitChildren(ast, out);  
    	}
    
    public void visitPredicateDeclaration(PredicateDeclaration ast, BufferedWriter out) throws ParseException
    { 
    	try {
    		out.write("const unique "+ast.getIdentifier().getName().toLowerCase() +"P: PredicateTypes;\n"); 
    	}
    	catch (Exception e) {
    		System.err.println("Error: " + e.getMessage());
    	}
    	
    	visitChildren(ast, out); 
    }
    
    

    public void visitMethodDeclaration(MethodDeclaration ast, BufferedWriter out) throws ParseException
    {
    	visitChildren(ast, out);
    }

    public void visitReturnStatement(ReturnStatement ast, BufferedWriter out) throws ParseException
    {
        visitChildren(ast, out);

    }

    public void visitFieldSelection(FieldSelection ast, BufferedWriter out) throws ParseException
    {
    	visitChildren(ast, out);
    }

    public void visitBinaryExpression(BinaryExpression ast, BufferedWriter out) throws ParseException
    {
        visitChildren(ast, out);    

    }
    
    public void visitObjectProposition(ObjectProposition ast, BufferedWriter out) throws ParseException
    {
        visitChildren(ast, out);
    }

   
    public void visitUnaryExpression(UnaryExpression ast, BufferedWriter out) throws ParseException
    {
        visitChildren(ast, out);
    }

    public void visitPrimaryExpression(PrimaryExpression ast, BufferedWriter out) throws ParseException
    {
        visitChildren(ast, out);
    }
    
    public void visitFormalParameter(FormalParameter ast, BufferedWriter out) throws ParseException
    {
        visitChildren(ast, out);

    }
    
    public void visitLocalVariableDeclaration(LocalVariableDeclaration ast, BufferedWriter out) throws ParseException
    {
    	visitChildren(ast, out);
    }
    
    public void visitKeywordExpression(KeywordExpression ast, BufferedWriter out) throws ParseException
    { 
    	visitChildren(ast, out);

    }
 
    public void visitIdentifierExpression(IdentifierExpression ast, BufferedWriter out) throws ParseException
    {
       
    	visitChildren(ast, out);
    }
    
    public void visitIfStatement(IfStatement ast, BufferedWriter out) throws ParseException
    {
        visitChildren(ast, out);

    }
    
    public void visitWhileStatement(WhileStatement ast, BufferedWriter out) throws ParseException
    {
        visitChildren(ast, out);

    }


}
