package com.example.anush.healthport.model;

import android.content.Context;

import java.io.File;

public class Storage {

    /**
     * Gets a directory from the app storage. If the directory doesn't exist, creates it.
     * @param context   App context.
     * @param dir_name  Directory name.
     * @return the directory.
     */
    public static File getAppDir(Context context, String dir_name)
    {
        File dir = new File(context.getApplicationContext().getFilesDir(), dir_name);
        if (!dir.exists())
        {
            dir.mkdir();
        }
        return dir;
    }
}
