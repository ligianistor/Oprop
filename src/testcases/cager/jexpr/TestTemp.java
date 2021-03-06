//import cager.jexpr.test.*;
package testcases.cager.jexpr;

public class TestTemp
{
    public static void main(String[] args)
    {
    	/*
        System.out.println(Temp.stringFunc("YY"));

        assert Temp.stringFunc("YY").equals("YY");
        assert Temp.testInt2(null, null) == 3;
        assert Temp.stringFunc2("XX").equals("XXHello");
        assert Temp.nullVector() == null;
        assert Temp.someVector(new java.util.Vector()) instanceof java.util.Vector;

        Object obj1 = new java.util.ArrayList();
        Object obj2 = new Object();

        System.out.println(Temp.stringConcat1(obj1));
        System.out.println(Temp.stringConcat2(obj1, obj2));

        assert Temp.stringConcat1(obj1).equals(obj1 + ">");
        assert Temp.stringConcat2(obj1, obj2).equals(obj1 + " : " + obj2);

        assert Temp.stringCast("XX") == "XX";       // Use == rather than .equals to compare refs.

        System.out.println(Temp.testIf(1,2));
        System.out.println(Temp.testIf(2,1));

        assert Temp.testIf(1,2) == 2;
        assert Temp.testIf(2,1) == 2;

        assert Temp.testChar('A', 1) == 'B';

        assert Temp.implicitWiden((short)12) == 12;
*/
        System.out.println("Completed test of generated class");
    }

    public static int hello(int p1, int p2)
    {
        short s =12;
        char c = ' ';

        int res = -s;
        char ch = (char)(-c);

        return p1;
    }

    public static void p1()
    {
        int i = 12;
        short j = 4;

        int res = i + j;

        i = -i;
        i = ~i;
    }

    public static void p2()
    {
        boolean x = true;
        boolean y = false;

        x = x || y;
    }

    public static void p3()
    {
        boolean x = true;
        boolean y = false;

        x = !x;
    }

    public static void p4()
    {
        boolean x = true;
        boolean y = false;

        x = x | y;
    }

    public static void p5()
    {
        boolean x = true;
        boolean y = false;

        if (x || y)
            System.out.println();

        if (x && y)
            System.out.println();
    }

    public static void p6()
    {
        int x = 10;
        int y;

        y = +x + 2;
        y = -x + 2;
        y = x + 2;
    }

    public static void p7()
    {
        int i = 4;
        double d = 5;

        i++;

        d++;

        i--;

        i += 4;
    }

    public static boolean p8()
    {
        double d1 = 10;
        double d2 = 5;

        return d1 > d2;
    }


/*
4.2.1 Integral Types and Values
The values of the integral types are integers in the following ranges:

For byte, from -128 to 127, inclusive
For short, from -32768 to 32767, inclusive
For int, from -2147483648 to 2147483647, inclusive
For long, from -9223372036854775808 to 9223372036854775807, inclusive
For char, from '\u0000' to '\uffff' inclusive, that is, from 0 to 65535
*/
    public static int p9()
    {
        short   s1 = 22;
        char    c1 = 5;

        // short and char have overlapping (rather than nested ranges):

        short   s2 = (short)(c1 + s1);
        char    c2 = (char)(c1 + s1);

        //c2 = s1;  // Neither of these are allowed - "possible loss of precision".
        //s2 = c1;  // So can not assume that short is longer than char or vice-versa

        int x = c1 + s1;    // This is allowed, so must have numeric promotion.

        return c2;
    }

    public static void p10()
    {
        int x = 12;
        int y = 14;

        //if (x == y)
        //{
        //    x = 1;
        //}

        boolean b = x < y;
    }

    public static void p11()
    {
        long x = 12;
        long y = 14;

        //if (x == y)
        //{
        //    x = 1;
        //}

        boolean b = x < y;
    }

    public static void p12()
    {
        Object a = null;
        int i = 0;

        if ((a == null) || (i < 2))
            System.out.println("Oops");
    }

    public static void p13()
    {
        Object a = null;
        int i = 0;

        if ((a == null) | (i < 2))
            System.out.println("Oops");
    }

    public static String p14()
    {
        Object obj = null;
        return obj.toString();
    }

    public static void p15()
    {
        Object obj = new Object();
        System.out.println("obj" + obj);
        String s = (String)obj;
    }

    public static void p16(int x)
    {
        assert x == 0;
    }

    public void p17()
    {
        super.toString();
    }

    private char testChar(char c, int i)
    {
        return (char)(c + i);
    }

}
