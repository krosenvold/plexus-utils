package org.codehaus.plexus.util.pipelined;

import junit.framework.TestCase;

/**
 * @author Kristian Rosenvold
 */
public class IteratorApiTest
    extends TestCase
{
    public void testNext(){
        IteratorApi iteratorApi = new IteratorApi();
        final IteratorApi.MyIterator iterator = (IteratorApi.MyIterator) iteratorApi.iterator();
        assertFalse( iterator.hasAvailableElement());
        iteratorApi.addElement( "ABC" );
        assertTrue( iterator.hasNext() );
        iterator.next();
        assertFalse( iterator.hasAvailableElement());
        iteratorApi.addElement( "DEF" );
        iteratorApi.addElement( "GHI" );
        assertTrue( iterator.hasNext() );
        iterator.next();
        assertTrue( iterator.hasNext() );
        iterator.next();
        assertFalse( iterator.hasAvailableElement());

    }
}
