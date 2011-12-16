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
        this.includes = new String[]{};
        this.excludes = new String[]{};
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
}
