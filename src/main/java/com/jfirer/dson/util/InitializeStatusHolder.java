package com.jfirer.dson.util;

public interface InitializeStatusHolder
{
    default void ensureInitialized()
    {
    }

    default void setInitialized()
    {
    }

    abstract class InitializeStatusHolderImpl implements InitializeStatusHolder
    {
        protected volatile boolean initialized = false;

        @Override
        public void ensureInitialized()
        {
            if (initialized)
            {
                return;
            }
            synchronized (this)
            {
                if (initialized)
                {
                    return;
                }
                do
                {
                    try
                    {
                        wait();
                    }
                    catch (InterruptedException e)
                    {
                        throw new RuntimeException(e);
                    }
                } while (initialized == false);
            }
        }

        public synchronized void setInitialized()
        {
            initialized = true;
            notifyAll();
        }
    }
}
