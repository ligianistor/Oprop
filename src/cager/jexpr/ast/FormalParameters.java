package cager.jexpr.ast;

import org.apache.bcel.generic.Type;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;

import cager.jexpr.*;
import cager.jexpr.visitor.Visitor;

public class FormalParameters extends AST   // implements some Map
{
    private List parameters = new ArrayList();
    private FormalParameter[] children = null;

    public FormalParameters()
    {
    }

    public void add(FormalParameter p)
    {
        parameters.add(p);
        children = null;
    }

    public FormalParameter getParameter(int index)
    {
        return (FormalParameter)(getChildren()[index]);
    }

    public FormalParameter getParameter(String paramName)
    {
        FormalParameter[] c = (FormalParameter[])(getChildren());
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
            children = (FormalParameter[])(parameters.toArray(new FormalParameter[0]));
        return children;
    }

    public FormalParameter[] getParameters()
    {
        getChildren();
        return children;
    }

    public Object visit(Visitor v, Object o) throws ParseException
    {
        return v.visitFormalParameters(this, o);
    }

    public void dump(int level)
    {
        System.out.println(dumpPrefix(level) + "FormalParameters:");
        dumpChildren(level + 1);
    }
}