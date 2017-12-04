package com.example.personal.newsfeeder.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.example.personal.newsfeeder.R;

public class SectionActivity extends AppCompatActivity {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_section);

        initToolbar();

        Intent intent = getIntent();
        String sectionTitle = intent.getStringExtra("Section Type");

        TextView title = (TextView)findViewById(R.id.toolbar_title);

        title.setText(sectionTitle);


    }
}
