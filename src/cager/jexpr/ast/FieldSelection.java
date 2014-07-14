package cager.jexpr.ast;

import java.io.BufferedWriter;

import cager.jexpr.*;
import cager.jexpr.visitor.Visitor;

public class FieldSelection extends Expression
{
    private Identifier id;
    private Expression[] parameters = null;
    private Expression[] children;

    public FieldSelection(Identifier id)
    {
        this.id = id;
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

    public AST[] getChildren()
    {
        if (children == null)
        {
            children = new Expression[] { };
        }

        return children;
    }

    public void accept(Visitor v, BufferedWriter out, String namePredicate) throws ParseException
    {
        v.visitFieldSelection(this, out, namePredicate);
        //return "TODO";
    }

    public void dump(int level, BufferedWriter out)    
    {
    	try {
        out.write(dumpPrefix(level) + "FieldSelection (Type: " + ( type == null ? "" : type.toString()) + ")\n");
        out.write(dumpPrefix(level + 1) + "ID=" + id+"\n");}
    	catch (Exception e) {
    		System.err.println("Error: " + e.getMessage());
    	}
    }
}