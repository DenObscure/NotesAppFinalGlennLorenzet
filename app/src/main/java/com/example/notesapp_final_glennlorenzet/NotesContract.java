package com.example.notesapp_final_glennlorenzet;

import android.content.UriMatcher;
import android.net.Uri;
import android.provider.BaseColumns;

public final class NotesContract {

    public static final String AUTHORITY = "com.example.notesapp_final_glennlorenzet";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    public static final String NOTE_PATH = "note";

    public static final int ALL_NOTES = 1;
    public static final int NOTE_ID = 2;
    private NotesContract() {}

    public static UriMatcher buildUriMatcher() {
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AUTHORITY, NOTE_PATH, ALL_NOTES);
        uriMatcher.addURI(AUTHORITY, NOTE_PATH + "/#", NOTE_ID);

        return uriMatcher;
    }

    /* Inner class that defines the table contents */
    public static class NoteEntry implements BaseColumns {
        public static final String TABLE_NAME = "tblNotes";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_CONTENT = "content";

        public static final String SQL_CREATE_TABLE =
                "CREATE TABLE " +
                        TABLE_NAME + " (" +
                        _ID + " INTEGER PRIMARY KEY," +
                        COLUMN_NAME_TITLE + " TEXT," +
                        COLUMN_NAME_CONTENT + " TEXT)";

        public static final String SQL_DELETE_TABLE =
                "DROP TABLE IF EXISTS " + TABLE_NAME;

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(NOTE_PATH).build();

    }
}