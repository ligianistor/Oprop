package cager.jexpr.ast;

import org.apache.bcel.generic.Type;

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

    public Object visit(Visitor v, Object o) throws ParseException
    {
        return v.visitFieldSelection(this, o);
        //return "TODO";
    }

    public void dump(int level)    
    {
        System.out.println(dumpPrefix(level) + "FieldSelection (Type: " + ( type == null ? "" : type.toString()) + ")");
        System.out.println(dumpPrefix(level + 1) + "ID=" + id);
    }
}