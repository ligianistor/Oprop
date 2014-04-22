package cager.jexpr.ast;

import java.util.ArrayList;
import java.util.List;

import org.apache.bcel.generic.Type;
import cager.jexpr.*;
import cager.jexpr.visitor.Visitor;

/**
*   jhlee
*/

public class ArgumentList extends Expression
{
	private List arguments = new ArrayList();
    private Expression[] children = null;

    public ArgumentList()
    {
    }

    public void add(Expression a)
    {
    	arguments.add(a);
        children = null;
    }

    public Expression getArgument(int index)
    {
        return (Expression)(getChildren()[index]);
    }

    public Expression getArgument(Expression argumExpr)
    {
    	Expression[] c = (Expression[])(getChildren());
        for (int i = 0; i < c.length; i++)
        {
            if (c[i].equals(argumExpr))
                return c[i];
        }

        return null;
    }

    public Expression[] getArguments()
    {
        getChildren();
        return children;
    }

    public AST[] getChildren()
    {
        if (children == null)
            children = (Expression[])(arguments.toArray(new Expression[0]));
        return children;
    }    
    
    public Object visit(Visitor v, Object o) throws ParseException
    {
        return v.visitArgumentList(this, o);
    }

    public void dump(int level)
    {
        System.out.println(dumpPrefix(level) + "ArgumentList: ");
        dumpChildren(level + 1);
    }
	

    
}
