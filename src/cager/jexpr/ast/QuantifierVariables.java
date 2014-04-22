package cager.jexpr.ast;

import java.util.ArrayList;
import java.util.List;

import cager.jexpr.*;
import cager.jexpr.visitor.Visitor;

public class QuantifierVariables extends AST   // implements some Map
{
    private List qvari = new ArrayList();
    private QuantifierVariable[] children = null;

    public QuantifierVariables()
    {
    }

    public void add(QuantifierVariable p)
    {
        qvari.add(p);
        children = null;
    }

    public QuantifierVariable getQuantifierVariable(int index)
    {
        return (QuantifierVariable)(getChildren()[index]);
    }

    public QuantifierVariable getQuantifierVariable(String variName)
    {
    	QuantifierVariable[] c = (QuantifierVariable[])(getChildren());
        for (int i = 0; i < c.length; i++)
        {
            if (c[i].getName().equals(variName))
                return c[i];
        }

        return null;
    }

    public AST[] getChildren()
    {
        if (children == null)
            children = (QuantifierVariable[])(qvari.toArray(new QuantifierVariable[0]));
        return children;
    }

    public QuantifierVariable[] getQuantifierVariables()
    {
        getChildren();
        return children;
    }

    public Object visit(Visitor v, Object o) throws ParseException
    {
        return v.visitQuantifierVariables(this, o);
    }

    public void dump(int level)
    {
        System.out.println(dumpPrefix(level) + "QuantifierVariables:");
        dumpChildren(level + 1);
    }
}