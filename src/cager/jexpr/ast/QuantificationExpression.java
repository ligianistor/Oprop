/**
 * jhlee
 */

package cager.jexpr.ast;

import java.io.BufferedWriter;
import java.util.ArrayList;
import java.util.List;

import cager.jexpr.*;
import cager.jexpr.visitor.Visitor;

public class QuantificationExpression extends Expression
{
	private String quantifier;
	private QuantifierVariables qvs;
	private Expression body;

    public QuantificationExpression()
    {
    }

    public QuantificationExpression(String quantifier, QuantifierVariables qvs, Expression body)
    {
    	assert (quantifier == "exists" || quantifier == "forall");
    	this.quantifier = quantifier;
    	this.qvs = qvs;
        this.body = body;
    }
    
    public String getQuantifier() {
    	return quantifier;
    }
    
    public void setQuantifier(String quantifier) {
    	this.quantifier = quantifier;
    }
    
    public QuantifierVariables getQuantifierVariables() {
    	return qvs;
    }
    
    public void setQuantifierVariables(QuantifierVariables qvs) {
    	this.qvs = qvs;
    }
    
    public Expression getBody() {
    	return body;
    }
    
    public void setBody(Expression b) {
    	this.body = b;
    }

    public AST[] getChildren()
    {
        return new AST[] { qvs, body };
    }

    public void accept(Visitor v) throws ParseException
    {
        v.visitQuantificationExpression(this);
    }

    public void dump(int level, BufferedWriter out)
    {
    	try {
        out.write(dumpPrefix(level) + "QuantificationExpression: " + quantifier + "\n");
    	}
    	catch (Exception e){
    		System.err.println("Error: " + e.getMessage());
    	}
    	
        dumpChildren(level + 1, out);
    }
}

/*
public class ExistentialQuantification extends AST   // implements some Map
{
    private List qvariables = new ArrayList();
    private QuantifierVariable[] children = null;

    public ExistentialQuantification()
    {
    }

    public void add(QuantifierVariable p)
    {
    	qvariables.add(p);
        children = null;
    }

    public QuantifierVariable getQuantifierVariable(int index)
    {
        return (QuantifierVariable)(getChildren()[index]);
    }

    public QuantifierVariable getQuantifierVariable(String paramName)
    {
    	QuantifierVariable[] c = (QuantifierVariable[])(getChildren());
        for (int i = 0; i < c.length; i++)
        {
            if (c[i].getName().equals(paramName))
                return c[i];
        }

        return null;
    }

    public AST[] getChildren()
    {
        if (children == null)
            children = (QuantifierVariable[])(qvariables.toArray(new QuantifierVariable[0]));
        return children;
    }

    public QuantifierVariable[] getQuantifierVariables()
    {
        getChildren();
        return children;
    }

    public Object visit(Visitor v, Object o) throws ParseException
    {
        return v.visitExistentialQuantification(this, o);
    }

    public void dump(int level)
    {
        System.out.println(dumpPrefix(level) + "ExistentialQuantification:");
        dumpChildren(level + 1);
    }
}
*/