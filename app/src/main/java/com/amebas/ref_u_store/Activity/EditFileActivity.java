package com.amebas.ref_u_store.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.amebas.ref_u_store.Model.BinaryAction;
import com.amebas.ref_u_store.Model.Document;
import com.amebas.ref_u_store.Model.Pdf;
import com.amebas.ref_u_store.Model.Profile;
import com.amebas.ref_u_store.Model.SessionManager;
import com.amebas.ref_u_store.Model.Storage;
import com.amebas.ref_u_store.R;
import com.amebas.ref_u_store.Utilities.GeneralUtilities;
import com.tom_roush.pdfbox.pdmodel.PDDocument;

import java.io.File;
import java.util.HashMap;

/**
 * Activity for editing details of existing documents.
 */
public class EditFileActivity extends FilePreviewAbstract
{
    private String old_name;
    private String old_profile;
    private String old_profile_id; // Profile ID
    private File old_path;
    private String old_tags;
    private String old_id; // Document ID

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        // Update confirmation button for editing.
        Button update_button = findViewById(R.id.confirm_button);
        update_button.setText(getString(R.string.update_button));
    }

    @Override
    public Bundle loadBundle(int pos)
    {
        Bundle bundle = new Bundle();
        bundle.putBoolean("isEdit", true);
        bundle.putInt("page_num", pos + 1);
        bundle.putString("old_name", old_name);
        bundle.putString("old_profile", old_profile);
        bundle.putString("old_profile_id", old_profile_id);
        bundle.putString("old_tags", old_tags);
        bundle.putString("old_id", old_id);
        bundle.putSerializable("old_path", old_path);
        bundle.putSerializable("doc", getPdf().getLocation());

        return bundle;
    }

    @Override
    public void unloadBundle(Bundle b)
    {
        this.old_name = b.getString("old_name");
        this.old_profile = b.getString("old_profile");
        this.old_tags = b.getString("old_tags");
        this.old_id = b.getString("old_id");
        this.old_profile_id = b.getString("old_profile_id");
        if (b.getSerializable("old_path") != null)
        {
            this.old_path = (File) b.getSerializable("old_path");
        }
        else
        {
            this.old_path = getPdf().getLocation();
            createTempPdf();
        }
    }

    @Override
    public void makeChanges(View v)
    {
        if (areInputsValid())
        {
            // Ensure user actually wants to make changes.
            GeneralUtilities.askConfirmation(this, getString(R.string.edit_confirm), new BinaryAction()
            {
                @Override
                public void confirmAction()
                {
                    deleteOld();
                    String filename = ((EditText) findViewById(R.id.filename_input)).getText().toString();
                    String profile = ((Spinner) findViewById(R.id.profile_select)).getSelectedItem().toString();
                    String email = SessionManager.getInstance().getSessionAccount().getEmail();
                    File dir = new Storage(getApplicationContext()).getUserDocs(email, profile);
                    File file = new File(dir, filename + ".pdf");
                    Document d = createDocument(file, getTags());
                    uploadDocument(d, profile);
                    // Move to confirmation page.
                    Intent intent = new Intent(getApplicationContext(), DocConfirmActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("doc", file);
                    bundle.putBoolean("isNew", false);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }

                @Override
                public void denyAction() { }
            });

        }
    }

    /**
     * Deletes old file.
     */
    public void deleteOld()
    {
        // Delete file in local storage.
        if (old_path.exists())
        {
            old_path.delete();
        }
        // Delete file in profile's document list.
        Document to_delete = null;
        for (Profile p: SessionManager.getInstance().getSessionAccount().getProfiles())
        {
            if (p.getName().equals(old_profile))
            {
                for (Document d: p.getDocuments())
                {
                    if (d.getId().equals(old_id))
                    {
                        to_delete = d;
                        break;
                    }
                }
                if (to_delete != null)
                {
                    p.getDocuments().remove(to_delete);
                }
                break;
            }
        }
        // Delete file in database.
        SessionManager.getInstance().getDatabase().deleteDocument(old_profile_id, old_id, old_path.toString());
    }

    @Override
    public void goToDashboard(View view)
    {
        // Returns user to document view, rather than dashboard when canceling editing.
        GeneralUtilities.askConfirmation(this, getString(R.string.cancel_confirm), new BinaryAction()
        {
            @Override
            public void confirmAction()
            {
                clearTemporaries();
                Intent intent = new Intent(getApplicationContext(), ViewDocActivity.class);
                Bundle bundle = new Bundle();
                Document document = new Document();
                document.setId(old_id);
                document.setName(old_name + ".pdf");
                document.setPath(old_path.getAbsolutePath());
                document.setFromTagString(old_tags);

                bundle.putSerializable("doc", (HashMap) document.toMap());

                intent.putExtras(bundle);
                startActivity(intent);
            }

            @Override
            public void denyAction() {}
        });
    }

    /**
     * Creates a temporary PDF to make changes in.
     */
    private void createTempPdf()
    {
        Pdf pdf = getPdf();
        Storage storage = new Storage(this);
        File temp_dir = storage.getTempFile("temporary_file.pdf");
        try
        {
            PDDocument doc = pdf.getPdf();
            doc.save(temp_dir);
            doc.close();
            setPdf(new Pdf(temp_dir));
        }
        catch (java.io.IOException e)
        {
            Log.d("ERROR", "Was unable to save to temporary directory");
        }
    }
}
