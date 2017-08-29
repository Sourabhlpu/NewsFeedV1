package com.example.personal.newsfeeder;

import android.content.AsyncTaskLoader;
import android.content.Context;

import java.util.List;

/**
 * Created by personal on 3/16/2017.
 * this is the class that does all the background work loading the data for the loader
 * it extends from the AsyncTaskLoader and overrides the important methods
 */

public class ArticleLoader extends AsyncTaskLoader<List<TheArticle>> {

    // mURL stores the url for fetching the data.
    private String mURL;
    List mArticles;

    /*
     * this constructor initializes the mURL
     * @param context is the context that's required by the super class
     * @param url is the url that is used to fetch the data
     */
    public ArticleLoader(Context context,String url)
    {
        super(context);
        mURL = url;
    }

    @Override
    protected void onStartLoading() {

        if(mArticles != null)
        {
            deliverResult(mArticles);
        }
        else {

            //this method ignores the previously loaded data and load a new one.
            forceLoad();
        }
    }

    /*
     * this is the only method that runs in the background thread
     * it validates the url and returns if its null.
     * the fetchArticles of the queryUtils uses this url to fetch the JSON Data.
     * this method finally returns the articles that are returned by the fetchArticles method
     */
    @Override
    public List<TheArticle> loadInBackground() {
        if(mURL == null)
        {
            return null;
        }
        mArticles = QueryUtils.fetchArticles(mURL);
        return mArticles;
    }

    @Override
    public void deliverResult(List<TheArticle> data) {
        mArticles = data;
        super.deliverResult(data);
    }
}
