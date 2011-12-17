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
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

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

    private final CountDownLatch sem = new CountDownLatch( 1 );

    private final AtomicInteger count = new AtomicInteger();

    private final List<List<ScannedFile>> listOfLists = new ArrayList<List<ScannedFile>>();

    private final class ThreadList
        extends Thread
    {
        private final List<ScannedFile> files;

        public ThreadList( Runnable r )
        {
            super( r );
            files = new ArrayList<ScannedFile>();
            listOfLists.add( files );
        }
    }

    public DirScanner( ScannerOptions scannerOptions)
    {
        this.scannerOptions = scannerOptions;
        executor = Executors.newFixedThreadPool( scannerOptions.getThreads(), new ThreadFactory()
        {
            public Thread newThread( Runnable r )
            {
                return new ThreadList( r );
            }
        } );
    }

    public Collection<ScannedFile> scan( final File dir )
        throws InterruptedException, ExecutionException
    {
        executor.submit( new Runnable()
        {
            public void run()
            {
                innerScan( dir );
            }
        } );
        sem.await();

        List<ScannedFile> ret = new ArrayList<ScannedFile>();
        for ( List<ScannedFile> files : listOfLists )
        {
            ret.addAll( files );
        }
        return ret;
    }

    private void innerScan( File dir )
    {
        final List<ScannedFile> threadFiles = ( (ThreadList) Thread.currentThread() ).files;
        File[] files = dir.listFiles();
        if (files == null){
            files = new File[]{};
        }
        for ( final File file : files )
        {
            final ScannedFile scannedFile = new ScannedFile( file );
            threadFiles.add( scannedFile );
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
            sem.countDown();
        }
    }

    public void close()
    {
        executor.shutdown();
    }

    public static Iterable<ScannedFile> scanDir( File dir, ScannerOptions scannerOptions )
        throws InterruptedException, ExecutionException
    {
        DirScanner scanner = new DirScanner( scannerOptions);
        try
        {
            return scanner.scan( dir );
        }
        finally
        {
            scanner.close();
        }
    }
}