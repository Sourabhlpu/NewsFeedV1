package com.example.personal.newsfeeder;


import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.customtabs.CustomTabsIntent;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.personal.newsfeeder.utilities.NetworkUtils;
import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;



/*
 * The main activity implements the LoaderManager to handle all the loading of data off the main thread
 * we also implement ListItemOnClickHandler to handle the clicks on the article
*/
public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<TheArticle>>,
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
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private View content;

    //Firebase
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;


    private  String page = "1";

    /*
      * this method will build our string to query for the right data
      * it will return the query String.
      * @param currentPage is passed in which is the page #.
     */



    //Overriding the three loader methods

    /*
     * this method is called when the loader is created.
     * it uses the createAPIQueryString to create a query for the api
     * the it passes that string with the context to the ArticleLoader object
     */

    @Override
    public Loader<List<TheArticle>> onCreateLoader(int id, Bundle args) {


        URL queryUrl = NetworkUtils.createAPIQueryString(page,this);


        return new ArticleLoader(this, queryUrl);
    }

    /*
     * this method is called when the loading of data is done
     * this method does all the work of updating the UI
     * updating of UI happens via updating the adapter.
     */

    @Override
    public void onLoadFinished(Loader<List<TheArticle>> loader, List<TheArticle> articles) {

        //make the progress bar invisible as the data load has finished
        findViewById(R.id.progress_bar).setVisibility(RecyclerView.GONE);

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
            findViewById(R.id.progress_bar).setVisibility(RecyclerView.GONE);
        }

    }

    // for now we don't do anything with this method

    @Override
    public void onLoaderReset(Loader<List<TheArticle>> loader) {


    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recycler_view);

        //initializing the firebase auth variable
        mFirebaseAuth = FirebaseAuth.getInstance();

        //initializing the firebase realtime database
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference().child("users");

        //mEmptyTextView is used to display an error message when we cannot load the data
        mEmptyTextView = (TextView) findViewById(R.id.empty_text_view);

        //this is the recycler view that displays the data in the list
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);


        //check if we are connected to the internet
        ConnectivityManager cm = (ConnectivityManager) this.getSystemService(this.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();

        //if we are connected then start the loader
        if (info != null && info.isConnected()) {
            LoaderManager loaderManager = getLoaderManager();
            loaderManager.initLoader(EARTHQUAKE_LOADER_ID, null, this);

        }
        // otherwise hide the progress bar and show the appropriate error message
        else {
            findViewById(R.id.progress_bar).setVisibility(RecyclerView.GONE);
            mEmptyTextView.setText("No internet connection");
        }


        mRecyclerView.setHasFixedSize(true);

        mEmptyTextView = (TextView) findViewById(R.id.empty_text_view);


        mLayoutManager = new LinearLayoutManager(this);

        mRecyclerView.setLayoutManager(mLayoutManager);

        mSwipeContainer = (SwipeRefreshLayout)findViewById(R.id.swipeContainer);

        mSwipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                fetchDataAfterSwipeUp(1);

            }
        });

        mSwipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);




        /*
         * initializing the adapter
         * the first param takes the context of this activity to pass the context
         * the second param is passed the an article list object to initialize the list in the adapter
         * the third param is passed this coz this activity implements the onClick method of the
         * listItemOnclickHandler abstract class.
         *
         */
        mAdapter = new RVAdapter(this, new ArrayList<TheArticle>(), this, this,this);
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



        content = findViewById(R.id.content);


        initToolbar();
        setupDrawerLayout();

        mAuthStateListener = new FirebaseAuth.AuthStateListener(){
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if(user != null)
                {
                    //user is signed in
                    Toast.makeText(MainActivity.this,"You are signed in", Toast.LENGTH_SHORT).show();
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


    }

    private void setupDrawerLayout()
    {
        drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        navigationView = (NavigationView)findViewById(R.id.navigation_view);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                Snackbar.make(content, item.getTitle() + " pressed", Snackbar.LENGTH_LONG).show();
                item.setChecked(true);
                drawerLayout.closeDrawers();
                return true;
            }
        });

    }

    private void initToolbar()
    {
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);

        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
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

    /*
     * this method creates the menu for our main activity
     */

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    /*
     * we use this method to open the settings activity using intents
     */

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
            case R.id.settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return true;
            case R.id.signout:
                AuthUI.getInstance().signOut(this);
        }

        return super.onOptionsItemSelected(item);

        }


    /*
    * this is the method that main activity overrides to implement onClick
    * of the ListItemOnClickHandler interface from the adapter.
    *  We use intent to open the detail activity and pass the details of the article with it
     */

    @Override
    public void onClick(TheArticle article) {

        String uriString = article.getmDetailPageLink();

        Uri uri = Uri.parse(uriString);
        CustomTabsIntent.Builder intentBuilder = new CustomTabsIntent.Builder();

        intentBuilder.setToolbarColor(ContextCompat.getColor(this,R.color.colorPrimary));
        intentBuilder.setSecondaryToolbarColor(ContextCompat.getColor(this,R.color.colorPrimaryDark));

        intentBuilder.setExitAnimations(this, R.anim.right_to_left_end,
                R.anim.left_to_right_end);
        intentBuilder.setStartAnimations(this, R.anim.left_to_right_start,
                R.anim.right_to_left_start);

        CustomTabsIntent customTabsIntent = intentBuilder.build();

        customTabsIntent.launchUrl(this,uri);
    }

    @Override
    public void onBookmarkClick(TheArticle article) {

       /* boolean isBookmarked = NewsPreferences.isBookmarked(article.getmId(),this);

        if(!isBookmarked) {
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
      mDatabaseReference.child(mFirebaseAuth.getCurrentUser().getUid())

              .child("bookmarks")
              .push()
              .setValue(article);

    }

    @Override
    public void onShareClick(TheArticle article) {

        Intent shareIntent = ShareCompat.IntentBuilder.from(this)
                .setType("text/plain")
                .setText(article.getmDetailPageLink())
                .getIntent();
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);

        startActivity(shareIntent);

    }

    @Override
    protected void onPause() {
        super.onPause();
        mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }
}
