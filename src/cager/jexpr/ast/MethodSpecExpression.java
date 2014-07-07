/**
 * jhlee
 */

package cager.jexpr.ast;

import java.io.BufferedWriter;
import java.util.ArrayList;
import java.util.List;

import cager.jexpr.*;
import cager.jexpr.visitor.Visitor;

public class MethodSpecExpression extends Expression
{
	private MethodSpecVariables msvariables;
	private Expression precondition;
	private Expression postcondition;

    public MethodSpecVariables getMethodSpecVariables()
    {
        return msvariables;
    }

    public void setMethodSpecVariables(MethodSpecVariables p)
    {
    	msvariables = p;
    }
    
    public Expression getPrecondition() {
    	return precondition;
    }
    
    public void setPrecondition(Expression b) {
    	this.precondition = b;
    }
    
    public Expression getPostcondition() {
    	return postcondition;
    }
    
    public void setPostcondition(Expression b) {
    	this.postcondition = b;
    }

    public AST[] getChildren()
    {
    	List<AST> result = new ArrayList<AST>();
    	if (msvariables != null)
    		result.add(msvariables);
    	if (precondition != null)
    		result.add(precondition);
    	if (postcondition != null)
    		result.add(postcondition);
    	return result.toArray(new AST[0]);
    }

    public Object visit(Visitor v, Object o, BufferedWriter out) throws ParseException
    {
        return v.visitMethodSpecExpression(this, o, out);
    }

    public void dump(int level, BufferedWriter out)
    {
    	try {
        out.write(dumpPrefix(level) + "MethodSpecExpression: \n" );
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