package cc.jfire.dson.reader.support;

public class FieldIndexNode
{
    private final FieldIndexNode[] next  = new FieldIndexNode[90];
    private       int              index = -1;

    public FieldIndexNode getNext(char c)
    {
        if (c <= 'z')
        {
            return next[c - 33];
        }
        else
        {
            throw new IllegalArgumentException(String.valueOf(c));
        }
    }

    public int getIndex()
    {
        return index;
    }

    public void put(String name, int index)
    {
        char c = name.charAt(0);
        if (c <= 'z')
        {
            int nextIndex = c - 33;
            if (next[nextIndex] == null)
            {
                next[nextIndex] = new FieldIndexNode();
            }
            next[nextIndex].put(name, 0, index);
        }
        else
        {
            throw new IllegalArgumentException();
        }
    }

    private void put(String name, int i, int index)
    {
        if (i < name.length())
        {
            if (i + 1 == name.length())
            {
                this.index = index;
            }
            else
            {
                char c         = name.charAt(i + 1);
                int  nextIndex = 0;
                if (c <= 'z')
                {
                    nextIndex = c - 33;
                }
                else
                {
                    throw new IllegalArgumentException();
                }
                if (next[nextIndex] == null)
                {
                    next[nextIndex] = new FieldIndexNode();
                }
                next[nextIndex].put(name, i + 1, index);
            }
        }
    }
}
