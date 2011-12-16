package org.codehaus.plexus.util.concurrentdirscanner;

import junit.framework.TestCase;
import org.codehaus.plexus.util.DirectoryScanner;

import java.io.File;
import java.util.Collection;

/**
 * @author Kristian Rosenvold
 */
public class DirScannerTest
    extends TestCase
{

    public void testSimpleScan()
        throws Exception
    {
        File file = new File("/home/kristian/lsrc/maven-surefire");
        ScannerOptions  scannerOptions = new ScannerOptions(  );
        DirScanner.scanDir( file, scannerOptions );

        DirectoryScanner directoryScanner = new DirectoryScanner();
        directoryScanner.setBasedir(  file );
        directoryScanner.scan();
        
        long start = System.currentTimeMillis();
        DirScanner.scanDir( file, scannerOptions );
        System.out.println("FastScanner" + (System.currentTimeMillis() - start));

        start = System.currentTimeMillis();
        directoryScanner.scan();
        System.out.println("OldScanner" + (System.currentTimeMillis() - start));
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
