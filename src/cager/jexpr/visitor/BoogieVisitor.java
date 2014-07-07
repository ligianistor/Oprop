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
	
    public Object visitCompilationUnits(CompilationUnits ast, Object o, BufferedWriter out) throws ParseException
    {
        visitChildren(ast, o, out);
        return null;
    }
    
    public Object visitCompilationUnit(CompilationUnit ast, Object o, BufferedWriter out) throws ParseException
    {
    	try {
        out.write("type Ref;\n");
    	}
    	catch (Exception e) {
    		System.err.println("Error: " + e.getMessage());
    	}
        visitChildren(ast, o, out);
        return null;
    }
    

    public Object visitMethodDeclaration(MethodDeclaration ast, Object o, BufferedWriter out) throws ParseException
    {
    	visitChildren(ast, o, out);

        return null;
    }

    public Object visitReturnStatement(ReturnStatement ast, Object o, BufferedWriter out) throws ParseException
    {
        visitChildren(ast, o, out);

        return null;
    }

    public Object visitFieldSelection(FieldSelection ast, Object o, BufferedWriter out) throws ParseException
    {
    	visitChildren(ast, o, out);
    	
        return null;
    }

    public Object visitBinaryExpression(BinaryExpression ast, Object o, BufferedWriter out) throws ParseException
    {
        visitChildren(ast, o, out);    

        return null;
    }
    
    public Object visitObjectProposition(ObjectProposition ast, Object o, BufferedWriter out) throws ParseException
    {
        visitChildren(ast, o, out);

        return null;
    }

   
    public Object visitUnaryExpression(UnaryExpression ast, Object o, BufferedWriter out) throws ParseException
    {
        visitChildren(ast, o, out);

        return null;
    }

    public Object visitPrimaryExpression(PrimaryExpression ast, Object o, BufferedWriter out) throws ParseException
    {
        visitChildren(ast, o, out);

        return null;
    }
    
    public Object visitFormalParameter(FormalParameter ast, Object o, BufferedWriter out) throws ParseException
    {
        visitChildren(ast, o, out);

        return null;
    }
    
    public Object visitLocalVariableDeclaration(LocalVariableDeclaration ast, Object o, BufferedWriter out) throws ParseException
    {
    	visitChildren(ast, o, out);

        return null;
    }
    
    public Object visitKeywordExpression(KeywordExpression ast, Object o, BufferedWriter out) throws ParseException
    { 
    	visitChildren(ast, o, out);
    	
    	return null;
    }
 
    public Object visitIdentifierExpression(IdentifierExpression ast, Object o, BufferedWriter out) throws ParseException
    {
       
    	visitChildren(ast, o, out);
        return null;
    }
    
    public Object visitIfStatement(IfStatement ast, Object o, BufferedWriter out) throws ParseException
    {
        visitChildren(ast, o, out);

        return null;
    }
    
    public Object visitWhileStatement(WhileStatement ast, Object o, BufferedWriter out) throws ParseException
    {
        visitChildren(ast, o, out);

        return null;
    }


}
