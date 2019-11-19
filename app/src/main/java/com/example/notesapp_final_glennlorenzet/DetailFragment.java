package com.example.notesapp_final_glennlorenzet;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


/**
 * A fragment representing a single Item detail screen.

 * on handsets.
 */
public class DetailFragment extends Fragment {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_INDEX = "index";
    public static final String ARG_NOTE_ID = "note_id";
    public static final String ARG_NOTE_TITLE = "note_title";
    public static final String ARG_NOTE_CONTENT = "note_content";

    EditText title;
    EditText content;
    String id;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public DetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_NOTE_ID)) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.

            //mItem = DummyContent.ITEM_MAP.get(getArguments().getString(ARG_ITEM_ID));

            Activity activity = this.getActivity();

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        // Show the dummy content as text in a TextView.
        //if (mItem != null) {
        //    ((TextView) rootView.findViewById(R.id.item_detail)).setText(mItem.details);
        //}



        ((TextView) rootView.findViewById(R.id.Edittitletext)).setText(getArguments().getString(ARG_NOTE_TITLE));
        ((TextView) rootView.findViewById(R.id.Editcontenttext)).setText(getArguments().getString(ARG_NOTE_CONTENT));

        Button buttonSave = (Button) rootView.findViewById(R.id.Editsavebutton);
        buttonSave.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                Uri newUri;

                title = rootView.findViewById(R.id.Edittitletext);
                content = rootView.findViewById(R.id.Editcontenttext);

                // Defines an object to contain the updated values
                ContentValues updateValues = new ContentValues();

                //updateValues.put(NotesContract.NoteEntry.COLUMN_NAME_TITLE, data.getStringExtra("title"));
                updateValues.put(NotesContract.NoteEntry.COLUMN_NAME_TITLE, title.getText().toString());
                updateValues.put(NotesContract.NoteEntry.COLUMN_NAME_CONTENT, content.getText().toString());

                // Defines selection criteria for the rows you want to update
                String selectionClause = NotesContract.NoteEntry._ID +  " = ?";
                String[] selectionArgs = {getArguments().getString(ARG_NOTE_ID)};

                // Defines a variable to contain the number of updated rows
                int rowsUpdated = 0;

                /*
                 * Sets the updated value and updates the selected words.
                 */

                rowsUpdated = getActivity().getContentResolver().update(
                        NotesContract.NoteEntry.CONTENT_URI,// the user dictionary content URI
                        updateValues,                       // the columns to update
                        selectionClause,                    // the column to select on
                        selectionArgs                       // the value to compare to
                );

                int index = getArguments().getInt(ARG_INDEX);

                ((MainActivity)getActivity()).updateItem(index, new Note(getArguments().getString(ARG_NOTE_ID), title.getText().toString(), content.getText().toString()));
                closeKeyboard();
            }
        });

        Button buttonDelete = (Button) rootView.findViewById(R.id.Deletebutton);
        buttonDelete.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // Defines selection criteria for the rows you want to delete
                String selectionClause = NotesContract.NoteEntry._ID + " LIKE ?";
                String[] selectionArgs = {getArguments().getString(ARG_NOTE_ID)};

                // Defines a variable to contain the number of rows deleted
                int rowsDeleted = 0;

                getActivity().getContentResolver();
                // Deletes the words that match the selection criteria
                rowsDeleted = getActivity().getContentResolver().delete(
                        NotesContract.NoteEntry.CONTENT_URI,   // the user dictionary content URI
                        selectionClause,                   // the column to select on
                        selectionArgs                      // the value to compare to
                );
                int noteid = getArguments().getInt(ARG_INDEX);
                ((MainActivity)getActivity()).removeItem(noteid);

                closeKeyboard();
            }
        });

        return rootView;
    }

    public void closeKeyboard()
    {
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
        getActivity().getWindow().getDecorView().clearFocus();
    }
}
