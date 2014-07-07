package cager.jexpr.ast;

import java.io.BufferedWriter;

import org.apache.bcel.generic.Type;

import cager.jexpr.*;
import cager.jexpr.visitor.Visitor;

public class LiteralExpression extends Expression
{
    public Object value;

    public LiteralExpression(Object value)
    {
        this(value, null);
    }

    public LiteralExpression(Object value, Type type)
    {
        this.value = value;
        if (type == null)
        {
            if (value instanceof String)
                this.type = Type.STRING;
            else if (value instanceof Integer)
                this.type = Type.INT;
            else if (value instanceof Long)
                this.type = Type.LONG;
            else if (value instanceof Short)
                this.type = Type.SHORT;
            else if (value instanceof Character)
                this.type = Type.CHAR;
            else if (value instanceof Boolean)
                this.type = Type.BOOLEAN;
            else if (value instanceof Byte)
                this.type = Type.BYTE;
            else if (value instanceof Double)
                this.type = Type.DOUBLE;
            else if (value instanceof Float)
                this.type = Type.FLOAT;
            else if (value instanceof Character)
                this.type = Type.CHAR;
            else
                throw new Error("Invalid type: " + value);
        }
        else
        {
            this.type = type;
        }
    }

    public AST[] getChildren()
    {
        return new AST[] { };
    }

    public Object getValue()
    {
        return value;
    }

    public Object visit(Visitor v, Object o, BufferedWriter out) throws ParseException
    {
        return v.visitLiteralExpression(this, o, out);
    }

    public void dump(int level, BufferedWriter out)
    { try {
        out.write(dumpPrefix(level) + "Literal: " + value + " (Type: " + 
                 ( type == null ? "" : ": " + type.toString()) + ")\n");
    }
    catch(Exception e)
    {
    	System.err.println("Error: " + e.getMessage());
    }
    dumpChildren(level + 1, out);
    }
}