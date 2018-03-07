package com.example.ireos.testemail2;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.ireos.testemail2.data.MailContract.*;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by tav on 05/03/2018.
 */

public class SentEditActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private EditText mEmailToEditText;

    private EditText mEmailSubjectEditText;

    private EditText mEmailBodyEditText;

    private static final int EXISTING_MAIL_LOADER = 0;

    private Uri mCurrentMailUri;

    private boolean mMailHasChanged = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sent_edit);

        Intent intent = getIntent();
        mCurrentMailUri = intent.getData();

        if (mCurrentMailUri == null){
            setTitle(getString(R.string.sent_edit_title_new_mail));
            invalidateOptionsMenu();
        } else {
            setTitle(getString(R.string.sent_edit_title_edit_mail));
            getLoaderManager().initLoader(EXISTING_MAIL_LOADER, null, this);
        }

        mEmailToEditText = (EditText) findViewById(R.id.iet_add_mail_to);
        mEmailSubjectEditText = (EditText) findViewById(R.id.iet_add_mail_subject);
        mEmailBodyEditText = (EditText) findViewById(R.id.et_add_mail_desc);

        mEmailToEditText.setOnTouchListener(mTouchListener);
        mEmailSubjectEditText.setOnTouchListener(mTouchListener);
        mEmailBodyEditText.setOnTouchListener(mTouchListener);

        FloatingActionButton fab =
                (FloatingActionButton) findViewById(R.id.fab_edit_mail_done);
        fab.setImageResource(R.drawable.ic_send);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isValidEmail(mEmailToEditText.getText().toString())){
                    mEmailToEditText.setError(getString(R.string.invalid_email));
                    mEmailToEditText.requestFocus();
                } else {
                    sentMail();
                    finish();
                }
            }
        });

    }

    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mMailHasChanged = true;
            return false;
        }
    };

    private void showUnsavedChangesDiscard(
            DialogInterface.OnClickListener discardClickListener){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsent_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null){
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public void onBackPressed() {
        if (!mMailHasChanged){
            super.onBackPressed();
            return;
        }

        DialogInterface.OnClickListener discardClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                finish();
            }
        };

        showUnsavedChangesDiscard(discardClickListener);
    }

    private boolean isValidEmail(String email){
        Pattern pattern = Patterns.EMAIL_ADDRESS;
        Matcher matcher = pattern.matcher(email);
        return  matcher.matches();
    }

    private void sentMail(){
        String to = mEmailToEditText.getText().toString();
        String subject = mEmailSubjectEditText.getText().toString();
        String desc = mEmailBodyEditText.getText().toString();

        if (mCurrentMailUri == null
                && TextUtils.isEmpty(to)
                && TextUtils.isEmpty(subject)
                && TextUtils.isEmpty(desc)){
            return;
        }

        ContentValues values = new ContentValues();
        values.put(MailEntry.COLUMN_EMAIL_TO, to);
        values.put(MailEntry.COLUMN_EMAIL_SUBJECT, subject);
        values.put(MailEntry.COLUMN_EMAIL_BODY, desc);


        if (mCurrentMailUri == null){

            Uri newUri = getContentResolver().insert(MailEntry.CONTENT_URI, values);

            if (newUri == null){
                Toast.makeText(this, R.string.sent_insert_mail_failed, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, R.string.sent_insert_mail_successfull, Toast.LENGTH_SHORT).show();
            }
        } else {

            int rowsAffected = getContentResolver().update(mCurrentMailUri, values, null, null);

            if (rowsAffected == 0){
                Toast.makeText(this, getString(R.string.sent_edit_mail_failed), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, R.string.sent_edit_mail_successfull, Toast.LENGTH_SHORT).show();
            }
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                if (!mMailHasChanged){
                    NavUtils.navigateUpFromSameTask(this);
                    return true;
                }

                DialogInterface.OnClickListener discardOnClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        NavUtils.navigateUpFromSameTask(SentEditActivity.this);
                    }
                };

                showUnsavedChangesDiscard(discardOnClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

        String[] projection = {
                MailEntry._ID,
                MailEntry.COLUMN_EMAIL_TO,
                MailEntry.COLUMN_EMAIL_SUBJECT,
                MailEntry.COLUMN_EMAIL_BODY
        };

        return new CursorLoader(this,
                mCurrentMailUri,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor == null || cursor.getCount() < 1){
            return;
        }

        if (cursor.moveToFirst()){
            int emailToIndex = cursor.getColumnIndex(MailEntry.COLUMN_EMAIL_TO);
            int emailSubjectIndex = cursor.getColumnIndex(MailEntry.COLUMN_EMAIL_SUBJECT);
            int emailBodyIndex = cursor.getColumnIndex(MailEntry.COLUMN_EMAIL_BODY);

            String to = cursor.getString(emailToIndex);
            String subject = cursor.getString(emailSubjectIndex);
            String desc = cursor.getString(emailBodyIndex);

            mEmailToEditText.setText(to);
            mEmailSubjectEditText.setText(subject);
            mEmailBodyEditText.setText(desc);

        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mEmailToEditText.setText("");
        mEmailSubjectEditText.setText("");
        mEmailBodyEditText.setText("");
    }
}
