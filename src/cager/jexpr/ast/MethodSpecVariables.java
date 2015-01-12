/**
 * jhlee
 */

package cager.jexpr.ast;

import java.io.BufferedWriter;
import java.util.ArrayList;
import java.util.List;

import cager.jexpr.*;
import cager.jexpr.visitor.Visitor;

public class MethodSpecVariables extends AST   // implements some Map
{
    private List<MethodSpecVariable> msvariables = new ArrayList<MethodSpecVariable>();
    private MethodSpecVariable[] children = null;

    public MethodSpecVariables()
    {
    }

    public void add(MethodSpecVariable p)
    {
    	msvariables.add(p);
        children = null;
    }

    public MethodSpecVariable getMethodSpecVariable(int index)
    {
        return (MethodSpecVariable)(getChildren()[index]);
    }

    public MethodSpecVariable getMethodSpecVariable(String variName)
    {
    	MethodSpecVariable[] c = (MethodSpecVariable[])(getChildren());
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
            children = (MethodSpecVariable[])(msvariables.toArray(new MethodSpecVariable[0]));
        return children;
    }

    public MethodSpecVariable[] getMethodSpecVariables()
    {
        getChildren();
        return children;
    }

    public void accept(Visitor v) throws ParseException
    {
        v.visitMethodSpecVariables(this);
    }

    public void dump(int level, BufferedWriter out)
    {
    	try {
        out.write(dumpPrefix(level) + "MethodSpecVariables:\n");
    	}
    	catch (Exception e) {
    		System.err.println("Error: " + e.getMessage());
    	}
        dumpChildren(level + 1, out);
    }
}