package com.example.personal.newsfeeder.ui.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.customtabs.CustomTabsIntent;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.webkit.WebView;
import android.widget.TextView;

import com.example.personal.newsfeeder.R;

public class DetailActivity extends AppCompatActivity {

    private TextView avatarNameTv, avatarSubTv, avatarImage;
    WebView mWebView;
    private String  detailPageUrl;
    private static final String LOG_TAG = DetailActivity.class.getSimpleName();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        avatarNameTv = (TextView) findViewById(R.id.avatar_name);
        avatarSubTv = (TextView) findViewById(R.id.Date);
        mWebView = (WebView) findViewById(R.id.webView);
        avatarImage = (TextView) findViewById(R.id.avatar_image);


        Intent intentThatStartedThisActivity = getIntent();

        if (intentThatStartedThisActivity != null) {
            if (intentThatStartedThisActivity.hasExtra(getString(R.string.putExtra_avatar_name)) &&
                    intentThatStartedThisActivity.hasExtra(getString(R.string.putExtra_avatar_sub)) &&
                    intentThatStartedThisActivity.hasExtra(getString(R.string.detailActivityIntent))) {
                avatarNameTv.setText(intentThatStartedThisActivity.
                        getStringExtra(getString(R.string.putExtra_avatar_name)));
                avatarNameTv.setTextColor(ContextCompat.getColor(this, R.color.white));

                avatarSubTv.setText(intentThatStartedThisActivity.
                        getStringExtra(getString(R.string.putExtra_avatar_sub)));
                avatarSubTv.setTextColor(ContextCompat.getColor(this, R.color.white));

                avatarImage.setText(intentThatStartedThisActivity.
                        getStringExtra(getString(R.string.putExtra_name_initial_letter)));

                detailPageUrl = intentThatStartedThisActivity.getStringExtra(getString(R.string.detailActivityIntent));

                Log.v(LOG_TAG, "The link to url is " + detailPageUrl);

                Uri uri = Uri.parse(detailPageUrl);

                CustomTabsIntent.Builder intentBuilder = new CustomTabsIntent.Builder();

                intentBuilder.setToolbarColor(ContextCompat.getColor(this,R.color.colorPrimary));
                intentBuilder.setSecondaryToolbarColor(ContextCompat.getColor(this,R.color.colorPrimaryDark));

                intentBuilder.setStartAnimations(this,android.R.anim.slide_out_right,
                        android.R.anim.slide_in_left);
                intentBuilder.setExitAnimations(this,android.R.anim.slide_in_left,
                        android.R.anim.slide_out_right);

                CustomTabsIntent customTabsIntent = intentBuilder.build();

                customTabsIntent.launchUrl(this,uri);


            }

            /*
             * using the app Toolbar view.
             * App Toolbar makes it easy to customise the action bar.
             * Here we set the Toolbar to act as action bar
             */

            //find the toolbar inside the activity_detail
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_detail);

            if (toolbar != null) {
                //set the toolbar to act as action bar for this activity
                setSupportActionBar(toolbar);
            }

            //setting the back button on the action bar
            // see this for reference https://stackoverflow.com/questions/26651602/display-back-arrow-on-toolbar-android
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

        @Override
        public boolean onCreateOptionsMenu (Menu menu){
            getMenuInflater().inflate(R.menu.menu_detail, menu);
            return true;
        }
    }

