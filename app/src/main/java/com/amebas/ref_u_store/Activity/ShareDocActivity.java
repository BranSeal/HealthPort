package com.amebas.ref_u_store.Activity;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.amebas.ref_u_store.Model.Document;
import com.amebas.ref_u_store.Model.Pdf;
import com.amebas.ref_u_store.Model.Storage;
import com.amebas.ref_u_store.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.tom_roush.pdfbox.pdmodel.PDDocument;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import static android.content.ContentValues.TAG;

public class ShareDocActivity extends AppCompatActivity {

    private Pdf pdf;
    private File file;
    private Document doc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_doc);

        // Load in document.
        TextView title = findViewById(R.id.Title);
        TextView link = findViewById(R.id.link);
        Bundle extras = getIntent().getExtras();
        if (extras != null)
        {
            file = (File) extras.getSerializable("doc");
            title.setText("Share " + file.getName().split(".pdf")[0]);

            StorageReference image = FirebaseStorage.getInstance()
                    .getReference()
                    .child("temp/" + file.getPath());
            Uri fileUri = Uri.fromFile(new File(file.getPath()));
            UploadTask uploadTask = image.putFile(fileUri);

            // Register observers to listen for when the download is done or if it fails
            uploadTask
                    .addOnFailureListener(exception -> Log.d(TAG,"Could not upload  temporary document" + exception.toString()))
                    .addOnSuccessListener(snapshot -> {
                        //TODO: Confirmation that file is uploaded

                        image.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                // Got the download URL for 'users/me/profile.png'
                                Log.d(TAG, "Generated link for " + uri);
                                final String url = uri.toString();
                                runOnUiThread(new Runnable() {

                                    @Override
                                    public void run() {
                                        Retrieveurl retrieveurl = new Retrieveurl();
                                        retrieveurl.execute(url);
                                    }
                                });
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                link.setText("Could not find document");
                            }
                        });
                    });
        }
        else
        {
            Log.d("ERROR", "Was not passed a PDF, creating empty");
            pdf = new Pdf(new Storage(this).getTempFile("temp.pdf"));
        }
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
            setPdf(new Pdf(temp_dir));
            doc.close();
        }
        catch (java.io.IOException e)
        {
            Log.d("ERROR", "Was unable to save to temporary directory");
        }
    }

    private class Retrieveurl extends AsyncTask<String, Void, String> {
        TextView link = findViewById(R.id.link);
        @Override
        protected String doInBackground(String... params) {
            String url = "http://tinyurl.com/api-create.php?url=" + params[0];
            try {
                URL tinyLink = new URL(url);
                HttpURLConnection conn = (HttpURLConnection) tinyLink.openConnection();
                conn.setRequestMethod("GET");
                conn.connect();
                InputStream is = conn.getInputStream();
                BufferedReader in = new BufferedReader(new InputStreamReader(
                        is));
                setTextAsync(link, in.readLine());
            } catch (Exception e) {
                link.setText("Could not generate temporary link" + e.toString());
                Log.d(TAG, "Could not generate link : " + e.toString());
            }
            return "Process done";
        }
    }

    private void setTextAsync(final TextView text,final String value){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                text.setText(value);
            }
        });
    }
}
