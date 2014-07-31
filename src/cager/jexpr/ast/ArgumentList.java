package cager.jexpr.ast;

import java.io.BufferedWriter;
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
	private List<Expression> arguments = new ArrayList<Expression>();
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
    
    public void accept(Visitor v) throws ParseException
    {
       v.visitArgumentList(this);
    }

    public void dump(int level, BufferedWriter out)
    { try {
        out.write(dumpPrefix(level) + "ArgumentList: \n");
    }
    catch(Exception e){
    	System.err.println("Error: " + e.getMessage());
    }
        dumpChildren(level + 1, out);
    }
	

    
}
