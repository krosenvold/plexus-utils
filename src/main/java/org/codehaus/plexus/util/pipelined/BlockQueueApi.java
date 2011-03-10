package org.codehaus.plexus.util.pipelined;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author Kristian Rosenvold
 */
public class BlockQueueApi extends PipelineApi
{
    LinkedBlockingQueue linkedBlockingQueue = new LinkedBlockingQueue(  );
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
