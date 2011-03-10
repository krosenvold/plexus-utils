package org.codehaus.plexus.util.pipelined;

import java.util.Iterator;
import java.util.Vector;

/**
 * @author Kristian Rosenvold
 */
public class IteratorApi
    extends PipelineApi
{
    private final Vector elements = new Vector();

    private volatile boolean done = false;

    protected void addElement( String fileName )
    {
        //noinspection StringEquality
        if ( fileName == PipelinedDirectoryScanner.POISON )
        {
            done = true;
        }
        else
        {
            elements.add( fileName );
        }
        synchronized ( elements )
        {
            elements.notify();
        }
    }

    public Iterator iterator()
    {
        return new MyIterator();
    }


    public class MyIterator
        implements Iterator
    {
        private int clientPos = 0;

        public boolean hasNext()
        {
            final boolean has = hasAvailableElement();
            if (has) return true;
            if ( !done )
            {
                synchronized ( elements )
                {
                    try
                    {
                        elements.wait();
                    }
                    catch ( InterruptedException e )
                    {
                        throw new RuntimeException( e );
                    }
                }
            }
            return hasAvailableElement();
        }

        boolean hasAvailableElement()
        {
            return clientPos < elements.size();
        }

        public Object next()
        {
            return elements.get( clientPos++ );
        }

        public void remove()
        {
            throw new UnsupportedOperationException( "Not supported" );
        }
    }
}
