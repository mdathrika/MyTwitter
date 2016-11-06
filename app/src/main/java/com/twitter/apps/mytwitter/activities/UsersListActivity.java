package com.twitter.apps.mytwitter.activities;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.animation.OvershootInterpolator;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.twitter.apps.mytwitter.MyTwitterApplication;
import com.twitter.apps.mytwitter.R;
import com.twitter.apps.mytwitter.adapter.TweetsArrayAdapter;
import com.twitter.apps.mytwitter.adapter.UsersArrayAdapter;
import com.twitter.apps.mytwitter.decoration.DividerItemDecoration;
import com.twitter.apps.mytwitter.fragments.Profile;
import com.twitter.apps.mytwitter.fragments.TweetsListFragment;
import com.twitter.apps.mytwitter.listeners.EndlessRecyclerViewScrollListener;
import com.twitter.apps.mytwitter.models.Tweet;
import com.twitter.apps.mytwitter.models.User;
import com.twitter.apps.mytwitter.serviceclient.TwitterClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator;

public class UsersListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeContainer;
    TwitterClient client;
    private UsersArrayAdapter adapter;
    List<User> users;
    String nextCursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.timeline_toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.twitter);
        getSupportActionBar().setDisplayUseLogoEnabled(true);


        users = new ArrayList<>();
        adapter = new UsersArrayAdapter(this, users);

        client = MyTwitterApplication.getRestClient();

        recyclerView =  (RecyclerView) findViewById(R.id.rvUsers);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);

        recyclerView.setLayoutManager(layoutManager);

        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL_LIST));

        recyclerView.addOnScrollListener(new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                String maxId = Long.toString(users.get(users.size()-1).getUid());
                //me.populateTimeline(maxId, true);
            }
        });

        recyclerView.setItemAnimator(new SlideInUpAnimator(new OvershootInterpolator(1f)));
        recyclerView.setAdapter(adapter);

        String screenName = getIntent().getStringExtra("screen_name");
        String actionType = getIntent().getStringExtra("action_type");

        getSupportActionBar().setTitle(screenName +" " + actionType);

        if("Followers".equals(actionType)) {
            getFollowers(screenName, null, false);
        } else {
            getFollowing(screenName, null, false);
        }

        attachSwipeRefresh();
    }

    public Context getContext() {
        return this;
    }

    public void attachSwipeRefresh() {

        final String screenName = getIntent().getStringExtra("screen_name");
        final String actionType = getIntent().getStringExtra("action_type");

        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);

        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                if("Followers".equals(actionType)) {
                    getFollowers(screenName, nextCursor, true);
                } else {
                    getFollowing(screenName, nextCursor, true);
                }
                //TweetsListFragment fragment = (TweetsListFragment)pagerAdapter.getRegisteredFragment(viewPager.getCurrentItem());
                //fragment.refresh();
            }
        });

        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
    }

    private void getFollowers(String screenName, String nextPage, final boolean append) {
        client.getFollowersList(screenName, nextPage, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                if(!append)
                    clearTweets();

                try {
                    JSONArray usersArray =  response.getJSONArray("users");
                    nextCursor =  response.getString("next_cursor_str");
                    addAll(User.fromJSONArray(usersArray));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                doneRefreshing();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                doneRefreshing();
            }
        });
    }

    private void getFollowing(String screenName, String nextPage, final boolean append) {
        client.getFriendsList(screenName, nextPage, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                if(!append)
                    clearTweets();

                try {
                    JSONArray usersArray =  response.getJSONArray("users");
                    nextCursor =  response.getString("next_cursor_str");
                    addAll(User.fromJSONArray(usersArray));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                doneRefreshing();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                doneRefreshing();
            }
        });
    }

    public void addAll(List<User> users) {
        this.users.addAll(users);
        adapter.notifyDataSetChanged();
    }

    public void clearTweets() {
        this.users.clear();
    }

    public void doneRefreshing() {
        swipeContainer.setRefreshing(false);
    }
}
