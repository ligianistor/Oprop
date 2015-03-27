/**
 * lnistor
 */

package cager.jexpr.ast;

import java.io.BufferedWriter;
import java.util.ArrayList;
import java.util.List;

import cager.jexpr.*;
import cager.jexpr.visitor.Visitor;

public class MethodSpecFracVariables extends AST   // implements some Map
{
    private List<MethodSpecFracVariable> msvariables = new ArrayList<MethodSpecFracVariable>();
    private MethodSpecFracVariable[] children = null;

    public MethodSpecFracVariables()
    {
    }

    public void add(MethodSpecFracVariable p)
    {
    	msvariables.add(p);
        children = null;
    }

    public MethodSpecFracVariable getMethodSpecFracVariable(int index)
    {
        return (MethodSpecFracVariable)(getChildren()[index]);
    }

    public MethodSpecFracVariable getMethodSpecFracVariable(String variName)
    {
    	MethodSpecFracVariable[] c = (MethodSpecFracVariable[])(getChildren());
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
            children = (MethodSpecFracVariable[])(msvariables.toArray(new MethodSpecFracVariable[0]));
        return children;
    }

    public MethodSpecFracVariable[] getMethodSpecFracVariables()
    {
        getChildren();
        return children;
    }

    public void accept(Visitor v) throws ParseException
    {
        v.visitMethodSpecFracVariables(this);
    }

    public void dump(int level, BufferedWriter out)
    {
    	try {
        out.write(dumpPrefix(level) + "MethodSpecFracVariables:\n");
    	}
    	catch (Exception e) {
    		System.err.println("Error: " + e.getMessage());
    	}
        dumpChildren(level + 1, out);
    }
}
