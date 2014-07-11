package cager.jexpr.visitor;

import java.io.BufferedWriter;

import cager.jexpr.ParseException;
import cager.jexpr.ast.*;

/*
*/

public interface Visitor {

  public abstract void visitCompilationUnit(CompilationUnit ast, BufferedWriter out) throws ParseException;
  public abstract void visitClassDeclaration(ClassDeclaration ast, BufferedWriter out) throws ParseException;
  public abstract void visitMethodDeclaration(MethodDeclaration ast, BufferedWriter out) throws ParseException;
  public abstract void visitBinaryExpression(BinaryExpression ast, BufferedWriter out) throws ParseException;
  public abstract void visitPrimaryExpression(PrimaryExpression ast, BufferedWriter out) throws ParseException;
  public abstract void visitFieldSelection(FieldSelection ast, BufferedWriter out) throws ParseException;
  public abstract void visitCastExpression(CastExpression ast, BufferedWriter out) throws ParseException;
  public abstract void visitFormalParameter(FormalParameter ast, BufferedWriter out) throws ParseException;
  public abstract void visitFormalParameters(FormalParameters ast, BufferedWriter out) throws ParseException;
  public abstract void visitIdentifierExpression(IdentifierExpression ast, BufferedWriter out) throws ParseException;
  public abstract void visitKeywordExpression(KeywordExpression ast, BufferedWriter out) throws ParseException;
  public abstract void visitLiteralExpression(LiteralExpression ast, BufferedWriter out) throws ParseException;
  public abstract void visitUnaryExpression(UnaryExpression ast, BufferedWriter out) throws ParseException;
  public abstract void visitVariableDeclaration(VariableDeclaration ast, BufferedWriter out) throws ParseException;
  public abstract void visitReturnStatement(ReturnStatement ast, BufferedWriter out) throws ParseException;
  public abstract void visitIfStatement(IfStatement ast, BufferedWriter out) throws ParseException;
  public abstract void visitBlock(Block ast, BufferedWriter out) throws ParseException;
  public abstract void visitArgumentList(ArgumentList ast, BufferedWriter out) throws ParseException;
  
  
  public abstract void visitEnsuresExpression(EnsuresExpression ast, BufferedWriter out) throws ParseException;
  public abstract void visitFieldDeclaration(FieldDeclaration ast, BufferedWriter out) throws ParseException;
  public abstract void visitPredicateDeclaration(PredicateDeclaration ast, BufferedWriter out) throws ParseException;
  public abstract void visitQuantifierVariable(QuantifierVariable ast, BufferedWriter out) throws ParseException;
  public abstract void visitRequiresExpression(RequiresExpression ast, BufferedWriter out) throws ParseException;
  public abstract void visitObjectProposition(ObjectProposition ast, BufferedWriter out) throws ParseException;
  public abstract void visitConstructorDeclaration(ConstructorDeclaration ast, BufferedWriter out) throws ParseException;
  public abstract void visitStatementExpression(StatementExpression ast, BufferedWriter out) throws ParseException;
  public abstract void visitQuantificationExpression(QuantificationExpression quantificationExpression, BufferedWriter out) throws ParseException;
  public abstract void visitMethodSpecVariable(MethodSpecVariable methodSpecVariable, BufferedWriter out) throws ParseException;
  public abstract void visitMethodSpecVariables(MethodSpecVariables methodSpecVariables, BufferedWriter out) throws ParseException;
  public abstract void visitMethodSpecExpression(MethodSpecExpression methodSpecExpression, BufferedWriter out) throws ParseException;
  public abstract void visitAllocationExpression(AllocationExpression allocationExpression, BufferedWriter out) throws ParseException;
  public abstract void visitCompilationUnits(CompilationUnits compilationUnits, BufferedWriter out) throws ParseException;
  public abstract void visitLocalVariableDeclaration(LocalVariableDeclaration localVariableDeclaration, BufferedWriter out) throws ParseException;
  public abstract void visitDeclarationStatement(DeclarationStatement declarationStatement, BufferedWriter out) throws ParseException;
  public abstract void visitQuantifierVariables(QuantifierVariables quantifierVariables, BufferedWriter out) throws ParseException;
  public abstract void visitWhileStatement(WhileStatement whileStatement, BufferedWriter out) throws ParseException;
  public abstract void visitForStatement(ForStatement forStatement, BufferedWriter out) throws ParseException;
}
