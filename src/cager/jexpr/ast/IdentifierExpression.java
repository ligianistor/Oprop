package cager.jexpr.ast;

import java.io.BufferedWriter;

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

    public void visit(Visitor v, BufferedWriter out) throws ParseException
    {
         v.visitIdentifierExpression(this, out);
    }

    public String toString()
    {
        return "IdentifierExpression: " + name;
    }

    public void dump(int level, BufferedWriter out)
    {  
    	try {
    	out.write(dumpPrefix(level) + "Identifier: " + name + "\n");
    	}
    	catch (Exception e) {
    		System.err.println("Error: " + e.getMessage());
    	}
    			//+ " (Type: " + ( type == null ? "" : type.toString()) + ")");
        
    }
}