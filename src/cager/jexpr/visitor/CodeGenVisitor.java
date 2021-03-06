package cager.jexpr.visitor;

import java.io.File;
import cager.jexpr.*;
import cager.jexpr.ast.*;

import org.apache.bcel.Constants;
import org.apache.bcel.generic.BranchInstruction;
import org.apache.bcel.generic.ClassGen;
import org.apache.bcel.generic.CompoundInstruction;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.GOTO;
import org.apache.bcel.generic.IFEQ;
import org.apache.bcel.generic.IFNE;
import org.apache.bcel.generic.Instruction;
import org.apache.bcel.generic.InstructionConstants;
import org.apache.bcel.generic.InstructionList;
import org.apache.bcel.generic.MethodGen;
import org.apache.bcel.generic.PUSH;
import org.apache.bcel.generic.ReferenceType;
import org.apache.bcel.generic.Type;

public class CodeGenVisitor extends NullVisitor implements org.apache.bcel.Constants
{
    CompilationUnit cu;

    ClassGen cg;
    ConstantPoolGen cp;
    InstructionList il;
    MethodGen  mg;
    MyInstructionFactory factory;
    String className;
    String packageName;
    File outputDirectory;

    public CodeGenVisitor(String outputDirectory)
    {
        this.outputDirectory = new File(outputDirectory);
    }

    public void visitCompilationUnit(CompilationUnit ast) throws ParseException
    {
        cu = ast;

        packageName = ast.packageName;

        visitChildren(ast);
    }

    public void visitClassDeclaration(ClassDeclaration ast) throws ParseException
    {
        className = ast.getIdentifier().getName();

        createClass();

        visitChildren(ast);

        closeClass();

    }

    public void visitMethodDeclaration(MethodDeclaration ast) throws ParseException
    {
        createMethod(ast.getIdentifier().getName(), ast.getType(), ast.getParameters());

        visitChildren(ast);

        closeMethod(ast.getType());
    }

    public void visitBinaryExpression(BinaryExpression ast) throws ParseException
    {
        System.out.println("CodeGen visit BinOp " + ast.toString());

        if (ast.getType().equals(Type.STRING))
        {
            generateStringConcat(ast);
        }

        if (ast.op.getId() == JExprConstants.SC_OR || ast.op.getId() == JExprConstants.SC_AND)
        {
            generateConditionalOp(ast);
        }

        ast.E1.accept(this);
        ast.E2.accept(this);

        if (ast.getType() instanceof ReferenceType)
        {
            // should never get here
            throw new RuntimeException("BinaryExpression " + ast.getType());
        }
        else
        {
            assert ast.E1.getType().equals(ast.E1.getType());

            il.append(MyInstructionFactory.createBinaryOp(ast.op, ast.E1.getType()));
        }

    }

    private void generateStringConcat(BinaryExpression ast) throws ParseException
    {
        assert ast.op.getId() == JExprConstants.PLUS;

        // Create StringBuffer
        il.append(factory.createNew(Type.STRINGBUFFER));
        il.append(InstructionConstants.DUP);
        il.append(factory.createInvoke("java.lang.StringBuffer", "<init>", Type.VOID, Type.NO_ARGS, Constants.INVOKESPECIAL));

        ast.E1.accept(this);
        appendToString(ast.E1.getType());


        ast.E2.accept(this);
        appendToString(ast.E2.getType());

        il.append(factory.createInvoke("java.lang.StringBuffer", "toString", Type.STRING, Type.NO_ARGS, Constants.INVOKEVIRTUAL));
    }

    private void appendToString(Type type) throws ParseException
    {
        il.append(factory.createAppend(type));

    }

