package com.amebas.ref_u_store.Activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.amebas.ref_u_store.Model.Pdf;
import com.amebas.ref_u_store.R;
import com.github.barteksc.pdfviewer.PDFView;

import java.io.File;
import java.util.HashMap;

public class PagePreviewActivity extends AppCompatActivity {

    private PDFView pdfView;
    private Pdf document;
    private HashMap<String, String> values;
    private int page_num; // Should be 1+
    // Fields for if editing.
    private boolean isEdit;
    private String old_name;
    private String old_profile;
    private File old_path;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewpage);

        pdfView = findViewById(R.id.pdfView);

        Bundle extras = getIntent().getExtras();
        if (extras != null)
        {
            document = new Pdf(((File) extras.getSerializable("doc")));
            page_num = extras.getInt("page_num");
            page_num = (page_num < 1) ? 1 : page_num;
            values = (HashMap<String, String>) extras.getSerializable("values");
            isEdit = extras.getBoolean("isEdit");
            if (isEdit)
            {
                old_name = extras.getString("old_name");
                old_profile = extras.getString("old_profile");
                old_path = (File) extras.getSerializable("old_path");
            }
        }
        TextView label = findViewById(R.id.page_num_title);
        label.setText(getString(R.string.page_view_header) + " " + page_num);
        if (document != null)
        {
            pdfView.fromFile(document.getLocation())
                .onError(t -> {
                    Log.d("ERROR", "Failed to load pdf");
                })
                .pages(page_num - 1)
                .load();
        }
    }

    /**
     * Returns to file preview screen. Requires PDF file of all pages of current document to
     * be bundled into the intent.
     *
     * @param v  the view calling the function.
     */
    public void exit(View v)
    {
        Bundle bundle = new Bundle();
        bundle.putSerializable("doc", document.getLocation());
        bundle.putSerializable("values", values);
        Intent returnIntent;
        if (isEdit)
        {
            bundle.putString("old_name", old_name);
            bundle.putString("old_profile", old_profile);
            bundle.putSerializable("old_path", old_path);
            returnIntent = new Intent(this, EditFileActivity.class);
        }
        else
        {
            returnIntent = new Intent(this, FilePreviewActivity.class);
        }
        returnIntent.putExtras(bundle);
        startActivity(returnIntent);
    }

    /**
     * Asks whether to delete the page from the document. If confirms, deletes page and returns
     * to file preview screen. If not, does nothing.
     *
     * @param v  the view calling the function.
     */
    public void delete(View v)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.page_delete_confirm));
        builder.setCancelable(true);

        builder.setPositiveButton(getString(R.string.yes), (dialog, id) -> {
            dialog.cancel();
            try
            {
                document.removePage(this, page_num);
                exit(v);
            }
            catch (java.io.IOException e)
            {
                Log.d("ERROR", "Page deletion failed: " + e.getMessage());
            }
        });
        builder.setNegativeButton(getString(R.string.no), (dialog, id) -> dialog.cancel());

        AlertDialog alert = builder.create();
        alert.show();
    }
}
