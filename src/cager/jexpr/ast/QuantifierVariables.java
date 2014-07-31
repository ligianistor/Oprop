package cager.jexpr.ast;

import java.io.BufferedWriter;
import java.util.ArrayList;
import java.util.List;

import cager.jexpr.*;
import cager.jexpr.visitor.Visitor;

public class QuantifierVariables extends AST   // implements some Map
{
    private List<QuantifierVariable> qvari = new ArrayList<QuantifierVariable>();
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

    public void accept(Visitor v) throws ParseException
    {
        v.visitQuantifierVariables(this);
    }

    public void dump(int level, BufferedWriter out)
    {
    	try {
        out.write(dumpPrefix(level) + "QuantifierVariables:\n");
    	}
    	catch (Exception e) {
    		System.err.println("Error: " + e.getMessage());
    	}
        dumpChildren(level + 1, out);
    }
}