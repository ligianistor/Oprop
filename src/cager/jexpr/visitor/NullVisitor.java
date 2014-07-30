package cager.jexpr.visitor;

import java.io.BufferedWriter;

import cager.jexpr.ParseException;
import cager.jexpr.ast.AllocationExpression;
import cager.jexpr.ast.ArgumentList;
import cager.jexpr.ast.AST;
import cager.jexpr.ast.BinaryExpression;
import cager.jexpr.ast.Block;
import cager.jexpr.ast.CastExpression;
import cager.jexpr.ast.ClassDeclaration;
import cager.jexpr.ast.CompilationUnit;
import cager.jexpr.ast.CompilationUnits;
import cager.jexpr.ast.ConstructorDeclaration;
import cager.jexpr.ast.DeclarationStatement;
import cager.jexpr.ast.EnsuresExpression;
import cager.jexpr.ast.FieldDeclaration;
import cager.jexpr.ast.ForStatement;
import cager.jexpr.ast.LocalVariableDeclaration;
import cager.jexpr.ast.MethodSpecExpression;
import cager.jexpr.ast.MethodSpecVariable;
import cager.jexpr.ast.MethodSpecVariables;
import cager.jexpr.ast.ObjectProposition;
import cager.jexpr.ast.PredicateDeclaration;
import cager.jexpr.ast.QuantificationExpression;
import cager.jexpr.ast.QuantifierVariable;
import cager.jexpr.ast.QuantifierVariables;
import cager.jexpr.ast.StatementExpression;
import cager.jexpr.ast.FieldSelection;
import cager.jexpr.ast.FormalParameter;
import cager.jexpr.ast.FormalParameters;
import cager.jexpr.ast.IdentifierExpression;
import cager.jexpr.ast.IfStatement;
import cager.jexpr.ast.KeywordExpression;
import cager.jexpr.ast.LiteralExpression;
import cager.jexpr.ast.MethodDeclaration;
import cager.jexpr.ast.PrimaryExpression;
import cager.jexpr.ast.RequiresExpression;
import cager.jexpr.ast.ReturnStatement;
import cager.jexpr.ast.UnaryExpression;
import cager.jexpr.ast.VariableDeclaration;
import cager.jexpr.ast.WhileStatement;

public class NullVisitor implements Visitor
{

  public void visitBinaryExpression(BinaryExpression ast, String namePredicate) 
		  throws ParseException 
		  { visitChildren(ast,  namePredicate); }
  
  public void visitFieldSelection(FieldSelection ast, String namePredicate) 
		  throws ParseException 
		  { visitChildren(ast,  namePredicate); }
  
  public void visitCastExpression(CastExpression ast,   String namePredicate) 
		  throws ParseException
		  { visitChildren(ast,  namePredicate); }
  
  public void visitFormalParameter(FormalParameter ast,   String namePredicate) 
		  throws ParseException
		  { visitChildren(ast,  namePredicate); }
  
  public void visitFormalParameters(FormalParameters ast,   String namePredicate) 
		  throws ParseException 
		  { visitChildren(ast,  namePredicate); }
  
  public void visitIdentifierExpression(IdentifierExpression ast,   String namePredicate) 
		  throws ParseException
		  { visitChildren(ast,  namePredicate); }
  
  public void visitKeywordExpression(KeywordExpression ast,   String namePredicate) 
		  throws ParseException 
		  { visitChildren(ast,  namePredicate); }
  
  public void visitLiteralExpression(LiteralExpression ast,   String namePredicate)
		  throws ParseException
		  { visitChildren(ast,  namePredicate); }
  
  public void visitUnaryExpression(UnaryExpression ast,   String namePredicate)
		  throws ParseException 
		  { visitChildren(ast,  namePredicate); }
  
  public void visitVariableDeclaration(VariableDeclaration ast,   String namePredicate) 
		  throws ParseException
		  { visitChildren(ast,  namePredicate); }
  
  public void visitCompilationUnit(CompilationUnit ast,   String namePredicate) 
		  throws ParseException
		  { visitChildren(ast,  namePredicate); }
  
  public void visitClassDeclaration(ClassDeclaration ast,   String namePredicate)
		  throws ParseException 
		  { visitChildren(ast,  namePredicate); }
  
  public void visitMethodDeclaration(MethodDeclaration ast,   String namePredicate) 
		  throws ParseException 
		  { visitChildren(ast,  namePredicate); }
  
