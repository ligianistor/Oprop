package cager.jexpr.ast;

import java.io.BufferedWriter;

import cager.jexpr.*;
import cager.jexpr.visitor.Visitor;

public class PackUnpackAnnotation extends Expression
{
    public String name;
    private VariableDeclaration declaration;
    private Expression objPropChild;


    public PackUnpackAnnotation(String name)
    {
        //System.out.println("IdentifierExpression: " + name);
        this.name = name;
    }
    
    public void add(Expression e)
    {
    	objPropChild = e;
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
    	AST[] children = new AST[1];
    	children[0] = objPropChild;
    	return children;
    }

    public void accept(Visitor v) throws ParseException
    {
         v.visitPackUnpackAnnotation(this);
    }

    public String toString()
    {
        return "PackUnpackAnnotation: " + name;
    }

    public void dump(int level, BufferedWriter out)
    {  
    	try {
    	out.write(dumpPrefix(level) + "PackUnpackAnnotation: " + name + "\n");
    	}
    	catch (Exception e) {
    		System.err.println("Error: " + e.getMessage());
    	}
    	  dumpChildren(level + 1, out);
        
    }
}
