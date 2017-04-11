package cager.jexpr;

import java.io.BufferedWriter;
import java.util.*;
import org.apache.bcel.generic.Type;
import org.apache.bcel.Constants;

/**
 * A <I>type resolver</I> implementation for operators.
 * For example the ">" operator always returns a
 * boolean, while the "+" operator can return various types,
 * depending on the types of its arguments.
 *
 * @author  Paul Cager.
 * @see     cager.jexpr.OperatorTypeInfo
 */
public class Operator
{
    int id;
    String name;
    TypeResolver typeResolver;
    boolean binary;

    // Prevent direct creations.
    private Operator(int id,String name, TypeResolver typeResolver, boolean binary)
    {
        this.id = id;
        this.name = name;
        this.typeResolver = typeResolver;
        this.binary = binary;
    }

    public String getName()
    {
        return name;
    }

    public int getId()
    {
        return id;
    }

    private static Map<String, Operator> binaryOperatorMap; //binary also includes assignOperator
    private static Map<String, Operator> unaryOperatorMap;

    private static void putBinary(int id, String name, TypeResolver resolver)
    {
        binaryOperatorMap.put(name, new Operator(id, name, resolver, true));
    }

    private static void putUnary(int id, String name, TypeResolver resolver)
    {
        unaryOperatorMap.put(name, new Operator(id, name, resolver, false));
    }
    

    static
    {
        binaryOperatorMap = new HashMap<String, Operator>();
        unaryOperatorMap = new HashMap<String, Operator>();

        NumericTypeResolver nt = new NumericTypeResolver();
        BooleanTypeResolver bt = new BooleanTypeResolver();
        ComparatorTypeResolver ct = new ComparatorTypeResolver();

        putBinary(JExprConstants.PLUS , "+", new AddTypeResolver());
        putBinary(JExprConstants.MINUS, "-", nt);
        putBinary(JExprConstants.BIT_OR, "|", nt);
        putBinary(JExprConstants.STAR, "*", nt);
        putBinary(JExprConstants.REM, "%", nt);

        putBinary(JExprConstants.SC_AND, "&&", bt);
        putBinary(JExprConstants.SC_OR, "||", bt);
        putBinary(JExprConstants.IMPLIES, "~=>", bt);
        putBinary(JExprConstants.GT, ">", ct);
        putBinary(JExprConstants.GE, ">=", ct);
        putBinary(JExprConstants.EQ, "==", ct);
        putBinary(JExprConstants.NE, "!=", ct);
        putBinary(JExprConstants.LT, "<", ct);
        putBinary(JExprConstants.LE, "<=", ct);

        putBinary(JExprConstants.DOT, ".", new DotTypeResolver());
        
        putBinary(JExprConstants.KEYACCESS, "->", new KeyAccessTypeResolver()); //jhlee
        putBinary(JExprConstants.SLASH, "/", new SlashTypeResolver()); //lnistor
        
        putBinary(JExprConstants.ASSIGN, "=", new AssignTypeResolver()); //jhlee

        putUnary(JExprConstants.INCR, "++", nt);
        putUnary(JExprConstants.DECR, "--", nt);

        putUnary(JExprConstants.BANG, "!", bt);
    }

    public static Operator getBinary(String name)
    {
        return (Operator)binaryOperatorMap.get(name);
    }

    public static Operator getUnary(String name)
    {
        return (Operator)unaryOperatorMap.get(name);
    }
    

    public OperatorTypeInfo getTypeInfo(Type t1, Type t2, BufferedWriter out) throws ParseException
    {
        return typeResolver.binaryOperatorType(this, t1, t2, out);
    }

    public OperatorTypeInfo getTypeInfo(Type t1, BufferedWriter out) throws ParseException
    {
        return typeResolver.unaryOperatorType(this, t1, out);
    }
    

    public void dump(int level, BufferedWriter out)
    {
        StringBuffer sb = new StringBuffer(level);
        for (int i = 0; i < level * 2; i++)
            sb.append(' ');

        sb.append("Operator ");
        sb.append(name);
       try {
        out.write(sb.toString()+ "\n");
       }
       catch (Exception e) {
    	   System.err.println("Error: " + e.getMessage());
       }
      
    }
}

