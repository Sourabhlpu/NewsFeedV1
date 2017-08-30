package com.example.personal.newsfeeder.utilities;

import android.text.TextUtils;
import android.util.Log;

import com.example.personal.newsfeeder.TheArticle;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by personal on 8/30/2017.
 */

public class OpenNewsJsonUtils {

    private static final String LOG_TAG = OpenNewsJsonUtils.class.getSimpleName();

    public static ArrayList<TheArticle> extractArticles(String jsonResponse)
    {
        Log.v("we got the response",LOG_TAG + jsonResponse);
        if(TextUtils.isEmpty(jsonResponse))
        {
            return null;
        }

        ArrayList<TheArticle> articles = new ArrayList<>();

        try
        {
            JSONObject root = new JSONObject(jsonResponse);
            JSONObject response = root.getJSONObject("response");
            JSONArray results = response.optJSONArray("results");

            for(int i=0; i<results.length(); i++)
            {
                JSONObject result = results.optJSONObject(i);
                String detailPageLink = result.optString("webUrl");
                String sectionId = result.optString("sectionId");
                String id = result.optString("id");
                if(!sectionId.equals("crosswords")) {
                    JSONObject fields = result.optJSONObject("fields");
                    String title = fields.optString("byline");
                    String subhead = result.optString("webPublicationDate");
                    String imageUrl = fields.optString("thumbnail");
                    String theTitle = result.optString("webTitle");
                    String description = fields.optString("trailText");

                    Log.v(LOG_TAG, "The link saved to the article object " + detailPageLink);


                    if (imageUrl.equals("")) {
                        imageUrl = "https://goo.gl/PHbk71";
                    }
                    if(!title.equals("") && !subhead.equals("") && !theTitle.equals("") ) {
                        TheArticle singleArticle = new TheArticle(title, subhead, imageUrl, theTitle, description,detailPageLink,id);
                        articles.add(singleArticle);
                    }
                }


            }
        }
        catch(JSONException e)
        {
            Log.e(LOG_TAG,"Json not formed",e);
        }
        return articles;
    }
}
