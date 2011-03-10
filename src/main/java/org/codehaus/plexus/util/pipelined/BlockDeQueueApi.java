package org.codehaus.plexus.util.pipelined;

import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * @author Kristian Rosenvold
 */
public class BlockDeQueueApi
    extends PipelineApi
{
    BlockingDeque linkedBlockingQueue = new LinkedBlockingDeque( );
    protected void addElement( String fileName )
    {
        linkedBlockingQueue.add(  fileName );
    }

    public String take()
        throws InterruptedException
    {
        return (String) linkedBlockingQueue.take();
    }
}
