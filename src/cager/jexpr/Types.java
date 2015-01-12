package cager.jexpr;

import org.apache.bcel.generic.Type;
import org.apache.bcel.generic.BasicType;
import org.apache.bcel.generic.ObjectType;
import org.apache.bcel.Constants;

public class Types
{
    // Prevent direct creations.
    private Types()
    {
    }

    public static boolean isNumericType(Type type)
    {
        return
            type.equals(Type.BYTE) ||
            type.equals(Type.CHAR) ||
            type.equals(Type.DOUBLE) ||
            type.equals(Type.FLOAT) ||
            type.equals(Type.INT) ||
            type.equals(Type.LONG) ||
            type.equals(Type.SHORT);
    }

    public static boolean isBooleanType(Type type)
    {
        return type.equals(Type.BOOLEAN);
    }

    public static Type getType(String type)
    {
        if (type.equals("boolean"))
            return Type.BOOLEAN;
        else if (type.equals("byte"))
            return Type.BYTE;
        else if (type.equals("char"))
            return Type.CHAR;
        else if (type.equals("double"))
            return Type.DOUBLE;
        else if (type.equals("float"))
            return Type.FLOAT;
        else if (type.equals("int"))
            return Type.INT;
        else if (type.equals("long"))
            return Type.LONG;
        else if (type.equals("java.lang.Object") || type.equals("Object"))
            return Type.OBJECT;
        else if (type.equals("short"))
            return Type.SHORT;
        else if (type.equals("java.lang.String") || type.equals("String"))
            return Type.STRING;
        else if (type.equals("java.lang.StringBuffer") || type.equals("StringBuffer"))
            return Type.STRINGBUFFER;
        else if (type.equals("java.lang.Throwable") || type.equals("Throwable"))
            return Type.THROWABLE;
        else if (type.equals("void"))
            return Type.VOID;

        return new ObjectType(type);
    }

    /*
    ** Returns: -1  Types equally wide.
    **           1  t1 is the widest.
    **           2  t2 is the widest.
    **
    ** NB uses Java type conventions, where integer is wider than short (the
    ** JVM stores shorts and ints identically).
    ** TODO - write this comment in English...
    */
    public static int widest(BasicType t1, BasicType t2)
    {
        if (t1.getType() == t2.getType())
            return -1;

        switch (t1.getType())
        {
        case Constants.T_DOUBLE:
            // A Double is bigger than anything else.
            return 1;

        case Constants.T_FLOAT:
            // A float is bigger than anything except a double.
            if (t2.getType() == Constants.T_DOUBLE)
            {
                return 2;
            }
            else
            {
                return 1;
            }

        case Constants.T_LONG:
            // A long is bigger than anything except a double or a float.
            if (t2.getType() == Constants.T_DOUBLE || t2.getType() == Constants.T_FLOAT)
            {
                return 2;
            }
            else
            {
                return 1;
            }

        case Constants.T_INT:
            switch (t2.getType())
            {
                case Constants.T_DOUBLE:
                case Constants.T_FLOAT:
                case Constants.T_LONG:
                    return 2;
                default:
                    return 1;
            }

        case Constants.T_SHORT:
        case Constants.T_CHAR:
        case Constants.T_BYTE:
        case Constants.T_BOOLEAN:

        default:
            // Short, Byte and Char are treated as integers in bytecode.

            if (t2.getType() == Constants.T_DOUBLE || t2.getType() == Constants.T_FLOAT || t2.getType() == Constants.T_LONG)
            {
                return 2;
            }
            else if (t1.getType() == Constants.T_DOUBLE || t1.getType() == Constants.T_FLOAT || t1.getType() == Constants.T_LONG)
            {
                return 1;
            }
            else
            {
                return -1;
            }

        }
    }
}
