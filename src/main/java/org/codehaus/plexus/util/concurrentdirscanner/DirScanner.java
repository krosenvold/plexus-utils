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
import java.util.Iterator;
import java.util.NoSuchElementException;
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

    private final AtomicInteger scheduledCount = new AtomicInteger();

    private final LinkedBlockingQueue<ScannedFile> result = new LinkedBlockingQueue<ScannedFile>();

    private final ScannedFile posion = new ScannedFile( new File( "." ) );

    public DirScanner( ScannerOptions scannerOptions )
    {
        this.scannerOptions = scannerOptions;
        scannerOptions.setupDefaultFilters();
        executor = Executors.newFixedThreadPool( scannerOptions.getThreads() );
    }

    public Iterable<ScannedFile> scan( final File dir )
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
            if ( isIncluded( scannedFile ) )
            {
                if ( !isExcluded( scannedFile ) )
                {

                    if ( scannedFile.isDirectory() )
                    {
                        scheduledCount.incrementAndGet();

                        executor.submit( new Runnable()
                        {
                            public void run()
                            {
                                innerScan( file );
                            }
                        } );
                    }
                    else
                    {

                        result.add( scannedFile );
                    }
                }
            }
        }
        if ( scheduledCount.decrementAndGet() < 0 )
        {
            result.add( posion );
        }
    }

    public void close()
    {
        executor.shutdown();
    }

    /**
     * Tests whether or not a name matches against at least one include
     * pattern.
     *
     * @param name The name to match. Must not be <code>null</code>.
     * @return <code>true</code> when the name matches against at least one
     *         include pattern, or <code>false</code> otherwise.
     */
    protected boolean isIncluded( ScannedFile name )
    {
        final String[] includes = scannerOptions.getIncludes();
        for ( int i = 0; i < includes.length; i++ )
        {
            if ( matchPath( includes[i], name.getFile().getName(), scannerOptions.isCaseSensitive()) )
            {
                return true;
            }
        }
        return false;
    }

    /**
     * Tests whether or not a name matches against at least one exclude
     * pattern.
     *
     * @param name The name to match. Must not be <code>null</code>.
     * @return <code>true</code> when the name matches against at least one
     *         exclude pattern, or <code>false</code> otherwise.
     */
    protected boolean isExcluded( ScannedFile name )
    {
        final String[] excludes = scannerOptions.getExcludes();
        for ( int i = 0; i < excludes.length; i++ )
        {
            if ( matchPath( excludes[i], name.getFile().getName(), scannerOptions.isCaseSensitive() ) )
            {
                return true;
            }
        }
        return false;
    }

    protected static boolean matchPath( String pattern, String str, boolean isCaseSensitive )
    {
        return org.codehaus.plexus.util.SelectorUtils.matchPath( pattern, str, isCaseSensitive );
    }

}