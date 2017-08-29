package com.example.personal.newsfeeder.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by personal on 8/2/2017.
 */

public class NewsContract {

    public static final String CONTENT_AUTHORITY = "com.example.android.newsfeeder";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String NEWS_PATH = "news";

    public static final class NewsEntry implements BaseColumns
    {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(NEWS_PATH)
                .build();

        public static final String  TABLE_NAME = "news";

        public static final String COLUMN_DATE = "date";

        public static final String COLUMN_NAME = "name";

        public static final String IMAGE_URL = "imageUrl";

        public static final String TITLE = "title";

        public static final String ShortDescription = "description";

        public static final String DetailPageLink = "pageLink";
    }
}
