package org.codehaus.plexus.util.concurrentdirscanner;

import java.io.File;
import org.codehaus.plexus.util.DirectoryScanner;

import junit.framework.TestCase;

/**
 * @author Kristian Rosenvold
 */
public class DirScannerTest
    extends TestCase
{

    public void testSimpleScan()
        throws Exception
    {
        File file = new File( "/home/kristian/lsrc/maven-surefire" );
        ScannerOptions scannerOptions = new ScannerOptions();
        DirScanner.scanDir( file, scannerOptions );

        DirectoryScanner directoryScanner = new DirectoryScanner();
        directoryScanner.setBasedir( file );
        directoryScanner.scan();

        for ( int i = 0; i < 10; i++ )
        {
            long start = System.currentTimeMillis();
            final Iterable<ScannedFile> scannedFiles = DirScanner.scanDir( file, scannerOptions );
            int j = 0;
            int count = 0;
            for ( ScannedFile scannedFile : scannedFiles )
            {
                j += scannedFile.getFile().getName().length();
                count++;
            }
            final long fastElapsed = System.currentTimeMillis() - start;
            System.out.print( count + "FastScanner(" + j + ")" + fastElapsed );

            start = System.currentTimeMillis();
            directoryScanner.scan();
            int k = 0; 
            count = 0;
            for ( String scannedFile : directoryScanner.getIncludedFiles() )
            {
                k += scannedFile.length();
                count++;
            }
            final long slowElapsed = System.currentTimeMillis() - start;
            System.out.print("/" + count + " OldScanner(" +k + ")" + slowElapsed );

            final long diff = slowElapsed - fastElapsed;
            System.out.println( "Time diff " + diff + ((diff < 0) ? " old faster" : ""));
        }
    }

    public void testScan()
        throws Exception
    {
/*
        File file = new File("/home/kristian/fud1/");
        assertFalse( file.isFile() );
        assertTrue( file.isDirectory() );
        file = new File("/home/kristian/fud2/");
        assertFalse( file.isFile() );
        assertTrue( file.isDirectory() );
        file = new File("/home/kristian/fud3/");
        assertTrue( file.isFile() );
        assertFalse( file.isDirectory() );
  */
    }
}
