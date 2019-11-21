package com.example.notesapp_final_glennlorenzet;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements NoteAdapter.ListItemClickListener {

    static final int NEW_NOTE_REQUEST = 1;  // The request code

    private NoteAdapter mAdapter;
    private RecyclerView mNumbersList;
    private ArrayList<Note> notesList = new ArrayList<Note>();

    private Toast mToast;

    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (findViewById(R.id.item_detail_container) != null) {
            mTwoPane = true;
        }

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mTwoPane)
                {
                    Uri newUri;

                    ContentValues newValues = new ContentValues();

                    newValues.put(NotesContract.NoteEntry.COLUMN_NAME_TITLE, "New Note");
                    newValues.put(NotesContract.NoteEntry.COLUMN_NAME_CONTENT, "");

                    newUri = getContentResolver().insert(
                            NotesContract.NoteEntry.CONTENT_URI,
                            newValues
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

        String[] mProjection =
        {
                NotesContract.NoteEntry._ID,    // Contract class constant for the _ID column name
                NotesContract.NoteEntry.COLUMN_NAME_TITLE,
                NotesContract.NoteEntry.COLUMN_NAME_CONTENT
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


        if (null == mCursor) {}
        else if (mCursor.getCount() < 1) {}
        else {

            if (mCursor.moveToFirst()) {
                do {
                    notesList.add(new Note(mCursor.getString(0), mCursor.getString(1), mCursor.getString(2)));
                } while (mCursor.moveToNext());

            }
        }

        mNumbersList = (RecyclerView) findViewById(R.id.rv_numbers);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mNumbersList.setLayoutManager(layoutManager);


        mNumbersList.setHasFixedSize(false);

        mAdapter = new NoteAdapter(notesList, this);
        mNumbersList.setAdapter(mAdapter);



    }

    @Override
    public void onResume(){
        super.onResume();

        /* IF INTENT COMING FROM WIDGET */
        Bundle extras = getIntent().getExtras();
        if (extras != null)
        {
            System.out.println("EXTRAS:::::");
            System.out.println(extras);
            String value = extras.getString("widgetNoteId");
            if (value != null)
            {
                if (extras.getString("action").equalsIgnoreCase("widget")) {
                    for (Note note : notesList) {
                        if (note.getId().equalsIgnoreCase(value)) {
                            Intent in = new Intent(MainActivity.this, EditNoteActivity.class);
                            in.putExtra("title", note.getTitle());
                            in.putExtra("content", note.getContent());
                            in.putExtra("id", note.getId());


                            getIntent().replaceExtras(new Bundle());
                            getIntent().setAction("");
                            getIntent().setData(null);
                            getIntent().setFlags(0);

                            startActivityForResult(in, 2);
                        }
                    }

                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_Update_Widget:
                updateWidgets(getApplicationContext());
                return true;
            default:
                return true;
        }
    }

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
        if (requestCode == NEW_NOTE_REQUEST) {
            if (resultCode == RESULT_OK) {
                Uri newUri;

                ContentValues newValues = new ContentValues();

                newValues.put(NotesContract.NoteEntry.COLUMN_NAME_TITLE, data.getStringExtra("title"));
                newValues.put(NotesContract.NoteEntry.COLUMN_NAME_CONTENT, data.getStringExtra("content"));

                newUri = getContentResolver().insert(
                        NotesContract.NoteEntry.CONTENT_URI,
                        newValues
                );

                finish();
                startActivity(getIntent());
            }
        }

        if (requestCode == 2) {
            if (resultCode == RESULT_OK) {
                if (data.getStringExtra("action").equals("edit")) {
                    ContentValues updateValues = new ContentValues();

                    updateValues.put(NotesContract.NoteEntry.COLUMN_NAME_TITLE, data.getStringExtra("newTitle"));
                    updateValues.put(NotesContract.NoteEntry.COLUMN_NAME_CONTENT, data.getStringExtra("newContent"));

                    String selectionClause = NotesContract.NoteEntry._ID +  " LIKE ?";
                    String[] selectionArgs = {data.getStringExtra("id")};

                    int rowsUpdated = 0;

                    rowsUpdated = getContentResolver().update(
                            NotesContract.NoteEntry.CONTENT_URI,// the content URI
                            updateValues,                       // the columns to update
                            selectionClause,                    // the column to select on
                            selectionArgs                       // the value to compare to
                    );
                    finish();
                    startActivity(this.getIntent());
                }
                else if (data.getStringExtra("action").equals("delete"))
                {
                    String selectionClause = NotesContract.NoteEntry._ID + " LIKE ?";
                    String[] selectionArgs = {data.getStringExtra("id")};

                    int rowsDeleted = 0;

                    rowsDeleted = getContentResolver().delete(
                            NotesContract.NoteEntry.CONTENT_URI,   // the content URI
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

        AppWidgetManager widgetManager = AppWidgetManager.getInstance(context);
        int[] ids = widgetManager.getAppWidgetIds(new ComponentName(context, CollectionWidget.class));

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
            widgetManager.notifyAppWidgetViewDataChanged(ids, R.id.widget_list);

        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
        context.sendBroadcast(intent);
    }


}
