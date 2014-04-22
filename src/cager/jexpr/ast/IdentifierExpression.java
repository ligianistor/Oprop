package cager.jexpr.ast;

import org.apache.bcel.generic.Type;

import cager.jexpr.*;
import cager.jexpr.visitor.Visitor;

public class IdentifierExpression extends Expression
{
    public String name;
    private VariableDeclaration declaration;

    public IdentifierExpression(String name)
    {
        //System.out.println("IdentifierExpression: " + name);
        this.name = name;
    }

    public String getName()
    {
        return name;
    }

    public VariableDeclaration getDeclaration()
    {
        return declaration;
    }

    public void setDeclaration(VariableDeclaration d)
    {
        declaration = d;
    }

    public AST[] getChildren()
    {
        return new AST[] { };
    }

    public Object visit(Visitor v, Object o) throws ParseException
    {
        return v.visitIdentifierExpression(this, o);
    }

    public String toString()
    {
        return "IdentifierExpression: " + name;
    }

    public void dump(int level)
    {
    	System.out.println(dumpPrefix(level) + "Identifier: " + name);
    			//+ " (Type: " + ( type == null ? "" : type.toString()) + ")");
        
    }
}