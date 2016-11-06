package com.twitter.apps.mytwitter.activities;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.twitter.apps.mytwitter.R;
import com.twitter.apps.mytwitter.fragments.Profile;
import com.twitter.apps.mytwitter.fragments.TweetsListFragment;
import com.twitter.apps.mytwitter.models.User;
import com.twitter.apps.mytwitter.serviceclient.TwitterClient;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * Created by mdathrika on 11/5/16.
 */

public abstract class BaseActivity extends AppCompatActivity {

    TwitterClient client;
    User myDetails;

    private SwipeRefreshLayout swipeContainer;

    public void getMyDetails() {
        client.getUserDetails(new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                myDetails = User.fromJSON(response);

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                throwable.printStackTrace();
            }
        });
    }


}
