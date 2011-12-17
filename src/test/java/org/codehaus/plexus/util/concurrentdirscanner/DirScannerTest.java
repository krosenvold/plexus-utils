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
        DirScanner scanner0 = new DirScanner( scannerOptions );
        scanner0.scan( file );

        DirectoryScanner directoryScanner1 = new DirectoryScanner();
        directoryScanner1.setBasedir( file );
        directoryScanner1.scan();

        for ( int i = 0; i < 6; i++ )
        {
            long start = System.currentTimeMillis();

            int j = 0;
            int count = 0;
            DirScanner scanner1 = new DirScanner( scannerOptions );
            scanner1.scan(  file );
            ScannedFile scannedFile;
            long startat = System.currentTimeMillis();
            while  ((scannedFile = scanner1.take())!=null){
                if ( j == 0 )
                {
                    System.out.println("First file available after " + (System.currentTimeMillis() - startat));
                } 
                j += scannedFile.getFile().getName().length();
                count++;
            }
            final long fastElapsed = System.currentTimeMillis() - start;
            System.out.print( count + "FastScanner(" + j + ")" + fastElapsed );

            start = System.currentTimeMillis();
            directoryScanner1.scan();
            int k = 0; 
            count = 0;
            for ( String sscannedFile : directoryScanner1.getIncludedFiles() )
            {
                k += sscannedFile.length();
                count++;
            }
            final long slowElapsed = System.currentTimeMillis() - start;
            System.out.print( "/" + count + " OldScanner(" + k + ")" + slowElapsed );

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