/**
Type Resolver. Given a unary/binary operator and the types of its argument(s), a
OperatortypeInfo object is returned.
*/
abstract class TypeResolver
{
    protected OperatorTypeInfo info = new OperatorTypeInfo();

    public OperatorTypeInfo binaryOperatorType(Operator op, Type t1, Type t2, BufferedWriter out) throws ParseException
    {
        throw new ParseException("Invalid operator: " + t1 + ", " + t2 + " for op " + op.getName());
    }

    public OperatorTypeInfo unaryOperatorType(Operator op, Type t1, BufferedWriter out) throws ParseException
    {
        throw new ParseException("Invalid operator: " + t1 + " for op " + op.getName());
    }
    
}

/*
** JLS - 5.6.2 Binary Numeric Promotion
**  When an operator applies binary numeric promotion to a pair of operands, 
**  each of which must denote a value of a numeric type, the following rules apply, in order, using widening conversion
**  to convert operands as necessary:
**
**  If either operand is of type double, the other is converted to double.
**  Otherwise, if either operand is of type float, the other is converted to float.
**  Otherwise, if either operand is of type long, the other is converted to long.
**  Otherwise, both operands are converted to type int.
*/
class NumericTypeResolver extends TypeResolver
{
    public OperatorTypeInfo binaryOperatorType(Operator op, Type t1, Type t2, BufferedWriter out) throws ParseException
    {
        int typeId1 = t1.getType();
        int typeId2 = t2.getType();

        if (typeId1 == Constants.T_DOUBLE || typeId2 == Constants.T_DOUBLE)
        {
            info.resultType = Type.DOUBLE;
            if (typeId1 != Constants.T_DOUBLE)
                info.implicitCast1 = Type.DOUBLE;
            if (typeId2 != Constants.T_DOUBLE)
                info.implicitCast2 = Type.DOUBLE;
        }
        else if (typeId1 == Constants.T_FLOAT || typeId2 == Constants.T_FLOAT)
        {
            info.resultType = Type.FLOAT;
            if (typeId1 != Constants.T_FLOAT)
                info.implicitCast1 = Type.FLOAT;
            if (typeId2 != Constants.T_FLOAT)
                info.implicitCast2 = Type.FLOAT;
        }
        else if (typeId1 == Constants.T_LONG || typeId2 == Constants.T_LONG)
        {
            info.resultType = Type.LONG;
            if (typeId1 != Constants.T_LONG)
                info.implicitCast1 = Type.LONG;
            if (typeId2 != Constants.T_LONG)
                info.implicitCast2 = Type.LONG;
        }
        else if (Types.isNumericType(t1) && Types.isNumericType(t2))
        {
            info.resultType = Type.INT;
            if (typeId1 != Constants.T_INT)
                info.implicitCast1 = Type.INT;
            if (typeId2 != Constants.T_INT)
                info.implicitCast2 = Type.INT;
        }
        else
        {
            throw new ParseException("Non-numeric Types: " + t1 + ", " + t2 + " for op " + op.getName());
        }

        return info;
    }

    public OperatorTypeInfo unaryOperatorType(Operator op, Type t, BufferedWriter out) throws ParseException
    {
        if (Types.isNumericType(t))
        {
            info.resultType = t;
            return info;
        }
        else
        {
            throw new ParseException("Non-numeric Type: " + t + " for op " + op.getName());
        }
    }
}

/**
The binary "+" operator is a special case, in that it can handle strings / objects.
** TODO - can you not also say Obj1 + Obj2??
*/
class AddTypeResolver extends NumericTypeResolver
{
    public OperatorTypeInfo binaryOperatorType(Operator op, Type t1, Type t2, BufferedWriter out) throws ParseException
    {
    	/*try {
    		out.write("AddTypeResolver: " + t1 + " + " + t2+"\n");
    	}
    	catch (Exception e) {
    		System.err.println("Error: " + e.getMessage());
    	}*/

        if (t1.equals(Type.STRING) || t2.equals(Type.STRING))
        {
            info.resultType = Type.STRING;

            return info;
        }
        else
        {
            return super.binaryOperatorType(op, t1, t2, out);
        }
    }
}

