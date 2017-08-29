package com.example.personal.newsfeeder;

/**
 * Created by personal on 4/11/2017.
 */

public class Section {

    private int mImageSource;
    private String mSectionName;

    public Section(int imageSource, String sectionName)
    {
        mImageSource = imageSource;
        mSectionName = sectionName;
    }

    public int getmImageSource()
    {
        return mImageSource;
    }

    public String getmSectionName()
    {
        return mSectionName;
    }
}
