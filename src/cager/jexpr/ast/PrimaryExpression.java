package cager.jexpr.ast;

import java.io.BufferedWriter;
import java.util.List;

import cager.jexpr.*;
import cager.jexpr.visitor.Visitor;

public class PrimaryExpression extends Expression
{
    private Expression[] children;

    private List<Expression> components = new java.util.ArrayList<Expression>(8);

    public PrimaryExpression()
    {
    }

    public void add(Expression e)
    {
        components.add(e);
        children = null;
    }
    

    public AST[] getChildren()
    {
        if (children == null)
        {
            children = (Expression[]) (components.toArray(new Expression[0]));
        }

        return children;
    }

    public void accept(Visitor v, String namePredicate) throws ParseException
    {
        v.visitPrimaryExpression(this, namePredicate);
    }

    public void dump(int level, BufferedWriter out)
    {
    	try {
        out.write(dumpPrefix(level) + "PrimaryExpression (Type: " + ( type == null ? "" : type.toString()) + ")\n");
    	}
    	catch(Exception e) {
    		System.err.println("Error: " + e.getMessage());
    	}
        dumpChildren(level + 1, out);
    }
}