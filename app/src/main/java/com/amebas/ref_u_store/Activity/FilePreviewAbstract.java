package com.amebas.ref_u_store.Activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.amebas.ref_u_store.Model.Document;
import com.amebas.ref_u_store.Model.Pdf;
import com.amebas.ref_u_store.Model.BinaryAction;
import com.amebas.ref_u_store.Model.Profile;
import com.amebas.ref_u_store.Model.SessionManager;
import com.amebas.ref_u_store.Model.Storage;
import com.amebas.ref_u_store.R;
import com.amebas.ref_u_store.Utilities.GeneralUtilities;
import com.tom_roush.pdfbox.pdmodel.PDDocument;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Abstract class for viewing and editing a document's details.
 */
public abstract class FilePreviewAbstract extends AppCompatActivity
{
    private Pdf pdf;
    private FileUploadSelector selector;

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
            unloadBundle(extras);
        }
        else
        {
            Log.d("ERROR", "Was not passed a PDF, creating empty");
            pdf = new Pdf(new Storage(this).getTempFile("temp.pdf"));
        }

        // Create profile select dropdown.
        List<Profile> profiles = SessionManager.getInstance().getSessionAccount().getProfiles();
        Profile[] names = new Profile[profiles.size()];
        for (int i = 0; i < profiles.size(); i++)
        {
            names[i] = profiles.get(i);
        }
        Spinner s = findViewById(R.id.profile_select);
        ArrayAdapter<Profile> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, names);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Profile current = SessionManager.getInstance().getCurrentProfile();
        s.setAdapter(adapter);
        s.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                ((TextView) parent.getChildAt(0)).setTextColor(getResources().getColor(R.color.in_app_primary));
                ((TextView) parent.getChildAt(0)).setTextSize(18);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });
        s.setSelection(adapter.getPosition(current));

        // Create "Add page" dialog
        createPageAddButton();

        // Create actions for page adder.
        selector = new FileUploadSelector(this, getApplicationContext(), () ->
        {
            // Create new PDF with new image
            File img = new File(selector.getImgPath());
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
                new_pdf = new Pdf(temp_pdf);
            }
            // Merge PDFs
            this.pdf = Pdf.append(this, this.pdf, new_pdf, this.pdf.getLocation());
            populatePages(this.pdf);
            // Delete 2nd temp
            temp_pdf.delete();
        }, () ->
        {
            // Append file from local storage.
            Pdf temp_pdf = new Pdf(selector.getSelectedFile());
            this.pdf = Pdf.append(this, this.pdf, temp_pdf, this.pdf.getLocation());
            populatePages(this.pdf);
        });

        // Set pages view.
        createPageList();

        // Load previous values if they exist.
        if (extras != null)
        {
            HashMap<String, String> values = (HashMap<String, String>) extras.getSerializable("values");
            if (values != null)
            {
                ((EditText) findViewById(R.id.filename_input)).setText(values.get("filename"));
                ((EditText) findViewById(R.id.tag_input)).setText(values.get("tags"));
                for (Profile p: SessionManager.getInstance().getSessionAccount().getProfiles())
                {
                    if (p.getId().equals(values.get("profile_id")))
                    {
                        s.setSelection(adapter.getPosition(p));
                        break;
                    }
                }
            }
        }
    }

    /**
     * Create a bundle and load it with whatever values need to be passed to page preview.
     *
     * @param pos  the position of the page list that was selected.
     * @return bundle with values to pass.
     */
    public abstract Bundle loadBundle(int pos);

    /**
     * Unloads a bundle that was passed.
     *
     * @param b  bundle to unload.
     */
    public abstract void unloadBundle(Bundle b);

    /**
     * If all inputs are valid, makes given changes to the document.
     *
     * @param v  the view that called the method.
     */
    public abstract void makeChanges(View v);

    /**
     * Checks inputs, sees if file name was given and if file with same name already exists.
     * Override with to add additional checks.
     *
     * @return whether the inputs were valid or not.
     */
    public boolean areInputsValid()
    {
        PDDocument doc = pdf.getPdf();
        int count = doc.getPages().getCount();
        Pdf.close(doc);
        if (count < 1)
        {
            GeneralUtilities.displayMessage(this, getString(R.string.no_pages), () -> {});
            return false;
        }
        String filename = ((EditText) findViewById(R.id.filename_input)).getText().toString();
        if (filename.length() < 1)
        {
            GeneralUtilities.displayMessage(this, getString(R.string.no_filename), () -> {});
            return false;
        }
        return true;
    }

    /**
     * Creates document.
     *
     * @param path  where to save new document.
     * @param tags  list of tags given to the document.
     * @return the created document.
     */
    public Document createDocument(File path, ArrayList<String> tags)
    {
        try
        {
            PDDocument doc = pdf.getPdf();
            doc.save(path);
            doc.close();
        }
        catch (java.io.IOException e)
        {
            Log.d("ERROR", "Path doesn't exist");
        }
        Document doc = new Document(path.getName(), path.getAbsolutePath(), tags);
        return doc;
    }

    /**
     * Uploads a document to a profile locally and to the database.
     *
     * @param doc           the document to upload.
     * @param profile_name  the name of the profile to upload to.
     */
    public void uploadDocument(Document doc, String profile_name)
    {
        Profile to_assign = null;
        for (Profile p: SessionManager.getInstance().getSessionAccount().getProfiles())
        {
            if (p.getName().equals(profile_name))
            {
                to_assign = p;
                break;
            }
        }
        if (to_assign == null)
        {
            to_assign = SessionManager.getInstance().getCurrentProfile();
        }
        SessionManager.getInstance().getDatabase().updateDocumentInFireStore(doc, to_assign);
        to_assign.getDocuments().add(doc);
        clearTemporaries();
    }

    /**
     * Cancels document modification. Asks for confirmation first.
     *
     * @param view  view that called the method.
     */
    public void goToDashboard(View view) {
        GeneralUtilities.askConfirmation(this, getString(R.string.cancel_confirm), new BinaryAction()
        {
            @Override
            public void confirmAction()
            {
                clearTemporaries();
                Intent intent = new Intent(getApplicationContext(), AccountDashboardActivity.class);
                startActivity(intent);
            }

            @Override
            public void denyAction() {}
        });
    }

    @Override
    public void onBackPressed()
    {
        // Cancels file preview.
        goToDashboard(findViewById(R.id.cancel_button));
    }

    /**
     * Gets the pdf instance being worked with.
     *
     * @return the pdf instance.
     */
    protected Pdf getPdf()
    {
        return this.pdf;
    }

    /**
     * Sets the pdf instance being worked with.
     *
     * @param pdf  the pdf to set.
     */
    protected void setPdf(Pdf pdf)
    {
        this.pdf = pdf;
    }

    /**
     * Gets a list of the tags that the user typed into the tag field.
     *
     * @return list of tags.
     */
    protected ArrayList<String> getTags()
    {
        String text = ((EditText) findViewById(R.id.tag_input)).getText().toString().trim();
        ArrayList<String> tags = new ArrayList<>();
        if (text.length() <= 0)
        {
            return tags;
        }
        for (String tag: text.split(" "))
        {
            tags.add(tag);
        }
        return tags;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        selector.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * Clear temporary directories.
     */
    protected void clearTemporaries()
    {
        Storage storage = new Storage(this);
        storage.clearTemp();
        storage.clearImgDir();
    }

    /**
     * Creates button for adding new pages to document.
     */
    private void createPageAddButton()
    {
        String[] options = {getString(R.string.pic_button), getString(R.string.up_button)};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.choice_confirm);
        builder.setItems(options, (dialog, which) ->
        {
            if (which == 0) {
                selector.dispatchTakePictureIntent();
            }
            else if (which == 1) {
                selector.dispatchChooseFileIntent();
            }
        });
        Button add_button = findViewById(R.id.add_button);
        add_button.setOnClickListener(view -> builder.show());
    }

    /**
     * Creates page list.
     */
    private void createPageList()
    {
        populatePages(pdf);
        ((ListView) findViewById(R.id.table_scroll)).setOnItemClickListener((parent, view, pos, id) ->
        {
            // Load in current values for file preview.
            HashMap<String, String> values = new HashMap<>();
            values.put("filename", ((EditText) findViewById(R.id.filename_input)).getText().toString());
            values.put("tags", ((EditText) findViewById(R.id.tag_input)).getText().toString());
            values.put("profile_id", ((Profile) ((Spinner) findViewById(R.id.profile_select)).getSelectedItem()).getId());
            Intent intent = new Intent(this, PagePreviewActivity.class);
            Bundle bundle = loadBundle(pos);
            bundle.putSerializable("values", values);
            intent.putExtras(bundle);
            startActivity(intent);
        });
    }

    /**
     * Fills out the list view of pages.
     *
     * @param pdf  the pdf file to get pages from.
     */
    private void populatePages(Pdf pdf)
    {
        PDDocument doc = pdf.getPdf();
        int num_pages = doc.getNumberOfPages();
        Pdf.close(doc);
        ArrayList<String> pages = new ArrayList<>(num_pages);
        for (int i = 0; i < num_pages; i++)
        {
            pages.add(getString(R.string.page) + " " + (i + 1));
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.page_listview_design, R.id.title, pages);
        ((ListView) findViewById(R.id.table_scroll)).setAdapter(adapter);
    }
}
