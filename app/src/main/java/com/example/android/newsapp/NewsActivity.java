package com.example.android.newsapp;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import static com.example.android.newsapp.R.id.search;

public class NewsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<News>> {


    private SearchView mSearch;

    private String mQuery;

    private NewsAdapter mAdapter;

    private TextView mEmptyTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.news_activity);

        ListView newsListView = (ListView) findViewById(R.id.list);

        mEmptyTextView = (TextView) findViewById(R.id.empty_textView);
        newsListView.setEmptyView(mEmptyTextView);

        mAdapter = new NewsAdapter(this, new ArrayList<News>());
        newsListView.setAdapter(mAdapter);

        mSearch = (SearchView) findViewById(search);
        mSearch.setSubmitButtonEnabled(true);


        newsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                News currentNews = mAdapter.getItem(position);

                Uri newsUri = Uri.parse(currentNews.getmUrl());

                Intent webIntent = new Intent(Intent.ACTION_VIEW, newsUri);
                startActivity(webIntent);
            }

        });

        ConnectivityManager cm = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnectedOrConnecting()) {

            getLoaderManager().initLoader(0, null, this);
        } else {
            mEmptyTextView.setText("No internet connection");
            mEmptyTextView.setGravity(Gravity.CENTER);
        }

        mSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }

            @Override
            public boolean onQueryTextSubmit(String query) {

                ConnectivityManager cm = (ConnectivityManager)
                        getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = cm.getActiveNetworkInfo();

                if (networkInfo != null && networkInfo.isConnectedOrConnecting()) {

                    getLoaderManager().restartLoader(0, null, NewsActivity.this);

                    return true;

                } else {
                    mAdapter.clear();

                    mEmptyTextView.setText("No internet connection.");

                    return false;
                }
            }

        });
    }

    @Override
    public Loader<List<News>> onCreateLoader(int id, Bundle args) {
        mQuery = mSearch.getQuery().toString();

        NewsLoader loader = new NewsLoader(this, mQuery);
        return loader;
    }

    @Override
    public void onLoadFinished(Loader<List<News>> loader, List<News> news) {
        mAdapter.clear();

        if (news != null && !news.isEmpty()) {
            mAdapter.addAll(news);
        }

        mEmptyTextView.setText("No news found.");
        mEmptyTextView.setGravity(Gravity.CENTER);
    }

    @Override
    public void onLoaderReset(Loader<List<News>> loader) {
        mAdapter.clear();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("insert query", mQuery);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mQuery = savedInstanceState.getString("insert query");
    }
}
