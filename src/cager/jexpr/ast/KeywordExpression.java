package cager.jexpr.ast;

import java.io.BufferedWriter;

import org.apache.bcel.generic.Type;

import cager.jexpr.*;
import cager.jexpr.visitor.Visitor;

public class KeywordExpression extends Expression
{
    private Object value;

    public KeywordExpression(Object value, Type type)
    {
        this.value = value;
        this.type = type;
    }

    public Object getValue()
    {
        return value;
    }

    public void setValue(Object v)
    {
        value = v;
    }

    public AST[] getChildren()
    {
        return new AST[] { };
    }

    public void accept(Visitor v, String namePredicate) throws ParseException
    {
        v.visitKeywordExpression(this, namePredicate);
    }

    public void dump(int level)
    {
        System.out.println(dumpPrefix(level) + "Keyword: " + value);
    }
}