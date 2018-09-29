package com.amebas.healthport.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.amebas.healthport.R;
import com.github.barteksc.pdfviewer.PDFView;

import java.io.File;

public class PagePreviewActivity extends AppCompatActivity {

    private PDFView pdfView;
    private File current_page;
    private File all_pages;
    private int page_num;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewpage);

        pdfView = findViewById(R.id.pdfView);

        // Gets the pdf info from bundled intent. Only current_page and page_num are necessary
        // for this screen; all_pages is used for when returning back to previous.
        Bundle extras = getIntent().getExtras();
        if (extras != null)
        {
            current_page = (File) extras.getSerializable("current");
            all_pages = (File) extras.getSerializable("all_pages");
            page_num = extras.getInt("page_num");
        }
        if (page_num > 0)
        {
            TextView label = findViewById(R.id.page_num_title);
            label.setText(getString(R.string.page_num) + " " + page_num);
        }
        if (current_page != null)
        {
            pdfView.fromFile(current_page)
                .onError(t -> {
                    Log.d("ERROR", "Failed to load pdf");
                })
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
        bundle.putSerializable("all_pages", all_pages);
        filePreviewIntent.putExtras(bundle);
        startActivity(filePreviewIntent);
    }
}
