/**
 * jhlee
 */

package cager.jexpr.ast;

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

    public Object visit(Visitor v, Object o) throws ParseException
    {
        return v.visitQuantifierVariable(this, o);
    }

    public void dump(int level)
    {
        System.out.println(dumpPrefix(level) + "QuantifierVariable: " + getName() + " type: " + type);
    }
}