package cager.jexpr.ast;

import java.io.BufferedWriter;

import cager.jexpr.*;
import cager.jexpr.visitor.Visitor;

public class WhileStatement extends Statement
{
    private Expression e;
    private Statement doClause;

    public Expression getExpression()
    {
        return e;
    }

    public void setExpression(Expression e)
    {
        this.e = e;
    }

    public Statement getDoClause()
    {
        return doClause;
    }

    public void setDoClause(Statement s)
    {
        doClause = s;
    }

    public AST[] getChildren()
    {
    	return new AST[] { e, doClause };
    }

    public Object visit(Visitor v, Object o, BufferedWriter out) throws ParseException
    {
        return v.visitWhileStatement(this, o, out);
    }

    public void dump(int level, BufferedWriter out)
    {
    	try {
        out.write(dumpPrefix(level) + "WhileStatement\n");
    	}
    	catch (Exception e)
    	{
    		System.err.println("Error: " + e.getMessage());
    	}
        if (e != null) e.dump(level + 1, out);
        if (doClause != null) doClause.dump(level + 1, out);
    }
}