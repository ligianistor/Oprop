/**
 * jhlee
 */

package cager.jexpr.ast;

import java.io.BufferedWriter;

import cager.jexpr.*;
import cager.jexpr.visitor.Visitor;

public class PredicateDeclaration extends AST
{
    private Identifier identifier;
    private FormalParameters parameters;
    //private ExistentialQuantification existquan;
    private Expression exp;
    
    public Identifier getIdentifier()
    {
        return identifier;
    }

    public void setIdentifier(Identifier id)
    {
        this.identifier = id;
    }

    public FormalParameters getParameters()
    {
        return parameters;
    }

    public void setParameters(FormalParameters p)
    {
        parameters = p;
    }
/*
    public ExistentialQuantification getExistentialQuantification() {
		return existquan;
	}

    public void setExistentialQuantification(ExistentialQuantification eq) {
		this.existquan = eq;
	}
*/
    public Expression getExpression() {
		return exp;
	}

    public void setExpression(Expression ex) {
		this.exp = ex;
	}

    public AST[] getChildren()
    {
        return new AST[] { parameters, exp };
    }

    public void accept(Visitor v, String namePredicate) throws ParseException
    {
        v.visitPredicateDeclaration(this, namePredicate);
    }

    public void dump(int level, BufferedWriter out)
    {
    	try {
        out.write(dumpPrefix(level) + "PredicateDeclaration: " + identifier +"\n");
    	}
    	catch (Exception e) {
    		System.err.println("Error: " + e.getMessage());
    	}
        dumpChildren(level + 1, out);
    }

}

