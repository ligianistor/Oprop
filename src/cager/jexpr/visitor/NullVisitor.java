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
import cager.jexpr.ast.MethodSelection;
import cager.jexpr.ast.MethodSpecExpression;
import cager.jexpr.ast.MethodSpecFracVariable;
import cager.jexpr.ast.MethodSpecFracVariables;
import cager.jexpr.ast.MethodSpecVariable;
import cager.jexpr.ast.MethodSpecVariables;
import cager.jexpr.ast.ObjectProposition;
import cager.jexpr.ast.PackUnpackAnnotation;
import cager.jexpr.ast.FractionAnnotation;
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

  public void visitBinaryExpression(BinaryExpression ast) 
		  throws ParseException 
		  { visitChildren(ast); }
  
  public void visitFieldSelection(FieldSelection ast) 
		  throws ParseException 
		  { visitChildren(ast); }
  
  public void visitCastExpression(CastExpression ast ) 
		  throws ParseException
		  { visitChildren(ast); }
  
  public void visitFormalParameter(FormalParameter ast ) 
		  throws ParseException
		  { visitChildren(ast); }
  
  public void visitFormalParameters(FormalParameters ast ) 
		  throws ParseException 
		  { visitChildren(ast); }
  
  public void visitIdentifierExpression(IdentifierExpression ast ) 
		  throws ParseException
		  { visitChildren(ast); }
  
  
  public void visitFractionAnnotation(FractionAnnotation ast) 
		  throws ParseException
		  { visitChildren(ast); }
  
  public void visitPackUnpackAnnotation(PackUnpackAnnotation ast) 
		  throws ParseException
		  { visitChildren(ast); }
  
  public void visitKeywordExpression(KeywordExpression ast ) 
		  throws ParseException 
		  { visitChildren(ast); }
  
  public void visitLiteralExpression(LiteralExpression ast )
		  throws ParseException
		  { visitChildren(ast); }
  
  public void visitUnaryExpression(UnaryExpression ast )
		  throws ParseException 
		  { visitChildren(ast); }
  
  public void visitVariableDeclaration(VariableDeclaration ast ) 
		  throws ParseException
		  { visitChildren(ast); }
  
  public void visitCompilationUnit(CompilationUnit ast ) 
		  throws ParseException
		  { visitChildren(ast); }
  
  public void visitClassDeclaration(ClassDeclaration ast )
		  throws ParseException 
		  { visitChildren(ast); }
  
  public void visitMethodDeclaration(MethodDeclaration ast ) 
		  throws ParseException 
		  { visitChildren(ast); }
  
  public void visitReturnStatement(ReturnStatement ast )
          throws ParseException 
          { visitChildren(ast); }
  
  public void visitIfStatement(IfStatement ast ) 
		  throws ParseException 
		  { visitChildren(ast); }
  
  public void visitBlock(Block ast ) 
		  throws ParseException 
		  { visitChildren(ast); }
  
  public void visitArgumentList(ArgumentList ast )
		  throws ParseException 
		  { visitChildren(ast); }
  
  public void visitPrimaryExpression(PrimaryExpression ast ) 
		  throws ParseException 
		  { visitChildren(ast); }
  
  public void visitRequiresExpression(RequiresExpression ast ) 
		  throws ParseException 
		  { visitChildren(ast); }
  
  public void visitEnsuresExpression(EnsuresExpression ast )
		  throws ParseException 
		  { visitChildren(ast); }
  
  public void visitFieldDeclaration(FieldDeclaration ast ) 
		  throws ParseException 
		  { visitChildren(ast); }
  
  //  public abstract Object visit( ast, Object o);

  public void visitChildren(AST ast ) 
		  throws ParseException
  {
	  if (ast!=null) {
	  AST[] children = ast.getChildren();
	  for (int i = 0; i < children.length; i++) {
		  children[i].accept(this);
	  }
	  }
  }

  public void visitPredicateDeclaration(PredicateDeclaration ast ) 
		  throws ParseException
		  { visitChildren(ast); }
  
  public void visitQuantifierVariable(QuantifierVariable ast )
		  throws ParseException
		  { visitChildren(ast); }
  
  public void visitObjectProposition(ObjectProposition ast )
		  throws ParseException
		  { visitChildren(ast); }
  
  public void visitConstructorDeclaration(ConstructorDeclaration ast )
		  throws ParseException
		  { visitChildren(ast); }
  
  public void visitStatementExpression(StatementExpression ast )
		  throws ParseException
		  { visitChildren(ast); }
  
  public void visitQuantificationExpression(QuantificationExpression ast ) 
		  throws ParseException 
		  { visitChildren(ast); }
  
  public void visitMethodSpecVariable(MethodSpecVariable ast ) 
		  throws ParseException 
		  { visitChildren(ast); }
  
  public void visitMethodSpecVariables(MethodSpecVariables ast )
		  throws ParseException 
		  { visitChildren(ast); }
  
  public void visitMethodSpecExpression(MethodSpecExpression ast ) 
		  throws ParseException 
		  { visitChildren(ast); }
  
  public void visitAllocationExpression(AllocationExpression ast ) 
		  throws ParseException 
		  { visitChildren(ast); }
  
  public void visitCompilationUnits(CompilationUnits ast ) 
		  throws ParseException 
		  { visitChildren(ast); }
  
  public void visitLocalVariableDeclaration(LocalVariableDeclaration ast ) 
		  throws ParseException 
		  { visitChildren(ast); }
  
  public void visitDeclarationStatement(DeclarationStatement ast ) 
		  throws ParseException 
		  { visitChildren(ast); }
  
  public void visitQuantifierVariables(QuantifierVariables ast ) 
		  throws ParseException 
		  { visitChildren(ast); }
  
  public void visitWhileStatement(WhileStatement ast ) 
		  throws ParseException 
		  { visitChildren(ast ); }
  
  public void visitForStatement(ForStatement ast) 
		  throws ParseException 
		  { visitChildren(ast); }

  public void visitMethodSelection(MethodSelection ast) 
		  throws ParseException 
		  { visitChildren(ast); }

  public void visitMethodSpecFracVariable(
		MethodSpecFracVariable ast) 
				throws ParseException 
		  { visitChildren(ast); }


  public void visitMethodSpecFracVariables(MethodSpecFracVariables ast) 
		throws ParseException 
		{ visitChildren(ast); }

}