class BooleanTypeResolver extends TypeResolver
{
    public OperatorTypeInfo binaryOperatorType(Operator op, Type t1, Type t2, BufferedWriter out) throws ParseException
    {
    	/*try {
    		out.write(t1 + " " + op.getName() + " " + t2+ "\n");
    	}
    	catch (Exception e) {
    		System.err.println("Error: " + e.getMessage());
    	}*/
        if (t1.getType() != Constants.T_BOOLEAN || t2.getType() != Constants.T_BOOLEAN)
            throw new ParseException("Non-boolean Types: " + t1 + ", " + t2 + " for op " + op.getName());

        info.resultType = Type.BOOLEAN;
        return info;
    }

    public OperatorTypeInfo unaryOperatorType(Operator op, Type t1) throws ParseException
    {
        if (t1.getType() != Constants.T_BOOLEAN)
            throw new ParseException("Non-boolean Type: " + t1 + " for op " + op.getName());

        info.resultType = Type.BOOLEAN;
        return info;
    }
}

class ComparatorTypeResolver extends NumericTypeResolver
{
    public OperatorTypeInfo binaryOperatorType(Operator op, Type t1, Type t2, BufferedWriter out) throws ParseException
    {
        if ((Types.isNumericType(t1) && Types.isNumericType(t2)) ) 
        {
            OperatorTypeInfo opInfo = super.binaryOperatorType(op, t1, t2, out);
            opInfo.resultType = Type.BOOLEAN;

            return opInfo;
        }
        else if (((!Types.isNumericType(t1) && t2.equals(Type.OBJECT)) 
        		|| (!Types.isNumericType(t2) && t1.equals(Type.OBJECT)) 
        		|| (t1.toString().equals(t2.toString())) ) &&  //lnistor
        		(op == Operator.getBinary("==") || op == Operator.getBinary("!="))) //jhlee
        {
        	//Checks the case where we check the null equivalence. e.g. "Hi" == null 
        	info.resultType = Type.BOOLEAN;
        	return info;
        }
        else
        {
            throw new ParseException("Non-numeric Types: " + t1 + ", " + t2 + " for op " + op.getName());
        }
    }
}

class DotTypeResolver extends TypeResolver
{
    public OperatorTypeInfo binaryOperatorType(Operator op, Type t1, Type t2, BufferedWriter out) throws ParseException
    {
        // TODO - a tricky one this. For now...

        throw new ParseException("Dot operator not implemented");
    }
}

/*
 * jhlee
 */
class KeyAccessTypeResolver extends TypeResolver
{
    public OperatorTypeInfo binaryOperatorType(Operator op, Type t1, Type t2, BufferedWriter out) throws ParseException
    {
    	if (t1.equals(t2) || 
    			t2.equals(Type.OBJECT)) 
        {
    		info.resultType = Type.BOOLEAN;
            return info;
        }
        else
        {
            throw new ParseException("Non-matching Types: " + t1 + ", " + t2 + " for op " + op.getName());
        }
    }
}

/*
 * lnistor
 */
class SlashTypeResolver extends TypeResolver
{
    public OperatorTypeInfo binaryOperatorType(Operator op, Type t1, Type t2, BufferedWriter out) throws ParseException
    {
    		info.resultType = Type.DOUBLE;
            return info;    
    }
}

/*
 * jhlee
 */
class AssignTypeResolver extends TypeResolver
{
    public OperatorTypeInfo binaryOperatorType(Operator op, Type t1, Type t2, BufferedWriter out) throws ParseException
    {
    	if (t1.equals(t2))
        {
    		info.resultType = Type.BOOLEAN;
            return info;
        }
        else
        {
            throw new ParseException("Non-matching Types: " + t1 + ", " + t2 + " for op " + op.getName());
        }
    }
}
