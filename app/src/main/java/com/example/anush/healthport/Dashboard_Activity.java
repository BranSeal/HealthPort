package com.example.anush.healthport;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.anush.healthport.model.File_Mover;
import com.example.anush.healthport.model.Storage;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FilenameUtils;

public class Dashboard_Activity extends AppCompatActivity {

    private String TAG = "DashboardActivity";
    private String folder = "documents";

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        populateDash();
        // When clicking on Add button.
        findViewById(R.id.add_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performFileSearch();
            }
        });
        // When clicking on Inbox button.
        findViewById(R.id.inbox_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Deletes all files in local storage.
                for (File file: Storage.getAppDir(getApplicationContext(), folder).listFiles())
                {
                    file.delete();
                    populateDash();
                }
                Log.i(TAG, "Deleted all files in app storage");
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if (item.getItemId() == R.id.action_settings)
        {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private static final int READ_REQUEST_CODE = 42;
    /**
     * Fires an intent to spin up the "file chooser" UI and select an image.
     */
    public void performFileSearch() {

        // ACTION_OPEN_DOCUMENT is the intent to choose a file via the system's file
        // browser.
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);

        // Filter to only show results that can be "opened", such as a
        // file (as opposed to a list of contacts or timezones)
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        // Filter to show only images, using the image MIME data type.
        // If one wanted to search for ogg vorbis files, the type would be "audio/ogg".
        // To search for all documents available via installed storage providers,
        // it would be "*/*".
        intent.setType("*/*");
        String[] mimetypes = {"image/*", "application/pdf"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimetypes);

        startActivityForResult(intent, READ_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                 Intent resultData) {
        // The ACTION_OPEN_DOCUMENT intent was sent with the request code
        // READ_REQUEST_CODE. If the request code seen here doesn't match, it's the
        // response to some other intent, and the code below shouldn't run at all.
        if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            // The document selected by the user won't be returned in the intent.
            // Instead, a URI to that document will be contained in the return intent
            // provided to this method as a parameter.
            // Pull that URI using resultData.getData().
            if (resultData != null) {
                Uri uri = resultData.getData();
                addFileToApp(uri);
                populateDash();
            }
        }
    }

    /**
     * Adds a file to the app's local storage from a given URI.
     *
     * @param uri  URI of the file to add.
     */
    public void addFileToApp(Uri uri)
    {
        File original = new File(uri.getPath());
        File dir = Storage.getAppDir(this, folder);
        File new_file = new File(dir, original.getName());
        int counter = 2;
        // If filename already exists, add number.
        while (new_file.exists()) {
            String new_name = FilenameUtils.removeExtension(original.getName());
            String extension = FilenameUtils.getExtension(original.getName());
            new_name += " (" + counter + ")." + extension;

            new_file = new File(dir, new_name);
            counter++;
        }
        try
        {
            File_Mover.move(getApplicationContext(), uri, new_file);
        }
        catch (IOException e)
        {
            Log.e(TAG, e.getMessage());
        }
    }

    /**
     * Populates the dashboard with textviews of all the documents in the app storage.
     */
    private void populateDash()
    {
        File dir = Storage.getAppDir(this, folder);
        File[] files = dir.listFiles();
        LinearLayout ll = (LinearLayout) findViewById(R.id.document_list);
        ll.removeAllViews();
        if (files.length == 0)
        {
            TextView view = createTextView("No Documents Found", 0);
            ll.addView(view);
        }
        int counter = 0;
        for (File file: files)
        {
            TextView view = createTextView(file.getName(), counter);
            ll.addView(view);
            counter++;
        }
    }

    /**
     * Creates a text view object.
     * @param text  the text to populate the view.
     * @param id    the id to give the view.
     * @return the text view.
     */
    private TextView createTextView(String text, int id)
    {
        TextView view = new TextView(this);
        view.setText(text);
        view.setId(id);
        view.setLayoutParams(new ActionBar.LayoutParams(
                ActionBar.LayoutParams.MATCH_PARENT,
                ActionBar.LayoutParams.WRAP_CONTENT
        ));
        return view;
    }
}
