package org.codehaus.plexus.util.concurrentdirscanner;

import java.util.regex.Pattern;

/**
 * @author Kristian Rosenvold
 */
public class RegExSelector
{
    private final Pattern pattern;

    public RegExSelector( String regex )
    {
        pattern = Pattern.compile( regex );
    }

    public boolean matches(String str){
        return pattern.matcher( str ).matches();
    }
}
