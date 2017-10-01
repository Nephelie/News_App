package com.example.android.newsapp;


import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class QueryUtils {

    private static final String LOG_TAG = QueryUtils.class.getSimpleName();

    private QueryUtils() {
        //An empty private constructor makes sure that the class is not going to be initialised.
    }

    public static final List<News> fetchData(String query) {
        URL url = createUrl(query);

        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }

        List<News> news = extractFromJson(jsonResponse);

        return news;
    }

    private static URL createUrl(String query) {
        URL url = null;
        try {
            url = new URL("https://content.guardianapis.com/search?q=debates&api-key=test&q=" + query);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Problem building the URL ", e);
        }
        return url;

    }

    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream stream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(15000);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            if (urlConnection.getResponseCode() == 200) {
                stream = urlConnection.getInputStream();
                jsonResponse = readFromStream(stream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (stream != null) {

                stream.close();
            }
        }
        return jsonResponse;
    }

    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder builder = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                builder.append(line);
                line = reader.readLine();
            }
        }
        return builder.toString();
    }

    private static List<News> extractFromJson(String responseJSON) {
        if (TextUtils.isEmpty(responseJSON)) {
            return null;
        }
        List<News> news = new ArrayList<>();
        try {
            JSONObject results;
            JSONObject baseJsonResponse = new JSONObject(responseJSON);
            JSONObject jResults = baseJsonResponse.getJSONObject("response");

            if (jResults.has("results")) {
                JSONArray newsArray = jResults.getJSONArray("results");

                for (int i = 0; i < newsArray.length(); i++) {
                    results = newsArray.getJSONObject(i);

                    String sectionName;
                    if (results.has("sectionName")) {
                        sectionName = results.getString("sectionName");
                    } else {
                        sectionName = "Random Section";
                    }

                    String webTitle;
                    if (results.has("webTitle")) {
                        webTitle = results.getString("webTitle");
                    } else {
                        webTitle = "Title N/A";
                    }

                    String url = results.getString("webUrl");

                    News news1 = new News(sectionName, webTitle, url);
                    news.add(news1);
                }
            }


        } catch (JSONException e) {
            Log.e(LOG_TAG, "Can't extract the data from JSON response", e);
        }
        return news;
    }
}
