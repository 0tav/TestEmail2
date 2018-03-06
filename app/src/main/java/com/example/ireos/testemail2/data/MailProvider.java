package com.example.ireos.testemail2.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.ireos.testemail2.data.MailContract.*;

/**
 * Created by tav on 05/03/2018.
 */

public class MailProvider extends ContentProvider {

    public static final String LOG_TAG = MailProvider.class.getSimpleName();

    private MailDbHelper mDbHelper;

    private static final int MAILS = 100;
    private static final int MAIL_ID = 101;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(MailContract.CONTENT_AUTHORITY, MailContract.PATH_MAILS, MAILS);
        sUriMatcher.addURI(MailContract.CONTENT_AUTHORITY, MailContract.PATH_MAILS + "/#", MAIL_ID);
    }

    @Override
    public boolean onCreate() {
        mDbHelper = new MailDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection,
                        @Nullable String[] selectionArgs, @Nullable String sortOrder) {

        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        Cursor cursor;

        int match = sUriMatcher.match(uri);
        switch (match) {
            case MAILS:
                cursor = db.query(MailEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null,null, sortOrder);
                break;
            case MAIL_ID:
                selection = MailEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                cursor = db.query(MailEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match){
            case MAILS:
                return insertMail(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    private Uri insertMail(Uri uri, ContentValues values){

        String emailTo = values.getAsString(MailEntry.COLUMN_EMAIL_TO);
        if (emailTo == null){
            throw new IllegalArgumentException("Requires a email");
        }

        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        long id = db.insert(MailEntry.TABLE_NAME, null, values);
        if (id == -1){
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues,
                      @Nullable String selection, @Nullable String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match){
            case MAILS:
                return updateMail(uri, contentValues, selection, selectionArgs);
            case MAIL_ID:
                selection = MailEntry._ID + "=?";
                selectionArgs = new String[]{ String.valueOf(ContentUris.parseId(uri)) };
                return updateMail(uri, contentValues,selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    private int updateMail(Uri uri, ContentValues values,
                           String selection, String[] selectionArgs){
        if (values.containsKey(MailEntry.COLUMN_EMAIL_TO)){
            String emailTo = values.getAsString(MailEntry.COLUMN_EMAIL_TO);
            if (emailTo == null){
                throw new IllegalArgumentException("Requires email");
            }
        }

        if (values.size() == 0){
            return 0;
        }

        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        int rowsUpdated = db.update(MailEntry.TABLE_NAME, values, selection, selectionArgs);

        if (rowsUpdated != 0){
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsUpdated;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        int rowsDeleted;

        final int match = sUriMatcher.match(uri);
        switch (match){
            case MAILS:
                rowsDeleted = db.delete(MailEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case MAIL_ID:
                selection = MailEntry._ID + "=?";
                selectionArgs = new String[]{ String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = db.delete(MailEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion in snot supported for " + uri);
        }

        if (rowsDeleted != 0){
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsDeleted;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match){
            case MAILS:
                return MailEntry.CONTENT_LIST_TYPE;
            case MAIL_ID:
                return MailEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("Unknown URI " +  uri + " with match" + match);
        }
    }
}
