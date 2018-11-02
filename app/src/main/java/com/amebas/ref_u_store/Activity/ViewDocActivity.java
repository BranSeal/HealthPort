package com.amebas.ref_u_store.Activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;

import com.amebas.ref_u_store.Model.BinaryAction;
import com.amebas.ref_u_store.R;
import com.amebas.ref_u_store.Utilities.GeneralUtilities;

/**
 * Activity for viewing a document.
 */
public class ViewDocActivity extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_doc);
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
        s.setSpan(Color.RED, 0, s.length(), 0);
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
                    deleteDocument();
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
    }

    /**
     * Moves to edit the viewed document.
     */
    private void editDocument()
    {
    }

    /**
     * Asks to delete a document. If confirms, deletes and returns to dashboard. If not, does nothing.
     */
    private void deleteDocument()
    {
        GeneralUtilities.askConfirmation(this, getString(R.string.delete_doc_ask), new BinaryAction()
        {
            @Override
            public void confirmAction()
            {
            }

            @Override
            public void denyAction() { }
        });
    }
}


