package cager.jexpr.ast;

import java.io.BufferedWriter;

import cager.jexpr.*;
import cager.jexpr.visitor.Visitor;

/**
 * The base class for all AST classes.
 *
 * @author  Paul Cager.
 */
public abstract class AST
{
    private AST parent;

    public AST()
    {
    }

    public AST(AST parent)
    {
        this.parent = parent;
    }

    public void setParent(AST parent)
    {
        this.parent = parent;
    }

    public AST getParent()
    {
        return parent;
    }

    public abstract AST[] getChildren();


    /**
    *   Dump this node and all children to System.out.
    */
    public void dump(int level, BufferedWriter out)
    {
    	try {
        out.write(dumpPrefix(level) + getClass().toString() + "\n");
    	}
    	catch (Exception e) {
    		System.err.println("Error: " + e.getMessage());
    	}
        dumpChildren(level + 1, out);
    }

    protected void dumpChildren(int level, BufferedWriter out)
    {
        AST[] children = getChildren();
        for (int i = 0; i < children.length; i++)
            children[i].dump(level, out);
    }

    static protected String dumpPrefix(int level)
    {
        StringBuffer sb = new StringBuffer(level);
        for (int i = 0; i < level * 2; i++)
            sb.append(' ');

        return sb.toString();
    }

    /**
    *   Provided to implement the visitor pattern.
    *   This should be called accept
    */
    public abstract void accept(Visitor v, BufferedWriter out, String namePredicate) throws ParseException;

}