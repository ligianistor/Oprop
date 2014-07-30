/**
 * jhlee
 */

package cager.jexpr.ast;

import java.io.BufferedWriter;
import java.util.ArrayList;
import java.util.List;

import org.apache.bcel.generic.Type;

import cager.jexpr.*;
import cager.jexpr.visitor.Visitor;

public class CompilationUnits extends AST
{
	private List<CompilationUnit> units = new ArrayList<CompilationUnit>();
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

    public void accept(Visitor v, String namePredicate) throws ParseException
    {
        v.visitCompilationUnits(this, namePredicate);
    }

    public void dump(int level, BufferedWriter out)
    {
    	try {
        out.write(dumpPrefix(level) + "CompilationUnits:\n");
    	}
    	catch(Exception e){
    		System.err.println("Error: " + e.getMessage());
    	}
        dumpChildren(level + 1, out);
    }
	
}