    private void generateConditionalOp(BinaryExpression ast) throws ParseException
    {
        assert ast.getType().equals(Type.BOOLEAN) && ast.E1.getType().equals(Type.BOOLEAN) && ast.E2.getType().equals(Type.BOOLEAN);

        // Visit the first operand, which will leave a 1 or 0 on TOS.
        ast.E1.accept(this);

        il.append(InstructionConstants.DUP);

        BranchInstruction bi;

        if (ast.op.getId() == JExprConstants.SC_OR)
            bi = new IFNE(null);
        else
            bi = new IFEQ(null);

        il.append(bi);

        il.append(InstructionConstants.POP);

        ast.E2.accept(this);

        bi.setTarget(il.append(InstructionConstants.NOP));

    }

    public void visitUnaryExpression(UnaryExpression ast) throws ParseException
    {
        ast.E.accept(this);

        il.append(factory.createUnaryOp(ast.op, ast.getType()));
        
    }

    public void visitCastExpression(CastExpression ast) throws ParseException
    {
        Type type = ast.getExpression().getType();
        Type targetType = ast.getType();


        ast.getExpression().accept(this);

        if (targetType.equals(type) || (targetType.equals(Type.INT) && (
                                            type.equals(Type.BYTE) ||
                                            type.equals(Type.SHORT) ||
                                            type.equals(Type.CHAR))))
        {
            return;
        }

        il.append(factory.createCast(ast.getExpression().getType(), ast.getType()));

    }

    public void visitIfStatement(IfStatement ast) throws ParseException
    {
        // Visit the expression, which will leave a 1 or 0 on TOS.
        ast.getExpression().accept(this);

        BranchInstruction bi = new IFEQ(null);

        il.append(bi);

        ast.getThenClause().accept(this);

        GOTO g = new GOTO(null);
        il.append(g);

        bi.setTarget(il.append(InstructionConstants.NOP));
        if (ast.getElseClause() != null)
        {
            ast.getElseClause().accept(this);
        }

        g.setTarget(il.append(InstructionConstants.NOP));

    }
    
    
    public void visitFormalParameters(FormalParameters ast) throws ParseException
    {
        //System.out.println("Formal Params: parameters: " + ast.parameters);

        visitChildren(ast);

    }

    public void visitFormalParameter(FormalParameter ast) throws ParseException
    {
        visitChildren(ast);

    }
    
    //jhlee
    public void visitFieldDeclaration(FieldDeclaration ast) throws ParseException
    {
    	System.out.println("Field Gen: " + ast.getName());
    	visitChildren(ast);
    }

    public void visitIdentifierExpression(IdentifierExpression ast) throws ParseException
    {
        System.out.println("Code Gen ID: " + ast.getName());

        VariableDeclaration v = ast.getDeclaration();
        
        if (v == null)
        {
            // Not defined.
        }

        il.append(MyInstructionFactory.createLoad(v.getType(), v.frameIndex));

    }

    public void visitLiteralExpression(LiteralExpression ast) throws ParseException
    {
        Object v = ast.getValue();
        CompoundInstruction i;

        if (v instanceof Boolean)
            i = new PUSH(cp, (Boolean)v);
        else if (v instanceof Integer || v instanceof Short || v instanceof Byte)
            i = new PUSH(cp, ((Number)v).intValue());
        else if (v instanceof Long)
            i = new PUSH(cp, (Long)v);
        else if (v instanceof Character)
            i = new PUSH(cp, (Character)v);
        else if (v instanceof String)
            i = new PUSH(cp, (String)v);
        else if (v instanceof Double)
            i = new PUSH(cp, (Double)v);
        else if (v instanceof Float)
            i = new PUSH(cp, (Float)v);
        else
            throw new Error("Funny literal type: " + v);

        il.append(i);

    }

    public void visitKeywordExpression(KeywordExpression ast) throws ParseException
    {
        // this, super and null.

        Object v = ast.getValue();
        Instruction i;

        if (v == null)
            i = InstructionConstants.ACONST_NULL;
        else
            throw new Error("Not Implemented");

        il.append(i);
        
    }

