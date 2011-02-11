package org.codehaus.plexus.util;

import java.io.File;
import java.util.Vector;

/**
 * @author Kristian Rosenvold
 */
public class TokenizedName
{
    private final String name;

    private final Vector tokenizedName;


    public TokenizedName( String name )
    {
        this.name = name;
        this.tokenizedName =  SelectorUtils.tokenizePath( name, File.separator );
    }

    public String getName()
    {
        return name;
    }

    public Vector getTokenizedName()
    {
        return tokenizedName;
    }
}
