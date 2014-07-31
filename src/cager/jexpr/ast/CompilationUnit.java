package cager.jexpr.ast;

import java.io.BufferedWriter;

import org.apache.bcel.generic.Type;

import cager.jexpr.*;
import cager.jexpr.visitor.Visitor;

public class CompilationUnit extends AST
{
    public ClassDeclaration classDeclaration;
    public String packageName;

    public CompilationUnit(String packageName, ClassDeclaration classDeclaration)
    {
        this.packageName = packageName;
        this.classDeclaration = classDeclaration;
    }

    public ClassDeclaration getClassDeclaration()
    {
    	return classDeclaration;
    }
    
    public String getPackageName()
    {
    	return packageName;
    }
    
    public AST[] getChildren()
    {
        return new AST[] { classDeclaration };
    }

    public void accept(Visitor v) throws ParseException
    {
         v.visitCompilationUnit(this);
    }

    public void dump(int level, BufferedWriter out)
    {  
    	try { 
        out.write(dumpPrefix(level) + "CompilationUnit:\n" );
    	}
    	catch (Exception e) {
    		System.err.println("Error: " + e.getMessage());
    	}
        dumpChildren(level + 1, out);
    }
}
