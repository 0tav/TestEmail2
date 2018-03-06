package com.example.ireos.testemail2.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.ireos.testemail2.data.MailContract.*;

/**
 * Created by tav on 05/03/2018.
 */

public class MailDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "mail_sent.db";

    private static final int DATABASE_VERSION = 1;

    public MailDbHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_MAILS_TABLE = "CREATE TABLE " + MailEntry.TABLE_NAME + "("
                + MailEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + MailEntry.COLUMN_EMAIL_TO + " TEXT NOT NULL, "
                + MailEntry.COLUMN_EMAIL_SUBJECT + " TEXT, "
                + MailEntry.COLUMN_EMAIL_BODY + " TEXT);";

        db.execSQL(SQL_CREATE_MAILS_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
