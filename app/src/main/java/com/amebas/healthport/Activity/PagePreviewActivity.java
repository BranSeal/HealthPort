package com.amebas.healthport.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.amebas.healthport.Model.Pdf;
import com.amebas.healthport.R;
import com.github.barteksc.pdfviewer.PDFView;

import java.io.File;

public class PagePreviewActivity extends AppCompatActivity {

    private PDFView pdfView;
    private Pdf document;
    private int page_num; // Should be 1+

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewpage);

        pdfView = findViewById(R.id.pdfView);

        Bundle extras = getIntent().getExtras();
        if (extras != null)
        {
            document = (Pdf) extras.getSerializable("doc");
            page_num = extras.getInt("page_num");
            page_num = (page_num < 1) ? 1 : page_num;
        }
        TextView label = findViewById(R.id.page_num_title);
        label.setText(getString(R.string.page_num) + " " + page_num);
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
        Intent filePreviewIntent = new Intent(this, FilePreviewActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("doc", document);
        filePreviewIntent.putExtras(bundle);
        startActivity(filePreviewIntent);
    }
}
