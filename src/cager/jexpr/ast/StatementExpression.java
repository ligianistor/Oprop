package cager.jexpr.ast;

import java.io.BufferedWriter;

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

    public void visit(Visitor v, BufferedWriter out) throws ParseException
    {
        v.visitStatementExpression(this, out);
    }

    public void dump(int level, BufferedWriter out)
    {
    	try {
        out.write(dumpPrefix(level) + "StatementExpression\n");
    	}
    	catch (Exception e) {
    		System.err.println("Error: " + e.getMessage());
    	}
        if (e != null) e.dump(level + 1, out);
    }
}