package org.codehaus.plexus.util.pipelined;

import java.util.regex.Pattern;

/**
 * @author Kristian Rosenvold
 */
public class RegexSelector implements Selector
{
    private final Pattern pattern;

    public RegexSelector( String regex )
    {
        pattern = Pattern.compile(  regex);
    }

    public boolean matches( String string )
    {
        return pattern.matcher( string ).matches();
    }

    public boolean matchPatternStart( String str )
    {
            // FIXME: ICK! But we can't do partial matches for regex, so we have to reserve judgement until we have
            // a file to deal with, or we can definitely say this is an exclusion...
            return true;
    }
}
