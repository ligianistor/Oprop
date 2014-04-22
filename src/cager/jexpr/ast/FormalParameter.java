package cager.jexpr.ast;

import org.apache.bcel.generic.Type;

import cager.jexpr.*;
import cager.jexpr.visitor.Visitor;

public class FormalParameter extends VariableDeclaration
{
    FormalParameter()
    {
        super(null, null);
    }

    public FormalParameter(String id, Type type)
    {
        super(id, type);
    }

    public AST[] getChildren()
    {
        return new AST[0];
    }

    public Object visit(Visitor v, Object o) throws ParseException
    {
        return v.visitFormalParameter(this, o);
    }

    public void dump(int level)
    {
        System.out.println(dumpPrefix(level) + "FormalParameter: " + getName() + " type: " + type);
    }
}