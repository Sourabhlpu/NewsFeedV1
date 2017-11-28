package com.example.personal.newsfeeder;


import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.customtabs.CustomTabsIntent;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.personal.newsfeeder.data.NewsPreferences;
import com.example.personal.newsfeeder.utilities.NetworkUtils;
import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by personal on 10/22/2017.
 */

public class MainActivityFragment extends Fragment implements android.support.v4.app.LoaderManager.LoaderCallbacks<List<TheArticle>>,
        RVAdapter.ListItemOnClickHandler, RVAdapter.BookmarkOnClickHandler, RVAdapter.ShareOnClickHandler {

    //we give the loader an id. This is just a random unique value.
    private static final int EARTHQUAKE_LOADER_ID = 1;

    //constant for the start activity for result method for user signin
    private static final int RC_SIGN_IN = 2;

    private static final String LOG_TAG = MainActivity.class.getSimpleName();



    private RecyclerView mRecyclerView;
    private RVAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;
    private TextView mEmptyTextView;
    private EndlessRecyclerViewScrollListener scrollListener;
    private SwipeRefreshLayout mSwipeContainer;


    //Firebase
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;

    private ArrayList<TheArticle> mBookmarks;

    private HashMap<String, String> mBookmarkIds;


    private  String page = "1";


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    //Overriding the three loader methods

    /*
     * this method is called when the loader is created.
     * it uses the createAPIQueryString to create a query for the api
     * the it passes that string with the context to the ArticleLoader object
     */

    @Override
    public android.support.v4.content.Loader<List<TheArticle>> onCreateLoader(int id, Bundle args) {


        URL queryUrl = NetworkUtils.createAPIQueryString(page,getContext());


        return new ArticleLoader(getContext(), queryUrl);
    }

     /*
     * this method is called when the loading of data is done
     * this method does all the work of updating the UI
     * updating of UI happens via updating the adapter.
     */

    @Override
    public void onLoadFinished(android.support.v4.content.Loader<List<TheArticle>> loader, List<TheArticle> articles) {

        //make the progress bar invisible as the data load has finished
        getView().findViewById(R.id.progress_bar).setVisibility(RecyclerView.GONE);

        //check if we were successfully able to load the data by checking if the articles list is empty
        // if its not empty then update the adapter
        if (articles != null && !articles.isEmpty()) {

            //this method updates the adapter.
            mAdapter.updateDataset(articles, mRecyclerView);
            Log.v(LOG_TAG, "The articles object is " + articles);

        }
        // if the articles list is not loaded then show the error message.
        else {
            mEmptyTextView.setText("No atricles found");
            getView().findViewById(R.id.progress_bar).setVisibility(RecyclerView.GONE);
        }


    }

    // for now we don't do anything with this method

    @Override
    public void onLoaderReset(android.support.v4.content.Loader<List<TheArticle>> loader) {

    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.main_activity_fragment,container,false);

        //initializing the firebase auth variable
        mFirebaseAuth = FirebaseAuth.getInstance();


       // this stores all the bookmars that are stored in the firebase
        mBookmarks = new ArrayList<TheArticle>();

        //there is a bookmard id's for each bookmark stored on firebase. We store it there
        mBookmarkIds = new HashMap<>();

        //initializing the firebase realtime database
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        //get a reference to users object in firebase database
        mDatabaseReference = mFirebaseDatabase.getReference().child("users");

        /*
         * we want to read all the bookmardIds that are stored in the firebase database.
         * we want to save all those ids in a hashmap
         * after its savend in the hashmap we want to save that hashmap in the shared preferences
         * so that when the recyclerview creates the list items it automatically displays all the
         * items that were bookmarked already.
         */

        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for(DataSnapshot bookmarkIdSnapshot : dataSnapshot.getChildren())
                {
                    mBookmarkIds.put(bookmarkIdSnapshot.getValue().toString(), bookmarkIdSnapshot.getKey());
                }

                Log.v(LOG_TAG, "the bookmark ids are " + mBookmarkIds);
                NewsPreferences.setmBookmarkIds(mBookmarkIds);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        mDatabaseReference.child(mFirebaseAuth.getCurrentUser().getUid())
                .child("bookmarkIds")
                .addListenerForSingleValueEvent(valueEventListener);



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
            loaderManager.initLoader(EARTHQUAKE_LOADER_ID, null, this);

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




        /*
         * initializing the adapter
         * the first param takes the context of this activity to pass the context
         * the second param is passed the an article list object to initialize the list in the adapter
         * the third param is passed this coz this activity implements the onClick method of the
         * listItemOnclickHandler abstract class.
         *
         */


                mAdapter = new RVAdapter(getContext(), new ArrayList<TheArticle>(), this, this,this);
                mRecyclerView.setAdapter(mAdapter);



        /*
         * EndlessRecyclerViewScrollListener class is an abstract class that extends RecyclerView's
         * OnScrollListener class. This is used for infinite loading of data
         * Here we initialize the class. We pass in the layoutManager that our recycler uses
         *
         */



        scrollListener = new EndlessRecyclerViewScrollListener(mLayoutManager) {

            /*
              * onLoadMore method is implemented here which uses currentPage to load data for the next page
              * @param currentPage is very important here. The api uses current page parameter to load pages
                from the database.
              * When the threshold is reached in the onScrolled method of the abstract class we increment
                the currentPage number and pass it to the onLoadMore method
             */

            @Override
            public void onLoadMore(int currentPage, int totalItemCount, RecyclerView recyclerView) {

                loadNextDataFromApi(currentPage);

            }
        };

        //adding the onScrollListener for the recycler view

        mRecyclerView.addOnScrollListener(scrollListener);

        scrollListener.resetState();


        mAuthStateListener = new FirebaseAuth.AuthStateListener(){
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if(user != null)
                {
                    //user is signed in
                    Toast.makeText(getContext(),"You are signed in", Toast.LENGTH_SHORT).show();
                    mDatabaseReference.child(user.getUid()).child("name").setValue(user.getDisplayName());
                    mDatabaseReference.child(user.getUid()).child("email").setValue(user.getEmail());
                    //mDatabaseReference.child(user.getUid()).child("bookmarks").push();
                }
                else {
                    //user is signed out
                    startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setIsSmartLockEnabled(false)
                                    .setProviders(
                                            AuthUI.GOOGLE_PROVIDER,
                                            AuthUI.EMAIL_PROVIDER)
                                    .build(),
                            RC_SIGN_IN);
                }

            }
        };

        return rootView;
    }

    @Override
    public void onClick(TheArticle article) {

        String uriString = article.getmDetailPageLink();

        Uri uri = Uri.parse(uriString);
        CustomTabsIntent.Builder intentBuilder = new CustomTabsIntent.Builder();

        intentBuilder.setToolbarColor(ContextCompat.getColor(getContext(),R.color.colorPrimary));
        intentBuilder.setSecondaryToolbarColor(ContextCompat.getColor(getContext(),R.color.colorPrimaryDark));

        intentBuilder.setExitAnimations(getContext(), R.anim.right_to_left_end,
                R.anim.left_to_right_end);
        intentBuilder.setStartAnimations(getContext(), R.anim.left_to_right_start,
                R.anim.right_to_left_start);

        CustomTabsIntent customTabsIntent = intentBuilder.build();

        customTabsIntent.launchUrl(getActivity(),uri);
    }


    @Override
    public void onBookmarkClick(TheArticle article) {

       boolean isBookmarked = NewsPreferences.getmBookmarkIds().containsKey(article.getmId());

        /* if(!isBookmarked) {
            ContentValues values = new ContentValues();

            values.put(NewsContract.NewsEntry.COLUMN_NAME, article.getmAvatarName());
            values.put(NewsContract.NewsEntry.COLUMN_DATE, article.getmDate());
            values.put(NewsContract.NewsEntry.IMAGE_URL, article.getmImageURL());
            values.put(NewsContract.NewsEntry.TITLE, article.getmTheTitle());
            values.put(NewsContract.NewsEntry.ShortDescription, article.getmTheThreeLines());
            values.put(NewsContract.NewsEntry.DetailPageLink, article.getmDetailPageLink());
            values.put(NewsContract.NewsEntry.linkId,article.getmId());

            Uri insertedUri = this.getContentResolver().insert(NewsContract.NewsEntry.CONTENT_URI, values);

            Log.v(LOG_TAG, insertedUri + "");

            findViewById(R.id.bookmark_image).setBackgroundResource(R.drawable.bookmark);

            NewsPreferences.saveBookmark(article.getmId(),this);

            if (insertedUri != null) {
                Toast.makeText(this, "Bookmarked", Toast.LENGTH_SHORT).show();
            }
        }
        else
        {
            String[] selectionArgs = new String[]{article.getmId()};
            //delete the bookmark

            int numOfRowsDeleted = this.getContentResolver().delete(NewsContract.NewsEntry.CONTENT_URI,
                    NewsContract.NewsEntry.linkId + "=?",selectionArgs);
            if(numOfRowsDeleted > 0) {
                NewsPreferences.removeBookmark(article.getmId(),this);
                findViewById(R.id.bookmark_image).setBackgroundResource(R.drawable.bookmark_outline);
                Toast.makeText(this, "Bookmark Removed", Toast.LENGTH_SHORT).show();
            }
        }

       */

        if(!isBookmarked) {
            mDatabaseReference.child(mFirebaseAuth.getCurrentUser().getUid())

                    .child("bookmarks")
                    .push()
                    .setValue(article);

            mDatabaseReference.child(mFirebaseAuth.getCurrentUser().getUid())
                    .child("bookmarkIds")
                    .push()
                    .setValue(article.getmId());

            Toast.makeText(getContext(), "Bookmarked", Toast.LENGTH_SHORT).show();
        }
        else
        {
           mDatabaseReference.child(mFirebaseAuth.getCurrentUser().getUid())
                   .child("bookmarkIds")
                   .child(NewsPreferences.getmBookmarkIds().get(article.getmId()))
                   .removeValue();
            NewsPreferences.getmBookmarkIds().remove(article.getmId());

            getView().findViewById(R.id.bookmark_image).setBackgroundResource(R.drawable.bookmark_outline);

            Toast.makeText(getContext(), "Bookmark removed", Toast.LENGTH_SHORT).show();

        }

    }

    @Override
    public void onShareClick(TheArticle article) {

        Intent shareIntent = ShareCompat.IntentBuilder.from(getActivity())
                .setType("text/plain")
                .setText(article.getmDetailPageLink())
                .getIntent();
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);

        startActivity(shareIntent);

    }




    public void fetchDataAfterSwipeUp(int currentPage)
    {
        mAdapter.clear();

        page =  currentPage + "";
        //Log.v(LOG_TAG,"PAGE VALUE IN THE load.. function " + page);
        getLoaderManager().destroyLoader(EARTHQUAKE_LOADER_ID);
        getLoaderManager().restartLoader(EARTHQUAKE_LOADER_ID, null, this);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mSwipeContainer.setRefreshing(false);
            }
        }, 5000);

    }

    /*
      * here in this method we finally take the currentPage value and the update the page string
      * so before the loader is destroyed and restarted the page variable is updated and the next page
      * is loaded
     */
    public void loadNextDataFromApi(int currentPage) {
        page = currentPage + "";
        //Log.v(LOG_TAG,"PAGE VALUE IN THE load.. function " + page);
        getLoaderManager().destroyLoader(EARTHQUAKE_LOADER_ID);
        getLoaderManager().restartLoader(EARTHQUAKE_LOADER_ID, null, this);
    }

    @Override
    public void onPause() {
        super.onPause();
        mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
    }

    @Override
    public void onResume() {
        super.onResume();
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }


}

