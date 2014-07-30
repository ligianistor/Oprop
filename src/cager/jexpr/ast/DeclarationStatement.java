/**
 * jhlee
 */

package cager.jexpr.ast;

import java.io.BufferedWriter;

import cager.jexpr.*;
import cager.jexpr.visitor.Visitor;

/*
 * DeclarationStatement covers both cases:
 *  1. Just (Declaration) e.g. int i;
 *  2. (Declaration + Initialization) e.g. int i = 0;
 *  
 *  In the first case, Operator and Expression are null.
 */
public class DeclarationStatement extends Statement
{
    private VariableDeclaration vd;
    private Operator op = null;
    private Expression exp = null;

    public DeclarationStatement(VariableDeclaration vd, Operator op, Expression E)
    {
        assert op != null;
        this.vd = vd;
        this.op = op;
        this.exp = E;
    }

    public DeclarationStatement(VariableDeclaration vd, String op, Expression E)
    {
        this(vd, Operator.getBinary(op), E);
    }
    
    public VariableDeclaration getDeclaration()
    {
        return vd;
    }

    public void setDeclaration(VariableDeclaration vd)
    {
        this.vd = vd;
    }

    public AST[] getChildren()
    {
        if (vd == null)
            return new AST[0];
        else if (exp == null)
        	return new AST[] { vd };
        else
            return new AST[] { vd, exp };
    }

    public void accept(Visitor v, String namePredicate) throws ParseException
    {
         v.visitDeclarationStatement(this, namePredicate);
    }

    public void dump(int level, BufferedWriter out)
    {   
    	try {
        out.write(dumpPrefix(level) + "DeclarationStatement\n");
    	}
    	catch(Exception e) {
    		System.err.println("Error: " + e.getMessage());
    	}
        if (vd != null) vd.dump(level + 1, out);
        if (op != null) op.dump(level + 1, out);
        if (exp != null) exp.dump(level + 1, out);
    }
}