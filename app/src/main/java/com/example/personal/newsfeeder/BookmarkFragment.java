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
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by personal on 10/23/2017.
 */

public class BookmarkFragment extends Fragment {

    private static final String LOG_TAG = BookmarkFragment.class.getSimpleName();

    private ArrayList<TheArticle> mBookmarks;

    //Firebase
    private FirebaseAuth mFirebaseAuth;

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;

    private RecyclerView mRecyclerView;
    private BookmarkAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;
    private TextView mEmptyTextView;
    private EndlessRecyclerViewScrollListener scrollListener;
    private SwipeRefreshLayout mSwipeContainer;
    private ProgressBar mProgressbar;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.main_activity_fragment,container,false);

        //initializing the firebase auth variable
        mFirebaseAuth = FirebaseAuth.getInstance();
        //initializing the firebase realtime database
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference().child("users");


        //mBookmarks = ((MainActivity)getActivity()).getmBookmarks();
        mBookmarks = new ArrayList<TheArticle>();

        //
        mProgressbar = (ProgressBar)rootView.findViewById(R.id.progress_bar);

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

        mDatabaseReference.child(mFirebaseAuth.getCurrentUser().getUid())
                .child("bookmarks")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if (!dataSnapshot.hasChildren()) {
                            mEmptyTextView.setText("No Bookmarks");
                        } else {

                            for (DataSnapshot bookmarkSnapshot : dataSnapshot.getChildren()) {
                                TheArticle bookmark = bookmarkSnapshot.getValue(TheArticle.class);
                                mBookmarks.add(bookmark);
                                //mAdapter.replaceAll(mBookmarks);
                                Log.v(LOG_TAG, "the retrieved bookmark object is " + bookmark);
                            }

                            Log.v(LOG_TAG, "the bookmark collection is " + mBookmarks);
                            Collections.reverse(mBookmarks);
                            mAdapter.updateDataset(mBookmarks);
                            mProgressbar.setVisibility(View.INVISIBLE);

                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


        mRecyclerView.setHasFixedSize(true);

        mEmptyTextView = (TextView) rootView.findViewById(R.id.empty_text_view);


        mLayoutManager = new LinearLayoutManager(getContext());

        mRecyclerView.setLayoutManager(mLayoutManager);
        Log.v(LOG_TAG, "The bookmark object is " + mBookmarks);
        mAdapter = new BookmarkAdapter(getActivity(),mBookmarks);
        mRecyclerView.setAdapter(mAdapter);


        return rootView;
    }
}
