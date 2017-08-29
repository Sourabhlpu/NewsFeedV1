package com.example.personal.newsfeeder.utilities;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by personal on 7/23/2017.
 */

public  final class NewsFeederDateUtils {

    private static final String LOG_TAG = NewsFeederDateUtils.class.getSimpleName();

    public static String getSimpleDate(String dateFromApi)
    {
        String pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'";
        SimpleDateFormat completeDate = new SimpleDateFormat(pattern);
        //completeDate.setTimeZone(TimeZone.getTimeZone("GMT"));
        SimpleDateFormat monthName = new SimpleDateFormat("MMM yyyy", Locale.ENGLISH);
        String  monthWithYear = "";

        try {
            Date date = completeDate.parse(dateFromApi);
            monthWithYear = monthName.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Log.v(LOG_TAG, monthWithYear);

        return monthWithYear;

    }
}
