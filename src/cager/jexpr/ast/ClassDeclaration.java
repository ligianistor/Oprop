/*package cager.jexpr.ast;

import org.apache.bcel.generic.Type;
import java.util.Map;
import java.util.HashMap;
import java.util.Vector;
import java.util.List;

import cager.jexpr.*;
import cager.jexpr.visitor.Visitor;

public class ClassDeclaration extends AST
{
    private List methods = new java.util.ArrayList();
    private MethodDeclaration[] children_md = null;
    
    private List predicates = new java.util.ArrayList();
    private PredicateDeclaration[] children_pd = null;
    private Identifier name;

    public Identifier getName()
    {
        return name;
    }

    public void setName(Identifier name)
    {
        this.name = name;
    }

    public void addMethod(MethodDeclaration md)
    {
        methods.add(md);
        children_md = null;
    }

    public MethodDeclaration getMethod(int index)
    {
        return (MethodDeclaration)(getChildren()[index]);
    }

    public MethodDeclaration getMethod(String methodName)
    {
        MethodDeclaration[] c = (MethodDeclaration[])(getChildren());
        for (int i = 0; i < c.length; i++)
        {
            if (c[i].getName().equals(methodName))
                return c[i];
        }

        return null;
    }

    public void addPredicate(PredicateDeclaration pd)
    {
    	predicates.add(pd);
        children_pd = null;
    }

    public PredicateDeclaration getPredicate(int index)
    {
        return (PredicateDeclaration)(getChildren()[index]);
    }

    public PredicateDeclaration getPredicate(String predicateName)
    {
    	PredicateDeclaration[] p = (PredicateDeclaration[])(getChildren());
        for (int i = 0; i < p.length; i++)
        {
            if (p[i].getName().equals(predicateName))
                return p[i];
        }

        return null;
    }
    
    public AST[] getChildren()
    {
        if (children_md == null)
        	children_md = (MethodDeclaration[])(methods.toArray(new MethodDeclaration[0]));
        return children_md;
    }

    public MethodDeclaration[] getMethods()
    {
        getChildren();
        return children_md;
    }

    public Object visit(Visitor v, Object o) throws ParseException
    {
        return v.visitClassDeclaration(this, o);
    }

    public void dump(int level)
    {
        System.out.println(dumpPrefix(level) + "ClassDeclaration: " + name);
        dumpChildren(level + 1);
    }
}*/

/*
package cager.jexpr.ast;

import java.util.List;

import cager.jexpr.*;
import cager.jexpr.visitor.Visitor;

public class ClassDeclaration extends AST
{
    private List methods = new java.util.ArrayList();
    private MethodDeclaration[] children_md = null;
    
    private List predicates = new java.util.ArrayList();
    private PredicateDeclaration[] children_pd = null;
    
    private Identifier name;

    public Identifier getName()
    {
        return name;
    }

    public void setName(Identifier name)
    {
        this.name = name;
    }

    public void add(MethodDeclaration md)
    {
        methods.add(md);
        children_md = null;
    }
    
    // Ji Hyun
    public void addPredicate(PredicateDeclaration pd)
    {
    	predicates.add(pd);
        children_pd = null;
    }

    public MethodDeclaration getMethod(int index)
    {
        //return (MethodDeclaration)(getChildren()[index]);
    }

    public MethodDeclaration getMethod(String methodName)
    {
        MethodDeclaration[] c = (MethodDeclaration[])(getChildren());
        for (int i = 0; i < c.length; i++)
        {
            if (c[i].getName().equals(methodName))
                return c[i];
        }

        return null;
    }

    public AST[] getChildren()
    {
        //if (children == null)
            //children = (MethodDeclaration[])(methods.toArray(new MethodDeclaration[0]));
        //return children;
    	if (children_pd == null)
    		children_pd = (PredicateDeclaration[])(predicates.toArray(new PredicateDeclaration[0]));
    	if (children_md == null)
    		children_md = (MethodDeclaration[])(methods.toArray(new MethodDeclaration[0]));
    	
    	int i;
    	int pd_len = children_pd.length;
    	int md_len = children_md.length;
    	AST[] children = new AST[md_len + pd_len];
    	
    	for (i = 0; i < pd_len; i++) 
    		children[i] = children_pd[i];
    	for (i = 0; i < md_len; i++)
    		children[i + pd_len] = children_md[i];
    		
    	return children;
    }

    public MethodDeclaration[] getMethods()
    {
        getChildren();
        return children_md;
    }

    public Object visit(Visitor v, Object o) throws ParseException
    {
        return v.visitClassDeclaration(this, o);
    }

    public void dump(int level)
    {
        System.out.println(dumpPrefix(level) + "ClassDeclaration: " + name);
        dumpChildren(level + 1);
    }
}*/

