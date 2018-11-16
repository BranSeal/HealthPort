package com.amebas.ref_u_store.Activity;

import android.net.Uri;
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
import com.tom_roush.pdfbox.pdmodel.PDDocument;

import java.io.File;

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

            FirebaseStorage storage = FirebaseStorage.getInstance();

            // Create a storage reference from our app
            StorageReference storageRef = storage.getReference();

            storageRef.child("/images/" + file.getPath()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    // Got the download URL for 'users/me/profile.png'
                    link.setText(uri.toString());
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    link.setText("Could not find document");
                }
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
}
