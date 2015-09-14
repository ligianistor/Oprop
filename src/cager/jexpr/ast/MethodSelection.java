package cager.jexpr.ast;

import java.io.BufferedWriter;

import cager.jexpr.*;
import cager.jexpr.visitor.Visitor;

public class MethodSelection extends Expression
{
    private Identifier id;
    private Expression[] parameters = null;
    private Expression[] existentialParameters = null;
    private Expression[] children = new Expression[2];

    public MethodSelection(Identifier id)
    {
        this.id = id;
    }
    
    public void add(Expression e)
    {
        children[0] = e;
    }
    
    public void addExistentialParameters(Expression e)
    {
        children[1] = e;
    }

    public Identifier getIdentifier()
    {
    	return id;
    }
    
    public void setParameters(Expression[] p)
    {
        parameters = p;
    }

    public Expression[] getParameters()
    {
        return parameters;
    }
    
    public void setExistentialParameters(Expression[] p)
    {
        existentialParameters = p;
    }

    public Expression[] getExistentialParameters()
    {
        return existentialParameters;
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
        v.visitMethodSelection(this);
    }

    public void dump(int level, BufferedWriter out)    
    {
    	try {
        out.write(dumpPrefix(level) + "MethodSelection (Type: " + ( type == null ? "" : type.toString()) + ")\n");
        out.write(dumpPrefix(level + 1) + "Identifier=" + id+"\n");}
    	catch (Exception e) {
    		System.err.println("Error: " + e.getMessage());
    	}
    	 dumpChildren(level + 1, out);
    }
}