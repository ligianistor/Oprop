package cager.jexpr.ast;

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

    public void dump(int level)
    {
		System.out.println(dumpPrefix(level) + getClass().toString() + " [Type: " + type.toString() + "]");
		dumpChildren(level + 1);
	}
}