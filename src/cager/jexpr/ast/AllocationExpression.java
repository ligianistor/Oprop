package cager.jexpr.ast;

import java.io.BufferedWriter;

import cager.jexpr.*;
import cager.jexpr.visitor.Visitor;

public class AllocationExpression extends Expression
{
	private String alloc_func;
	private String predicate;
	private Expression body;
	
    public AllocationExpression(String alloc, String predicate, Expression body)
    {
    	this.alloc_func = alloc;
    	this.predicate = predicate;
        this.body = body;
    }
   
    public AST[] getChildren()
    {
        return new AST[] { body };
    }
    
    public String getAlloc_func() {
    	return alloc_func;
    }

    public String getPredicate() {
    	return predicate;
    }
    
    public void accept(Visitor v) throws ParseException
    {
        v.visitAllocationExpression(this);
    }

    public void dump(int level, BufferedWriter out)
    {  try {
        out.write(dumpPrefix(level) + "AllocationExpression: new " + alloc_func + "("+predicate+")\n");
    }
    catch(Exception e){
    	System.err.println("Error: " + e.getMessage());
    }
        dumpChildren(level + 1, out);
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