package com.amebas.ref_u_store.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.amebas.ref_u_store.Model.Document;
import com.amebas.ref_u_store.R;

import java.util.ArrayList;

public class DocumentsAdapter extends ArrayAdapter<Document> implements Filterable {
    private ArrayList<Document> mOriginalValues; // Original Values
    private ArrayList<Document> mDisplayedValues;    // Values to be displayed
    LayoutInflater inflater;

    public DocumentsAdapter(Context context, ArrayList<Document> documents) {
        super(context, 0, documents);
        mDisplayedValues = documents;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Document document = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.document_listview_design, parent, false);
        }
        // Lookup view for data population
        TextView title = convertView.findViewById(R.id.title);
        TextView tags = convertView.findViewById(R.id.tags);
        // Populate the data into the template view using the data object
        String name = document.getName();
        String tagsString = document.getTagString();


        title.setText(name);
        tags.setText(tagsString);
        // Return the completed view to render on screen
        return convertView;
    }

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint,FilterResults results) {
                mDisplayedValues = (ArrayList<Document>) results.values; // has the filtered values
                notifyDataSetChanged();  // notifies the data with new filtered values
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();        // Holds the results of a filtering operation in values
                ArrayList<Document> FilteredArrList = new ArrayList<>();

                if (mOriginalValues == null) {
                    mOriginalValues = new ArrayList<>(mDisplayedValues); // saves the original data in mOriginalValues
                }

                /********
                 *
                 *  If constraint(CharSequence that is received) is null returns the mOriginalValues(Original) values
                 *  else does the Filtering and returns FilteredArrList(Filtered)
                 *
                 ********/
                if (constraint == null || constraint.length() == 0) {

                    // set the Original result to return
                    results.count = mOriginalValues.size();
                    results.values = mOriginalValues;
                } else {
                    constraint = constraint.toString().toLowerCase();
                    for (int i = 0; i < mOriginalValues.size(); i++) {
                        String data = mOriginalValues.get(i).getName();
                        if (data.toLowerCase().startsWith(constraint.toString())) {
                            FilteredArrList.add(new Document(mOriginalValues.get(i).getName(),mOriginalValues.get(i).getTags()));
                        }
                    }
                    // set the Filtered result to return
                    results.count = FilteredArrList.size();
                    results.values = FilteredArrList;
                }
                return results;
            }
        };
        return filter;
    }

}
