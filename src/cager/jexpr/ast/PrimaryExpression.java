package cager.jexpr.ast;

import java.util.List;

import cager.jexpr.*;
import cager.jexpr.visitor.Visitor;

public class PrimaryExpression extends Expression
{
    private Expression[] children;

    private List components = new java.util.ArrayList(8);

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

    public Object visit(Visitor v, Object o) throws ParseException
    {
        return v.visitPrimaryExpression(this, o);
    }

    public void dump(int level)
    {
        System.out.println(dumpPrefix(level) + "PrimaryExpression (Type: " + ( type == null ? "" : type.toString()) + ")");
        dumpChildren(level + 1);
    }
}