package cager.jexpr.ast;

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

    public Object visit(Visitor v, Object o) throws ParseException
    {
        return v.visitWhileStatement(this, o);
    }

    public void dump(int level)
    {
        System.out.println(dumpPrefix(level) + "WhileStatement");
        if (e != null) e.dump(level + 1);
        if (doClause != null) doClause.dump(level + 1);
    }
}