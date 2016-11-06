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

public class UserTimelineFragment extends TweetsListFragment {

    Timeline timeline;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        timeline = (Timeline)getActivity();

        getUserTimeline(null, false);

        fetchUserDetails();
    }

    @Override
    public void refresh() {
        getUserTimeline(null, false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    private void getUserTimeline(String maxId, final boolean append) {
        String screenName = getArguments().getString("screen_name");
        final Profile profile = (Profile)getActivity();
        client.getUserTimeline(screenName, null, new JsonHttpResponseHandler(){
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

    protected void fetchUserDetails() {
        final Profile profile = (Profile)getActivity();
        String screenName = getArguments().getString("screen_name");
        client.findUser(screenName, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                List<User> user = User.fromJSONArray(response);
                if(user != null && user.size() > 0) {
                    profile.populateHeader(user.get(0));
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                throwable.printStackTrace();
            }
        });
    }

}
