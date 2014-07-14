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

  public void visitBinaryExpression(BinaryExpression ast, BufferedWriter out, String namePredicate) 
		  throws ParseException 
		  { visitChildren(ast, out, namePredicate); }
  
  public void visitFieldSelection(FieldSelection ast, BufferedWriter out, String namePredicate) 
		  throws ParseException 
		  { visitChildren(ast, out, namePredicate); }
  
  public void visitCastExpression(CastExpression ast, BufferedWriter out, String namePredicate) 
		  throws ParseException
		  { visitChildren(ast, out, namePredicate); }
  
  public void visitFormalParameter(FormalParameter ast, BufferedWriter out, String namePredicate) 
		  throws ParseException
		  { visitChildren(ast, out, namePredicate); }
  
  public void visitFormalParameters(FormalParameters ast, BufferedWriter out, String namePredicate) 
		  throws ParseException 
		  { visitChildren(ast, out, namePredicate); }
  
  public void visitIdentifierExpression(IdentifierExpression ast, BufferedWriter out, String namePredicate) 
		  throws ParseException
		  { visitChildren(ast, out, namePredicate); }
  
  public void visitKeywordExpression(KeywordExpression ast, BufferedWriter out, String namePredicate) 
		  throws ParseException 
		  { visitChildren(ast, out, namePredicate); }
  
  public void visitLiteralExpression(LiteralExpression ast, BufferedWriter out, String namePredicate)
		  throws ParseException
		  { visitChildren(ast, out, namePredicate); }
  
  public void visitUnaryExpression(UnaryExpression ast, BufferedWriter out, String namePredicate)
		  throws ParseException 
		  { visitChildren(ast, out, namePredicate); }
  
  public void visitVariableDeclaration(VariableDeclaration ast, BufferedWriter out, String namePredicate) 
		  throws ParseException
		  { visitChildren(ast, out, namePredicate); }
  
  public void visitCompilationUnit(CompilationUnit ast, BufferedWriter out, String namePredicate) 
		  throws ParseException
		  { visitChildren(ast, out, namePredicate); }
  
  public void visitClassDeclaration(ClassDeclaration ast, BufferedWriter out, String namePredicate)
		  throws ParseException 
		  { visitChildren(ast, out, namePredicate); }
  
  public void visitMethodDeclaration(MethodDeclaration ast, BufferedWriter out, String namePredicate) 
		  throws ParseException 
		  { visitChildren(ast, out, namePredicate); }
  
  public void visitReturnStatement(ReturnStatement ast, BufferedWriter out, String namePredicate)
          throws ParseException 
          { visitChildren(ast, out, namePredicate); }
  
  public void visitIfStatement(IfStatement ast, BufferedWriter out, String namePredicate) 
		  throws ParseException 
		  { visitChildren(ast, out, namePredicate); }
  
  public void visitBlock(Block ast, BufferedWriter out, String namePredicate) 
		  throws ParseException 
		  { visitChildren(ast, out, namePredicate); }
  
  public void visitArgumentList(ArgumentList ast, BufferedWriter out, String namePredicate)
		  throws ParseException 
		  { visitChildren(ast, out, namePredicate); }
  
  public void visitPrimaryExpression(PrimaryExpression ast, BufferedWriter out, String namePredicate) 
		  throws ParseException 
		  { visitChildren(ast, out, namePredicate); }
  
  public void visitRequiresExpression(RequiresExpression ast, BufferedWriter out, String namePredicate) 
		  throws ParseException 
		  { visitChildren(ast, out, namePredicate); }
  
  public void visitEnsuresExpression(EnsuresExpression ast, BufferedWriter out, String namePredicate)
		  throws ParseException 
		  { visitChildren(ast, out, namePredicate); }
  
  public void visitFieldDeclaration(FieldDeclaration ast, BufferedWriter out, String namePredicate) 
		  throws ParseException 
		  { visitChildren(ast, out, namePredicate); }
  
  //  public abstract Object visit( ast, Object o);

  public void visitChildren(AST ast, BufferedWriter out, String namePredicate) 
		  throws ParseException
  {
	  AST[] children = ast.getChildren();
	  for (int i = 0; i < children.length; i++) {
		  children[i].accept(this, out, namePredicate);
	  }
  }

  public void visitPredicateDeclaration(PredicateDeclaration ast, BufferedWriter out, String namePredicate) 
		  throws ParseException
		  { visitChildren(ast, out, namePredicate); }
  
  public void visitQuantifierVariable(QuantifierVariable ast, BufferedWriter out, String namePredicate)
		  throws ParseException
		  { visitChildren(ast, out, namePredicate); }
  
  public void visitObjectProposition(ObjectProposition ast, BufferedWriter out, String namePredicate)
		  throws ParseException
		  { visitChildren(ast, out, namePredicate); }
  
  public void visitConstructorDeclaration(ConstructorDeclaration ast, BufferedWriter out, String namePredicate)
		  throws ParseException
		  { visitChildren(ast, out, namePredicate); }
  
  public void visitStatementExpression(StatementExpression ast, BufferedWriter out, String namePredicate)
		  throws ParseException
		  { visitChildren(ast, out, namePredicate); }
  
  public void visitQuantificationExpression(QuantificationExpression ast, BufferedWriter out, String namePredicate) 
		  throws ParseException 
		  { visitChildren(ast, out, namePredicate); }
  
  public void visitMethodSpecVariable(MethodSpecVariable ast, BufferedWriter out, String namePredicate) 
		  throws ParseException 
		  { visitChildren(ast, out, namePredicate); }
  
  public void visitMethodSpecVariables(MethodSpecVariables ast, BufferedWriter out, String namePredicate)
		  throws ParseException 
		  { visitChildren(ast, out, namePredicate); }
  
  public void visitMethodSpecExpression(MethodSpecExpression ast, BufferedWriter out, String namePredicate) 
		  throws ParseException 
		  { visitChildren(ast, out, namePredicate); }
  
  public void visitAllocationExpression(AllocationExpression ast, BufferedWriter out, String namePredicate) 
		  throws ParseException 
		  { visitChildren(ast, out, namePredicate); }
  
  public void visitCompilationUnits(CompilationUnits ast, BufferedWriter out, String namePredicate) 
		  throws ParseException 
		  { visitChildren(ast, out, namePredicate); }
  
  public void visitLocalVariableDeclaration(LocalVariableDeclaration ast, BufferedWriter out, String namePredicate) 
		  throws ParseException 
		  { visitChildren(ast, out, namePredicate); }
  
  public void visitDeclarationStatement(DeclarationStatement ast, BufferedWriter out, String namePredicate) 
		  throws ParseException 
		  { visitChildren(ast, out, namePredicate); }
  
  public void visitQuantifierVariables(QuantifierVariables ast, BufferedWriter out, String namePredicate) 
		  throws ParseException 
		  { visitChildren(ast, out, namePredicate); }
  
  public void visitWhileStatement(WhileStatement ast, BufferedWriter out, String namePredicate) 
		  throws ParseException 
		  { visitChildren(ast, out, namePredicate); }
  
  public void visitForStatement(ForStatement ast, BufferedWriter out, String namePredicate) 
		  throws ParseException 
		  { visitChildren(ast, out, namePredicate); }

}
