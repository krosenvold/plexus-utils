package org.codehaus.plexus.util.pipelined;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author Kristian Rosenvold
 */
public class BlockQueueApi implements PipelineApi
{
    LinkedBlockingQueue linkedBlockingQueue = new LinkedBlockingQueue(  );
    public void addElement( String fileName )
    {
        linkedBlockingQueue.add(  fileName );
    }

    public String take()
        throws InterruptedException
    {
        return (String) linkedBlockingQueue.take();
    }
}
