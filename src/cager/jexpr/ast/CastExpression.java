package cager.jexpr.ast;

import java.io.BufferedWriter;

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

    public Object visit(Visitor v, Object o, BufferedWriter out) throws ParseException
    {
        return v.visitCastExpression(this, o, out);
    }

    public void dump(int level, BufferedWriter out)
    {  try {
        out.write(dumpPrefix(level) + "Cast: " + type + "\n");
    }
    catch (Exception e) {
    	System.err.println("Error: " + e.getMessage());
    }
        if (expression != null) expression.dump(level + 1, out);
    }
}