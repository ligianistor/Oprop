package cager.jexpr.ast;

import java.io.BufferedWriter;
import java.util.ArrayList;
import java.util.List;

import cager.jexpr.*;
import cager.jexpr.visitor.Visitor;

public class FormalParameters extends AST   // implements some Map
{
    private List<FormalParameter> parameters = new ArrayList<FormalParameter>();
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

    public void accept(Visitor v) throws ParseException
    {
        v.visitFormalParameters(this);
    }

    public void dump(int level, BufferedWriter out)
    {
    	try {
        out.write(dumpPrefix(level) + "FormalParameters:\n");
    	}
    	catch (Exception e) {
    		System.err.println("Error: " + e.getMessage());
    	}
        dumpChildren(level + 1, out);
    }
}