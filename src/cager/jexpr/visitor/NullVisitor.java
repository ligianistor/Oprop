package cager.jexpr.visitor;

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

  public Object visitBinaryExpression(BinaryExpression ast, Object o) throws ParseException { visitChildren(ast, o); return null; }
  public Object visitFieldSelection(FieldSelection ast, Object o) throws ParseException { visitChildren(ast, o); return null; }
  public Object visitCastExpression(CastExpression ast, Object o) throws ParseException { visitChildren(ast, o); return null; }
  public Object visitFormalParameter(FormalParameter ast, Object o) throws ParseException { visitChildren(ast, o); return null; }
  public Object visitFormalParameters(FormalParameters ast, Object o) throws ParseException { visitChildren(ast, o); return null; }
  public Object visitIdentifierExpression(IdentifierExpression ast, Object o) throws ParseException { visitChildren(ast, o); return null; }
  public Object visitKeywordExpression(KeywordExpression ast, Object o) throws ParseException { visitChildren(ast, o); return null; }
  public Object visitLiteralExpression(LiteralExpression ast, Object o) throws ParseException { visitChildren(ast, o); return null; }
  public Object visitUnaryExpression(UnaryExpression ast, Object o) throws ParseException { visitChildren(ast, o); return null; }
  public Object visitVariableDeclaration(VariableDeclaration ast, Object o) throws ParseException { visitChildren(ast, o); return null; }
  public Object visitCompilationUnit(CompilationUnit ast, Object o) throws ParseException { visitChildren(ast, o); return null; }
  public Object visitClassDeclaration(ClassDeclaration ast, Object o) throws ParseException { visitChildren(ast, o); return null; }
  public Object visitMethodDeclaration(MethodDeclaration ast, Object o) throws ParseException { visitChildren(ast, o); return null; }
  public Object visitReturnStatement(ReturnStatement ast, Object o) throws ParseException  { visitChildren(ast, o); return null; }
  public Object visitIfStatement(IfStatement ast, Object o) throws ParseException  { visitChildren(ast, o); return null; }
  public Object visitBlock(Block ast, Object o) throws ParseException  { visitChildren(ast, o); return null; }
  public Object visitArgumentList(ArgumentList ast, Object o) throws ParseException  { visitChildren(ast, o); return null; }
  
  public Object visitPrimaryExpression(PrimaryExpression ast, Object o) throws ParseException { visitChildren(ast, o); return null; }
  public Object visitRequiresExpression(RequiresExpression ast, Object o) throws ParseException { visitChildren(ast, o); return null; }
  public Object visitEnsuresExpression(EnsuresExpression ast, Object o) throws ParseException { visitChildren(ast, o); return null; }
  
  public Object visitFieldDeclaration(FieldDeclaration ast, Object o) throws ParseException { visitChildren(ast, o); return null; }
  
  //  public abstract Object visit( ast, Object o);

  public void visitChildren(AST ast, Object o) throws ParseException
  {
	  AST[] children = ast.getChildren();
	  for (int i = 0; i < children.length; i++) {
		  children[i].visit(this, o);
	  }
  }

  public Object visitPredicateDeclaration(PredicateDeclaration ast, Object o) throws ParseException { visitChildren(ast, o); return null; }
  public Object visitQuantifierVariable(QuantifierVariable ast, Object o) throws ParseException { visitChildren(ast, o); return null; }
  public Object visitObjectProposition(ObjectProposition ast, Object o) throws ParseException { visitChildren(ast, o); return null; }
  public Object visitConstructorDeclaration(ConstructorDeclaration ast, Object o) throws ParseException { visitChildren(ast, o); return null; }
  public Object visitStatementExpression(StatementExpression ast, Object o) throws ParseException { visitChildren(ast, o); return null; }
  public Object visitQuantificationExpression(QuantificationExpression ast, Object o) throws ParseException { visitChildren(ast, o); return null; }
  public Object visitMethodSpecVariable(MethodSpecVariable ast, Object o) throws ParseException { visitChildren(ast, o); return null; }
  public Object visitMethodSpecVariables(MethodSpecVariables ast, Object o) throws ParseException { visitChildren(ast, o); return null; }
  public Object visitMethodSpecExpression(MethodSpecExpression ast, Object o) throws ParseException { visitChildren(ast, o); return null; }
  public Object visitAllocationExpression(AllocationExpression ast, Object o) throws ParseException { visitChildren(ast, o); return null; }
  public Object visitCompilationUnits(CompilationUnits ast, Object o) throws ParseException { visitChildren(ast, o); return null; }
  public Object visitLocalVariableDeclaration(LocalVariableDeclaration ast, Object o) throws ParseException { visitChildren(ast, o); return null; }
  public Object visitDeclarationStatement(DeclarationStatement ast, Object o) throws ParseException { visitChildren(ast, o); return null; }
  public Object visitQuantifierVariables(QuantifierVariables ast, Object o) throws ParseException { visitChildren(ast, o); return null; }
  public Object visitWhileStatement(WhileStatement ast, Object o) throws ParseException { visitChildren(ast, o); return null; }
  public Object visitForStatement(ForStatement ast, Object o) throws ParseException { visitChildren(ast, o); return null; }

}
