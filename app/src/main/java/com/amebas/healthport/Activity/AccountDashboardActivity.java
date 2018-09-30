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

import com.amebas.healthport.Adapter.DocumentsAdapter;
import com.amebas.healthport.Model.Document;
import com.amebas.healthport.Model.Pdf;
import com.amebas.healthport.Model.Profile;
import com.amebas.healthport.Model.SessionManager;
import com.amebas.healthport.Model.Storage;
import com.amebas.healthport.R;
import com.leinardi.android.speeddial.SpeedDialActionItem;
import com.leinardi.android.speeddial.SpeedDialView;
import com.tom_roush.pdfbox.pdmodel.PDDocument;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static android.content.ContentValues.TAG;

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
                adapter.getFilter().filter(s.toString());
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
                pdf = new Pdf(temp, new PDDocument());
            }
            Bundle bundle = new Bundle();
            bundle.putSerializable("doc", pdf.getLocation());
            intent.putExtras(bundle);
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

    public void showToast(String text)
    {
        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
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
     * This function populates the listview with the current user's documents
     */
    public void populateDocuments() {
        ArrayList<Document> documents = getDocuments();
        this.adapter = new DocumentsAdapter(this, documents);

        ListView listview = findViewById(R.id.documentViewer);
        listview.setAdapter(adapter);

        listview.setOnItemClickListener((parent, view, position, id) -> {
            Document document =  (Document) parent.getItemAtPosition(position);
            showToast(document.getName() + document.getTagString());
            //openDocument(document, position);
        });
    }

    /**
     * This functions relays the user to the document viewer for the document opened
     * @param document the string containing the file directory of the document selected
     * @param position the position in the listview of the document
     */
    public void openDocument(String document, int position) {

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

    //temporary document population function
    public void populateProfileWithDocuments() {
        SessionManager instance = SessionManager.getInstance();
        Profile currProfile = instance.getCurrentProfile();

        ArrayList<Document> docs = currProfile.getDocuments();
        File dir = new Storage(this).getUserDocs(currProfile.getName());
        File[] files = dir.listFiles() == null ? new File[0] : dir.listFiles();
        for (File file: files)
        {
            if (!file.isDirectory())
            {
                Document doc = new Document(file.getName());
                ArrayList<String> tags = new ArrayList<>();
                tags.add("Plz");
                doc.setTags(tags);
                docs.add(doc);
            }
        }
        currProfile.setDocuments(docs);
    }
}
