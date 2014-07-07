/**
 * jhlee
 */

package cager.jexpr.ast;

import java.io.BufferedWriter;

import org.apache.bcel.generic.Type;

import cager.jexpr.*;
import cager.jexpr.visitor.Visitor;

public class QuantifierVariable extends VariableDeclaration
{
    QuantifierVariable()
    {
        super(null, null);
    }

    public QuantifierVariable(String id, Type type)
    {
        super(id, type);
    }

    public AST[] getChildren()
    {
        return new AST[0];
    }

    public Object visit(Visitor v, Object o, BufferedWriter out) throws ParseException
    {
        return v.visitQuantifierVariable(this, o, out);
    }

    public void dump(int level, BufferedWriter out)
    {
    	try {
        out.write(dumpPrefix(level) + "QuantifierVariable: " + getName() + " type: " + type + "\n");
    	}
    	catch (Exception e) {
    		System.err.println("Error: " + e.getMessage());
    	}
    }
}