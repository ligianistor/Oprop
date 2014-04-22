package cager.jexpr.ast;

import org.apache.bcel.generic.Type;
import java.util.Map;
import java.util.HashMap;
import java.util.Vector;
import java.util.List;

import cager.jexpr.*;
import cager.jexpr.visitor.Visitor;

/**
*   An AST node to represent a block of statements.
*   <P>
*   Each statement within the block is held in a list.
*   </P>
*/

public class Block extends Statement
{
    private List statements = new java.util.ArrayList();

    /**
    *   Add a statement to this block's list.
    */
    public void add(Statement s)
    {
        statements.add(s);
    }

    /**
    *   Get the statement indexed by <code>index</code>.
    */
    public Statement getStatement(int index)
    {
        return (Statement)(getChildren()[index]);
    }

    public AST[] getChildren()
    {
        return getStatements();
    }

    /**
    *   Returns children as Statement[].
    */
    public Statement[] getStatements()
    {
        return (Statement[])(statements.toArray(new Statement[statements.size()]));
    }

    public Object visit(Visitor v, Object o) throws ParseException
    {
        return v.visitBlock(this, o);
    }

    public void dump(int level)
    {
        System.out.println(dumpPrefix(level) + "Block");
        dumpChildren(level + 1);
    }
}
