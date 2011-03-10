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

    protected void addElement( String fileName )
    {
        elements.add( fileName );
    }

    public Iterator iterator()
    {
        return elements.iterator();
    }

}
