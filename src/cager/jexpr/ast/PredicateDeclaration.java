/**
 * jhlee
 */

package cager.jexpr.ast;

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

    public Object visit(Visitor v, Object o) throws ParseException
    {
        return v.visitPredicateDeclaration(this, o);
    }

    public void dump(int level)
    {
        System.out.println(dumpPrefix(level) + "PredicateDeclaration: " + identifier );
        dumpChildren(level + 1);
    }

}

