package cager.jexpr.ast;

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

    public Object visit(Visitor v, Object o) throws ParseException
    {
        return v.visitCompilationUnit(this, o);
    }

    public void dump(int level)
    {
        System.out.println(dumpPrefix(level) + "CompilationUnit:" );
        dumpChildren(level + 1);
    }
}
