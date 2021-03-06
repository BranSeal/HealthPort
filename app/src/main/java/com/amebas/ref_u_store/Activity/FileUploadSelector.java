package com.amebas.ref_u_store.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.amebas.ref_u_store.Model.SingleAction;
import com.amebas.ref_u_store.Model.Storage;
import com.amebas.ref_u_store.R;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Activity for choosing how to upload files (via camera or local storage).
 */
public class FileUploadSelector extends Activity
{
    private String mCurrentPhotoPath;
    private static final int FILE_SELECT_CODE = 0;
    private static final int REQUEST_TAKE_PHOTO = 1;

    private Activity activity;
    private Context c;
    private SingleAction pictureAction;
    private SingleAction selectAction;

    private File selected_file;

    /**
     * Creates an upload selector activity.
     *
     * @param a              the activity to be called in.
     * @param c              the context of the application.
     * @param pictureAction  the action to take upon camera picture taken.
     * @param selectAction   the action to take upon file selection
     */
    public FileUploadSelector(Activity a, Context c, SingleAction pictureAction, SingleAction selectAction)
    {
        this.activity = a;
        this.c = c;
        this.pictureAction = pictureAction;
        this.selectAction = selectAction;
    }

    /**
     * Gets the image path for the picture taken.
     *
     * @return the image path.
     */
    public String getImgPath()
    {
        return this.mCurrentPhotoPath;
    }

    /**
     * Gets the file that was selected by the file selector.
     *
     * @return the selected file.
     */
    public File getSelectedFile()
    {
        return this.selected_file;
    }

    /**
     * Action to take a picture and perform.
     */
    public void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(this.c.getPackageManager()) != null)
        {
            // Create the File where the photo should go
            File photoFile = null;
            try
            {
                photoFile = createImageFile();
            }
            catch (IOException ex)
            {
                Log.e("ERROR", "Failed to create image file from camera");
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(
                    this.c,
                    "com.example.android.fileprovider",
                    photoFile
                );
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                this.activity.startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    /**
     * Action to choose file from local storage.
     */
    public void dispatchChooseFileIntent()
    {
        Intent intent = new Intent(this.c, LocalUploadConfirmActivity.class);
        activity.startActivityForResult(intent, FILE_SELECT_CODE);
    }

    @Override
    /**
     * Need to override this method in the activity it is being called in, and then call this in
     * the override.
     */
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == REQUEST_TAKE_PHOTO) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK)
            {
                pictureAction.perform();
            }
            else if (resultCode == RESULT_CANCELED)
            {
                File deleteF = (mCurrentPhotoPath!= null) ? new File(mCurrentPhotoPath) : null;
                if (deleteF != null)
                {
                    deleteF.delete();
                }
            }
        }
        else if (requestCode == FILE_SELECT_CODE)
        {
            if (resultCode == RESULT_OK)
            {
                selected_file = (File) data.getExtras().getSerializable("file");
                selectAction.perform();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.account_dashboard_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.action_menu:
                Intent profileIntent = new Intent(this.c, ViewProfile.class);
                startActivity(profileIntent);
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Creates a file from a picture.
     *
     * @return the created file.
     * @throws IOException if file path could not be created.
     */
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = new Storage(this.c).getImgDir();
        File image = File.createTempFile(
            imageFileName,  /* prefix */
            ".jpg",    /* suffix */
            storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }
}
