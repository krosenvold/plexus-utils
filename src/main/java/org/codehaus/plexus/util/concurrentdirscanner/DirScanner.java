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

import org.codehaus.plexus.util.PathTool;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 * @author <a href="mailto:kristian.rosenvold@gmail.com">Kristian Rosenvold</a>
 */
public class DirScanner
{
    // Included files, included directories, includes, excludes
    //ds.setCaseSensitive( isCaseSensitive() );
    //ds.setFollowSymlinks( isFollowingSymLinks() );

    private final ExecutorService executor;

    private final AtomicInteger scheduledCount = new AtomicInteger();

    private final LinkedBlockingQueue<ScannedFile> result = new LinkedBlockingQueue<ScannedFile>();

    private final ScannedFile posion = new ScannedFile( new File( "." ) );
    
    private final Matchers includes;
    
    private final Matchers excludes;

    public DirScanner( ScannerOptions scannerOptions )
    {
        scannerOptions.setupDefaultFilters();
        executor = Executors.newFixedThreadPool( scannerOptions.getThreads() );
        this.includes = new Matchers(scannerOptions.getIncludes(), scannerOptions.isCaseSensitive());
        this.excludes= new Matchers(scannerOptions.getExcludes(), scannerOptions.isCaseSensitive());
    }

    public Iterable<ScannedFile> scan( final File dir )
    {
        executor.submit( new Runnable()
        {
            public void run()
            {
                innerScan( dir, "");
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

    private void innerScan( File rootDir, String subPath )
    {
        String[] files = rootDir.list();
        if ( files == null )
        {
            files = new String[]{ };
        }
        for ( final String fileName : files )
        {
            final String relativeFile = subPath.length() > 0 ?  subPath + File.separator + fileName
                : fileName;
            final File file = new File( rootDir, relativeFile );
            final ScannedFile scannedFile = new ScannedFile( file );
            if ( includes.isMatch( relativeFile ) )
            {
                if ( !excludes.isMatch( relativeFile ) )
                {
                    if ( scannedFile.isDirectory() )
                    {
                        scheduledCount.incrementAndGet();
                        executor.submit( new Runnable()
                        {
                            public void run()
                            {
                                innerScan( file, relativeFile );
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

}