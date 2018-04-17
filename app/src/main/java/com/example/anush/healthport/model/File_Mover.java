package com.example.anush.healthport.model;

import android.content.Context;
import android.net.Uri;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class File_Mover {

    /**
     * Copies a file from the source to a given destination.
     *
     * @param src  the source file.
     * @param dst  the destination file.
     * @return if the copy was successful.
     */
    public static void move(Context context, Uri src, File dst) throws IOException
    {
        InputStream in = context.getContentResolver().openInputStream(src);
        try {
            OutputStream out = new FileOutputStream(dst);
            try {
                // Transfer bytes from in to out
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
            } finally {
                out.close();
            }
        } finally {
            in.close();
        }
    }
}
