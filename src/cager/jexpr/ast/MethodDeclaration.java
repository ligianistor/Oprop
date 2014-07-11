/*package cager.jexpr.ast;

import cager.jexpr.*;
import cager.jexpr.visitor.Visitor;

public class MethodDeclaration extends TypedAST
{
    private Identifier name;
    private FormalParameters parameters;
    private RequiresExpression reqexp;
    private EnsuresExpression ensexp;
    private Block block;

    public Identifier getName()
    {
        return name;
    }

    public void setName(Identifier name)
    {
        this.name = name;
    }

    public FormalParameters getParameters()
    {
        return parameters;
    }

    public void setParameters(FormalParameters p)
    {
        parameters = p;
    }
    
    public RequiresExpression getRequiresExpression()
    {
    	return reqexp;
    }
    
    public void setRequiresExpression(RequiresExpression rexp)
    {
    	this.reqexp = rexp;
    }
    
    public EnsuresExpression getEnsuresExpression()
    {
    	return ensexp;
    }
    
    public void setEnsuresExpression(EnsuresExpression eexp)
    {
    	this.ensexp = eexp;
    }

    public Block getBlock()
    {
        return block;
    }

    public void setBlock(Block b)
    {
        block = b;
    }

    public AST[] getChildren()
    {
    	return new AST[] { parameters, reqexp, ensexp, block };
    }

    public Object visit(Visitor v, Object o) throws ParseException
    {
        return v.visitMethodDeclaration(this, o);
    }

    public void dump(int level)
    {
        System.out.println(dumpPrefix(level) + "MethodDeclaration: " + name + "(" + getType() + ")");
        //dumpChildren(level + 1);
        if (parameters != null) parameters.dump(level + 1);
        if (reqexp != null) reqexp.dump(level + 1);
        if (ensexp != null) ensexp.dump(level + 1);
        if (block != null) block.dump(level + 1);
    }
}*/

package cager.jexpr.ast;

import org.apache.bcel.generic.Type;

import java.io.BufferedWriter;
import java.util.Map;
import java.util.HashMap;
import java.util.Vector;
import java.util.List;

import cager.jexpr.*;
import cager.jexpr.visitor.Visitor;

public class MethodDeclaration extends TypedAST
{
    private Identifier identifier;
    private FormalParameters parameters;
    //private Expression exp;
    private MethodSpecExpression msexp;
    //private RequiresExpression rexp;
    //private EnsuresExpression eexp;
    private Block block;
    
    public Identifier getIdentifier()
    {
        return identifier;
    }

    public void setIdentifier(Identifier id)
    {
        this.identifier = id;
    }

    public FormalParameters getParameters()
    {
        return parameters;
    }

    public void setParameters(FormalParameters p)
    {
        parameters = p;
    }
    
    public MethodSpecExpression getMethodSpecExpression()
    {
        return this.msexp;
    }

    public void setMethodSpecExpression(MethodSpecExpression p)
    {
        msexp = p;
    }
    
    /*    
    public Expression getExpression()
    {
        return exp;
    }

    public void setExpression(Expression p)
    {
        exp = p;
    }

    public RequiresExpression getRequiresExpression()
    {
        return rexp;
    }

    public void setRequiresExpression(RequiresExpression p)
    {
        rexp = p;
    }
    
    public EnsuresExpression getEnsuresExpression()
    {
        return eexp;
    }

    public void setEnsuresExpression(EnsuresExpression p)
    {
        eexp = p;
    }
*/  
    public Block getBlock()
    {
        return block;
    }

    public void setBlock(Block b)
    {
        block = b;
    }

    public AST[] getChildren()
    {
    	if (msexp == null) return new AST[] { parameters, block };
        return new AST[] { parameters, msexp, block };
    }

    public void visit(Visitor v, BufferedWriter out) throws ParseException
    {
        v.visitMethodDeclaration(this, out);
    }

    public void dump(int level, BufferedWriter out)
    {
    	try {
        out.write(dumpPrefix(level) + "MethodDeclaration: " + identifier + "(" + getType() + ")\n");
    	}
    	catch(Exception e){
    		System.err.println("Error: " + e.getMessage());
    	}
        dumpChildren(level + 1, out);
    }
}

