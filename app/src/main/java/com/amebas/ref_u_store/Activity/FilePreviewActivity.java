package com.amebas.ref_u_store.Activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.amebas.ref_u_store.Model.Account;
import com.amebas.ref_u_store.Model.Document;
import com.amebas.ref_u_store.Model.Pdf;
import com.amebas.ref_u_store.Model.Profile;
import com.amebas.ref_u_store.Model.SessionManager;
import com.amebas.ref_u_store.Model.Storage;
import com.amebas.ref_u_store.R;
import com.amebas.ref_u_store.Utilities.GeneralUtilities;
import com.tom_roush.pdfbox.pdmodel.PDDocument;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

public class FilePreviewActivity extends AppCompatActivity
{
    private AlertDialog.Builder builder;
    private String mCurrentPhotoPath;
    static final int REQUEST_TAKE_PHOTO = 1;
    private static final int FILE_SELECT_CODE = 0;
    ArrayAdapter pagesAdapter;

    private Pdf pdf;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filepreview);

        // Get passed pdf.
        Bundle extras = getIntent().getExtras();
        if (extras != null)
        {
            pdf = new Pdf((File) extras.getSerializable("doc"));
        }
        else
        {
            Log.d("ERROR", "Was not passed a PDF, creating empty");
            pdf = new Pdf(new Storage(this).getTempFile("temp.pdf"), new PDDocument());
        }

        // Create profile select dropdown.
        Spinner s = findViewById(R.id.profile_select);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, getProfilesForAccount());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Profile current = SessionManager.getInstance().getCurrentProfile();
        s.setAdapter(adapter);
        s.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                ((TextView) parent.getChildAt(0)).setTextColor(getResources().getColor(R.color.text_color));
                ((TextView) parent.getChildAt(0)).setTextSize(18);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });
        s.setSelection(adapter.getPosition(current.getName()));

        // Create "Add page" dialog
        String[] colors = {"Take picture", "Upload file"};
        builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose what to do:");
        builder.setItems(colors, (dialog, which) -> {
            if (which == 0) {
                dispatchTakePictureIntent();
            } else if (which == 1) {
                dispatchChooseFileIntent();
            }
        });
        Button add_button = findViewById(R.id.add_button);
        add_button.setOnClickListener(view -> builder.show());

        // Set pages view.
        populatePages(pdf);
        ((ListView) findViewById(R.id.table_scroll)).setOnItemClickListener((parent, view, pos, id) ->
        {
            // Load in current values for file preview.
            HashMap<String, String> values = new HashMap<>();
            values.put("filename", ((EditText) findViewById(R.id.filename_input)).getText().toString());
            values.put("tags", ((EditText) findViewById(R.id.tag_input)).getText().toString());
            values.put("profile_name", ((Spinner) findViewById(R.id.profile_select)).getSelectedItem().toString());
            Intent intent = new Intent(this, PagePreviewActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("doc", pdf.getLocation());
            bundle.putInt("page_num", pos + 1);
            bundle.putSerializable("values", values);
            intent.putExtras(bundle);
            startActivity(intent);
        });

        // Load previous values if they exist.
        if (extras != null)
        {
            HashMap<String, String> values = (HashMap<String, String>) extras.getSerializable("values");
            if (values != null)
            {
                ((EditText) findViewById(R.id.filename_input)).setText(values.get("filename"));
                ((EditText) findViewById(R.id.tag_input)).setText(values.get("tags"));
                s.setSelection(adapter.getPosition(values.get("profile_name")));
            }
        }
    }

    /**
     * Gets a list of the tags that the user typed into the tag field.
     *
     * @return list of tags.
     */
    private String[] getTags()
    {
        String text = ((EditText) findViewById(R.id.tag_input)).getText().toString().trim();
        return (text.length() > 0) ? text.split(" ") : new String[0];
    }

    /**
     * Fills out the list view of pages.
     *
     * @param pdf  the pdf file to get pages from.
     */
    private void populatePages(Pdf pdf)
    {
        int num_pages = pdf.getPdf().getNumberOfPages();
        ArrayList<String> pages = new ArrayList<>(num_pages);
        for (int i = 0; i < num_pages; i++)
        {
            pages.add(getString(R.string.page_num) + " " + (i + 1));
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.page_listview_design, R.id.title, pages);
        ((ListView) findViewById(R.id.table_scroll)).setAdapter(adapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == REQUEST_TAKE_PHOTO) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                // Create new PDF with new image
                File img = new File(mCurrentPhotoPath);
                Storage temp = new Storage(this);
                File temp_pdf = temp.getTempFile("temp2.pdf");
                Pdf new_pdf;
                try
                {
                    new_pdf = Pdf.fromImage(this, img, temp_pdf);
                }
                catch (java.io.IOException e)
                {
                    Log.d("ERROR", "File not Found");
                    new_pdf = new Pdf(temp_pdf, new PDDocument());
                }
                // Merge PDFs
                this.pdf = Pdf.append(this, this.pdf, new_pdf, this.pdf.getLocation());
                populatePages(this.pdf);
                // Delete 2nd temp
                temp_pdf.delete();
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

    private String[] getProfilesForAccount() {
        Account acc = SessionManager.getInstance().getSessionAccount();
        List<Profile> profiles = acc.getProfiles();
        if(profiles == null) {
            GeneralUtilities.showToast(getApplicationContext(), "Something went wrong, please login again");
            return null;
        } else {
            String[] profileArr = new String[profiles.size()];
            for (int i = 0; i < profiles.size(); i++) {
                Profile prof = profiles.get(i);
                profileArr[i] = prof.getName();
            }
            return profileArr;
        }
    }

    public void goToDashboard(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.cancel_confirm));
        builder.setCancelable(true);

        builder.setPositiveButton(getString(R.string.yes), (dialog, id) -> {
            dialog.cancel();
            Storage storage = new Storage(this);
            storage.clearTemp();
            storage.clearImgDir();
            Intent intent = new Intent(this, AccountDashboardActivity.class);
            startActivity(intent);
        });
        builder.setNegativeButton(getString(R.string.no), (dialog, id) -> dialog.cancel());

        AlertDialog alert = builder.create();
        alert.show();
    }

    /**
     * Checks inputs, sees if file name was given and if file with same name already exists.
     *
     * @param v  the view that called this method.
     */
    public void checkInputs(View v)
    {
        String filename = ((EditText) findViewById(R.id.filename_input)).getText().toString();
        if (filename.length() < 1)
        {
            showErrorAlert(getString(R.string.no_filename));
            return;
        }
        String profile = ((Spinner) findViewById(R.id.profile_select)).getSelectedItem().toString();
        File dir = new Storage(this).getUserDocs(profile);
        File file = new File(dir, filename + ".pdf");
        if (file.exists())
        {
            showErrorAlert(getString(R.string.file_exists));
            return;
        }
        createDocument(file, profile, getTags());
    }

    /**
     * Creates document, adding to local storage and database.
     *
     * @param path     where to save new document.
     * @param profile  profile to assign to.
     * @param tags     list of tags given to the document.
     */
    public void createDocument(File path, String profile, String[] tags)
    {
        try
        {
            pdf.getPdf().save(path);
        }
        catch (java.io.IOException e)
        {
            Log.d("ERROR", "Path doesn't exist");
        }
        // Upload to database.
        ArrayList<String> tag_list = new ArrayList<>();
        for (String tag: tags)
        {
            tag_list.add(tag);
        }
        ArrayList<String> paths = new ArrayList<>();
        paths.add(path.getAbsolutePath());
        Document doc = new Document(paths, path.getName(), tag_list);
        Profile to_assign = null;
        for (Profile p: SessionManager.getInstance().getSessionAccount().getProfiles())
        {
            if (p.getName().equals(profile))
            {
                to_assign = p;
                break;
            }
        }
        if (to_assign == null)
        {
            to_assign = SessionManager.getInstance().getCurrentProfile();
        }
        SessionManager.getInstance().getDatabase().updateDocument(doc, to_assign);
        to_assign.getDocuments().add(doc);
        // Clear temp files
        Storage storage = new Storage(this);
        storage.clearTemp();
        storage.clearImgDir();
        // Move to confirmation page.
        Intent intent = new Intent(this, DocConfirmActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("doc", path);
        bundle.putBoolean("isNew", true);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    /**
     * Displays alert for any invalid inputs
     *
     * @msg  the message to display.
     */
    private void showErrorAlert(String msg)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(msg);
        builder.setCancelable(true);
        builder.setPositiveButton(getString(R.string.ok), (dialog, id) -> dialog.cancel());
        builder.create().show();
    }
}