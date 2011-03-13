package org.codehaus.plexus.util.pipelined;

import java.util.List;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * @author Kristian Rosenvold
 */
public class BlockDeQueueApi
    implements PipelineApi
{
    BlockingDeque linkedBlockingQueue = new LinkedBlockingDeque(  );
    public void addElement( String fileName )
    {
        linkedBlockingQueue.add(  fileName );
    }

    public void addElements( List elements )
    {
        linkedBlockingQueue.addAll(  elements );
    }

    public String take()
        throws InterruptedException
    {
        return (String) linkedBlockingQueue.take();
    }
}
