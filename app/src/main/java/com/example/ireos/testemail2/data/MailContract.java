package com.example.ireos.testemail2.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by tav on 05/03/2018.
 */

public final class MailContract {

    public static final String CONTENT_AUTHORITY = "com.example.ireos.testemail2";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_MAILS = "mails";

    private MailContract(){}

    public static final class MailEntry implements BaseColumns{

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_MAILS);

        public final static String TABLE_NAME = "mails";

        public final static String _ID = BaseColumns._ID;
        public final static String COLUMN_EMAIL_TO = "email_to";
        public final static String COLUMN_EMAIL_SUBJECT = "subject";
        public final static String COLUMN_EMAIL_BODY = "message";

        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MAILS;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MAILS;

    }
}
