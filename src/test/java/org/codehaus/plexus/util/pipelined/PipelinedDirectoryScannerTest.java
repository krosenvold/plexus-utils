package org.codehaus.plexus.util.pipelined;

import java.io.File;

import junit.framework.TestCase;
import org.codehaus.plexus.util.DirectoryScanner;

/**
 * @author Kristian Rosenvold
 */
public class PipelinedDirectoryScannerTest
    extends TestCase
{
    public void testScan()
        throws Exception
    {
        final String[] res2 = scanOld( new File( "." ) );
        final String[] res1= scanNew( new File( "." ) );
        assertEquals(  res1.length, res2.length );
        for (int i = 0; i < 5; i++){
        scanOld( new File( "." ) );
        scanNew( new File( "." ) );
        }

    }

    private String[] scanNew( File basedir )
    {
        long start = System.currentTimeMillis();
        PipelinedDirectoryScanner pipelinedDirectoryScanner = new PipelinedDirectoryScanner( basedir, null, null );
        final String[] scan = pipelinedDirectoryScanner.scan();
        System.out.println(", new=" + (System.currentTimeMillis() - start));
        return scan;
    }

    private String[] scanOld( File file )
    {
        long start = System.currentTimeMillis();
        DirectoryScanner directoryScanner = new DirectoryScanner( );
        directoryScanner.setIncludes( null );
        directoryScanner.setExcludes( null );
        directoryScanner.setBasedir( file );
        directoryScanner.scan();
        System.out.print("Elapsed, old=" + (System.currentTimeMillis() - start));
        return directoryScanner.getIncludedFiles();
    }
}
