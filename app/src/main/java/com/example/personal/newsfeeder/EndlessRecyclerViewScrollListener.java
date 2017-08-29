package com.example.personal.newsfeeder;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;

/**
 * Created by personal on 6/1/2017.
 * This is the class that is responsible for pagination aka infinite scroll.
 * We use the RecyclerView's OnScrollListener class and extend it here
 * The general idea is that as soon as some threshold value is reached we start loading the data again.
 * Each time we load new data we just increase the page number and pass it to the main class.
 * The main class then uses that page number as a query parameter to load the data again and the loader
   is started again.
 * Refer to this article for more details --> https://github.com/codepath/android_guides/wiki/Endless-Scrolling-with-AdapterViews-and-RecyclerView
 */

public abstract class EndlessRecyclerViewScrollListener extends RecyclerView.OnScrollListener {

    /*
     * @visibleThreshold is the minimum number of items that are below the current scroll position
       before we start loading more
     */

    private int visibleThreshold = 3;

    // this is the offset index of of data we have loaded.

    private int currentPage = 0 ;

    // sets the starting page index.

    private int startingPageIndex = 1;

    //total number of items in the dataset after the last load.

    private int previousItemCount = 0;

    //true if we are still waiting for the last set of data to load.

    private boolean loading = true;

    // we need the layout manager in order to find the last visible item position. As layout manager
    // is responsible for laying out the items for the recycler view.

    RecyclerView.LayoutManager mLayoutManager;

    /*
     * Here we have overloaded constructors for this class.
     * Depending on the layout type of the layout manager(i.e. gridlayout, linear layout or the staggered layout )
       we find the visible threshold and initialize accordingly.
     * Since other layouts except linear layout are 2 dimensional, we need to multiply the visible threshold with
       the number of columns too.
     */

    public EndlessRecyclerViewScrollListener(LinearLayoutManager layoutManager)
    {
        mLayoutManager = layoutManager;
    }

    public EndlessRecyclerViewScrollListener(GridLayoutManager layoutManager)
    {
        mLayoutManager = layoutManager;
        visibleThreshold = visibleThreshold*layoutManager.getSpanCount();
    }

    public EndlessRecyclerViewScrollListener(StaggeredGridLayoutManager layoutManager)
    {
        mLayoutManager = layoutManager;
        visibleThreshold = visibleThreshold*layoutManager.getSpanCount();
    }

    /*
     * this method just simply finds out the last visible item for 2-D Layouts.
     * @param lastVisibleItemPositions is an array of items in the last row
     * This function simply finds the max of this array
     */

    public int getLastVisibleItem(int[] lastVisibleItemPositions)
    {
        int maxSize = 0;
        for(int i=0; i<lastVisibleItemPositions.length; i++)
        {
            if(i == 0)
            {
                maxSize = lastVisibleItemPositions[i];
            }
            else if(lastVisibleItemPositions[i] > maxSize)
            {
                maxSize = lastVisibleItemPositions[i];
            }
        }
        return maxSize;
    }

    /*
     * This is the method that implements the logic for triggering the loading of data when threshold is reached
     * Lest dive in and explore this method more
     */

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

        // initialize the lastVisibleItemPosition to zero
        int lastVisibleItemPosition = 0;

        //find the total number of items in the dataset
        int totalItemCount = mLayoutManager.getItemCount();

       // check if layout manager uses Staggered Grid Layout
        if(mLayoutManager instanceof StaggeredGridLayoutManager)
        {
            //find the last row in the layout and save it in an int array.
            int[] lastVisibleItemPositions = ((StaggeredGridLayoutManager)mLayoutManager)
                    .findLastVisibleItemPositions(null);

            //to find the last visible item just use the getLastVisibleItem method and pass in the array
            lastVisibleItemPosition = getLastVisibleItem(lastVisibleItemPositions);
        }

        // check if the layout manager uses grid layout
        else if(mLayoutManager instanceof GridLayoutManager)
        {
            //just find the last visible item and initialize the local variable lastVisibleItemPosition.
            lastVisibleItemPosition = ((GridLayoutManager)mLayoutManager).findLastVisibleItemPosition();
        }

        //the the same stuff as for the above else if condition
        else if(mLayoutManager instanceof LinearLayoutManager)
        {
            lastVisibleItemPosition = ((LinearLayoutManager)mLayoutManager).findLastVisibleItemPosition();
        }

        // if the total item count is zero and the previous isn't, assume that the list is invalid
        // we just need to reset back to initial state

        if(totalItemCount < previousItemCount)
        {
            this.currentPage = this.startingPageIndex;
            previousItemCount = totalItemCount;
            if(totalItemCount == 0)
            {
                loading = true;
            }
        }

        // if its still loading, we check if the dataset count has changed, if so we conclude that
        // it has finished loading the current page number and total item count

        if(loading && (totalItemCount > previousItemCount))
        {
            loading = false;
            previousItemCount = totalItemCount;
        }

        /*
         * if currently its not loading then we check if the last visible items plus the threshold is
           greater than the total items in the data set. If so, then we need to load more data.
         */

        if(!loading && (lastVisibleItemPosition + visibleThreshold) > totalItemCount)
        {
            //just increment the page number as we want to load more data
            currentPage++;
            //pass in the correct arguments to the abstract method.
            onLoadMore(currentPage,totalItemCount,recyclerView);
            //as now the loading has started we set it to true
            loading = true;
        }
    }

    //this is the abstract method that we implement in the main activity to finally load more data.
    public abstract void onLoadMore(int currentPage, int totalItemCount, RecyclerView recyclerView);

    //if we want to reset everything to the begining.

    public void resetState()
    {
        this.currentPage = this.startingPageIndex;
        this.previousItemCount = 0;
        this.loading = true;
    }




}
