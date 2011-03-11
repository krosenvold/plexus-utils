package org.codehaus.plexus.util.pipelined;

import org.codehaus.plexus.util.DirectoryScanner;

import java.io.File;
import java.util.Iterator;

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
        final File file = new File( "/home/kristian/lsrc" );
        final int expected = scanOriginal( file ).length;
        final int expected2 = expected + 1;
        for ( int i = 0; i < 10; i++ )
        {
            assertEquals( expected, scanOriginal( file ).length );
            assertEquals( expected2, scanNewBDQ( file ) );
            scanNewNonThreaded(file);
            scanNewBQ( file );
        }

    }

    private int scanNew( File basedir )
        throws InterruptedException
    {
        IteratorApi iteratorApi = new IteratorApi();
        int i = 0;
        long first = 0;
        long start = System.currentTimeMillis();
        try
        {
            PipelinedDirectoryScanner pipelinedDirectoryScanner =
                new PipelinedDirectoryScanner( basedir, null, null, iteratorApi);
            pipelinedDirectoryScanner.scanThreaded();


            String take;
            for (Iterator iter = iteratorApi.iterator(); iter.hasNext();){
                take = (String) iter.next();
                i++;
                if (i == 1) first = System.currentTimeMillis() - start;
            }
            return i;
        }
        finally
        {
            System.out.print( ", new(" + first +"=" + ( System.currentTimeMillis() - start ) );
        }
    }
    private int scanNewBQ( File basedir )
        throws InterruptedException
    {
        BlockQueueApi blockQueueApi = new BlockQueueApi();
        int i = 0;
        long first = 0;
        long start = System.currentTimeMillis();
        try
        {
            PipelinedDirectoryScanner pipelinedDirectoryScanner =
                new PipelinedDirectoryScanner( basedir, null, null, blockQueueApi);
            pipelinedDirectoryScanner.scanThreaded();


            String take;
            do {
                take = blockQueueApi.take();
                i++;
                if (i == 1) first = System.currentTimeMillis() - start;
            }  while (take != PipelinedDirectoryScanner.POISON);
            return i;
        }
        finally
        {
            System.out.println( ", NEWBQ(" + first + "=" + ( System.currentTimeMillis() - start ) );
        }
    }
    private int scanNewNonThreaded( File basedir )
        throws InterruptedException
    {
        PipelineApi blockQueueApi = new BlockQueue2Api();
        int i = 0;
        long first = 0;
        long start = System.currentTimeMillis();
        try
        {
            PipelinedDirectoryScanner pipelinedDirectoryScanner =
                new PipelinedDirectoryScanner( basedir, null, null, blockQueueApi);
            pipelinedDirectoryScanner.scan();


            return i;
        }
        finally
        {
            System.out.print( ", nonThrNew(" + first + "=" + ( System.currentTimeMillis() - start ) );
        }
    }

    private int scanNewBDQ( File basedir )
        throws InterruptedException
    {
        BlockDeQueueApi blockQueueApi = new BlockDeQueueApi();
        int i = 0;
        long first = 0;
        long start = System.currentTimeMillis();
        try
        {
            PipelinedDirectoryScanner pipelinedDirectoryScanner =
                new PipelinedDirectoryScanner( basedir, null, null, blockQueueApi);
            pipelinedDirectoryScanner.scanThreaded();


            String take;
            do {
                take = blockQueueApi.take();
                i++;
                if (i == 1) first = System.currentTimeMillis() - start;

            }  while (take != PipelinedDirectoryScanner.POISON);
            return i;
        }
        finally
        {
            System.out.print( ", NEWBDQ(" + first +"=" + ( System.currentTimeMillis() - start ) );
        }
    }


    private String[] scanOriginal( File file )
    {
        long start = System.currentTimeMillis();
        int j = 0;
        try {
        DirectoryScanner directoryScanner = new DirectoryScanner();
        directoryScanner.setIncludes( null );
        directoryScanner.setExcludes( null );
        directoryScanner.setBasedir( file );
        directoryScanner.scan();

        final String[] includedFiles = directoryScanner.getIncludedFiles();
        int size = includedFiles.length;
        String foo;
        for ( int i = 0; i < size; i++ )
        {
            foo = includedFiles[i];
            j++;
        }
        return includedFiles;
        } finally {
            final long elapsed = System.currentTimeMillis() - start;
            System.out.print( "Elapsed, old=" + elapsed );

    }
    }
}
