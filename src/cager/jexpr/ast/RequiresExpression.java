package cager.jexpr.ast;

import java.io.BufferedWriter;

import cager.jexpr.ParseException;
import cager.jexpr.visitor.Visitor;

/**
 * 
 * @author Ji Hyun
 *
 */
public class RequiresExpression extends Expression {
	private Expression e;
	
	public RequiresExpression(Expression exp) {
		this.e = exp;
	}
	
    public Expression getRequiresExpression()
    {
        return e;
    }

    public void setRequiresExpression(Expression exp)
    {
        this.e = exp;
    }

    public AST[] getChildren()
    {
    	if (e == null)
            return new AST[0];
        else
            return new AST[] { e };
    }

    public void accept(Visitor v, BufferedWriter out, String namePredicate) throws ParseException
    {
        v.visitRequiresExpression(this, out, namePredicate);
    }

    public void dump(int level, BufferedWriter out)
    {
    	try {
        out.write(dumpPrefix(level) + "RequiresExpression\n");
    	}
    	catch (Exception e) {
    		System.err.println("Error: " + e.getMessage());
    	}
        if (e != null) e.dump(level + 1, out);
    }
}
