package cager.jexpr.ast;

import java.io.BufferedWriter;

import org.apache.bcel.generic.Type;

import cager.jexpr.*;
import cager.jexpr.visitor.Visitor;

public class UnaryExpression extends Expression
{
    public static final int ADD = 1;
    public static final int SUBTRACT = 2;

    public Operator op = null;
    public Expression E = null;
    public boolean postfix = false;

    public UnaryExpression(Expression E, Operator op, boolean postfix)
    {
        this.E = E;
        this.op = op;
        this.postfix = postfix;
    }

    public UnaryExpression(Expression E, String op, boolean postfix)
    {
        this(E, Operator.getUnary(op), postfix);
    }

//    public Type getType()
//    {
//        if (type == null)
//            type = op.getType(E.getType());
//
//        return type;
//    }

    public AST[] getChildren()
    {
        return new AST[] { E };
    }

    public void accept(Visitor v, BufferedWriter out, String namePredicate) throws ParseException
    {
        v.visitUnaryExpression(this, out, namePredicate);
    }

    public void dump(int level, BufferedWriter out)
    {
    	try {
        out.write(dumpPrefix(level) + "UnaryExpression" + (postfix ? " (postfix)" : " (not postfix)\n"));
    	}
    	catch (Exception e) {
    		System.err.println("Error: " + e.getMessage());
    	}
        if (op != null) op.dump(level + 1, out);
        if (E != null) E.dump(level + 1, out);
    }
}