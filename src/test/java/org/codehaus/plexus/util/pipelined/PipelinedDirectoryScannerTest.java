package org.codehaus.plexus.util.pipelined;

import java.io.File;

import junit.framework.TestCase;

/**
 * @author Kristian Rosenvold
 */
public class PipelinedDirectoryScannerTest
    extends TestCase
{
    public void testScan()
        throws Exception
    {
        PipelinedDirectoryScanner pipelinedDirectoryScanner = new PipelinedDirectoryScanner( new File("."), null, null );
        final String[] scan = pipelinedDirectoryScanner.scan();


    }
}
