package com.example.notesapp_final_glennlorenzet;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
                System.out.println("Contentressss");
                title = rootView.findViewById(R.id.Edittitletext);
                content = rootView.findViewById(R.id.Editcontenttext);

                //INTENT OBJ
                Intent i = new Intent(getActivity().getBaseContext(), MainActivity.class);

                //PACK DATA
                i.putExtra("SENDER_KEY", "DetailFragment");
                i.putExtra("ID_KEY", ARG_NOTE_ID);
                i.putExtra("TITLE_KEY", title.getText().toString());
                i.putExtra("CONTENT_KEY", content.getText().toString());

                //RESET WIDGETS

                //START ACTIVITY
                getActivity().startActivity(i);
            }
        });

        Button buttonDelete = (Button) rootView.findViewById(R.id.Deletebutton);
        buttonDelete.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                System.out.println("WANT TO DELETE!!!!!!!!!!!!!");

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

            }
        });

        return rootView;
    }
}