/**
 * jhlee
 */

package cager.jexpr.ast;

import cager.jexpr.ParseException;
import cager.jexpr.visitor.Visitor;

public class EnsuresExpression extends Expression {
	private Expression e;
	
	public EnsuresExpression(Expression exp) {
		this.e = exp;
	}
	
    public Expression getEnsuresExpression()
    {
        return e;
    }

    public void setEnsuresExpression(Expression exp)
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

    public Object visit(Visitor v, Object o) throws ParseException
    {
        return v.visitEnsuresExpression(this, o);
    }

    public void dump(int level)
    {
        System.out.println(dumpPrefix(level) + "EnsuresExpression");
        if (e != null) e.dump(level + 1);
    }
}
