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

  public void visitBinaryExpression(BinaryExpression ast, BufferedWriter out) throws ParseException { visitChildren(ast, out); }
  public void visitFieldSelection(FieldSelection ast, BufferedWriter out) throws ParseException { visitChildren(ast, out); }
  public void visitCastExpression(CastExpression ast, BufferedWriter out) throws ParseException { visitChildren(ast, out); }
  public void visitFormalParameter(FormalParameter ast, BufferedWriter out) throws ParseException { visitChildren(ast, out); }
  public void visitFormalParameters(FormalParameters ast, BufferedWriter out) throws ParseException { visitChildren(ast, out); }
  public void visitIdentifierExpression(IdentifierExpression ast, BufferedWriter out) throws ParseException { visitChildren(ast, out); }
  public void visitKeywordExpression(KeywordExpression ast, BufferedWriter out) throws ParseException { visitChildren(ast, out); }
  public void visitLiteralExpression(LiteralExpression ast, BufferedWriter out) throws ParseException { visitChildren(ast, out); }
  public void visitUnaryExpression(UnaryExpression ast, BufferedWriter out) throws ParseException { visitChildren(ast, out); }
  public void visitVariableDeclaration(VariableDeclaration ast, BufferedWriter out) throws ParseException { visitChildren(ast, out); }
  public void visitCompilationUnit(CompilationUnit ast, BufferedWriter out) throws ParseException { visitChildren(ast, out); }
  public void visitClassDeclaration(ClassDeclaration ast, BufferedWriter out) throws ParseException { visitChildren(ast, out); }
  public void visitMethodDeclaration(MethodDeclaration ast, BufferedWriter out) throws ParseException { visitChildren(ast, out); }
  public void visitReturnStatement(ReturnStatement ast, BufferedWriter out) throws ParseException  { visitChildren(ast, out); }
  public void visitIfStatement(IfStatement ast, BufferedWriter out) throws ParseException  { visitChildren(ast, out); }
  public void visitBlock(Block ast, BufferedWriter out) throws ParseException  { visitChildren(ast, out); }
  public void visitArgumentList(ArgumentList ast, BufferedWriter out) throws ParseException  { visitChildren(ast, out); }
  
  public void visitPrimaryExpression(PrimaryExpression ast, BufferedWriter out) throws ParseException { visitChildren(ast, out); }
  public void visitRequiresExpression(RequiresExpression ast, BufferedWriter out) throws ParseException { visitChildren(ast, out); }
  public void visitEnsuresExpression(EnsuresExpression ast, BufferedWriter out) throws ParseException { visitChildren(ast, out); }
  
  public void visitFieldDeclaration(FieldDeclaration ast, BufferedWriter out) throws ParseException { visitChildren(ast, out); }
  
  //  public abstract Object visit( ast, Object o);

  public void visitChildren(AST ast, BufferedWriter out) throws ParseException
  {
	  AST[] children = ast.getChildren();
	  for (int i = 0; i < children.length; i++) {
		  children[i].visit(this, out);
	  }
  }

  public void visitPredicateDeclaration(PredicateDeclaration ast, BufferedWriter out) throws ParseException { visitChildren(ast, out); }
  public void visitQuantifierVariable(QuantifierVariable ast, BufferedWriter out) throws ParseException { visitChildren(ast, out); }
  public void visitObjectProposition(ObjectProposition ast, BufferedWriter out) throws ParseException { visitChildren(ast, out); }
  public void visitConstructorDeclaration(ConstructorDeclaration ast, BufferedWriter out) throws ParseException { visitChildren(ast, out); }
  public void visitStatementExpression(StatementExpression ast, BufferedWriter out) throws ParseException { visitChildren(ast, out); }
  public void visitQuantificationExpression(QuantificationExpression ast, BufferedWriter out) throws ParseException { visitChildren(ast, out); }
  public void visitMethodSpecVariable(MethodSpecVariable ast, BufferedWriter out) throws ParseException { visitChildren(ast, out); }
  public void visitMethodSpecVariables(MethodSpecVariables ast, BufferedWriter out) throws ParseException { visitChildren(ast, out); }
  public void visitMethodSpecExpression(MethodSpecExpression ast, BufferedWriter out) throws ParseException { visitChildren(ast, out); }
  public void visitAllocationExpression(AllocationExpression ast, BufferedWriter out) throws ParseException { visitChildren(ast, out); }
  public void visitCompilationUnits(CompilationUnits ast, BufferedWriter out) throws ParseException { visitChildren(ast, out); }
  public void visitLocalVariableDeclaration(LocalVariableDeclaration ast, BufferedWriter out) throws ParseException { visitChildren(ast, out); }
  public void visitDeclarationStatement(DeclarationStatement ast, BufferedWriter out) throws ParseException { visitChildren(ast, out); }
  public void visitQuantifierVariables(QuantifierVariables ast, BufferedWriter out) throws ParseException { visitChildren(ast, out); }
  public void visitWhileStatement(WhileStatement ast, BufferedWriter out) throws ParseException { visitChildren(ast, out); }
  public void visitForStatement(ForStatement ast, BufferedWriter out) throws ParseException { visitChildren(ast, out); }

}
