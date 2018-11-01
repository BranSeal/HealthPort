package com.amebas.ref_u_store.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;

import com.amebas.ref_u_store.Model.Document;
import com.amebas.ref_u_store.Model.SessionManager;
import com.amebas.ref_u_store.Model.Storage;
import com.amebas.ref_u_store.R;
import com.amebas.ref_u_store.Utilities.GeneralUtilities;

import java.io.File;

/**
 * Activity for viewing and setting a new document's details.
 */
public class FilePreviewActivity extends FilePreviewAbstract
{
    @Override
    public Bundle loadBundle(int pos)
    {
        Bundle bundle = new Bundle();
        bundle.putSerializable("doc", getPdf().getLocation());
        bundle.putInt("page_num", pos + 1);
        return bundle;
    }

    @Override
    public void unloadBundle(Bundle b) { }

    @Override
    public void makeChanges(View v)
    {
        if (areInputsValid())
        {
            String filename = ((EditText) findViewById(R.id.filename_input)).getText().toString();
            String profile = ((Spinner) findViewById(R.id.profile_select)).getSelectedItem().toString();
            String email = SessionManager.getInstance().getSessionAccount().getEmail();
            File dir = new Storage(this).getUserDocs(email, profile);
            File file = new File(dir, filename + ".pdf");
            Document d = createDocument(file, getTags());
            uploadDocument(d, profile);
            // Move to confirmation page.
            Intent intent = new Intent(this, DocConfirmActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("doc", file);
            bundle.putBoolean("isNew", true);
            intent.putExtras(bundle);
            startActivity(intent);
        }
    }

    @Override
    public boolean areInputsValid()
    {
        if (!super.areInputsValid())
        {
            return false;
        }
        String filename = ((EditText) findViewById(R.id.filename_input)).getText().toString();
        String profile = ((Spinner) findViewById(R.id.profile_select)).getSelectedItem().toString();
        String email = SessionManager.getInstance().getSessionAccount().getEmail();
        File dir = new Storage(this).getUserDocs(email, profile);
        File file = new File(dir, filename + ".pdf");
        if (file.exists())
        {
            GeneralUtilities.displayMessage(this, getString(R.string.file_exists), () -> {});
            return false;
        }
        return true;
    }
}