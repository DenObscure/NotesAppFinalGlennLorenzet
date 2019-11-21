package com.example.notesapp_final_glennlorenzet;

import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v4.app.TaskStackBuilder;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.util.ArrayList;
import java.util.List;

import static android.R.id.text1;
import static android.R.layout.simple_list_item_1;

public class DataProvider implements RemoteViewsService.RemoteViewsFactory {

    List<Note> myListView = new ArrayList<>();
    Context mContext = null;

    public DataProvider(Context context, Intent intent) {
        mContext = context;
    }

    @Override
    public void onCreate() {
        initData();
    }

    @Override
    public void onDataSetChanged() {
        initData();
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        return myListView.size();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        RemoteViews view = new RemoteViews(mContext.getPackageName(),
                simple_list_item_1);
        view.setTextViewText(text1, myListView.get(position).getTitle());

        Intent fillInIntent = new Intent();
        fillInIntent.putExtra("widgetNoteId", myListView.get(position).getId());
        fillInIntent.putExtra("action", "widget");
        fillInIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
        view.setOnClickFillInIntent(text1, fillInIntent);


        return view;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    private void initData() {
        myListView.clear();

        // READ FROM DB

        ContentResolver result = (ContentResolver)mContext.getContentResolver();

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
        Cursor mCursor = mContext.getContentResolver().query(
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
                    myListView.add(new Note(mCursor.getString(0), mCursor.getString(1), mCursor.getString(2)));
                } while (mCursor.moveToNext());

            }
        }
    }
}