package cager.jexpr.ast;

import java.io.BufferedWriter;

import cager.jexpr.ParseException;
import cager.jexpr.visitor.Visitor;

/**
*   An abstract AST class to represent a generic expression. Could be a
* binary expression, unary expression, cast expression etc.
*/

public abstract class Expression extends TypedAST
{
    public Expression()
    {
        super();
    }
    
    public void dump(int level, BufferedWriter out)
    {   
    	try {
		out.write("Abstract class Expression\n");
    	}
    	catch(Exception e){
    		System.err.println("Error: " + e.getMessage());
    	}
		dumpChildren(level + 1, out);
	}
    
}
