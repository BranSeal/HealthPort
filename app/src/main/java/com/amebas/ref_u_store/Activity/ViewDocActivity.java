package com.amebas.ref_u_store.Activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.amebas.ref_u_store.Model.BinaryAction;
import com.amebas.ref_u_store.Model.Document;
import com.amebas.ref_u_store.Model.Pdf;
import com.amebas.ref_u_store.Model.Profile;
import com.amebas.ref_u_store.Model.SessionManager;
import com.amebas.ref_u_store.R;
import com.amebas.ref_u_store.Utilities.GeneralUtilities;
import com.github.barteksc.pdfviewer.PDFView;

import java.io.File;
import java.util.HashMap;

/**
 * Activity for viewing a document.
 */
public class ViewDocActivity extends AppCompatActivity
{
    private static final int SPACING = 10;

    private Document doc;
    private File file;
    private Pdf pdf;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_doc);

        // Load in document.
        TextView title = findViewById(R.id.Title);
        Bundle extras = getIntent().getExtras();
        if (extras != null)
        {
            doc = Document.fromMap((HashMap) extras.getSerializable("doc"));
            file = new File(doc.getReferenceIDs().get(0));
            pdf = new Pdf(file);
            title.setText(file.getName().split(".pdf")[0]);
        }

        // Add pdf file to view.
        PDFView pdfView = findViewById(R.id.pdfView);
        if (file.exists())
        {
            pdfView.fromFile(file)
                .spacing(ViewDocActivity.SPACING)
                .onError(t -> Log.d("ERROR", "Failed to load pdf"))
                .load();
        }
        else
        {
            Log.d("ERROR", "No Pdf to load");
        }
    }

    /**
     * Displays the menu.
     *
     * @param v  view that called the method.
     */
    public void showMenu(View v) {
        PopupMenu menu = new PopupMenu(this, v);
        MenuInflater inflater = menu.getMenuInflater();
        inflater.inflate(R.menu.view_document_options, menu.getMenu());
        // Set delete item to red.
        MenuItem item = menu.getMenu().getItem(menu.getMenu().size() - 1);
        SpannableString s = new SpannableString(getString(R.string.delete));
        s.setSpan(new ForegroundColorSpan(Color.RED), 0, s.length(), 0);
        item.setTitle(s);
        // Set listener.
        menu.setOnMenuItemClickListener(menu_item ->
        {
            switch (menu_item.getItemId())
            {
                case R.id.edit:
                    editDocument();
                    return true;
                case R.id.delete:
                    askToDelete();
                    return true;
                default:
                    return false;
            }
        });
        menu.show();
    }

    /**
     * Moves to share a document.
     *
     * @param v  the view that called the method.
     */
    public void shareDocument(View v)
    {
    }

    /**
     * Returns to the dashboard.
     *
     * @parma v  the view that called the method.
     */
    public void exit(View v)
    {
        Intent intent = new Intent(this, AccountDashboardActivity.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed()
    {
        // Switch profiles
        exit(findViewById(R.id.BackButton));
    }

    /**
     * Moves to edit the viewed document.
     */
    private void editDocument()
    {
        String filename = doc.getName().split(".pdf")[0];
        String profile = SessionManager.getInstance().getCurrentProfile().getName();
        HashMap<String, String> values = new HashMap<>();
        values.put("filename", filename);
        values.put("tags", doc.getTagString());
        values.put("profile_name", profile);
        Intent intent = new Intent(this, EditFileActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("doc", new File(doc.getReferenceIDs().get(0)));
        bundle.putSerializable("values", values);
        bundle.putString("old_name", filename);
        bundle.putString("old_profile", profile);
        bundle.putString("old_tags", doc.getTagString());
        intent.putExtras(bundle);
        startActivity(intent);
    }

    /**
     * Asks to delete a document. If confirms, deletes and returns to dashboard. If not, does nothing.
     */
    private void askToDelete()
    {
        GeneralUtilities.askConfirmation(this, getString(R.string.delete_doc_ask), new BinaryAction()
        {
            @Override
            public void confirmAction()
            {
                deleteDocument();
                exit(findViewById(R.id.Background));
            }

            @Override
            public void denyAction() { }
        });
    }

    /**
     * Deletes the document.
     */
    private void deleteDocument()
    {
        // Delete file in local storage.
        if (file.exists())
        {
            file.delete();
        }
        // Delete file in profile's document list.
        Document to_delete = null;
        Profile current = SessionManager.getInstance().getCurrentProfile();
        for (Document d: current.getDocuments())
        {
            if (d.getName().equals(doc.getName()))
            {
                to_delete = d;
                break;
            }
        }
        if (to_delete != null)
        {
            current.getDocuments().remove(to_delete);
        }
        // Delete file in database.
        SessionManager.getInstance().getDatabase().deleteDocument(current.getName(), doc.getName(), file.getAbsolutePath());
    }
}


