package com.amebas.ref_u_store.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.amebas.ref_u_store.Model.Pdf;
import com.amebas.ref_u_store.Model.Storage;
import com.amebas.ref_u_store.R;
import com.amebas.ref_u_store.Utilities.FileUtils;
import com.github.barteksc.pdfviewer.PDFView;

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
    private static final int SELECTOR_CODE = 0;

    private static final int SPACING = 10;

    private File pdf;
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
            this.startActivityForResult(Intent.createChooser(intent, getString(R.string.choose_file)), SELECTOR_CODE);
        }
        catch (android.content.ActivityNotFoundException ex)
        {
            // Potentially direct the user to the Market with a Dialog
            Toast.makeText(this, getString(R.string.no_file_manager), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Confirms the selected file to be used in the document.
     *
     * @param v  the view that called the method.
     */
    public void confirmSelection(View v)
    {
        Bundle bundle = new Bundle();
        bundle.putSerializable("file", pdf);
        endActivity(RESULT_OK, bundle);
    }

    /**
     * Opens the file selector again to select a different file.
     *
     * @param v  the view that called the method.
     */
    public void retrySelection(View v)
    {
        deleteTemp();
        openFileExplorer();
    }

    /**
     * Cancels the selection of a file and returns to the previous screen.
     *
     * @param v  the view that called the method.
     */
    public void cancelSelection(View v)
    {
        deleteTemp();
        endActivity(RESULT_CANCELED, null);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == SELECTOR_CODE)
        {
            if (resultCode == RESULT_OK)
            {
                String path = "";
                // Attempt to get file path from FileUtils.
                try
                {
                    path = FileUtils.getPath(this, data.getData());

                }
                // If was unable to get path to file, write into a temporary file.
                catch (Exception e)
                {
                    try
                    {
                        // Write to buffer
                        InputStream stream = getContentResolver().openInputStream(data.getData());
                        Storage storage = new Storage(this);
                        File temp = storage.getTempFile("temp_upload" + getFileExtension(data.getData().getPath()));
                        byte[] buffer = new byte[stream.available()];
                        stream.read(buffer);
                        // Read buffer into temp file.
                        OutputStream output = new FileOutputStream(temp);
                        output.write(buffer);
                        path = temp.getAbsolutePath();
                        temp_local = new File(path);
                    }
                    catch (Exception e2)
                    {
                        Log.d("ERROR", e2.getMessage());
                    }
                }
                pdf = new File(path);
                // If an image, convert to PDF and delete old temp file.
                if (isImage(pdf.getAbsolutePath()))
                {
                    pdf = imageToPdf(pdf);
                    if (temp_local != null && temp_local.exists())
                    {
                        temp_local.delete();
                    }
                }
                loadPdf(pdf);
            }
            else if (resultCode == RESULT_CANCELED)
            {
                // Returns to the previous activity if canceled file selection.
                deleteTemp();
                endActivity(RESULT_CANCELED, null);
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
        String file_extension = getFileExtension(filename).toLowerCase();
        for (String extension: image_extensions)
        {
            if (file_extension.equals(extension))
            {
                return true;
            }
        }
        return false;
    }

    /**
     * Converts an image file into a pdf.
     *
     * @param image  the image file to convert.
     * @return the path to the new pdf file.
     */
    private File imageToPdf(File image)
    {
        temp_pdf = new Storage(this).getTempFile("temp_pdf.pdf");
        try
        {
            Pdf.fromImage(this, image, temp_pdf);
        }
        catch (java.io.IOException e)
        {
            Log.d("ERROR", e.getMessage());
        }
        return temp_pdf;
    }

    /**
     * Loads a pdf file into the view.
     *
     * @param file  the file to load.
     */
    private void loadPdf(File file)
    {
        // Add pdf file to view.
        PDFView pdfView = findViewById(R.id.pdfView);
        if (file.exists())
        {
            pdfView.fromFile(file)
                .spacing(SPACING)
                .onError(t -> Log.d("ERROR", "Failed to load pdf"))
                .load();
        }
        else
        {
            Log.d("ERROR", "No Pdf to load");
        }
    }

    /**
     * Deletes the temporary files used here.
     */
    private void deleteTemp()
    {
        if (temp_local != null && temp_local.exists())
        {
            temp_local.delete();
        }
        if (temp_pdf != null && temp_pdf.exists())
        {
            temp_pdf.delete();
        }
    }

    /**
     * Ends the activities with the given result code and bundle.
     *
     * @param result_code  result code of the activity.
     * @param bundle       data to bundle along with activity end.
     */
    private void endActivity(int result_code, Bundle bundle)
    {
        Intent intent = new Intent();
        if (bundle != null)
        {
            intent.putExtras(bundle);
        }
        setResult(result_code, intent);
        finish();
    }
}
