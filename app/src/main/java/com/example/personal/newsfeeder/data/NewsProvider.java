package com.example.personal.newsfeeder.data;

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

/**
 * Created by personal on 8/7/2017.
 */

public class NewsProvider extends ContentProvider {

    NewsDbHelper mOpenHelper;

    private static final String LOG_TAG = NewsProvider.class.getSimpleName();
    private static final int CODE_NEWS = 100;
    private static final int CODE_NEWS_WITH_ID = 101;

    private static UriMatcher sUriMatcher = buildUriMatcher();

    private static UriMatcher buildUriMatcher() {
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        sUriMatcher.addURI(NewsContract.CONTENT_AUTHORITY, NewsContract.NEWS_PATH,CODE_NEWS);

        sUriMatcher.addURI(NewsContract.CONTENT_AUTHORITY, NewsContract.NEWS_PATH + "/#", CODE_NEWS_WITH_ID);

        return sUriMatcher;

    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new NewsDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        return null;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {

        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();

        int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match)
        {
            case CODE_NEWS:
                long id = db.insert(NewsContract.NewsEntry.TABLE_NAME,null,values);

                Log.v(LOG_TAG, id+"");
                if(id > 0)
                {
                    returnUri = ContentUris.withAppendedId(NewsContract.NewsEntry.CONTENT_URI,id);
                }
                else{
                    throw new android.database.SQLException("Failed to insert row  into " + uri);
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown Uri " + uri);

        }
        getContext().getContentResolver().notifyChange(uri,null);
        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }
}
