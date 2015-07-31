/**
 * jhlee
 */

package cager.jexpr.ast;

import java.io.BufferedWriter;

import cager.jexpr.ParseException;
import cager.jexpr.visitor.Visitor;

public class ObjectProposition extends Expression {
	private boolean isUnpacked = false;
	private Expression obj;
	private Expression frac; //a number with type Frac that ranges from 0 to 100
	private Expression pd;
	
	public ObjectProposition(boolean b, Expression obj, Expression frac, Expression pd) {
		this.isUnpacked = b;
		this.obj = obj;
		this.frac = frac;
		this.pd = pd;
	}
	
	public boolean getIsUnpacked() {
		return isUnpacked;
	}
	
	public TypedAST getObject()
    {
        return obj;
    }

    public void setObject(Expression obj)
    {
        this.obj = obj;
    }
    
    public void setIsUnpacked(boolean b) {
    	this.isUnpacked = b;
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

    public void accept(Visitor v) throws ParseException
    {
        v.visitObjectProposition(this);
    }

    public void dump(int level, BufferedWriter out)
    {
    	try {
    		if (isUnpacked){
        out.write(dumpPrefix(level) + " Unpacked ObjectProposition: \n" );
    		} else{
    			out.write(dumpPrefix(level) + "ObjectProposition: \n" );
    		}
    	}
    	catch (Exception e) {
    		System.err.println("Error: " + e.getMessage());
    	}
        dumpChildren(level + 1, out);
    }
}
