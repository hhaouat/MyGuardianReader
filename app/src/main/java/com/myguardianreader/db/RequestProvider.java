package com.myguardianreader.db;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.myguardianreader.BuildConfig;


public class RequestProvider extends ContentProvider {
    private static final String TAG = "RequestProvider";

    private DatabaseHelper databaseHelper;
    private static final UriMatcher sUriMatcher;

    private static final String AUTHORITY = BuildConfig.APPLICATION_ID;

    private static final int TABLE_ITEM = 100;

    private static final int TABLE_DIR = 101;

    static {
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        sUriMatcher.addURI(AUTHORITY, ArticleContract.ArticleEntry.TABLE_NAME + "/#", TABLE_ITEM);
        sUriMatcher.addURI(AUTHORITY, ArticleContract.ArticleEntry.TABLE_NAME, TABLE_DIR);
    }

    public static Uri urlForItems(int limit) {
        return Uri.parse("content://" + AUTHORITY + "/" + ArticleContract.ArticleEntry.TABLE_NAME + "/offset/" + limit);
    }

    @Override
    public boolean onCreate() {
        databaseHelper = new DatabaseHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {
            case TABLE_ITEM:
                retCursor = databaseHelper.getReadableDatabase().query(
                        ArticleContract.ArticleEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            case TABLE_DIR:
                retCursor = databaseHelper.getReadableDatabase().query(
                        ArticleContract.ArticleEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            default:
                throw new UnsupportedOperationException("Unknown Uri " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case TABLE_ITEM:
                return ArticleContract.ArticleEntry.CONTENT_ITEM_TYPE;
            case TABLE_DIR:
                return ArticleContract.ArticleEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown URI " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final SQLiteDatabase db = databaseHelper.getWritableDatabase();
        Uri returnUri;
        switch (sUriMatcher.match(uri)) {
            //Case for Post
            case TABLE_DIR:
                long _id = db.insert(ArticleContract.ArticleEntry.TABLE_NAME, null, contentValues);
                if (_id > 0)
                    returnUri = ArticleContract.ArticleEntry.buildPupilUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row " + uri);
                break;
            default:
                throw new UnsupportedOperationException("Unknown URI " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = databaseHelper.getWritableDatabase();
        int rowsDeleted;
        switch (sUriMatcher.match(uri)) {
            case TABLE_DIR:
                rowsDeleted = db.delete(ArticleContract.ArticleEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown URI " + uri);
        }
        if (selection == null || 0 != rowsDeleted)
            getContext().getContentResolver().notifyChange(uri, null);
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = databaseHelper.getWritableDatabase();
        int update;
        switch (sUriMatcher.match(uri)) {
            //Case for User
            case TABLE_DIR:
                update = db.update(ArticleContract.ArticleEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown URI " + uri);
        }
        if (update > 0)
            getContext().getContentResolver().notifyChange(uri, null);
        return update;
    }
}
