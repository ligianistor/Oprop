package cager.jexpr.ast;

import java.io.BufferedWriter;

import org.apache.bcel.generic.Type;

import cager.jexpr.*;
import cager.jexpr.visitor.Visitor;

public class FormalParameter extends VariableDeclaration
{
    FormalParameter()
    {
        super(null, null);
    }

    public FormalParameter(String id, Type type)
    {
        super(id, type);
    }

    public AST[] getChildren()
    {
        return new AST[0];
    }

    public void accept(Visitor v) throws ParseException
    {
        v.visitFormalParameter(this);
    }

    public void dump(int level, BufferedWriter out)
    {
    	try {
        out.write(dumpPrefix(level) + "FormalParameter: " + getName() + " type: " + type+"\n");
    	}
    	catch (Exception e){
    		System.err.println("Error: " + e.getMessage());
    	}
    }
}