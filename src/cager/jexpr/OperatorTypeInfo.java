package cager.jexpr;

import org.apache.bcel.generic.Type;
import org.apache.bcel.Constants;

/**
 * Information about an operation's <I>result type</I>.
 * For the expression <code>12 + 14</code> the result type
 * would be integer, while for the expression
 * <code>12 + "x"</code> the result would be a string.
 *
 * @author  Paul Cager.
 */

public class OperatorTypeInfo
{
    Type resultType;
    Type implicitCast1;
    Type implicitCast2;

    OperatorTypeInfo()
    {
    }

    OperatorTypeInfo(Type resultType, Type implicitCast1, Type implicitCast2)
    {
        this.resultType = resultType;
        this.implicitCast1 = implicitCast1;
        this.implicitCast2 = implicitCast2;
    }

    public String toString()
    {
        return "OperatorTypeInfo: result=" + resultType + ", cast1=" + implicitCast1 + ", cast2=" + implicitCast2;
    }

    public Type getResultType()
    {
        return resultType;
    }

    /** TODO - document these. */
    public Type getImplicitCast1()
    {
        return implicitCast1;
    }

    /** TODO - document these. */
    public Type getImplicitCast2()
    {
        return implicitCast2;
    }
}