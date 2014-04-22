package cager.jexpr.ast;

import cager.jexpr.*;
import cager.jexpr.visitor.Visitor;

public class StatementExpression extends Statement
{
    private Expression e;

    public StatementExpression(Expression e)
    {
        this.e = e;
    }

    public Expression getExpression()
    {
        return e;
    }

    public void setExpression(Expression e)
    {
        this.e = e;
    }

    public AST[] getChildren()
    {
        if (e == null)
            return new AST[0];
        else
            return new AST[] { e };
    }

    public Object visit(Visitor v, Object o) throws ParseException
    {
        return v.visitStatementExpression(this, o);
    }

    public void dump(int level)
    {
        System.out.println(dumpPrefix(level) + "StatementExpression");
        if (e != null) e.dump(level + 1);
    }
}