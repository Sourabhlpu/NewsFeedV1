package com.example.personal.newsfeeder.utilities;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Log;

import com.example.personal.newsfeeder.data.NewsPreferences;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * Created by personal on 8/30/2017.
 */

public class NetworkUtils {

    private static Uri.Builder uriBuilder;

    private static final String LOG_TAG = NetworkUtils.class.getSimpleName();


    //this is the string that we will use to fetch the json data.
    //for more details about this API refer to the following link --> http://open-platform.theguardian.com/explore/

    private static final String NEWS_REQUEST_URL = "http://content.guardianapis.com/" +
            "search";
    //we give the loader an id. This is just a random unique value.
    private static final int EARTHQUAKE_LOADER_ID = 1;

    //The api key that we need to pass in with our request to the server of the Gaurdian
    private static final String API_KEY = "ce10d58e-3c68-451c-beb3-7b6a79f5b75f";

    private static URL url;


    public static URL createAPIQueryString(String currentPage, Context context) {


        Log.v(LOG_TAG, "The value of the page index " + currentPage);

        //we fetch the settings the user has set to fetch the data in the settings activity.
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);

        //we want to get the value of the user preference of the sort order of the articles
        String orderBy = NewsPreferences.getOrderBy(context);
        // this setting of user will set that how many pages we want to load per query.
        String pageSize = NewsPreferences.getPageSize(context);

        /*
         * now we are done with fetching the user preferences
         * It is time  to create he uri. We will add the above user settings to the request url
         */

        //this takes the query string saved as string constant and converts it into a URI.
        // Now we can use this uri to add parameters to it.

        Uri baseUri = Uri.parse(NEWS_REQUEST_URL);
        uriBuilder = baseUri.buildUpon();

        if (orderBy.equals("oldest")) {
            uriBuilder.appendQueryParameter("from-date", "2017-01-01");
            uriBuilder.appendQueryParameter("use-date", "published");

        }

        /*
          * this is the sample url that's created with a few appended parameters
          * the following method adds the parameter to the uri
           http://content.guardianapis.com/search?order-by=relevance&use-date=published&api-key=ce10d58e-3c68-451c-beb3-7b6a79f5b75f
        */


        uriBuilder.appendQueryParameter("order-by", orderBy);
        uriBuilder.appendQueryParameter("show-fields", "byline,thumbnail,trailText,body");
        uriBuilder.appendQueryParameter("page", currentPage);
        uriBuilder.appendQueryParameter("page-size", pageSize);
        uriBuilder.appendQueryParameter("api-key", API_KEY);



        // convert the uri to string and then return it.
        try {
            url = new URL(uriBuilder.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

    public static String makeHttpRequest(URL inputURL) throws IOException
    {
      HttpURLConnection urlConnection = (HttpURLConnection) inputURL.openConnection();

        try
        {
            InputStream in = urlConnection.getInputStream();
            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            String response = null;
            if(hasInput)
            {
                response = scanner.next();
            }
            scanner.close();
            return response;
        }
        finally {
            urlConnection.disconnect();
        }
    }
}
