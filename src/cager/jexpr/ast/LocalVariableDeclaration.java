/**
 * jhlee
 */

package cager.jexpr.ast;

import java.io.BufferedWriter;

import org.apache.bcel.generic.Type;

import cager.jexpr.*;
import cager.jexpr.visitor.Visitor;

public class LocalVariableDeclaration extends VariableDeclaration
{
    public LocalVariableDeclaration(String name, Type type) {
		super(name, type);
	}

    public AST[] getChildren()
    {
        return new AST[] { };
    }

    public void accept(Visitor v) throws ParseException
    {
         v.visitLocalVariableDeclaration(this);
    }

    public void dump(int level, BufferedWriter out)
    {
    	try {
        out.write(dumpPrefix(level) + "LocalVariableDeclaration: " + getType() + " " + getName()+"\n");
    	}
    	catch(Exception e) {
    		System.err.println("Error: " + e.getMessage());
    	}
        dumpChildren(level + 1, out);
    }
}

