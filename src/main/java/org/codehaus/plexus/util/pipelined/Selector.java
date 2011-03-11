package org.codehaus.plexus.util.pipelined;

/**
 * @author Kristian Rosenvold
 */
public interface Selector
{
    public boolean matches( String string );

    public boolean matchPatternStart( String str );


}
