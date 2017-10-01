package com.example.android.newsapp;

public class News {

    private String mSectionName;
    private String mWebTitle;
    private String mUrl;


    public News(String sectionName, String webTitle, String url) {
        mSectionName = sectionName;
        mWebTitle = webTitle;
        mUrl = url;
    }

    public String getmSectionName() {
        return mSectionName;
    }

    public String getmWebTitle() {
        return mWebTitle;
    }

    public String getmUrl() {
        return mUrl;
    }

}


