/**
 * jhlee
 */

package cager.jexpr.ast;

import java.io.BufferedWriter;

import org.apache.bcel.generic.Type;

import cager.jexpr.*;
import cager.jexpr.visitor.Visitor;

public class MethodSpecVariable extends VariableDeclaration
{
    MethodSpecVariable()
    {
        super(null, null);
    }

    public MethodSpecVariable(String id, Type type)
    {
        super(id, type);
    }

    public AST[] getChildren()
    {
        return new AST[0];
    }

    public Object visit(Visitor v, Object o, BufferedWriter out) throws ParseException
    {
        return v.visitMethodSpecVariable(this, o, out);
    }

    public void dump(int level, BufferedWriter out)
    {
    	try {
        out.write(dumpPrefix(level) + "MethodSpecVariable: " + getName() + " type: " + type + "\n");
    	}
    	catch (Exception e) {
    		System.err.println("Error: " + e.getMessage());
    	}
    }
}