package cager.jexpr.ast;

import java.io.BufferedWriter;
import java.util.List;
import java.util.ArrayList;

import cager.jexpr.*;
import cager.jexpr.visitor.Visitor;

public class ClassDeclaration extends AST
{
	private List<FieldDeclaration> fields = new ArrayList<FieldDeclaration>(); //jhlee
	private List<ConstructorDeclaration> constructors = new ArrayList<ConstructorDeclaration>(); //jhlee
	private List<PredicateDeclaration> predicates = new ArrayList<PredicateDeclaration>(); //jhlee 
    private List<MethodDeclaration> methods = new ArrayList<MethodDeclaration>();
    //private MethodDeclaration[] children = null;
    private AST[] children = null; //jhlee
    
    private Identifier identifier;

    public Identifier getIdentifier()
    {
        return identifier;
    }

    public void setIdentifier(Identifier identifier)
    {
        this.identifier = identifier;
    }

    public void addField(FieldDeclaration fd)
    {
        fields.add(fd);
        children = null;
    }
    
    public void addConstructor(ConstructorDeclaration cond)
    {
    	constructors.add(cond);
        children = null;
    }
    
    public void addPredicate(PredicateDeclaration pd)
    {
    	predicates.add(pd);
        children = null;
    }
    
    public void add(MethodDeclaration md)
    {
        methods.add(md);
        children = null;
    }

    public FieldDeclaration getField(String fieldName)
    {
    	FieldDeclaration[] f = getFields(); //jhlee (MethodDeclaration[])(getChildren());
    	
    	for (int i = 0; i < f.length; i++)
        {
            if (f[i].getName().equals(fieldName))
                return f[i];
        }

        return null;
    }
    
    public MethodDeclaration getMethod(int index)
    {
        return (MethodDeclaration) getMethods()[index]; //jhlee (getChildren()[index]);
    }

    public MethodDeclaration getMethod(String methodName)
    {
        MethodDeclaration[] c = getMethods(); //jhlee (MethodDeclaration[])(getChildren());
        
        for (int i = 0; i < c.length; i++)
        {
            if (c[i].getIdentifier().getName().equals(methodName)) {
                return c[i];
            }
        }

        return null;
    }
    
    public MethodDeclaration[] getMethods()
    {
    	
        //getChildren();
        //return children;
    	getChildren();
    	return (MethodDeclaration[]) methods.toArray(new MethodDeclaration[0]);
    }

    public FieldDeclaration[] getFields()
    {
    	
        //getChildren();
        //return children;
    	getChildren();
    	return (FieldDeclaration[]) fields.toArray(new FieldDeclaration[0]);
    }
    
    public AST[] getChildren()
    {
    	
        //if (children == null)
            //children = (MethodDeclaration[])(methods.toArray(new MethodDeclaration[0]));
        //return children;
    	if (children == null) {
    		List<AST> temp = new java.util.ArrayList<AST>();
    		temp.addAll(fields);
    		temp.addAll(constructors);
    		temp.addAll(predicates);
    		temp.addAll(methods);
    		children = (AST[]) temp.toArray(new AST[0]);
    	}
    	return children;
    }

    public void visit(Visitor v, BufferedWriter out) throws ParseException
    {
        v.visitClassDeclaration(this, out);
    }

    public void dump(int level, BufferedWriter out)
    {  
    	try {
        out.write(dumpPrefix(level) + "ClassDeclaration: " + identifier + "\n");
    }
    catch (Exception e) {
    	System.err.println("Error: " + e.getMessage());
    }
        dumpChildren(level + 1, out);
    }
}

