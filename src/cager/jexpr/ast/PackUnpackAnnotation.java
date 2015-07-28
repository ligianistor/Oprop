package cager.jexpr.ast;

import java.io.BufferedWriter;

import cager.jexpr.*;
import cager.jexpr.visitor.Visitor;

public class PackUnpackAnnotation extends Expression
{
    public String name;
    private VariableDeclaration declaration;
    private Expression objPropChild;
    private Expression[] existentialParameters = null;
    private Expression[] children = new Expression[2];


    public PackUnpackAnnotation(String name)
    {
        //System.out.println("IdentifierExpression: " + name);
        this.name = name;
    }
    
    public void add(Expression e)
    {
    	objPropChild = e;
    	children[0] = e;
    }
    
    public void addExistentialParameters(Expression e)
    {
        children[1] = e;
    }

    public String getName()
    {
        return name;
    }

    public VariableDeclaration getDeclaration()
    {
        return declaration;
    }
    
    public Expression getObjPropChild() {
    	return objPropChild;
    }
    
    public Expression[] getExistentialParameters() {
    	return existentialParameters;
    }

    public void setDeclaration(VariableDeclaration d)
    {
        declaration = d;
    }

    public AST[] getChildren()
    {
        if (children == null)
        {
            children = new Expression[] { };
        }

        return children;
    }

    public void accept(Visitor v) throws ParseException
    {
         v.visitPackUnpackAnnotation(this);
    }

    public String toString()
    {
        return "PackUnpackAnnotation: " + name;
    }

    public void dump(int level, BufferedWriter out)
    {  
    	try {
    	out.write(dumpPrefix(level) + "PackUnpackAnnotation: " + name + "\n");
    	}
    	catch (Exception e) {
    		System.err.println("Error: " + e.getMessage());
    	}
    	  dumpChildren(level + 1, out);
        
    }
}