    public void visitReturnStatement(ReturnStatement ast) throws ParseException
    {
        if (ast.getExpression() == null)
        {
            il.append(MyInstructionFactory.createReturn(null));
            return;
        }

        // Generate the code to leave the expression's value on-stack
        visitChildren(ast);

        // Need to handle implicit casts, such as
        //long x() { byte b = 0; return b; }
        Type exprType = ast.getExpression().getType();

        emitCast(exprType, mg.getReturnType());

        il.append(MyInstructionFactory.createReturn(mg.getReturnType()));

    }

    private void createClass()
    {
        String fullName = packageName == null ? className : packageName + "." + className;

        System.out.println("fullName: " + fullName);

        cg = new ClassGen(fullName, "java.lang.Object",
                         "<generated by cager.jexpr.JEpr>", ACC_PUBLIC | ACC_SUPER,
                         null);
        cp = cg.getConstantPool(); // cg creates constant pool
        il = new InstructionList();
        factory = new MyInstructionFactory(cg);
    }

    private void createMethod(String methodName, Type returnType, FormalParameters formalParams)
    {
        FormalParameter[] params = formalParams.getParameters();
        Type[] paramTypes = new Type[params.length];
        String[] paramNames = new String[params.length];

        for (int i = 0; i < paramTypes.length; i++)
        {
            paramTypes[i] = ((FormalParameter)(params[i])).getType();
            paramNames[i] = ((FormalParameter)(params[i])).getName();
        }

        mg = new MethodGen(ACC_STATIC | ACC_PUBLIC, // access flags
                                returnType,               // return type
                                paramTypes,
                                paramNames,
                                methodName,
                                className,
                                il, cp);

        // Now record the indexes of the arguments (as local variables)

        for (int i = 0; i < params.length; i++)
        {
            params[i].frameIndex = i;
        }
    }

    private void closeMethod(Type returnType)
    {
        System.out.println("Locals: " + mg.getLocalVariableTable(cp).toString());
        mg.setMaxStack();
        cg.addMethod(mg.getMethod());
        System.out.println("closeMethod exit:");
        System.out.println(il.toString(true));
        System.out.println();
        il.dispose(); // Allow instruction handles to be reused
    }

    private void closeClass()
    {
        cg.addEmptyConstructor(ACC_PUBLIC);

        java.io.File directory = new File(outputDirectory, packageName.replace('.', '/'));
        java.io.File classFile = new File(directory, className + ".class");

        try
        {
            cg.getJavaClass().dump(classFile);
            //System.out.println("closeClass: " + classFile.getAbsolutePath());
        }
        catch(java.io.IOException e)
        {
            System.err.println(e);
        }
    }

    private void emitCast(Type from, Type to)
    {
        if (to != null && !from.equals(to))
        {
            il.append(factory.createCast(from, to));
        }
    }


/*
  public Object visitBinaryExpression(BinaryExpression ast, Object o) { return null; }
  public Object visitFormalParameter(FormalParameter ast, Object o) { return null; }
  public Object visitIdentifierExpression(IdentifierExpression ast, Object o) { return null; }
  public Object visitLiteralExpression(LiteralExpression ast, Object o) { return null; }


//  public abstract Object visit( ast, Object o);
*/
    
    /* Ji Hyun: Tentative function to decide the functionality of "requires" expression
    public Object visitRequiresExpression(ReqExpression ast, Object o) throws ParseException
    {
    	if (ast.getReqExpression() != null)
    	{
    		ast.getReqExpression().visit(this, o);
    		BranchInstruction bi = new IFEQ(null);

            il.append(bi);
            bi.setTarget(il.append(InstructionConstants.NOP));
            
            GOTO g = new GOTO(null);
            il.append(g);
            
            //currently the "requires" condition has no influence
            g.setTarget(il.append(InstructionConstants.NOP)); //ATHROW?
    	}
    	return null;
    }
     */
    

}
