package com.example.ireos.testemail2;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.ireos.testemail2.data.MailContract.*;

public class MailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    public static final String LOG_TAG = MailActivity.class.getSimpleName();

    private static final int MAIL_LOADER = 0;
    private MailCursorAdapter mCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sent);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_compose_mail);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                composeEmail();
            }
        });

        ListView mailListView = (ListView) findViewById(R.id.lv_mail);

        View emptyView = findViewById(R.id.ll_no_mail);
        mailListView.setEmptyView(emptyView);

        mCursorAdapter = new MailCursorAdapter(this, null);
        mailListView.setAdapter(mCursorAdapter);

        mailListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                editEmail(id);
            }
        });

        mailListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long id) {
                showDeleteConfirmation(id);
                return true;
            }
        });

        getLoaderManager().initLoader(MAIL_LOADER, null, this);

    }

    private void composeEmail(){
        Intent intent = new Intent(MailActivity.this, SentEditActivity.class);
        startActivity(intent);
    }

    private void editEmail(long id){
        Intent intent = new Intent(MailActivity.this, SentEditActivity.class);

        Uri currentUri = ContentUris.withAppendedId(MailEntry.CONTENT_URI, id);

        intent.setData(currentUri);

        startActivity(intent);
    }

    private void showDeleteAllConfirmationDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                deleteAllMails();
            }
        });

        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                if (dialog != null){
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void deleteAllMails(){
        int rowDeleted = getContentResolver().delete(MailEntry.CONTENT_URI, null, null);
        Log.v(LOG_TAG, rowDeleted + "rows deleted from mail database");
    }

    private void showDeleteConfirmation(final long id){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                deleteMail(id);
            }
        });

        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                if (dialog != null){
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void deleteMail(long id){
        Uri currentUri = ContentUris.withAppendedId(MailEntry.CONTENT_URI, id);
        if (currentUri != null){
            int rowsDeleted = getContentResolver().delete(currentUri, null, null);

            if (rowsDeleted == 0){
                Toast.makeText(this, R.string.sent_delete_mail_failed, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, R.string.sent_delete_mail_successfull, Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_fragment_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_delete:
                showDeleteAllConfirmationDialog();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = {
                MailEntry._ID,
                MailEntry.COLUMN_EMAIL_SUBJECT,
                MailEntry.COLUMN_EMAIL_TO
        };

        return new CursorLoader(this,
                MailEntry.CONTENT_URI,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        mCursorAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursorAdapter.swapCursor(null);
    }
}
