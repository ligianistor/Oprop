package cager.jexpr.ast;

import org.apache.bcel.generic.Type;

import cager.jexpr.*;
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
}
