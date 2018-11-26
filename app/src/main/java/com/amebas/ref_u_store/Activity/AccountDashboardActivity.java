package com.amebas.ref_u_store.Activity;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.amebas.ref_u_store.Adapter.DocumentsAdapter;
import com.amebas.ref_u_store.Model.Account;
import com.amebas.ref_u_store.Model.Document;
import com.amebas.ref_u_store.Model.Pdf;
import com.amebas.ref_u_store.Model.Profile;
import com.amebas.ref_u_store.Model.SessionManager;
import com.amebas.ref_u_store.Model.Storage;
import com.amebas.ref_u_store.R;
import com.amebas.ref_u_store.Utilities.TagFilter;
import com.leinardi.android.speeddial.SpeedDialActionItem;
import com.leinardi.android.speeddial.SpeedDialView;
import com.tom_roush.pdfbox.pdmodel.PDDocument;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class AccountDashboardActivity extends AppCompatActivity {

    static final int REQUEST_TAKE_PHOTO = 1;
    private static final int FILE_SELECT_CODE = 0;
    private String mCurrentPhotoPath;
    private DocumentsAdapter adapter;

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

        //attempts to set the fab grey permanenetly
        int grey = getResources().getColor(R.color.superLightGreyButtonColor);
        FloatingActionButton mainFab = speedDialView.getMainFab();
        mainFab.setBackgroundTintList(ColorStateList.valueOf(grey));
        mainFab.setRippleColor(grey);

        //populate listview with documents
        populateDocuments();


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

        EditText etSearch = findViewById(R.id.etSearch);
        // Add Text Change Listener to EditText
        etSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Call back the Adapter with current character to Filter
                searchDocuments(s.toString());

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
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
        else if (requestCode == FILE_SELECT_CODE)
        {
            if (resultCode == RESULT_OK)
            {
                File selected = (File) data.getExtras().getSerializable("file");
                File temp = new Storage(this).getTempFile("temp.pdf");
                try
                {
                    // Copy file data over to new temp file so it doesn't get overriden in future.
                    FileUtils.copyFile(selected, temp);
                }
                catch (java.io.IOException e)
                {
                    Log.e("ERROR", e.getMessage());
                }
                Bundle bundle = new Bundle();
                bundle.putSerializable("doc", temp);
                Intent intent = new Intent(this, FilePreviewActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
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
            File temp = new Storage(this).getTempFile("temp.pdf");
            Pdf pdf;
            try
            {
                pdf = Pdf.fromImage(this, new File(mCurrentPhotoPath), temp);
            }
            catch (java.io.IOException e)
            {
                Log.d("ERROR", "File not found");
                pdf = new Pdf(temp);
            }
            Bundle bundle = new Bundle();
            bundle.putSerializable("doc", pdf.getLocation());
            intent.putExtras(bundle);
            startActivity(intent);
        } else {
            throw new RuntimeException("mCurrentPhotoPath was never set!");
        }
    }

    private void dispatchChooseFileIntent()
    {
        Intent intent = new Intent(this, LocalUploadConfirmActivity.class);
        startActivityForResult(intent, FILE_SELECT_CODE);
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

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = new Storage(this).getImgDir();
        File image = File.createTempFile(
            imageFileName,  /* prefix */
            ".jpg",         /* suffix */
            storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    /**
     * This function populates the listview with new searched documents
     */
    public void searchDocuments(String filter) {
        ArrayList<Document> docs = getDocuments();
        ArrayList<Document> filteredDocs = TagFilter.filter(docs, filter);
//        if (filteredDocs.size() == 0)
//            return;
        this.adapter = new DocumentsAdapter(this, filteredDocs);
        ListView listview = findViewById(R.id.documentViewer);
        listview.setAdapter(adapter);
        listview.setOnItemClickListener((parent, view, position, id) ->
        {
            Document document = (Document) parent.getItemAtPosition(position);
            openDocument(document);
        });
        adapter.notifyDataSetChanged();
    }

    /**
     * This function populates the listview with the current user's documents
     */
    public void populateDocuments() {
        ArrayList<Document> documents = getDocuments();
        this.adapter = new DocumentsAdapter(this, documents);

        ListView listview = findViewById(R.id.documentViewer);
        listview.setAdapter(adapter);

        listview.setOnItemClickListener((parent, view, position, id) ->
        {
            Document document =  (Document) parent.getItemAtPosition(position);
            openDocument(document);
        });
    }

    /**
     * This functions relays the user to the document viewer for the document opened
     *
     * @param document nthe string containing the file directory of the document selected
     */
    public void openDocument(Document document)
    {
        Intent intent = new Intent(this, ViewDocActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("doc", (HashMap) document.toMap());
        intent.putExtras(bundle);
        startActivity(intent);
    }

    /**
     * This function stringifies the documents in order to be populated into the listview
     * @return docuemnt list
     */
    public ArrayList<Document> getDocuments() {
        SessionManager instance = SessionManager.getInstance();
        Profile currProfile = instance.getCurrentProfile();
        return currProfile.getDocuments();
    }

    @Override
    public void onBackPressed()
    {
        // Switch profiles
        SessionManager.getInstance().setCurrentProfile(null);
        Intent intent = new Intent(this, ProfileSelectActivity.class);
        startActivity(intent);
    }
}
