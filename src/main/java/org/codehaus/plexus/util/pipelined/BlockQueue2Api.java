package org.codehaus.plexus.util.pipelined;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Kristian Rosenvold
 */
public class BlockQueue2Api
    extends PipelineApi
{
    List linkedBlockingQueue = new ArrayList(  );
    protected void addElement( String fileName )
    {
        linkedBlockingQueue.add(  fileName );
    }

    private int pos;
    public String take()
        throws InterruptedException
    {
        final boolean hasMore = pos < linkedBlockingQueue.size();
        return hasMore ? (String) linkedBlockingQueue.get(pos++) : null;
    }
}
