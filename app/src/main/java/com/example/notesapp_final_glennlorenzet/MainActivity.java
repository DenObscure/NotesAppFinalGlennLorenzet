package com.example.notesapp_final_glennlorenzet;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
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

    /*
     * If we hold a reference to our Toast, we can cancel it (if it's showing)
     * to display a new Toast. If we didn't do this, Toasts would be delayed
     * in showing up if you clicked many list items in quick succession.
     */
    private Toast mToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();

                Intent intent = new Intent(MainActivity.this, NewNoteActivity.class);
                startActivityForResult(intent, NEW_NOTE_REQUEST);
            }
        });



        // READ FROM DB


        NotesDbHelper dbHelper = new NotesDbHelper(MainActivity.this);
        SQLiteDatabase rdb = dbHelper.getReadableDatabase();

        String[] projection = { BaseColumns._ID, NotesContract.NoteEntry.COLUMN_NAME_TITLE, NotesContract.NoteEntry.COLUMN_NAME_CONTENT };

        Cursor cursor = rdb.query(NotesContract.NoteEntry.TABLE_NAME, projection,null, null,null, null, null);

        if (cursor.moveToFirst()) {
            do {
                notesList.add(new Note(cursor.getString(0), cursor.getString(1), cursor.getString(2)));
            } while (cursor.moveToNext());

        }
        rdb.close();

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
        return true;
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


        // READ FROM DB

        Note cNote = notesList.get(clickedItemIndex);

        Intent in = new Intent(MainActivity.this, EditNoteActivity.class);
        in.putExtra("title", cNote.getTitle());
        in.putExtra("content", cNote.getContent());
        in.putExtra("id", cNote.getId());

        startActivityForResult(in, 2);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == NEW_NOTE_REQUEST) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {


                // Create new helper
                NotesDbHelper dbHelper = new NotesDbHelper(MainActivity.this);
                // Get the database. If it does not exist, this is where it will
                // also be created.
                SQLiteDatabase wdb = dbHelper.getWritableDatabase();

                // Create insert entries
                ContentValues values = new ContentValues();
                values.put(NotesContract.NoteEntry.COLUMN_NAME_TITLE, data.getStringExtra("title"));
                values.put(NotesContract.NoteEntry.COLUMN_NAME_CONTENT, data.getStringExtra("content"));

                // Insert the new row, returning the primary key value of the new row
                long newRowId;
                newRowId = wdb.insert(
                        NotesContract.NoteEntry.TABLE_NAME,
                        null,
                        values);

                Note newNote = new Note(Long.toString(newRowId), data.getStringExtra("title"), data.getStringExtra("content"));
                notesList.add(newNote);
                wdb.close();

                finish();
                startActivity(getIntent());
            }
        }

        if (requestCode == 2) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {

                if (data.getStringExtra("action").equals("edit")) {
                    ContentValues cv = new ContentValues();
                    cv.put(NotesContract.NoteEntry.COLUMN_NAME_TITLE, data.getStringExtra("newTitle"));
                    cv.put(NotesContract.NoteEntry.COLUMN_NAME_CONTENT, data.getStringExtra("newContent"));

                    NotesDbHelper dbHelper = new NotesDbHelper(MainActivity.this);

                    SQLiteDatabase wdb = dbHelper.getWritableDatabase();
                    wdb.update(NotesContract.NoteEntry.TABLE_NAME, cv, NotesContract.NoteEntry._ID + "=" + data.getStringExtra("id"), null);
                    System.out.println("EDITED SOMETHING!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                    finish();
                    startActivity(getIntent());
                }
                else if (data.getStringExtra("action").equals("delete"))
                {
                    String rowId = data.getStringExtra("id");

                    NotesDbHelper dbHelper = new NotesDbHelper(MainActivity.this);

                    SQLiteDatabase wdb = dbHelper.getWritableDatabase();
                    wdb.delete(
                            NotesContract.NoteEntry.TABLE_NAME,
                            NotesContract.NoteEntry._ID + " = ?",
                            new String[]{rowId});
                    wdb.close();

                    System.out.println("DELETED SOMETHING!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                    finish();
                    startActivity(getIntent());
                }
            }
        }
    }
}
