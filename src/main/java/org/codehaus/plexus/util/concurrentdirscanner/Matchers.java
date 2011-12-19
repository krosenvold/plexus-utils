package org.codehaus.plexus.util.concurrentdirscanner;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Kristian Rosenvold
 */
public class Matchers
{
    private static final String PATTERN_HANDLER_PREFIX = "[";

    private static final String PATTERN_HANDLER_SUFFIX = "]";

    private static final String REGEX_HANDLER_PREFIX = "%regex" + PATTERN_HANDLER_PREFIX;

    private static final String ANT_HANDLER_PREFIX = "%ant" + PATTERN_HANDLER_PREFIX;

    private final RegExSelector[] regExSelector;
    private final AntSelector[] antSelectors;
        
    public Matchers( String[] includes, boolean isCaseSensitive )
    {
        List<AntSelector> antSelectors = new ArrayList<AntSelector>(  );
        List<RegExSelector> regExSelectors = new ArrayList<RegExSelector>(  );
        for ( String pattern : includes )
        {
            if ( pattern.length() > ( REGEX_HANDLER_PREFIX.length() + PATTERN_HANDLER_SUFFIX.length() + 1 )
                && pattern.startsWith( REGEX_HANDLER_PREFIX ) && pattern.endsWith( PATTERN_HANDLER_SUFFIX ) )
            {
    
                pattern = pattern.substring( REGEX_HANDLER_PREFIX.length(), pattern.length()
                                             - PATTERN_HANDLER_SUFFIX.length() );
                regExSelectors.add( new RegExSelector(pattern));
            }
            else
            {
                if ( pattern.length() > ( ANT_HANDLER_PREFIX.length() + PATTERN_HANDLER_SUFFIX.length() + 1 )
                    && pattern.startsWith( ANT_HANDLER_PREFIX ) && pattern.endsWith( PATTERN_HANDLER_SUFFIX ) )
                {
                    pattern =
                        pattern.substring( ANT_HANDLER_PREFIX.length(), pattern.length() - PATTERN_HANDLER_SUFFIX.length() );
                }
    
                antSelectors.add( new AntSelector( pattern, isCaseSensitive ));
            }
        }
        this.antSelectors = antSelectors.toArray( new AntSelector[antSelectors.size()] );
        this.regExSelector= regExSelectors.toArray( new RegExSelector[regExSelectors.size()]);
    }

    protected boolean isMatch( String name )
    {
        String[] tokenizedStr = AntSelector.tokenizePath( name, File.separator );
        for ( AntSelector antSelector : antSelectors )
        {
            if ( antSelector.matches( name, tokenizedStr ) )
            {
                return true;
            }
        }
        for ( RegExSelector aRegExSelector : regExSelector )
        {
            if ( aRegExSelector.matches( name ) )
            {
                return true;
            }
        }
        return false;
    }
}
