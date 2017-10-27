package com.example.personal.newsfeeder;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by personal on 10/23/2017.
 */

public class BookmarkFragment extends Fragment {

    private static final String LOG_TAG = BookmarkFragment.class.getSimpleName();

    private ArrayList<TheArticle> mBookmarks;



    private RecyclerView mRecyclerView;
    private BookmarkAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;
    private TextView mEmptyTextView;
    private EndlessRecyclerViewScrollListener scrollListener;
    private SwipeRefreshLayout mSwipeContainer;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.main_activity_fragment,container,false);


        mBookmarks = ((MainActivity)getActivity()).getmBookmarks();

        Log.v(LOG_TAG, "The bookmark object is " + mBookmarks);

        //mEmptyTextView is used to display an error message when we cannot load the data
        mEmptyTextView = (TextView) rootView.findViewById(R.id.empty_text_view);

        //this is the recycler view that displays the data in the list
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);


        //check if we are connected to the internet
        ConnectivityManager cm = (ConnectivityManager) getContext().getSystemService(getContext().CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();

        //if we are connected then start the loader
        if (info != null && info.isConnected()) {
            LoaderManager loaderManager = getLoaderManager();


        }
        // otherwise hide the progress bar and show the appropriate error message
        else {
            rootView.findViewById(R.id.progress_bar).setVisibility(RecyclerView.GONE);
            mEmptyTextView.setText("No internet connection");
        }


        mRecyclerView.setHasFixedSize(true);

        mEmptyTextView = (TextView) rootView.findViewById(R.id.empty_text_view);


        mLayoutManager = new LinearLayoutManager(getContext());

        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new BookmarkAdapter(getActivity(),mBookmarks);
        mRecyclerView.setAdapter(mAdapter);


        return rootView;
    }
}
