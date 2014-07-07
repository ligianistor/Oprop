/**
 * jhlee
 */

package cager.jexpr.ast;

import java.io.BufferedWriter;

import org.apache.bcel.generic.Type;

import cager.jexpr.*;
import cager.jexpr.visitor.Visitor;

public class FieldDeclaration extends VariableDeclaration
{
    public FieldDeclaration(String name, Type type) {
		super(name, type);
	}

    public AST[] getChildren()
    {
        return new AST[] { };
    }

    public Object visit(Visitor v, Object o, BufferedWriter out) throws ParseException
    {
        return v.visitFieldDeclaration(this, o, out);
    }

    public void dump(int level, BufferedWriter out)
    {
    	try {
        out.write(dumpPrefix(level) + "FieldDeclaration: " + getType() + " " + getName() + "\n");
    	}
    	catch (Exception e) {
    		System.err.println("Error: " + e.getMessage());
    	}
        dumpChildren(level + 1, out);
    }
}

