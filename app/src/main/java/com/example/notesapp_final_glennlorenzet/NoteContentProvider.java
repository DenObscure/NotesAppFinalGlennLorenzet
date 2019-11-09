package com.example.notesapp_final_glennlorenzet;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.content.ContentUris;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

import java.util.HashMap;

import static com.example.notesapp_final_glennlorenzet.NotesContract.NoteEntry.TABLE_NAME;


/*
 *  URI ALL NOTES       -> com.example.notesapp_final_glennlorenzet.NoteContentProvider/notes
 *  URI CERTAIN NOTE    -> com.example.notesapp_final_glennlorenzet.NoteContentProvider/notes/#
 *  URI CERTAIN COLUMN  -> com.example.notesapp_final_glennlorenzet.NoteContentProvider/notes/*
 */

public class NoteContentProvider extends ContentProvider
{
    private NotesDbHelper mNotesDbHelper;



    static final String id = "id";
    static final String title = "title";
    static final String PROVIDER_NAME = "com.example.notesapp_final_glennlorenzet.NoteContentProvider";
    static final String URL = "content://" + PROVIDER_NAME + "/notes";
    static final Uri CONTENT_URI = Uri.parse(URL);
    static final int uriCode = 1;
    static final UriMatcher uriMatcher;
    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(PROVIDER_NAME, "notes", uriCode);
        uriMatcher.addURI(PROVIDER_NAME, "notes/*", uriCode);
    }
    private static HashMap<String, String> values;
    private SQLiteDatabase db;

    @Override
    public boolean onCreate() {
        Context context = getContext();
        mNotesDbHelper = new NotesDbHelper(context);

        db = mNotesDbHelper.getWritableDatabase();
        if (db != null) {
            return true;
        }
        return false;
    }

    @Override
    public Cursor query(Uri uri,String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(TABLE_NAME);

        switch (uriMatcher.match(uri)) {
            case uriCode:
                qb.setProjectionMap(values);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        if (sortOrder == null || sortOrder == "") {
            sortOrder = id;
        }
        Cursor c = qb.query(db, projection, selection, selectionArgs, null,
                null, sortOrder);
        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;
    }

    @Override
    public String getType(Uri uri){
        switch (uriMatcher.match(uri)) {
            case uriCode:
                return "com.example.notesapp_final_glennlorenzet.NoteContentProvider/notes";
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values){
        long rowID = db.insert(TABLE_NAME, "", values);
        if (rowID > 0) {
            Uri _uri = ContentUris.withAppendedId(CONTENT_URI, rowID);
            getContext().getContentResolver().notifyChange(_uri, null);
            return _uri;
        }
        throw new SQLiteException("Failed to add a record into " + uri);
    }

    @Override
    public int delete( Uri uri,String selection, String[] selectionArgs) {
        int count = 0;
        switch (uriMatcher.match(uri)) {
            case uriCode:
                count = db.delete(TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int count = 0;
        switch (uriMatcher.match(uri)) {
            case uriCode:
                count = db.update(TABLE_NAME, values, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }
}
