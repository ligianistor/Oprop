package cager.jexpr.ast;

import java.io.BufferedWriter;
import java.util.List;

import cager.jexpr.*;
import cager.jexpr.visitor.Visitor;

public class FractionAnnotation extends Expression
{
    public String name;
    private VariableDeclaration declaration;
    private Expression[] children;

    private List<Expression> components = new java.util.ArrayList<Expression>(8);


    public FractionAnnotation(String name)
    {
        this.name = name;
    }
    

    public void add(Expression e)
    {
        components.add(e);
        children = null;
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
        if (children == null)
        {
            children = (Expression[]) (components.toArray(new Expression[0]));
        }

        return children;
    }

    public void accept(Visitor v) throws ParseException
    {
         v.visitFractionAnnotation(this);
    }

    public String toString()
    {
        return "FractionAnnotation: " + name;
    }

    public void dump(int level, BufferedWriter out)
    {  
    	try {
    	out.write(dumpPrefix(level) + "FractionAnnotation: " + name + "\n");
    	}
    	catch (Exception e) {
    		System.err.println("Error: " + e.getMessage());
    	}
    	  dumpChildren(level + 1, out);
        
    }
}

