/**
 * jhlee
 */

package cager.jexpr.ast;

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

    public Object visit(Visitor v, Object o) throws ParseException
    {
        return v.visitLocalVariableDeclaration(this, o);
    }

    public void dump(int level)
    {
        System.out.println(dumpPrefix(level) + "LocalVariableDeclaration: " + getType() + " " + getName());
        dumpChildren(level + 1);
    }
}

