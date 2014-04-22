package cager.jexpr.ast;

import java.util.ArrayList;
import java.util.List;

import cager.jexpr.*;
import cager.jexpr.visitor.Visitor;

public class AllocationExpression extends Expression
{
	private String alloc_func;
	private Expression body;
	
    public AllocationExpression(String alloc, Expression body)
    {
    	this.alloc_func = alloc;
        this.body = body;
    }
   
    public AST[] getChildren()
    {
        return new AST[] { body };
    }

    public Object visit(Visitor v, Object o) throws ParseException
    {
        return v.visitAllocationExpression(this, o);
    }

    public void dump(int level)
    {
        System.out.println(dumpPrefix(level) + "AllocationExpression: new " + alloc_func);
        dumpChildren(level + 1);
    }
}

/*
public class ExistentialQuantification extends AST   // implements some Map
{
    private List qvariables = new ArrayList();
    private QuantifierVariable[] children = null;

    public ExistentialQuantification()
    {
    }

    public void add(QuantifierVariable p)
    {
    	qvariables.add(p);
        children = null;
    }

    public QuantifierVariable getQuantifierVariable(int index)
    {
        return (QuantifierVariable)(getChildren()[index]);
    }

    public QuantifierVariable getQuantifierVariable(String paramName)
    {
    	QuantifierVariable[] c = (QuantifierVariable[])(getChildren());
        for (int i = 0; i < c.length; i++)
        {
            if (c[i].getName().equals(paramName))
                return c[i];
        }

        return null;
    }

    public AST[] getChildren()
    {
        if (children == null)
            children = (QuantifierVariable[])(qvariables.toArray(new QuantifierVariable[0]));
        return children;
    }

    public QuantifierVariable[] getQuantifierVariables()
    {
        getChildren();
        return children;
    }

    public Object visit(Visitor v, Object o) throws ParseException
    {
        return v.visitExistentialQuantification(this, o);
    }

    public void dump(int level)
    {
        System.out.println(dumpPrefix(level) + "ExistentialQuantification:");
        dumpChildren(level + 1);
    }
}
*/