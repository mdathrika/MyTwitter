package com.twitter.apps.mytwitter.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.twitter.apps.mytwitter.models.Tweet;
import com.twitter.apps.mytwitter.models.User;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

import cz.msebera.android.httpclient.Header;



/**
 * Created by mdathrika on 11/4/16.
 */

public class UserFavoritesFragment extends TweetsListFragment {

    Timeline timeline;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        timeline = (Timeline)getActivity();

        getUserFavorites(null, false);

    }

    @Override
    public void refresh() {
        getUserFavorites(null, false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    private void getUserFavorites(String maxId, final boolean append) {
        String screenName = getArguments().getString("screen_name");
        final Profile profile = (Profile)getActivity();
        client.getFavoritesList(screenName, null, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                if(!append)
                    clearTweets();

                addAll(Tweet.fromJSONArray(response));

                timeline.doneRefreshing();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                timeline.doneRefreshing();
            }
        });
    }


}
