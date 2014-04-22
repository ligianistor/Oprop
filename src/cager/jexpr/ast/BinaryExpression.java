package cager.jexpr.ast;

import org.apache.bcel.generic.Type;
import cager.jexpr.*;
import cager.jexpr.visitor.Visitor;

/*
*   An AST node to represent a binary expression, such as x + y.
*/

public class BinaryExpression extends Expression
{
    public Operator op = null;
    public Expression E1 = null;
    public Expression E2 = null;
    private Expression[] children;

    public BinaryExpression(Expression E1, Operator op, Expression E2)
    {
        assert op != null;
        this.E1 = E1;
        this.E2 = E2;
        this.op = op;
    }

    public BinaryExpression(Expression E1, String op, Expression E2)
    {
        this(E1, Operator.getBinary(op), E2);
    }

    public AST[] getChildren()
    {
        if (children == null)
        {
            children = new Expression[] { E1, E2 };
        }

        return children;
    }

    public Object visit(Visitor v, Object o) throws ParseException
    {
        return v.visitBinaryExpression(this, o);
    }

    public void dump(int level)
    {
        System.out.println(dumpPrefix(level) + "BinaryExpression" + ( type == null ? "" : ": " + type.toString()));
        if (op != null) op.dump(level + 1);
        if (E1 != null) E1.dump(level + 1);
        if (E2 != null) E2.dump(level + 1);
    }
}