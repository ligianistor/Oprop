package cager.jexpr.ast;

import java.io.BufferedWriter;

import cager.jexpr.*;
import cager.jexpr.visitor.Visitor;

public class IfStatement extends Statement
{
    private Expression e;
    private Statement thenClause;
    private Statement elseClause;

    public Expression getExpression()
    {
        return e;
    }

    public void setExpression(Expression e)
    {
        this.e = e;
    }

    public Statement getThenClause()
    {
        return thenClause;
    }

    public void setThenClause(Statement s)
    {
        thenClause = s;
    }

    public Statement getElseClause()
    {
        return elseClause;
    }

    public void setElseClause(Statement s)
    {
        elseClause = s;
    }

    public AST[] getChildren()
    {
    	if (elseClause != null)
    		return new AST[] { e, thenClause, elseClause };
    	else
    		return new AST[] { e, thenClause };
    }

    public Object visit(Visitor v, Object o, BufferedWriter out) throws ParseException
    {
        return v.visitIfStatement(this, o, out);
    }

    public void dump(int level, BufferedWriter out)
    {
    	try {
        out.write(dumpPrefix(level) + "IfStatement\n");
    	}
    	catch (Exception e) {
    		System.err.println("Error: " + e.getMessage());
    	}
        if (e != null) e.dump(level + 1, out);
        if (thenClause != null) thenClause.dump(level + 1, out);
        if (elseClause != null) elseClause.dump(level + 1, out);
    }
}