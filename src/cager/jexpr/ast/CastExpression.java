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

    public void accept(Visitor v, BufferedWriter out, String namePredicate) throws ParseException
    {
        v.visitCastExpression(this, out, namePredicate);
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