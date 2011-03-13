package org.codehaus.plexus.util.pipelined;

import java.util.List;

/**
 * @author Kristian Rosenvold
 */
interface PipelineApi
{
    void addElement(String fileName);

    void addElements( List elements);
}
