package com.amebas.ref_u_store.Activity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.amebas.ref_u_store.Model.Document;
import com.amebas.ref_u_store.Model.Pdf;
import com.amebas.ref_u_store.R;
import com.amebas.ref_u_store.Utilities.GeneralUtilities;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

import static android.content.ContentValues.TAG;

/**
 * Activity for getting temporary URL for document.
 */
public class ShareDocActivity extends AppCompatActivity
{
    private Pdf pdf;
    private File file;
    private Document doc;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_doc);

        // Hide copy button.
        Button copy_button = findViewById(R.id.CopyButton);
        copy_button.setEnabled(false);
        copy_button.setVisibility(View.INVISIBLE);

        // Load in document.
        TextView title = findViewById(R.id.Title);
        TextView link = findViewById(R.id.link);
        Bundle extras = getIntent().getExtras();
        if (extras != null)
        {
            doc = Document.fromMap((HashMap) extras.getSerializable("doc"));
            file = new File(doc.getPath());
            String title_string = getString(R.string.share) + " \"" + file.getName().split(".pdf")[0] + "\"";
            title.setText(title_string);

            StorageReference image = FirebaseStorage.getInstance()
                .getReference()
                .child("temp/" + file.getPath());
            Uri fileUri = Uri.fromFile(new File(file.getPath()));
            UploadTask uploadTask = image.putFile(fileUri);

            // Register observers to listen for when the download is done or if it fails
            uploadTask
                .addOnFailureListener(exception -> Log.d(TAG,"Could not upload  temporary document" + exception.toString()))
                .addOnSuccessListener(snapshot ->
                {
                    //TODO: Confirmation that file is uploaded
                    image.getDownloadUrl()
                        .addOnSuccessListener(uri ->
                        {
                            // Got the download URL for 'users/me/profile.png'
                            Log.d(TAG, "Generated link for " + uri);
                            final String url = uri.toString();
                            runOnUiThread(() ->
                            {
                                RetrieveURL retrieveurl = new RetrieveURL();
                                retrieveurl.execute(url);
                            });
                        })
                        .addOnFailureListener(exception -> link.setText(getString(R.string.doc_not_found)));
                });
        }
        else
        {
            Log.e("ERROR", "Was not passed a PDF, creating empty");
        }
    }

    /**
     * Copies the shown link to the phone's clipboard.
     *
     * @param v  the view that called the method.
     */
    public void copyLink(View v)
    {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        String link = ((TextView) findViewById(R.id.link)).getText().toString();
        ClipData clip = ClipData.newPlainText(getString(R.string.link_label), link);
        clipboard.setPrimaryClip(clip);

        GeneralUtilities.showToast(this, getString(R.string.copy_link_confirm));
    }

    /**
     * Returns to the document view screen.
     *
     * @param v  the view that called the method.
     */
    public void exit(View v)
    {
        Intent intent = new Intent(this, ViewDocActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("doc", (HashMap<String, String>) doc.toMap());
        intent.putExtras(bundle);
        startActivity(intent);
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
     * Class to encapsulate asynchronous task for getting document URL.
     */
    private class RetrieveURL extends AsyncTask<String, Void, String>
    {
        TextView link = findViewById(R.id.link);

        @Override
        protected String doInBackground(String... params) {
            String url = "http://tinyurl.com/api-create.php?url=" + params[0];
            try
            {
                URL tinyLink = new URL(url);
                HttpURLConnection conn = (HttpURLConnection) tinyLink.openConnection();
                conn.setRequestMethod("GET");
                conn.connect();
                InputStream is = conn.getInputStream();
                BufferedReader in = new BufferedReader(new InputStreamReader(is));
                setTextAsync(link, in.readLine());
            }
            catch (Exception e)
            {
                link.setText(getString(R.string.link_failed));
                Log.d(TAG, "Could not generate link : " + e.toString());
            }
            return "Process done";
        }
    }

    /**
     * Sets the link text in the UI asynchronously.
     *
     * @param text   the text view to set the link in.
     * @param value  the link.
     */
    private void setTextAsync(final TextView text, final String value)
    {
        runOnUiThread(() ->
        {
            text.setText(value);

            // Show copy button.
            Button copy_button = findViewById(R.id.CopyButton);
            copy_button.setEnabled(true);
            copy_button.setVisibility(View.VISIBLE);
        });
    }
}
