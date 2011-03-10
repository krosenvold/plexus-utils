package org.codehaus.plexus.util.pipelined;

import java.io.File;
import java.util.Iterator;

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
        int count = scanNew( new File( "." ) );
        assertEquals( res2.length, count  );
        for (int i = 0; i < 5; i++){
        scanOld( new File( "." ) );
        scanNew( new File( "." ) );
        }

    }

    private int scanNew( File basedir )
    {
        long start = System.currentTimeMillis();
        IteratorApi iteratorApi = new IteratorApi();
        PipelinedDirectoryScanner pipelinedDirectoryScanner = new PipelinedDirectoryScanner( basedir, null, null, iteratorApi );
        pipelinedDirectoryScanner.scan();

        Iterator iter = iteratorApi.iterator();

        int i = 0;
        while (iter.hasNext()){
            iter.next();
            i++;
        }

        System.out.println(", new=" + (System.currentTimeMillis() - start));
        return i;
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
