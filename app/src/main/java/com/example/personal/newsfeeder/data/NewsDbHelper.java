package com.example.personal.newsfeeder.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.personal.newsfeeder.data.NewsContract.NewsEntry;

/**
 * Created by personal on 8/2/2017.
 */

public class NewsDbHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "weather.db";

    private static final int DATABASE_VERSION = 1;

    public NewsDbHelper(Context context)
    {
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        final String SQL_CREATE_NEWS_TABLE =

                "CREATE TABLE " + NewsEntry.TABLE_NAME + "(" +
                        NewsEntry._ID  + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        NewsEntry.COLUMN_NAME + " STRING NOT NULL, " +
                        NewsEntry.IMAGE_URL + " STRING NOT NULL, " +
                        NewsEntry.ShortDescription + " STRING NOT NULL, " +
                        NewsEntry.DetailPageLink + " STRING NOT NULL, " +
                        NewsEntry.TITLE + " STRING NOT NULL, " +
                        NewsEntry.COLUMN_DATE + " STRING NOT NULL, " +
                        NewsEntry.linkId + " STRING NOT NULL, " +
                        " UNIQUE (" + NewsEntry.COLUMN_DATE + ") ON CONFLICT REPLACE);";

        db.execSQL(SQL_CREATE_NEWS_TABLE);


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + NewsEntry.TABLE_NAME);

    }
}
