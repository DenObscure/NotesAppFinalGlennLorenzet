package com.example.notesapp_final_glennlorenzet;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.NetworkOnMainThreadException;
import android.preference.PreferenceManager;
import android.provider.BaseColumns;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NoteAdapter.ListItemClickListener {

    static final int NEW_NOTE_REQUEST = 1;  // The request code
    /*
     * References to RecyclerView and Adapter to reset the list to its
     * "pretty" state when the reset menu item is clicked.
     */
    private NoteAdapter mAdapter;
    private RecyclerView mNumbersList;
    private ArrayList<Note> notesList = new ArrayList<Note>();
    private boolean processed = false;


    /*
     * If we hold a reference to our Toast, we can cancel it (if it's showing)
     * to display a new Toast. If we didn't do this, Toasts would be delayed
     * in showing up if you clicked many list items in quick succession.
     */
    private Toast mToast;

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (findViewById(R.id.item_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mTwoPane)
                {
                    // Defines a new Uri object that receives the result of the insertion
                    Uri newUri;


                    // Defines an object to contain the new values to insert
                    ContentValues newValues = new ContentValues();

                    /*
                     * Sets the values of each column and inserts the word. The arguments to the "put"
                     * method are "column name" and "value"
                     */
                    newValues.put(NotesContract.NoteEntry.COLUMN_NAME_TITLE, "New Note");
                    newValues.put(NotesContract.NoteEntry.COLUMN_NAME_CONTENT, "");

                    newUri = getContentResolver().insert(
                            NotesContract.NoteEntry.CONTENT_URI,    // the user dictionary content URI
                            newValues                               // the values to insert
                    );


                    String id = newUri.getLastPathSegment();

                    Note newNote = new Note(id, "New Note", "");
                    mAdapter.addToList(newNote);
                }
                else
                {
                    Intent intent = new Intent(MainActivity.this, NewNoteActivity.class);
                    startActivityForResult(intent, NEW_NOTE_REQUEST);
                }
            }
        });




        // READ FROM DB

        // A "projection" defines the columns that will be returned for each row
        String[] mProjection =
        {
                NotesContract.NoteEntry._ID,    // Contract class constant for the _ID column name
                NotesContract.NoteEntry.COLUMN_NAME_TITLE,   // Contract class constant for the word column name
                NotesContract.NoteEntry.COLUMN_NAME_CONTENT  // Contract class constant for the locale column name
        };

        // Defines a string to contain the selection clause
        String selectionClause = null;

        // Initializes an array to contain selection arguments
        String[] selectionArgs = null;

        // Does a query against the table and returns a Cursor object
        Cursor mCursor = getContentResolver().query(
                NotesContract.NoteEntry.CONTENT_URI,  // The content URI of the words table
                mProjection,                       // The columns to return for each row
                selectionClause,                  // Either null, or the word the user entered
                selectionArgs,                    // Either empty, or the string the user entered
                null);                       // The sort order for the returned rows


        // Some providers return null if an error occurs, others throw an exception
        if (null == mCursor) {
            /*
             * Insert code here to handle the error. Be sure not to use the cursor! You may want to
             * call android.util.Log.e() to log this error.
             *
             */


        // If the Cursor is empty, the provider found no matches
        } else if (mCursor.getCount() < 1) {

            /*
             * Insert code here to notify the user that the search was unsuccessful. This isn't necessarily
             * an error. You may want to offer the user the option to insert a new row, or re-type the
             * search term.
             */


        } else {
            // Insert code here to do something with the results
            if (mCursor.moveToFirst()) {
                do {
                    notesList.add(new Note(mCursor.getString(0), mCursor.getString(1), mCursor.getString(2)));
                } while (mCursor.moveToNext());

            }
        }
        /*
         * Using findViewById, we get a reference to our RecyclerView from xml. This allows us to
         * do things like set the adapter of the RecyclerView and toggle the visibility.
         */
        mNumbersList = (RecyclerView) findViewById(R.id.rv_numbers);

        /*
         * A LinearLayoutManager is responsible for measuring and positioning item views within a
         * RecyclerView into a linear list. This means that it can produce either a horizontal or
         * vertical list depending on which parameter you pass in to the LinearLayoutManager
         * constructor. By default, if you don't specify an orientation, you get a vertical list.
         * In our case, we want a vertical list, so we don't need to pass in an orientation flag to
         * the LinearLayoutManager constructor.
         *
         * There are other LayoutManagers available to display your data in uniform grids,
         * staggered grids, and more! See the developer documentation for more details.
         */
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mNumbersList.setLayoutManager(layoutManager);

        /*
         * Use this setting to improve performance if you know that changes in content do not
         * change the child layout size in the RecyclerView
         */
        mNumbersList.setHasFixedSize(false);

        /*
         * The NoteAdapter is responsible for displaying each item in the list.
         */
        mAdapter = new NoteAdapter(notesList, this);
        mNumbersList.setAdapter(mAdapter);


        /* IF INTENT COMING FROM WIDGET */
        Bundle extras = getIntent().getExtras();
        if (extras != null)
        {
            System.out.println(extras);
            String value = extras.getString("widgetNoteId");
            if (extras.getString("action").equalsIgnoreCase("widget")) {
                for (Note note : notesList) {
                    if (note.getId().equalsIgnoreCase(value)) {
                        Intent in = new Intent(MainActivity.this, EditNoteActivity.class);
                        in.putExtra("title", note.getTitle());
                        in.putExtra("content", note.getContent());
                        in.putExtra("id", note.getId());
                        in.putExtra("action", "");
                        startActivityForResult(in, 2);
                    }
                }
                getIntent().removeExtra("widgetNoteId");
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.action_Update_Widget:
                updateWidgets(getApplicationContext());
                return true;
            default:
                return true;
        }
    }

    /**
     * This is where we receive our callback from
     * {@link com.example.notesapp_final_glennlorenzet.NoteAdapter.ListItemClickListener}
     *
     * This callback is invoked when you click on an item in the list.
     *
     * @param clickedItemIndex Index in the list of the item that was clicked.
     */
    @Override
    public void onListItemClick(int clickedItemIndex) {

        if (mToast != null) {
            mToast.cancel();
        }
        String toastMessage = "Item #" + clickedItemIndex + " clicked.";
        mToast = Toast.makeText(this, toastMessage, Toast.LENGTH_LONG);

        mToast.show();


        Note cNote = notesList.get(clickedItemIndex);

        if (mTwoPane)
        {

            Bundle arguments = new Bundle();
            arguments.putInt(DetailFragment.ARG_INDEX, clickedItemIndex);
            arguments.putString(DetailFragment.ARG_NOTE_ID, cNote.getId());
            arguments.putString(DetailFragment.ARG_NOTE_TITLE, cNote.getTitle());
            arguments.putString(DetailFragment.ARG_NOTE_CONTENT, cNote.getContent());

            DetailFragment fragment = new DetailFragment();
            fragment.setArguments(arguments);
            this.getSupportFragmentManager().beginTransaction()
                    .replace(R.id.item_detail_container, fragment)
                    .commit();
        }
        else
        {

            Intent in = new Intent(MainActivity.this, EditNoteActivity.class);
            in.putExtra("title", cNote.getTitle());
            in.putExtra("content", cNote.getContent());
            in.putExtra("id", cNote.getId());

            startActivityForResult(in, 2);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == NEW_NOTE_REQUEST) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                // Defines a new Uri object that receives the result of the insertion
                Uri newUri;


                // Defines an object to contain the new values to insert
                ContentValues newValues = new ContentValues();

                /*
                 * Sets the values of each column and inserts the word. The arguments to the "put"
                 * method are "column name" and "value"
                 */
                newValues.put(NotesContract.NoteEntry.COLUMN_NAME_TITLE, data.getStringExtra("title"));
                newValues.put(NotesContract.NoteEntry.COLUMN_NAME_CONTENT, data.getStringExtra("content"));

                newUri = getContentResolver().insert(
                        NotesContract.NoteEntry.CONTENT_URI,    // the user dictionary content URI
                        newValues                               // the values to insert
                );

                finish();
                startActivity(getIntent());
            }
        }

        if (requestCode == 2) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {

                if (data.getStringExtra("action").equals("edit")) {
                    // Defines an object to contain the updated values
                    ContentValues updateValues = new ContentValues();

                    //updateValues.put(NotesContract.NoteEntry.COLUMN_NAME_TITLE, data.getStringExtra("title"));
                    updateValues.put(NotesContract.NoteEntry.COLUMN_NAME_TITLE, data.getStringExtra("newTitle"));
                    updateValues.put(NotesContract.NoteEntry.COLUMN_NAME_CONTENT, data.getStringExtra("newContent"));

                    // Defines selection criteria for the rows you want to update
                    String selectionClause = NotesContract.NoteEntry._ID +  " LIKE ?";
                    String[] selectionArgs = {data.getStringExtra("id")};

                    // Defines a variable to contain the number of updated rows
                    int rowsUpdated = 0;

                    /*
                     * Sets the updated value and updates the selected words.
                     */

                    rowsUpdated = getContentResolver().update(
                            NotesContract.NoteEntry.CONTENT_URI,// the user dictionary content URI
                            updateValues,                       // the columns to update
                            selectionClause,                    // the column to select on
                            selectionArgs                       // the value to compare to
                    );
                    finish();
                    startActivity(this.getIntent());
                }
                else if (data.getStringExtra("action").equals("delete"))
                {
                    // Defines selection criteria for the rows you want to delete
                    String selectionClause = NotesContract.NoteEntry._ID + " LIKE ?";
                    String[] selectionArgs = {data.getStringExtra("id")};

                    // Defines a variable to contain the number of rows deleted
                    int rowsDeleted = 0;


                    // Deletes the words that match the selection criteria
                    rowsDeleted = getContentResolver().delete(
                            NotesContract.NoteEntry.CONTENT_URI,   // the user dictionary content URI
                            selectionClause,                   // the column to select on
                            selectionArgs                      // the value to compare to
                    );

                    finish();
                    startActivity(this.getIntent());
                }
            }
        }
    }

    public void removeItem(int i)
    {
        mAdapter.removeFromListAt(i);

        // remove fragment
        if(getSupportFragmentManager().findFragmentById(R.id.item_detail_container) != null) {
            getSupportFragmentManager()
                    .beginTransaction().
                    remove(getSupportFragmentManager().findFragmentById(R.id.item_detail_container)).commit();
        }
    }

    public void updateItem(int i, Note n)
    {
        mAdapter.updateItemFromListAt(i, n);
    }

    public static void updateWidgets(Context context) {
        Intent intent = new Intent(context.getApplicationContext(), CollectionWidget.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        // Use an array and EXTRA_APPWIDGET_IDS instead of AppWidgetManager.EXTRA_APPWIDGET_ID,
        // since it seems the onUpdate() is only fired on that:
        AppWidgetManager widgetManager = AppWidgetManager.getInstance(context);
        int[] ids = widgetManager.getAppWidgetIds(new ComponentName(context, CollectionWidget.class));

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
            widgetManager.notifyAppWidgetViewDataChanged(ids, R.id.widget_list);

        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
        context.sendBroadcast(intent);
    }
}
