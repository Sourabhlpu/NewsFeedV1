package com.example.personal.newsfeeder.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.example.personal.newsfeeder.R;

/**
 * Created by personal on 8/29/2017.
 */

public class NewsPreferences {

    public static boolean saveBookmarks(String url)
    {
         return true;
    }

    public static String getOrderBy(Context context)
    {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        String orderbyKey = context.getString(R.string.settings_order_by_key);
        String orderbyDefault = context.getString(R.string.settings_orderby_default);

        String orderBy = sp.getString(orderbyKey,orderbyDefault);

        return orderBy;
    }


    public static String getPageSize(Context context)
    {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        String pageSizeKey = context.getString(R.string.settings_page_size_key);
        String pageSizeDefault = context.getString(R.string.settings_page_size_default);

        String pageSize = sp.getString(pageSizeKey,pageSizeDefault);
        return pageSize;
    }
}
