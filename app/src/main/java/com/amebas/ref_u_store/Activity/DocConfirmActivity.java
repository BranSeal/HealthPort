package com.amebas.ref_u_store.Activity;

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

public class DocConfirmActivity extends AppCompatActivity {

    private PDFView pdfView;
    private Pdf doc;
    private TextView title;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_doc);

        pdfView = findViewById(R.id.pdfView);
        title = findViewById(R.id.docname);

        Bundle extras = getIntent().getExtras();
        if (extras != null)
        {
            doc = new Pdf((File) extras.getSerializable("doc"));
            title.setText(doc.getLocation().getName());
            // Pass in intents whether document was created or updated. Changes title accordingly.
            if (extras.getBoolean("isNew"))
            {
                ((TextView) findViewById(R.id.msg)).setText(R.string.confirm_add);
            }
        }
        if (doc != null)
        {
            pdfView.fromFile(doc.getLocation())
            .onError(t -> {
                Log.d("ERROR", "Failed to load pdf");
            })
            .pages(0)
            .load();
        }
    }

    /**
     * Returns to document dashboard.
     *
     * @param v  the view calling the function.
     */
    public void exit(View v)
    {
        Intent intent = new Intent(this, AccountDashboardActivity.class);
        startActivity(intent);
    }

}
