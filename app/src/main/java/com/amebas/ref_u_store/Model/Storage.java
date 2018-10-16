package com.amebas.ref_u_store.Model;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.File;

public class Storage extends File {

    private File temp;
    private File imgs;
    private File docs;

    /**
     * Gets internal storage and gets directories for temporary and document storage, creating them
     * if they don't already exist.
     *
     * @param context  app context.
     */
    public Storage(Context context)
    {
        super(context.getFilesDir().getAbsolutePath());
        this.temp = new File(this, "temp");
        if (!this.temp.exists())
        {
            this.temp.mkdir();
        }
        this.imgs = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        if (!this.imgs.exists())
        {
            this.imgs.mkdir();
        }
        this.docs = new File(this, "documents");
        if (!this.docs.exists())
        {
            this.docs.mkdir();
        }
    }

    /**
     * Gets File instance for file in temp directory. Creates temp directory if it doesn't exist.
     *
     * @param filename  filename of File to make.
     * @return file instance of file.
     */
    public File getTempFile(String filename)
    {
        return new File(this.temp, filename);
    }

    /**
     * Cleans out the temp directory.
     */
    public void clearTemp()
    {
        for (File file: this.temp.listFiles())
        {
            file.delete();
        }
    }

    /**
     * Gets directory in storage for a profile's documents, creating it if nonexistent.
     *
     * @param profile  profile whose directory to retrieve.
     * @return the profile' directory.
     */
    public File getUserDocs(String profile)
    {
        if (profile == "")
        {
            profile = "_";
        }
        profile = profile.replaceAll("\\s+","");
        File dir = new File(this.docs, profile);
        if (!dir.exists())
        {
            dir.mkdir();
        }
        return dir;
    }

    /**
     * Clears profile's storage directory.
     *
     * @param profile  the profile whose directory to clear
     */
    public void clearUserDocs(String profile)
    {
        if (profile == "")
        {
            profile = "_";
        }
        profile = profile.replaceAll("\\s+","");
        File dir = new File(this.docs, profile);
        if (dir.exists())
        {
            for (File file: dir.listFiles())
            {
                file.delete();
            }
        }
    }

    /**
     * Gets the image directory.
     *
     * @return the image directory.
     */
    public File getImgDir()
    {
        return this.imgs;
    }

    /**
     * Cleans the image directory.
     */
    public void clearImgDir()
    {
        for (File file: this.imgs.listFiles())
        {
            file.delete();
        }
    }
}