  public void visitReturnStatement(ReturnStatement ast,   String namePredicate)
          throws ParseException 
          { visitChildren(ast,  namePredicate); }
  
  public void visitIfStatement(IfStatement ast,   String namePredicate) 
		  throws ParseException 
		  { visitChildren(ast,  namePredicate); }
  
  public void visitBlock(Block ast,   String namePredicate) 
		  throws ParseException 
		  { visitChildren(ast,  namePredicate); }
  
  public void visitArgumentList(ArgumentList ast,   String namePredicate)
		  throws ParseException 
		  { visitChildren(ast,  namePredicate); }
  
  public void visitPrimaryExpression(PrimaryExpression ast,   String namePredicate) 
		  throws ParseException 
		  { visitChildren(ast,  namePredicate); }
  
  public void visitRequiresExpression(RequiresExpression ast,   String namePredicate) 
		  throws ParseException 
		  { visitChildren(ast,  namePredicate); }
  
  public void visitEnsuresExpression(EnsuresExpression ast,   String namePredicate)
		  throws ParseException 
		  { visitChildren(ast,  namePredicate); }
  
  public void visitFieldDeclaration(FieldDeclaration ast,   String namePredicate) 
		  throws ParseException 
		  { visitChildren(ast,  namePredicate); }
  
  //  public abstract Object visit( ast, Object o);

  public void visitChildren(AST ast,   String namePredicate) 
		  throws ParseException
  {
	  AST[] children = ast.getChildren();
	  for (int i = 0; i < children.length; i++) {
		  children[i].accept(this,  namePredicate);
	  }
  }

  public void visitPredicateDeclaration(PredicateDeclaration ast,   String namePredicate) 
		  throws ParseException
		  { visitChildren(ast,  namePredicate); }
  
  public void visitQuantifierVariable(QuantifierVariable ast,   String namePredicate)
		  throws ParseException
		  { visitChildren(ast,  namePredicate); }
  
  public void visitObjectProposition(ObjectProposition ast,   String namePredicate)
		  throws ParseException
		  { visitChildren(ast,  namePredicate); }
  
  public void visitConstructorDeclaration(ConstructorDeclaration ast,   String namePredicate)
		  throws ParseException
		  { visitChildren(ast,  namePredicate); }
  
  public void visitStatementExpression(StatementExpression ast,   String namePredicate)
		  throws ParseException
		  { visitChildren(ast,  namePredicate); }
  
  public void visitQuantificationExpression(QuantificationExpression ast,   String namePredicate) 
		  throws ParseException 
		  { visitChildren(ast,  namePredicate); }
  
  public void visitMethodSpecVariable(MethodSpecVariable ast,   String namePredicate) 
		  throws ParseException 
		  { visitChildren(ast,  namePredicate); }
  
  public void visitMethodSpecVariables(MethodSpecVariables ast,   String namePredicate)
		  throws ParseException 
		  { visitChildren(ast,  namePredicate); }
  
  public void visitMethodSpecExpression(MethodSpecExpression ast,   String namePredicate) 
		  throws ParseException 
		  { visitChildren(ast,  namePredicate); }
  
  public void visitAllocationExpression(AllocationExpression ast,   String namePredicate) 
		  throws ParseException 
		  { visitChildren(ast,  namePredicate); }
  
  public void visitCompilationUnits(CompilationUnits ast,   String namePredicate) 
		  throws ParseException 
		  { visitChildren(ast,  namePredicate); }
  
  public void visitLocalVariableDeclaration(LocalVariableDeclaration ast,   String namePredicate) 
		  throws ParseException 
		  { visitChildren(ast,  namePredicate); }
  
  public void visitDeclarationStatement(DeclarationStatement ast,   String namePredicate) 
		  throws ParseException 
		  { visitChildren(ast,  namePredicate); }
  
  public void visitQuantifierVariables(QuantifierVariables ast,   String namePredicate) 
		  throws ParseException 
		  { visitChildren(ast,  namePredicate); }
  
  public void visitWhileStatement(WhileStatement ast,   String namePredicate) 
		  throws ParseException 
		  { visitChildren(ast, namePredicate); }
  
  public void visitForStatement(ForStatement ast, String namePredicate) 
		  throws ParseException 
		  { visitChildren(ast, namePredicate); }

}
