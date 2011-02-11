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

import java.io.File;
import java.util.Vector;
import java.util.regex.Pattern;

/**
 * @author Kristian Rosenvold
 */
public abstract class SelectorPattern
{
    static final String PATTERN_HANDLER_PREFIX = "[";

    static final String PATTERN_HANDLER_SUFFIX = "]";

    private final String sourcePattern;

    protected SelectorPattern(String pattern)
    {
        this.sourcePattern = pattern;
    }

    public String getSourcePattern()
    {
        return sourcePattern;
    }

    static class AntSelectorPatter
        extends SelectorPattern
    {
        static final String ANT_HANDLER_PREFIX = "%ant" + PATTERN_HANDLER_PREFIX;

        private final String pattern;

        private final Vector tokenizedAntPattern;

        private final boolean startsWithPathSeparator;

        AntSelectorPatter( String patternString )
        {
            super( patternString);
            boolean isAntPrefixPattern = isAntPrefixPattern( patternString );
            this.pattern = isAntPrefixPattern ? getAntPrefixPattern( patternString ) : patternString;
            this.tokenizedAntPattern = SelectorUtils.tokenizePath( pattern, File.separator );
            startsWithPathSeparator = patternString.startsWith( File.separator );

        }

        public boolean matchPath( String str, boolean isCaseSensitive )
        {
            return SelectorUtils.matchAntPathPattern( tokenizedAntPattern, startsWithPathSeparator, str,
                                                      isCaseSensitive );
        }

        private static String getAntPrefixPattern( String pattern )
        {
            return pattern.substring( ANT_HANDLER_PREFIX.length(), pattern.length() - PATTERN_HANDLER_SUFFIX.length() );
        }


        private static boolean isAntPrefixPattern( String pattern )
        {
            return pattern.length() > ( ANT_HANDLER_PREFIX.length() + PATTERN_HANDLER_SUFFIX.length() + 1 )
                && pattern.startsWith( ANT_HANDLER_PREFIX ) && pattern.endsWith( PATTERN_HANDLER_SUFFIX );
        }
    }

    static class SubstringSelectorPattern
        extends SelectorPattern
    {
        public static final String REGEX_HANDLER_PREFIX = "%regex" + PATTERN_HANDLER_PREFIX;

        private final String pattern;

        private final Pattern compiledRegex;


        SubstringSelectorPattern( String pattern )
        {
            super( pattern );
            this.pattern = pattern;
            this.compiledRegex = Pattern.compile( getSubstringPattern( pattern ) );
        }

        public boolean matchPath( String str, boolean isCaseSensitive )
        {
            return compiledRegex.matcher( str ).matches();
        }

        private static String getSubstringPattern( String pattern )
        {
            return pattern.substring( REGEX_HANDLER_PREFIX.length(),
                                      pattern.length() - PATTERN_HANDLER_SUFFIX.length() );
        }

        static boolean isSubstringPattern( String pattern )
        {
            return pattern.length() > ( REGEX_HANDLER_PREFIX.length() + PATTERN_HANDLER_SUFFIX.length() + 1 )
                && pattern.startsWith( REGEX_HANDLER_PREFIX ) && pattern.endsWith( PATTERN_HANDLER_SUFFIX );
        }


    }


    public static SelectorPattern getPattern( String patternString )
    {
        return SubstringSelectorPattern.isSubstringPattern( patternString ) ? new SubstringSelectorPattern(
            patternString ) : (SelectorPattern) new AntSelectorPatter( patternString );
    }


    public abstract boolean matchPath( String str, boolean isCaseSensitive );

}
