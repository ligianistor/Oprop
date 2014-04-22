package cager.jexpr;

public class Identifier
{
    public String name;

    public Identifier(String name)
    {
        this.name = name;
    }

    public String getName()
    {
		return name;
	}

    public String toString()
    {
        return name;
    }

    public boolean equals(Object obj)
    {
        if (obj instanceof Identifier)
            return name.equals(((Identifier)obj).name);
        else
            return false;
    }

    public int hashCode()
    {
        return name.hashCode();
    }
}