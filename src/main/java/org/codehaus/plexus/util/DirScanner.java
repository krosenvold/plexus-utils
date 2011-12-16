/*
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2000-2003 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowlegement:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.codehaus.org/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The names "Ant" and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written
 *    permission, please contact codehaus@codehaus.org.
 *
 * 5. Products derived from this software may not be called "Apache"
 *    nor may "Apache" appear in their names without prior written
 *    permission of the Apache Group.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.codehaus.org/>.
 */
package org.codehaus.plexus.util;

/**
 * @author Kristian Rosenvold
 */

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author <a href="mailto:kristian@zenior.no">Kristian Rosenvold</a>
 */
public class DirScanner
{
    private final ExecutorService executor;

    private final Semaphore sem = new Semaphore( 1 );

    private final AtomicInteger count = new AtomicInteger();

    private final List<List<File>> listOfLists = new ArrayList<List<File>>();

    /**
     * Custom {@link Thread} returned by the {@link ThreadFactory} used by the
     * {@link ExecutorService} data member <tt>executor</tt>.
     */
    private final class ThreadWithList
        extends Thread
    {
        private final List<File> files;

        public ThreadWithList( Runnable r )
        {
            super( r );
            files = new ArrayList<File>();
            listOfLists.add( files );
        }

        public List<File> files()
        {
            return this.files;
        }
    }

    private DirScanner( int threads )
    {
        super();
        executor = Executors.newFixedThreadPool( threads, new ThreadFactory()
        {
            public Thread newThread( Runnable r )
            {
                return new ThreadWithList( r );
            }
        } );
    }

    public Collection<File> scan( final File dir )
        throws InterruptedException, ExecutionException
    {
        sem.acquire();
        executor.submit( new Runnable()
        {
            public void run()
            {
                scan0( dir );
            }
        } );
        sem.acquire();
        List<File> ret = new ArrayList<File>();
        for ( List<File> files : listOfLists )
        {
            ret.addAll( files );
        }
        return ret;
    }

    private void scan0( File dir )
    {
        for ( final File file : dir.listFiles() )
        {
            ( (ThreadWithList) Thread.currentThread() ).files.add( file );
            if ( file.isDirectory() )
            {
                count.incrementAndGet();
                executor.submit( new Runnable()
                {
                    public void run()
                    {
                        DirScanner.this.scan0( file );
                    }
                } );
            }
        }
        if ( count.decrementAndGet() < 0 )
        {
            sem.release();
        }
    }

    public void close()
    {
        executor.shutdown();
    }

    public static Collection<File> listAllContentsUnder( File dir, int threads )
        throws InterruptedException, ExecutionException
    {
        DirScanner scanner = new DirScanner( threads );
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