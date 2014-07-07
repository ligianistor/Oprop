package cager.jexpr.ast;

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
