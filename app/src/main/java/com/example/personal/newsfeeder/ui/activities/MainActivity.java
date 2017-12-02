package com.example.personal.newsfeeder.ui.activities;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.personal.newsfeeder.R;
import com.example.personal.newsfeeder.ui.fragments.BookmarkFragment;
import com.example.personal.newsfeeder.ui.fragments.MainActivityFragment;
import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static com.firebase.ui.auth.ui.AcquireEmailHelper.RC_SIGN_IN;


/*
 * The main activity implements the LoaderManager to handle all the loading of data off the main thread
 * we also implement ListItemOnClickHandler to handle the clicks on the article
*/
public class MainActivity extends AppCompatActivity {


    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    ValueEventListener mValueEventListener;

    private FirebaseAuth.AuthStateListener mAuthStateListener;

    private FirebaseAuth mFirebaseAuth;

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;


    private DrawerLayout mDrawer;
    private NavigationView navigationView;
    private View content;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recycler_view);

        content = findViewById(R.id.content);

        mFirebaseAuth = FirebaseAuth.getInstance();

        mFirebaseDatabase = FirebaseDatabase.getInstance();

        mDatabaseReference = mFirebaseDatabase.getReference().child("users");

        //this function sets up the toolbar
        initToolbar();

        navigationView = (NavigationView) findViewById(R.id.navigation_view);

        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);





        //function to setup the drawer layout.
        setupDrawerLayout(navigationView);

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

        /*
         * The main content where the cardview's are displayed is a fragment.
         * Based on what is selected from the navigation drawer the fragment is replaced.
         * By default the news articles list is displayed. That is done in the below code.
         */

        //getting a class object of the MainActivityFragment class.
        Class fragmentClass = MainActivityFragment.class;

        //initializing the fragment to null
        Fragment fragment = null;

        try {
            //initializing the fragment to the fragment class i.e. MainActivityFragment class.
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        //the usual stuff goes here. Getting the fragment manager.
        FragmentManager fragmentManager = getSupportFragmentManager();

        //replacing the main content view with the above fragment.
        fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();

    }


    /*
     * this function here sets listeners on the navigation view's items.
     * As any of the items in the navigation is selected this functions is called
     * The call lands up in onNavigationItemSelected which in turn calls selectDrawerItem()
     */

    private void setupDrawerLayout(NavigationView navigationView) {

        //setup listener on the navigationView
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {

                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {

                        selectDrawerItem(menuItem);
                        return true;
                    }
                }
        );
    }

    /*
     * this method replaces the main content view with the appropriate fragment depending on the
     * navigation item selected.
     */

    private void selectDrawerItem(MenuItem menuItem) {

        //initializing the fragment to null
        Fragment fragment = null;

        //creating a class object
        Class fragmentClass;

        //using switch cases to find which item was selected and initializing the fragment class accordingly
        switch (menuItem.getItemId()) {
            case R.id.drawer_home:
                fragmentClass = MainActivityFragment.class;
                break;
            case R.id.drawer_favourite:


                fragmentClass = BookmarkFragment.class;
                break;
            default:
                fragmentClass = MainActivityFragment.class;

        }

        //initializing the fragment
        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        //now here just replacing the fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();


        //after the item is selected

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

    private void initToolbar() {
        //find the toolbar in the recycler_view.xml file.
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_main);
        //set that toolbar as action bar
        setSupportActionBar(toolbar);

        //removing the defauld title of the action bar
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);

        if (actionBar != null) {

            //enabling the hamburger icon to be displayed
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);

            //make home return up to the next level in the UI rather than the top level.
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
