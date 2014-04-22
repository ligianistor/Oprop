package cager.jexpr.ast;

import org.apache.bcel.generic.Type;

import cager.jexpr.*;
import cager.jexpr.visitor.Visitor;

/**
*   An AST node to represent a Cast, such as <code>(Integer)obj</code>.
*/

public class CastExpression extends Expression
{
    Expression expression;

    public CastExpression(Type type, Expression expression)
    {
        this.type = type;
        this.expression = expression;
    }

    public AST[] getChildren()
    {
        return new AST[] { expression };
    }

    public Expression getExpression()
    {
        return expression;
    }

    public Object visit(Visitor v, Object o) throws ParseException
    {
        return v.visitCastExpression(this, o);
    }

    public void dump(int level)
    {
        System.out.println(dumpPrefix(level) + "Cast: " + type);
        if (expression != null) expression.dump(level + 1);
    }
}