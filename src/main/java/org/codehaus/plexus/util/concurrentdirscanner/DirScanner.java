/*
 * Copyright The Codehaus Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.codehaus.plexus.util.concurrentdirscanner;

/**
 * @author Kristian Rosenvold
 */

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 * @author <a href="mailto:kristian.rosenvold@gmail.com">Kristian Rosenvold</a>
 */
public class DirScanner
{
    // Included files, included directories, includes, excludes
    //ds.setCaseSensitive( isCaseSensitive() );
    //ds.setFollowSymlinks( isFollowingSymLinks() );

    private final ScannerOptions scannerOptions;

    private final ExecutorService executor;

    private final AtomicInteger count = new AtomicInteger();

    private final LinkedBlockingQueue<ScannedFile> result = new LinkedBlockingQueue<ScannedFile>();

    private final ScannedFile posion = new ScannedFile( new File( "." ) );

    private final class ThreadList
        extends Thread
    {
        private final List<ScannedFile> filesInThisThread;

        public ThreadList( Runnable r )
        {
            super( r );
            filesInThisThread = new ArrayList<ScannedFile>();
        }
    }

    public DirScanner( ScannerOptions scannerOptions )
    {
        this.scannerOptions = scannerOptions;
        executor = Executors.newFixedThreadPool( scannerOptions.getThreads() );
    }

    public Iterable<ScannedFile> scan( final File dir )
        throws InterruptedException, ExecutionException
    {
        executor.submit( new Runnable()
        {
            public void run()
            {
                innerScan( dir );
            }
        } );

        return new Iterable<ScannedFile>()
        {
            public Iterator<ScannedFile> iterator()
            {
                return new ScannedFileIterator();
            }
        };
    }

    private class ScannedFileIterator
        implements Iterator<ScannedFile>
    {
        private List<ScannedFile> currentList = null;

        private int currentPos = -1;

        private int outerPos = -1;

        ScannedFile next = null;

        public boolean hasNext()
        {
            try
            {
                next = result.take();
                return posion != next;
            }
            catch ( InterruptedException e )
            {
                throw new RuntimeException( e );
            }
        }

        public ScannedFile next()
        {
            if ( posion == next )
            {
                throw new NoSuchElementException( "We saw poison" );
            }
            return next;
        }

        public void remove()
        {
            throw new NotImplementedException();
        }
    }

    private void innerScan( File dir )
    {
        File[] files = dir.listFiles();
        if ( files == null )
        {
            files = new File[]{ };
        }
        for ( final File file : files )
        {
            final ScannedFile scannedFile = new ScannedFile( file );
            result.add( scannedFile );
            if ( scannedFile.isDirectory() )
            {
                count.incrementAndGet();

                executor.submit( new Runnable()
                {
                    public void run()
                    {
                        innerScan( file );
                    }
                } );
            }
        }
        if ( count.decrementAndGet() < 0 )
        {
            result.add( posion );
        }
    }

    public void close()
    {
        executor.shutdown();
    }

    public static Iterable<ScannedFile> scanDir( File dir, ScannerOptions scannerOptions )
        throws InterruptedException, ExecutionException
    {
        DirScanner scanner = new DirScanner( scannerOptions );
        try
        {
            return scanner.scan( dir );
        }
        finally
        {
          //  scanner.close();
        }
    }
}