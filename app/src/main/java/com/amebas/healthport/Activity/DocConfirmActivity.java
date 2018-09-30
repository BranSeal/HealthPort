package com.amebas.healthport.Activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.amebas.healthport.R;
import com.github.barteksc.pdfviewer.PDFView;

import java.io.File;

public class DocConfirmActivity extends AppCompatActivity {

    private PDFView pdfView;
    private File doc;
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
            doc = (File) extras.getSerializable("doc");
            title.setText(doc.getName());
            // Pass in intents whether document was created or updated. Changes title accordingly.
            if (extras.getBoolean("isNew"))
            {
                ((TextView) findViewById(R.id.msg)).setText(R.string.confirm_add);
            }
        }
        if (doc != null)
        {
            pdfView.fromFile(doc)
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
        // @TODO: go to dashboard.
    }

}
