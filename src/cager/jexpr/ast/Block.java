package cager.jexpr.ast;

import org.apache.bcel.generic.Type;

import java.io.BufferedWriter;
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
    private List<Statement> statements = new java.util.ArrayList<Statement>();

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

    public void visit(Visitor v, BufferedWriter out) throws ParseException
    {
         v.visitBlock(this, out);
    }

    public void dump(int level, BufferedWriter out)
    {   try {
        out.write(dumpPrefix(level) + "Block\n");
    }
    catch (Exception e) {
    	System.err.println("Error: " + e.getMessage());
    }
        dumpChildren(level + 1, out);
    }
}
