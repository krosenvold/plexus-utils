package org.codehaus.plexus.util.pipelined;

import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author Kristian Rosenvold
 */
public class BlockQueueApi implements PipelineApi
{
    ConcurrentLinkedQueue linkedBlockingQueue = new ConcurrentLinkedQueue( );
    public void addElement( String fileName )
    {
        linkedBlockingQueue.add(  fileName );
    }

    public void addElements( List elements )
    {
        linkedBlockingQueue.addAll(  elements);
    }

    public String take()
        throws InterruptedException
    {
        Object poll = linkedBlockingQueue.poll();
        while (poll == null){
            Thread.sleep(5);
            poll = linkedBlockingQueue.poll();
        }
        return (String) poll;
    }
}
