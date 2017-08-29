package com.example.personal.newsfeeder;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;

/**
 * Created by personal on 3/13/2017.
 *
 */

public final class QueryUtils {

    private static final String LOG_TAG = QueryUtils.class.getSimpleName();

    private QueryUtils()
    {

    }

    public static ArrayList<TheArticle> fetchArticles(String theRequestURL)
    {
        URL url = createURL(theRequestURL);

        String jsonResponse = null;

        try{
            jsonResponse = makeHttpRequest(url);
            Log.v("The json",LOG_TAG + jsonResponse);
        }
        catch(IOException e)
        {
            Log.v(LOG_TAG,"Couldn't fetch the json response",e);
        }

        ArrayList<TheArticle> articles = extractArticles(jsonResponse);
        return articles;

    }

   public static URL createURL(String requestuUrl)
   {
       URL url = null;

       try{
           url = new URL(requestuUrl);
       }
       catch(MalformedURLException e)
       {
           Log.v(LOG_TAG,"The url can't be formed",e);
       }
       return url;
   }

    public static String makeHttpRequest(URL inputURL) throws IOException
    {
        String jsonResponse = "";
        if(inputURL == null)
        {
            return jsonResponse;
        }
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;

        try
        {
            urlConnection = (HttpURLConnection)inputURL.openConnection();
            urlConnection.setReadTimeout(1000);
            urlConnection.setConnectTimeout(7000);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            if(urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK)
            {
               inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);

            }
            else
            {
                Log.e(LOG_TAG,"Error response code " + urlConnection.getResponseCode());
            }
        }
        catch (IOException e)
        {
            Log.e(LOG_TAG,"Problem retreiving earthquake json",e);
        }
        finally {
            if(urlConnection != null)
            {
                urlConnection.disconnect();
            }
            if(inputStream != null)
            {
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    public static String readFromStream(InputStream inputStream) throws IOException
    {
        StringBuilder output = new StringBuilder();

        if(inputStream != null)
        {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while(line != null)
            {
                output.append(line);
                line = reader.readLine();
            }

        }
        Log.v(LOG_TAG,"The json response in the read function " + output);
        return output.toString();
    }

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
