package cager.jexpr.ast;

import org.apache.bcel.generic.Type;

import cager.jexpr.*;

public abstract class VariableDeclaration extends TypedAST
{
	public Identifier identifier;
	public int frameIndex = -1;

	public VariableDeclaration(String name, Type type)
	{
		identifier = new Identifier(name);
		setType(type);
	}

    public String getName()
    {
        return identifier.name;
    }

}
