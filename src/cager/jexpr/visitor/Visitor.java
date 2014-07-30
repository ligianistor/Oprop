package cager.jexpr.visitor;

import java.io.BufferedWriter;

import cager.jexpr.ParseException;
import cager.jexpr.ast.*;

/*
*/

public interface Visitor {

  public abstract void visitCompilationUnit(CompilationUnit ast, String namePredicate) throws ParseException;
  public abstract void visitClassDeclaration(ClassDeclaration ast, String namePredicate) throws ParseException;
  public abstract void visitMethodDeclaration(MethodDeclaration ast, String namePredicate) throws ParseException;
  public abstract void visitBinaryExpression(BinaryExpression ast, String namePredicate) throws ParseException;
  public abstract void visitPrimaryExpression(PrimaryExpression ast, String namePredicate) throws ParseException;
  public abstract void visitFieldSelection(FieldSelection ast, String namePredicate) throws ParseException;
  public abstract void visitCastExpression(CastExpression ast, String namePredicate) throws ParseException;
  public abstract void visitFormalParameter(FormalParameter ast, String namePredicate) throws ParseException;
  public abstract void visitFormalParameters(FormalParameters ast, String namePredicate) throws ParseException;
  public abstract void visitIdentifierExpression(IdentifierExpression ast, String namePredicate) throws ParseException;
  public abstract void visitKeywordExpression(KeywordExpression ast, String namePredicate) throws ParseException;
  public abstract void visitLiteralExpression(LiteralExpression ast, String namePredicate) throws ParseException;
  public abstract void visitUnaryExpression(UnaryExpression ast, String namePredicate) throws ParseException;
  public abstract void visitVariableDeclaration(VariableDeclaration ast, String namePredicate) throws ParseException;
  public abstract void visitReturnStatement(ReturnStatement ast, String namePredicate) throws ParseException;
  public abstract void visitIfStatement(IfStatement ast, String namePredicate) throws ParseException;
  public abstract void visitBlock(Block ast, String namePredicate) throws ParseException;
  public abstract void visitArgumentList(ArgumentList ast, String namePredicate) throws ParseException;
  
  
  public abstract void visitEnsuresExpression(EnsuresExpression ast, String namePredicate) throws ParseException;
  public abstract void visitFieldDeclaration(FieldDeclaration ast, String namePredicate) throws ParseException;
  public abstract void visitPredicateDeclaration(PredicateDeclaration ast, String namePredicate) throws ParseException;
  public abstract void visitQuantifierVariable(QuantifierVariable ast, String namePredicate) throws ParseException;
  public abstract void visitRequiresExpression(RequiresExpression ast, String namePredicate) throws ParseException;
  public abstract void visitObjectProposition(ObjectProposition ast, String namePredicate) throws ParseException;
  public abstract void visitConstructorDeclaration(ConstructorDeclaration ast, String namePredicate) throws ParseException;
  public abstract void visitStatementExpression(StatementExpression ast, String namePredicate) throws ParseException;
  public abstract void visitQuantificationExpression(QuantificationExpression quantificationExpression, String namePredicate) throws ParseException;
  public abstract void visitMethodSpecVariable(MethodSpecVariable methodSpecVariable, String namePredicate) throws ParseException;
  public abstract void visitMethodSpecVariables(MethodSpecVariables methodSpecVariables, String namePredicate) throws ParseException;
  public abstract void visitMethodSpecExpression(MethodSpecExpression methodSpecExpression, String namePredicate) throws ParseException;
  public abstract void visitAllocationExpression(AllocationExpression allocationExpression, String namePredicate) throws ParseException;
  public abstract void visitCompilationUnits(CompilationUnits compilationUnits, String namePredicate) throws ParseException;
  public abstract void visitLocalVariableDeclaration(LocalVariableDeclaration localVariableDeclaration, String namePredicate) throws ParseException;
  public abstract void visitDeclarationStatement(DeclarationStatement declarationStatement, String namePredicate) throws ParseException;
  public abstract void visitQuantifierVariables(QuantifierVariables quantifierVariables, String namePredicate) throws ParseException;
  public abstract void visitWhileStatement(WhileStatement whileStatement, String namePredicate) throws ParseException;
  public abstract void visitForStatement(ForStatement forStatement, String namePredicate) throws ParseException;
}
