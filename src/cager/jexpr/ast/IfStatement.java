package cager.jexpr.ast;

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

    public Object visit(Visitor v, Object o) throws ParseException
    {
        return v.visitIfStatement(this, o);
    }

    public void dump(int level)
    {
        System.out.println(dumpPrefix(level) + "IfStatement");
        if (e != null) e.dump(level + 1);
        if (thenClause != null) thenClause.dump(level + 1);
        if (elseClause != null) elseClause.dump(level + 1);
    }
}