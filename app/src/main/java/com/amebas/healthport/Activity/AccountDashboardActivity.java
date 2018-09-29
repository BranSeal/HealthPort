package com.amebas.healthport.Activity;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.amebas.healthport.Model.Profile;
import com.amebas.healthport.Model.SessionManager;
import com.amebas.healthport.R;
import com.leinardi.android.speeddial.SpeedDialActionItem;
import com.leinardi.android.speeddial.SpeedDialView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AccountDashboardActivity extends AppCompatActivity {

    static final int REQUEST_TAKE_PHOTO = 1;
    private static final int FILE_SELECT_CODE = 0;
    private String mCurrentPhotoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_dashboard);

        SessionManager instance = SessionManager.getInstance();
        Profile currentProfile = instance.getCurrentProfile();

        TextView welcomeText = findViewById(R.id.welcomeText);
        welcomeText.setText("Welcome " + currentProfile.getName() + "!");

        SpeedDialView speedDialView = findViewById(R.id.speedDial);
        speedDialView.addActionItem(
                new SpeedDialActionItem.Builder(R.id.firstFab, R.drawable.ic_file_upload_white_24dp)
                        .setLabel("Upload a File")
                        .setTheme(R.style.HealthPort)
                        .create()
        );

        speedDialView.addActionItem(
                new SpeedDialActionItem.Builder(R.id.secondFab, R.drawable.ic_camera_alt_white_24dp)
                        .setLabel("Take a Picture")
                        .setTheme(R.style.HealthPort)
                        .create()
        );

        int grey = getResources().getColor(R.color.superLightGreyButtonColor);
        FloatingActionButton mainFab = speedDialView.getMainFab();
        mainFab.setBackgroundTintList(ColorStateList.valueOf(grey));
        mainFab.setRippleColor(grey);


        // Note: To ensure that launching the camera/file browser works, make sure that app permissions
        // are enabled
        speedDialView.setOnActionSelectedListener(new SpeedDialView.OnActionSelectedListener() {
            @Override
            public boolean onActionSelected(SpeedDialActionItem speedDialActionItem) {
                switch (speedDialActionItem.getId()) {
                    case R.id.firstFab:
                        dispatchChooseFileIntent();
                        return false; // true to keep the Speed Dial open
                    case R.id.secondFab:
                        dispatchTakePictureIntent();
                        return false; // true to keep the Speed Dial open
                    default:
                        return false;
                }
            }
        });
    }

    public void toProfile(View view) {
        Intent profileIntent = new Intent(this, ViewProfile.class);
        startActivity(profileIntent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == REQUEST_TAKE_PHOTO) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                dispatchFilePreview();
            } else if (resultCode == RESULT_CANCELED) {
                File deleteF = (mCurrentPhotoPath!= null) ? new File(mCurrentPhotoPath) : null;
                boolean deleted = false;
                if (deleteF != null)
                    deleted = deleteF.delete();
                Log.w("Delete Check", "Empty file: " + mCurrentPhotoPath + "Deleted: " + deleted);
            }
        }
    }
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    private void dispatchFilePreview() {
        Intent intent = new Intent(this, FilePreviewActivity.class);
        if (mCurrentPhotoPath != null) {
            intent.setData(Uri.parse(mCurrentPhotoPath));
            startActivity(intent);
        } else {
            throw new RuntimeException("mCurrentPhotoPath was never set!");
        }
    }

    private void dispatchChooseFileIntent() {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            //intent.setType("*/*");      //all files
            intent.setType("*/*");   //XML file only
            intent.addCategory(Intent.CATEGORY_OPENABLE);

            try {
                startActivityForResult(Intent.createChooser(intent, "Select a File to Upload"), FILE_SELECT_CODE);
            } catch (android.content.ActivityNotFoundException ex) {
                // Potentially direct the user to the Market with a Dialog
                Toast.makeText(this, "Please install a File Manager.", Toast.LENGTH_SHORT).show();
            }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.account_dashboard_menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_menu:
                Intent profileIntent = new Intent(this, ViewProfile.class);
                startActivity(profileIntent);
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void showToast(String text) {
        Toast toast = Toast.makeText(
                getApplicationContext(),
                text,
                Toast.LENGTH_SHORT);
        toast.show();
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }
}
