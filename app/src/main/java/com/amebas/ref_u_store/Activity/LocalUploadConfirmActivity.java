package com.amebas.ref_u_store.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.amebas.ref_u_store.Model.Storage;
import com.amebas.ref_u_store.R;
import com.amebas.ref_u_store.Utilities.FileUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Activity for confirming whether the selected file from local storage
 * should actually be added.
 */
public class LocalUploadConfirmActivity extends AppCompatActivity
{
    // Request codes for activities.
    private static final int FILE_SELECT_CODE = 0;
    private final String tag = "DEBUG_TAG";

    private File temp_local;
    private File temp_pdf;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_upload_confirm);
        openFileExplorer();
    }

    /**
     * Action to choose file from local storage.
     */
    public void openFileExplorer()
    {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        String[] mimetypes = {"image/*", "application/pdf"}; // Images and PDFs
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimetypes);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        try
        {
            this.startActivityForResult(Intent.createChooser(intent, getString(R.string.choose_file)), FILE_SELECT_CODE);
        }
        catch (android.content.ActivityNotFoundException ex)
        {
            // Potentially direct the user to the Market with a Dialog
            Toast.makeText(this, getString(R.string.no_file_manager), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == FILE_SELECT_CODE)
        {
            if (resultCode == RESULT_OK)
            {
                String path = "";
                try
                {
                    path = FileUtils.getPath(this, data.getData());

                }
                catch (Exception e)
                {
                    try
                    {
                        // If was unable to get path to file, write into a temporary file.
                        InputStream stream = getContentResolver().openInputStream(data.getData());
                        Storage storage = new Storage(this);
                        File temp = storage.getTempFile("temp_upload" + getFileExtension(data.getData().getPath()));
                        byte[] buffer = new byte[stream.available()];
                        stream.read(buffer);

                        OutputStream output = new FileOutputStream(temp);
                        output.write(buffer);
                        path = temp.getAbsolutePath();

                        // To delete at the end of the activity.
                        temp_local = new File(path);
                    }
                    catch (Exception e2)
                    {
                        Log.d(tag, e2.getMessage());
                    }
                }
                File file = new File(path);
                if (isImage(file.getAbsolutePath()))
                {
                    Log.d(tag, "IS IMAGE");
                }
                else
                {
                    Log.d(tag, "IS NOT IMAGE");
                }
                openFileExplorer();
            }
            else if (resultCode == RESULT_CANCELED)
            {
                // Returns to the previous activity if canceled file selection.
                finish();
            }
        }
    }

    /**
     * Gets the file extension for a file from a given path.
     *
     * @param path  the path to the file.
     * @return the file extension (includes the period in front).
     */
    private String getFileExtension(String path)
    {
        String[] divided = path.split("\\.");
        return "." + divided[divided.length - 1];
    }

    /**
     * Checks if a given file is an image (png, jpg, gif).
     *
     * @param filename  the filename to check. Needs to include extension.
     * @return whether or not the file is an image.
     */
    private boolean isImage(String filename)
    {
        String[] image_extensions = {".jpg", ".jpeg", ".png", ".gif"};
        for (String extension: image_extensions)
        {
            if (filename.endsWith(extension))
            {
                return true;
            }
        }
        return false;
    }

    private File imageToPdf(File image)
    {
        return image;
    }
}
