package com.example.personal.newsfeeder.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.example.personal.newsfeeder.R;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by personal on 8/29/2017.
 */

public class NewsPreferences {

    private static int BookmarkCount;

    private static ArrayList<String> mBookmarkIds;

    public static ArrayList<String> getmBookmarkIds(Context context)
    {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);

        Set<String> set = pref.getStringSet("List",null);

        if(set == null)
        {
            mBookmarkIds = new ArrayList<>();
        }
        else
        {
            mBookmarkIds = new ArrayList<String>(set);
        }



        return mBookmarkIds;
    }

    public static void setmBookmarkIds(ArrayList<String> bookmarkIds, Context context)
    {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);

        Set<String> set = new HashSet<String>();
        set.addAll(bookmarkIds);

        SharedPreferences.Editor editor = pref.edit();
        editor.putStringSet("List",set);
        editor.commit();
    }

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

    public static boolean isBookmarked(String id, Context context)
    {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);

        return sp.contains(id);
    }

    public static void saveBookmark(String id, Context context)
    {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();

        editor.putString(id,id);
        editor.apply();
    }

    public static void removeBookmark(String id, Context context)
    {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();

        editor.remove(id);
        editor.apply();
    }
}
