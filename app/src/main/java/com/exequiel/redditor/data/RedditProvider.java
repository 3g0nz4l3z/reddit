package com.exequiel.redditor.data;

import android.content.ContentProvider;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentValues;
import android.content.OperationApplicationException;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.exequiel.redditor.data.RedditContract.*;

import java.util.ArrayList;
import java.util.List;


public class RedditProvider extends ContentProvider {
    private SQLiteOpenHelper mOpenHelper;
    private static final UriMatcher sUriMatcher = buildUriMatcher();


    static final int SUBREDDIT = 100;
    static final int SUBREDDIT_WITH_ID = 101;
    static final int LINK = 200;
    static final int LINK_WITH_ID = 201;
    static final int COMMENT = 300;
    static final int COMMENT_WITH_ID = 301;


    interface Tables {
        String SUBREDDITS = "subreddits";
        String LINKS = "links";
        String COMMENTS = "comments";
    }

    private static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = RedditContract.CONTENT_AUTHORITY;
        matcher.addURI(authority, RedditContract.PATH_SUBREDDITS, SUBREDDIT); //out from 'insert'
        matcher.addURI(authority, RedditContract.PATH_SUBREDDITS + "/#", SUBREDDIT); //out from 'insert'
        matcher.addURI(authority, RedditContract.PATH_LINKS, LINK); //out from 'insert'
        matcher.addURI(authority, RedditContract.PATH_LINKS + "/#", LINK_WITH_ID); //out from 'insert'
        matcher.addURI(authority, RedditContract.PATH_COMMENTS, COMMENT); //out from 'insert'
        matcher.addURI(authority, RedditContract.PATH_COMMENTS + "/#", COMMENT_WITH_ID); //out from 'insert'
        return matcher;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new RedditDBHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        final SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        final SelectionBuilder builder = buildSelection(uri);
        Cursor cursor = builder.where(selection, selectionArgs).query(db, projection, sortOrder);
        if (cursor != null) {
            cursor.setNotificationUri(getContext().getContentResolver(), uri);
        }
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case SUBREDDIT:
                return SubReddits.CONTENT_TYPE;
            case SUBREDDIT_WITH_ID:
                return SubReddits.CONTENT_ITEM_TYPE;
            case LINK:
                return Links.CONTENT_TYPE;
            case LINK_WITH_ID:
                return Links.CONTENT_ITEM_TYPE;
            case COMMENT:
                return Comments.CONTENT_TYPE;
            case COMMENT_WITH_ID:
                return Comments.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case SUBREDDIT: {
                final long _id = db.insertOrThrow(Tables.SUBREDDITS, null, values);
                getContext().getContentResolver().notifyChange(uri, null);
                return RedditContract.SubReddits.buildUriWithRowId(_id);
            }
            case LINK: {
                final long _id = db.insertOrThrow(Tables.LINKS, null, values);
                getContext().getContentResolver().notifyChange(uri, null);
                return RedditContract.Links.buildUriWithRowId(_id);
            }
            case COMMENT: {
                final long _id = db.insertOrThrow(Tables.COMMENTS, null, values);
                getContext().getContentResolver().notifyChange(uri, null);
                return RedditContract.Comments.buildUriWithRowId(_id);
            }
            default: {
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
        }
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final SelectionBuilder builder = buildSelection(uri);
        getContext().getContentResolver().notifyChange(uri, null);
        return builder.where(selection, selectionArgs).delete(db);
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final SelectionBuilder builder = buildSelection(uri);
        getContext().getContentResolver().notifyChange(uri, null);
        return builder.where(selection, selectionArgs).update(db, values);
    }

    private SelectionBuilder buildSelection(Uri uri) {
        final SelectionBuilder builder = new SelectionBuilder();
        final int match = sUriMatcher.match(uri);
        return buildSelection(uri, match, builder);
    }

    private SelectionBuilder buildSelection(Uri uri, int match, SelectionBuilder builder) {
        final List<String> paths = uri.getPathSegments();
        switch (match) {
            case SUBREDDIT: {
                return builder.table(Tables.SUBREDDITS);
            }
            case SUBREDDIT_WITH_ID: {
                final String _id = paths.get(1);
                return builder.table(Tables.SUBREDDITS).where(RedditContract.SubReddits._ID + "=?", _id);
            }
            case LINK: {
                return builder.table(Tables.LINKS);
            }
            case LINK_WITH_ID: {
                final String _id = paths.get(1);
                return builder.table(Tables.LINKS).where(RedditContract.Links._ID + "=?", _id);
            }
            case COMMENT: {
                return builder.table(Tables.COMMENTS);
            }
            case COMMENT_WITH_ID: {
                final String _id = paths.get(1);
                return builder.table(Tables.COMMENTS).where(RedditContract.Comments._ID + "=?", _id);
            }
            default: {
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
        }
    }

    /**
     * Apply the given set of {@link ContentProviderOperation}, executing inside
     * a {@link SQLiteDatabase} transaction. All changes will be rolled back if
     * any single one fails.
     */
    public ContentProviderResult[] applyBatch(ArrayList<ContentProviderOperation> operations)
            throws OperationApplicationException {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        db.beginTransaction();
        try {
            final int numOperations = operations.size();
            final ContentProviderResult[] results = new ContentProviderResult[numOperations];
            for (int i = 0; i < numOperations; i++) {
                results[i] = operations.get(i).apply(this, results, i);
            }
            db.setTransactionSuccessful();
            return results;
        } finally {
            db.endTransaction();
        }
    }
}
