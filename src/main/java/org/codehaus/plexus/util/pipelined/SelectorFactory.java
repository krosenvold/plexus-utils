package org.codehaus.plexus.util.pipelined;

/**
 * @author Kristian Rosenvold
 */
public class SelectorFactory
{

    public static final String PATTERN_HANDLER_PREFIX = "[";

    public static final String PATTERN_HANDLER_SUFFIX = "]";

    public static final String REGEX_HANDLER_PREFIX = "%regex" + PATTERN_HANDLER_PREFIX;

    public static final String ANT_HANDLER_PREFIX = "%ant" + PATTERN_HANDLER_PREFIX;

    /**
     * Tests whether or not a given path matches a given pattern.
     *
     * @param pattern The pattern to match against. Must not be
     *                <code>null</code>.
     * @param isCaseSensitive Whether or not matching should be performed
     *                        case sensitively.
     *
     * @return <code>true</code> if the pattern matches against the string,
     *         or <code>false</code> otherwise.
     */
    public Selector create( String pattern, boolean isCaseSensitive ) {
        if ( pattern.length() > ( REGEX_HANDLER_PREFIX.length() + PATTERN_HANDLER_SUFFIX.length() + 1 )
            && pattern.startsWith( REGEX_HANDLER_PREFIX ) && pattern.endsWith( PATTERN_HANDLER_SUFFIX ) )
        {
            pattern = pattern.substring( REGEX_HANDLER_PREFIX.length(), pattern.length()
                                         - PATTERN_HANDLER_SUFFIX.length() );

            return new RegexSelector(pattern);
        }
        else
        {
            if ( pattern.length() > ( ANT_HANDLER_PREFIX.length() + PATTERN_HANDLER_SUFFIX.length() + 1 )
                && pattern.startsWith( ANT_HANDLER_PREFIX ) && pattern.endsWith( PATTERN_HANDLER_SUFFIX ) )
            {
                pattern =
                    pattern.substring( ANT_HANDLER_PREFIX.length(), pattern.length() - PATTERN_HANDLER_SUFFIX.length() );
            }

            return new AntSelector(pattern, isCaseSensitive);
        }
    }
}
