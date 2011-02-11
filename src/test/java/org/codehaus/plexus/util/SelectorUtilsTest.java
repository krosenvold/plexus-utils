package org.codehaus.plexus.util;
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

import junit.framework.Assert;
import junit.framework.TestCase;

import java.util.Vector;

/**
 * @author Kristian Rosenvold
 */
public class SelectorUtilsTest
    extends TestCase
{
    public void testTokenizePath()
        throws Exception
    {

        Vector vector = SelectorUtils.tokenizePath( "AB.CD.EF", '.' );
        Assert.assertEquals( 3, vector.size() );
        Assert.assertEquals( "AB", vector.get( 0 ) );
        Assert.assertEquals( "CD", vector.get( 1 ) );
        Assert.assertEquals( "EF", vector.get( 2 ) );
    }

    public void testTokenizeNull()
        throws Exception
    {

        Vector vector = SelectorUtils.tokenizePath( null, '.' );
        Assert.assertEquals( 0, vector.size() );
        vector = SelectorUtils.tokenizePath( "", '.' );
        Assert.assertEquals( 0, vector.size() );
    }

    public void testTokenizePathDotAtEnd()
        throws Exception
    {

        Vector vector = SelectorUtils.tokenizePath( "AB.CD.EF.", '.' );
        Assert.assertEquals( 3, vector.size() );
        Assert.assertEquals( "AB", vector.get( 0 ) );
        Assert.assertEquals( "CD", vector.get( 1 ) );
        Assert.assertEquals( "EF", vector.get( 2 ) );
    }

    public void testTokenizePathStartsWithDot()
        throws Exception
    {

        Vector vector = SelectorUtils.tokenizePath( ".AB.CD.EF.", '.' );
        Assert.assertEquals( 3, vector.size() );
        Assert.assertEquals( "AB", vector.get( 0 ) );
        Assert.assertEquals( "CD", vector.get( 1 ) );
        Assert.assertEquals( "EF", vector.get( 2 ) );
    }

}
