/**
 * jhlee
 */

package cager.jexpr.ast;

import java.util.ArrayList;
import java.util.List;

import org.apache.bcel.generic.Type;

import cager.jexpr.*;
import cager.jexpr.visitor.Visitor;

public class CompilationUnits extends AST
{
	private List units = new ArrayList();
    private CompilationUnit[] children = null;

    public CompilationUnits()
    {
    }

    public void add(CompilationUnit cu)
    {
        units.add(cu);
        children = null;
    }

    public CompilationUnit getCompilationUnit(int index)
    {
        return (CompilationUnit)(getChildren()[index]);
    }

    public CompilationUnit getCompilationUnit(String ClassName)
    {
    	CompilationUnit[] c = (CompilationUnit[])(getChildren());
        for (int i = 0; i < c.length; i++)
        {
            if (c[i].getClassDeclaration().getIdentifier().getName().equals(ClassName))
                return c[i];
        }

        return null;
    }

    public AST[] getChildren()
    {
        if (children == null)
            children = (CompilationUnit[])(units.toArray(new CompilationUnit[0]));
        return children;
    }

    public CompilationUnit[] getCompilationUnits()
    {
        getChildren();
        return children;
    }

    public Object visit(Visitor v, Object o) throws ParseException
    {
        return v.visitCompilationUnits(this, o);
    }

    public void dump(int level)
    {
        System.out.println(dumpPrefix(level) + "CompilationUnits:");
        dumpChildren(level + 1);
    }
	
}
