package com.twitter.apps.mytwitter.activities;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.customtabs.CustomTabsIntent;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.twitter.apps.mytwitter.MyTwitterApplication;
import com.twitter.apps.mytwitter.R;
import com.twitter.apps.mytwitter.adapter.ItemClickSupport;
import com.twitter.apps.mytwitter.decoration.DividerItemDecoration;
import com.twitter.apps.mytwitter.decoration.SpacesItemDecoration;
import com.twitter.apps.mytwitter.dialogs.TweetDialog;
import com.twitter.apps.mytwitter.listeners.EndlessRecyclerViewScrollListener;
import com.twitter.apps.mytwitter.models.User;
import com.twitter.apps.mytwitter.serviceclient.TwitterClient;
import com.twitter.apps.mytwitter.adapter.TweetsArrayAdapter;
import com.twitter.apps.mytwitter.models.Tweet;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import cz.msebera.android.httpclient.Header;
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator;

/**
 * Created by mdathrika on 10/27/16.
 */
public class TimelineActivity extends AppCompatActivity {

    private TwitterClient client;
    private List<Tweet> tweets;
    private TweetsArrayAdapter adapter;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeContainer;
    final Activity activity = this;
    final Context context = this;

    private User myDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);
        recyclerView =  (RecyclerView) findViewById(R.id.rvtweets);

        tweets = new ArrayList<>();
        adapter = new TweetsArrayAdapter(this, tweets);

        client = MyTwitterApplication.getRestClient();

        //postTweet("Raining...!!");
        populateTimeline(null,false);

        Toolbar toolbar = (Toolbar) findViewById(R.id.timeline_toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.twitter);
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        //StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        //recyclerView.addItemDecoration(new SpacesItemDecoration(16));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));

        recyclerView.addOnScrollListener(new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                String maxId = Long.toString(tweets.get(tweets.size()-1).getUid());
                populateTimeline(maxId, true);
            }
        });

        recyclerView.setItemAnimator(new SlideInUpAnimator(new OvershootInterpolator(1f)));
        recyclerView.setAdapter(adapter);

        ItemClickSupport.addTo(recyclerView).setOnItemClickListener(
                new ItemClickSupport.OnItemClickListener() {
                    @Override
                    public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                        if(tweets.get(position).getUrl() == null || tweets.get(position).getUrl().length() == 0) {
                            return;
                        }

                        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                        builder.setToolbarColor(ContextCompat.getColor(activity, R.color.colorBG));

                        Intent intent = new Intent(Intent.ACTION_SEND);
                        intent.setType("text/plain");
                        intent.putExtra(Intent.EXTRA_TEXT, tweets.get(position).getUrl());

                        int requestCode = 100;

                        PendingIntent pendingIntent = PendingIntent.getActivity(context,
                                requestCode,
                                intent,
                                PendingIntent.FLAG_UPDATE_CURRENT);

                        //builder.setActionButton(bitmap, "Share Link", pendingIntent, true);

                        CustomTabsIntent customTabsIntent = builder.build();
                        customTabsIntent.launchUrl(activity, Uri.parse(tweets.get(position).getUrl()));
                    }
                }
        );

        attachSwipeRefresh();

        fetchUserDetails();

        Intent intent = getIntent();
        if(intent.getBooleanExtra("fromShare", false)) {
            String title = intent.getStringExtra("title");
            String urlOfPage = intent.getStringExtra("urlOfPage");

            showTweetDialog(title + " " + urlOfPage);
        }

    }

    private void attachSwipeRefresh() {
        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);

        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                populateTimeline(null,false);
            }
        });

        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
    }

    private void populateTimeline(String maxId, final boolean append) {
        client.getHomeTimeline(maxId, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                System.out.println("**********"+response);
                if(!append)
                    tweets.clear();
                System.out.println(response);
                tweets.addAll(Tweet.fromJSONArray(response));
                adapter.notifyDataSetChanged();

                swipeContainer.setRefreshing(false);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                throwable.printStackTrace();

                swipeContainer.setRefreshing(false);
            }
        });
    }

    private void postTweet(String tweet) {
        client.postTweet(tweet, null, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_timeline, menu);
        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_tweet) {
            showTweetDialog(null);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showTweetDialog(String tweetFromExternal) {
        FragmentManager fm = getSupportFragmentManager();
        TweetDialog tweetDialog = new TweetDialog();
        Bundle bundle = new Bundle();
        bundle.putParcelable("user", myDetails);
        if(tweetFromExternal != null) {
            bundle.putString("tweet", tweetFromExternal);
        }

        tweetDialog.setArguments(bundle);
        tweetDialog.show(fm, "dialog_tweet");
    }

    private void fetchUserDetails() {
        client.getUserDetails(new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                System.out.println("*************fetchUserDetails**************"+response);
                myDetails = User.fromJSON(response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                System.out.println("*************fetchUserDetails**************");
                throwable.printStackTrace();
            }
        });
    }
}