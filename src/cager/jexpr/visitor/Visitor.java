package cager.jexpr.visitor;

import java.io.BufferedWriter;

import cager.jexpr.ParseException;
import cager.jexpr.ast.*;

/*
*/

public interface Visitor {

  public abstract Object visitCompilationUnit(CompilationUnit ast, Object o, BufferedWriter out) throws ParseException;
  public abstract Object visitClassDeclaration(ClassDeclaration ast, Object o, BufferedWriter out) throws ParseException;
  public abstract Object visitMethodDeclaration(MethodDeclaration ast, Object o, BufferedWriter out) throws ParseException;
  public abstract Object visitBinaryExpression(BinaryExpression ast, Object o, BufferedWriter out) throws ParseException;
  public abstract Object visitPrimaryExpression(PrimaryExpression ast, Object o, BufferedWriter out) throws ParseException;
  public abstract Object visitFieldSelection(FieldSelection ast, Object o, BufferedWriter out) throws ParseException;
  public abstract Object visitCastExpression(CastExpression ast, Object o, BufferedWriter out) throws ParseException;
  public abstract Object visitFormalParameter(FormalParameter ast, Object o, BufferedWriter out) throws ParseException;
  public abstract Object visitFormalParameters(FormalParameters ast, Object o, BufferedWriter out) throws ParseException;
  public abstract Object visitIdentifierExpression(IdentifierExpression ast, Object o, BufferedWriter out) throws ParseException;
  public abstract Object visitKeywordExpression(KeywordExpression ast, Object o, BufferedWriter out) throws ParseException;
  public abstract Object visitLiteralExpression(LiteralExpression ast, Object o, BufferedWriter out) throws ParseException;
  public abstract Object visitUnaryExpression(UnaryExpression ast, Object o, BufferedWriter out) throws ParseException;
  public abstract Object visitVariableDeclaration(VariableDeclaration ast, Object o, BufferedWriter out) throws ParseException;
  public abstract Object visitReturnStatement(ReturnStatement ast, Object o, BufferedWriter out) throws ParseException;
  public abstract Object visitIfStatement(IfStatement ast, Object o, BufferedWriter out) throws ParseException;
  public abstract Object visitBlock(Block ast, Object o, BufferedWriter out) throws ParseException;
  public abstract Object visitArgumentList(ArgumentList ast, Object o, BufferedWriter out) throws ParseException;
  
  
  public abstract Object visitEnsuresExpression(EnsuresExpression ast, Object o, BufferedWriter out) throws ParseException;
  public abstract Object visitFieldDeclaration(FieldDeclaration ast, Object o, BufferedWriter out) throws ParseException;
  public abstract Object visitPredicateDeclaration(PredicateDeclaration ast, Object o, BufferedWriter out) throws ParseException;
  public abstract Object visitQuantifierVariable(QuantifierVariable ast, Object o, BufferedWriter out) throws ParseException;
  public abstract Object visitRequiresExpression(RequiresExpression ast, Object o, BufferedWriter out) throws ParseException;
  public abstract Object visitObjectProposition(ObjectProposition ast, Object o, BufferedWriter out) throws ParseException;
  public abstract Object visitConstructorDeclaration(ConstructorDeclaration ast, Object o, BufferedWriter out) throws ParseException;
  public abstract Object visitStatementExpression(StatementExpression ast, Object o, BufferedWriter out) throws ParseException;
  public abstract Object visitQuantificationExpression(QuantificationExpression quantificationExpression, Object o, BufferedWriter out) throws ParseException;
  public abstract Object visitMethodSpecVariable(MethodSpecVariable methodSpecVariable, Object o, BufferedWriter out) throws ParseException;
  public abstract Object visitMethodSpecVariables(MethodSpecVariables methodSpecVariables, Object o, BufferedWriter out) throws ParseException;
  public abstract Object visitMethodSpecExpression(MethodSpecExpression methodSpecExpression, Object o, BufferedWriter out) throws ParseException;
  public abstract Object visitAllocationExpression(AllocationExpression allocationExpression, Object o, BufferedWriter out) throws ParseException;
  public abstract Object visitCompilationUnits(CompilationUnits compilationUnits, Object o, BufferedWriter out) throws ParseException;
  public abstract Object visitLocalVariableDeclaration(LocalVariableDeclaration localVariableDeclaration, Object o, BufferedWriter out) throws ParseException;
  public abstract Object visitDeclarationStatement(DeclarationStatement declarationStatement, Object o, BufferedWriter out) throws ParseException;
  public abstract Object visitQuantifierVariables(QuantifierVariables quantifierVariables, Object o, BufferedWriter out) throws ParseException;
  public abstract Object visitWhileStatement(WhileStatement whileStatement, Object o, BufferedWriter out) throws ParseException;
  public abstract Object visitForStatement(ForStatement forStatement, Object o, BufferedWriter out) throws ParseException;
}
