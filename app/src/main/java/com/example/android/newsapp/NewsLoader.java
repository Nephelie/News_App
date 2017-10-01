package com.example.android.newsapp;


import android.content.AsyncTaskLoader;
import android.content.Context;

import java.util.List;

public class NewsLoader extends AsyncTaskLoader<List<News>> {

    private String mQuery;

    public NewsLoader(Context context, String query) {
        super(context);
        mQuery = query;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public List<News> loadInBackground() {
        if (mQuery == null) {
            return null;
        }

        List<News> news = QueryUtils.fetchData(mQuery);
        return news;
    }
}

