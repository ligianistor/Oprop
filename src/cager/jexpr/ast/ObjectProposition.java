/**
 * jhlee
 */

package cager.jexpr.ast;

import java.io.BufferedWriter;

import cager.jexpr.ParseException;
import cager.jexpr.visitor.Visitor;

public class ObjectProposition extends Expression {
	private Expression obj;
	private Expression frac; //a number with type Frac that ranges from 0 to 100
	private Expression pd;
	
	public ObjectProposition(Expression obj, Expression frac, Expression pd) {
		this.obj = obj;
		this.frac = frac;
		this.pd = pd;
	}
	
	public TypedAST getObject()
    {
        return obj;
    }

    public void setObject(Expression obj)
    {
        this.obj = obj;
    }
    
    public Expression getFraction()
    {
        return frac;
    }

    public void setFraction(Expression frac)
    {
        this.frac = frac;
    }
	
    public Expression getPredicateDeclaration()
    {
        return pd;
    }

    public void setPredicateDeclaration(Expression pd)
    {
        this.pd = pd;
    }

    public AST[] getChildren()
    {
    	return new AST[] { obj, frac, pd };
    }

    public void visit(Visitor v, BufferedWriter out) throws ParseException
    {
        v.visitObjectProposition(this, out);
    }

    public void dump(int level, BufferedWriter out)
    {
    	try {
        out.write(dumpPrefix(level) + "ObjectProposition: \n" );
    	}
    	catch (Exception e) {
    		System.err.println("Error: " + e.getMessage());
    	}
        dumpChildren(level + 1, out);
    }
}
