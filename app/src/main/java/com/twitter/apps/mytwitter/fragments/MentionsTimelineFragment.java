package com.twitter.apps.mytwitter.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.twitter.apps.mytwitter.MyTwitterApplication;
import com.twitter.apps.mytwitter.models.Tweet;
import com.twitter.apps.mytwitter.models.User;
import com.twitter.apps.mytwitter.serviceclient.TwitterClient;
import cz.msebera.android.httpclient.Header;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by mdathrika on 11/4/16.
 */
public class MentionsTimelineFragment extends TweetsListFragment {
    private TwitterClient client;
    private User myDetails;
    Timeline timeline;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        timeline = (Timeline)getActivity();

        client = MyTwitterApplication.getRestClient();

        populateMentionsTimeline(null, false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    public void populateMentionsTimeline(String maxId, final boolean append) {
        client.getMentionsTimeline(maxId, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                if(!append)
                    clearTweets();

                addAll(Tweet.fromJSONArray(response));
                timeline.doneRefreshing();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                throwable.printStackTrace();

                timeline.doneRefreshing();
            }
        });
    }

    public void refresh() {
        populateMentionsTimeline(null, false);
    }
}
