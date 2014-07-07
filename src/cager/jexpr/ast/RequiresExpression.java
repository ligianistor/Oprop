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

    public Object visit(Visitor v, Object o, BufferedWriter out) throws ParseException
    {
        return v.visitRequiresExpression(this, o, out);
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
