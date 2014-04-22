/**
 * jhlee
 */

package cager.jexpr.ast;

import java.util.ArrayList;
import java.util.List;

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

    public Object visit(Visitor v, Object o) throws ParseException
    {
        return v.visitFieldDeclaration(this, o);
    }

    public void dump(int level)
    {
        System.out.println(dumpPrefix(level) + "FieldDeclaration: " + getType() + " " + getName());
        dumpChildren(level + 1);
    }
}

