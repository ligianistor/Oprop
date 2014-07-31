package cager.jexpr.visitor;

import java.io.BufferedWriter;

import cager.jexpr.ParseException;
import cager.jexpr.ast.*;

/*
*/

public interface Visitor {

  public abstract void visitCompilationUnit(CompilationUnit ast ) throws ParseException;
  public abstract void visitClassDeclaration(ClassDeclaration ast ) throws ParseException;
  public abstract void visitMethodDeclaration(MethodDeclaration ast ) throws ParseException;
  public abstract void visitBinaryExpression(BinaryExpression ast ) throws ParseException;
  public abstract void visitPrimaryExpression(PrimaryExpression ast ) throws ParseException;
  public abstract void visitFieldSelection(FieldSelection ast ) throws ParseException;
  public abstract void visitCastExpression(CastExpression ast ) throws ParseException;
  public abstract void visitFormalParameter(FormalParameter ast ) throws ParseException;
  public abstract void visitFormalParameters(FormalParameters ast ) throws ParseException;
  public abstract void visitIdentifierExpression(IdentifierExpression ast ) throws ParseException;
  public abstract void visitKeywordExpression(KeywordExpression ast ) throws ParseException;
  public abstract void visitLiteralExpression(LiteralExpression ast ) throws ParseException;
  public abstract void visitUnaryExpression(UnaryExpression ast ) throws ParseException;
  public abstract void visitVariableDeclaration(VariableDeclaration ast ) throws ParseException;
  public abstract void visitReturnStatement(ReturnStatement ast ) throws ParseException;
  public abstract void visitIfStatement(IfStatement ast ) throws ParseException;
  public abstract void visitBlock(Block ast ) throws ParseException;
  public abstract void visitArgumentList(ArgumentList ast ) throws ParseException;
  
  
  public abstract void visitEnsuresExpression(EnsuresExpression ast ) throws ParseException;
  public abstract void visitFieldDeclaration(FieldDeclaration ast ) throws ParseException;
  public abstract void visitPredicateDeclaration(PredicateDeclaration ast ) throws ParseException;
  public abstract void visitQuantifierVariable(QuantifierVariable ast ) throws ParseException;
  public abstract void visitRequiresExpression(RequiresExpression ast ) throws ParseException;
  public abstract void visitObjectProposition(ObjectProposition ast ) throws ParseException;
  public abstract void visitConstructorDeclaration(ConstructorDeclaration ast ) throws ParseException;
  public abstract void visitStatementExpression(StatementExpression ast ) throws ParseException;
  public abstract void visitQuantificationExpression(QuantificationExpression quantificationExpression ) throws ParseException;
  public abstract void visitMethodSpecVariable(MethodSpecVariable methodSpecVariable ) throws ParseException;
  public abstract void visitMethodSpecVariables(MethodSpecVariables methodSpecVariables ) throws ParseException;
  public abstract void visitMethodSpecExpression(MethodSpecExpression methodSpecExpression ) throws ParseException;
  public abstract void visitAllocationExpression(AllocationExpression allocationExpression ) throws ParseException;
  public abstract void visitCompilationUnits(CompilationUnits compilationUnits ) throws ParseException;
  public abstract void visitLocalVariableDeclaration(LocalVariableDeclaration localVariableDeclaration ) throws ParseException;
  public abstract void visitDeclarationStatement(DeclarationStatement declarationStatement ) throws ParseException;
  public abstract void visitQuantifierVariables(QuantifierVariables quantifierVariables ) throws ParseException;
  public abstract void visitWhileStatement(WhileStatement whileStatement ) throws ParseException;
  public abstract void visitForStatement(ForStatement forStatement ) throws ParseException;
}
