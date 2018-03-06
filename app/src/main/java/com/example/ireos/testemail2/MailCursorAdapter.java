package com.example.ireos.testemail2;

import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.ireos.testemail2.data.MailContract;

/**
 * Created by tav on 05/03/2018.
 */

public class MailCursorAdapter extends CursorAdapter {

    public MailCursorAdapter(Context context, Cursor c){
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.item_mail, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView tvSubject = (TextView) view.findViewById(R.id.subject);
        TextView tvEmailTo = (TextView) view.findViewById(R.id.email_to);

        int subjectindex = cursor.getColumnIndex(MailContract.MailEntry.COLUMN_EMAIL_SUBJECT);
        int emailToIndex = cursor.getColumnIndex(MailContract.MailEntry.COLUMN_EMAIL_TO);

        String emailSubject = cursor.getString(subjectindex);
        String emailTo = cursor.getString(emailToIndex);

        if (TextUtils.isEmpty(emailSubject)){
            emailSubject = context.getString(R.string.no_subject);
        }

        tvSubject.setText(emailSubject);
        tvEmailTo.setText(emailTo);
    }
}
