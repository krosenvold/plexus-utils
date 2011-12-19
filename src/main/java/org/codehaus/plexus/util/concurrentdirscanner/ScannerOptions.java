package org.codehaus.plexus.util.concurrentdirscanner;

/**
 * @author Kristian Rosenvold
 */
public class ScannerOptions
{
    private int threads = 3;

    private String[] includes;

    private String[] excludes;

    private boolean caseSensitive = true;

    private boolean followSymLinks = true;

    public ScannerOptions( String[] includes, String[] excludes )
    {
        this.includes = includes;
        this.excludes = excludes;
    }

    public ScannerOptions(  )
    {
        this.includes = null;
        this.excludes = null;
    }
    
    public int getThreads()
    {
        return threads;
    }

    public String[] getIncludes()
    {
        return includes;
    }

    public String[] getExcludes()
    {
        return excludes;
    }

    public boolean isCaseSensitive()
    {
        return caseSensitive;
    }

    public boolean isFollowSymLinks()
    {
        return followSymLinks;
    }

    public void setupDefaultFilters()
    {
        if ( includes == null )
        {
            // No includes supplied, so set it to 'matches all'
            includes = new String[1];
            includes[0] = "**";
        }
        if ( excludes == null )
        {
            excludes = new String[0];
        }
    }
}
