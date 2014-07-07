package cager.jexpr.ast;

import java.io.BufferedWriter;

import org.apache.bcel.generic.Type;

public abstract class TypedAST extends AST
{
	protected Type type;

	public TypedAST(AST parent)
	{
		super(parent);
	}

	public TypedAST()
	{
	}

	public void setType(Type type)
	{
		this.type = type;
	}

	public Type getType()
	{
		return type;
	}

    public void dump(int level, BufferedWriter out)
    {   
    	try {
		out.write(dumpPrefix(level) + getClass().toString() + " [Type: " + type.toString() + "]\n");
    	}
    	catch(Exception e){
    		System.err.println("Error: " + e.getMessage());
    	}
		dumpChildren(level + 1, out);
	}
}