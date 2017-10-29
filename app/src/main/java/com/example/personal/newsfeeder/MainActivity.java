package com.example.personal.newsfeeder;


import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.personal.newsfeeder.data.NewsPreferences;
import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;


/*
 * The main activity implements the LoaderManager to handle all the loading of data off the main thread
 * we also implement ListItemOnClickHandler to handle the clicks on the article
*/
public class MainActivity extends AppCompatActivity  {



    private static final String LOG_TAG = MainActivity.class.getSimpleName();




    private DrawerLayout mDrawer;
    private NavigationView navigationView;
    private View content;


    //Firebase
    private FirebaseAuth mFirebaseAuth;

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;

    private ArrayList<TheArticle> mBookmarks;

    private HashMap<String, String> mBookmarkIds;



    public ArrayList<TheArticle> getmBookmarks()
    {
        return mBookmarks;
    }




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recycler_view);



        mBookmarks = new ArrayList<TheArticle>();

        mBookmarkIds = new HashMap<>();


        //initializing the firebase auth variable
        mFirebaseAuth = FirebaseAuth.getInstance();
        //initializing the firebase realtime database
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference().child("users");

        content = findViewById(R.id.content);

        mDatabaseReference.child(mFirebaseAuth.getCurrentUser().getUid())
                .child("bookmarkIds").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot bookmarkIdSnapshot : dataSnapshot.getChildren())
                {
                    mBookmarkIds.put(bookmarkIdSnapshot.getValue().toString(), bookmarkIdSnapshot.getKey());
                }

                Log.v(LOG_TAG, "the bookmark ids are " + mBookmarkIds);
                NewsPreferences.setmBookmarkIds(mBookmarkIds);

                Log.v(LOG_TAG, "the bookmark ids in the hashmap are " + NewsPreferences.getmBookmarkIds());

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        initToolbar();

        navigationView = (NavigationView)findViewById(R.id.navigation_view);

        mDrawer = (DrawerLayout)findViewById(R.id.drawer_layout);

        setupDrawerLayout(navigationView);

        Class fragmentClass = MainActivityFragment.class;
        Fragment fragment = null;

        try{
            fragment = (Fragment)fragmentClass.newInstance();
        }catch (Exception e)
        {
            e.printStackTrace();
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.flContent,fragment).commit();

    }

    private void setupDrawerLayout(NavigationView navigationView)
    {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener(){

                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {

                        selectDrawerItem(menuItem);
                        return true;
                    }
                }
        );
    }

    private void selectDrawerItem(MenuItem menuItem)
    {
        Fragment fragment = null;
        Class fragmentClass;
        switch (menuItem.getItemId())
        {
            case R.id.drawer_home :
                fragmentClass = MainActivityFragment.class;
                break;
            case R.id.drawer_favourite :



                fragmentClass = BookmarkFragment.class;
                break;
            default:
                fragmentClass = MainActivityFragment.class;

        }

        try{
            fragment = (Fragment)fragmentClass.newInstance();
        }catch(Exception e)
        {
            e.printStackTrace();
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.flContent,fragment).commit();

        menuItem.setChecked(true);

        setTitle(menuItem.getTitle());

        mDrawer.closeDrawers();
    }

    /*private void setupDrawerLayout(NavigationView navigationView)
    {


        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                if(item.getTitle() == getResources().getString(R.string.favourite))
                {
                   mDatabaseReference.child(mFirebaseAuth.getCurrentUser().getUid())
                           .child("bookmarks")
                           .addListenerForSingleValueEvent(new ValueEventListener() {
                               @Override
                               public void onDataChange(DataSnapshot dataSnapshot) {
                                   for(DataSnapshot bookmarkSnapShot : dataSnapshot.getChildren()) {
                                       TheArticle bookmark = bookmarkSnapShot.getValue(TheArticle.class);
                                       mBookmarks.add(bookmark);
                                       //mAdapter.replaceAll(mBookmarks);
                                       Log.v(LOG_TAG, "the retrieved bookmark object is " + bookmark);
                                   }
                               }

                               @Override
                               public void onCancelled(DatabaseError databaseError) {

                               }
                           });

                }
                Snackbar.make(content, item.getTitle() + " pressed", Snackbar.LENGTH_LONG).show();
                item.setChecked(true);
                drawerLayout.closeDrawers();
                return true;
            }
        });

    }*/

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
                mDrawer.openDrawer(GravityCompat.START);
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

}
