package org.codehaus.plexus.util.concurrentdirscanner;

import java.io.File;

/**
 * @author Kristian Rosenvold
 */
public class ScannedFile
{
    private final File file;
    private final boolean isFile;
    private Boolean directory;

    public ScannedFile( File file )
    {
        this.file = file;
        this.isFile = file.isFile();
    }

    public File getFile()
    {
        return file;
    }

    public boolean isFile()
    {
        return isFile;
    }
    
    public boolean isDirectory()
    {
        if (isFile) return false;
        if ( directory == null){
            directory = file.isDirectory();
        }
        return directory;
    }

}
