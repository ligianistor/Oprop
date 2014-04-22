/**
 * jhlee
 */

package cager.jexpr.ast;

import org.apache.bcel.generic.Type;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;

import cager.jexpr.*;
import cager.jexpr.visitor.Visitor;

public class MethodSpecVariables extends AST   // implements some Map
{
    private List msvariables = new ArrayList();
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

    public Object visit(Visitor v, Object o) throws ParseException
    {
        return v.visitMethodSpecVariables(this, o);
    }

    public void dump(int level)
    {
        System.out.println(dumpPrefix(level) + "MethodSpecVariables:");
        dumpChildren(level + 1);
    }
}