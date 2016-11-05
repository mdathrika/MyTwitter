package com.twitter.apps.mytwitter.activities;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.customtabs.CustomTabsIntent;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
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
import com.astuetz.PagerSlidingTabStrip;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.twitter.apps.mytwitter.MyTwitterApplication;
import com.twitter.apps.mytwitter.R;
import com.twitter.apps.mytwitter.TweetDetailsActivity;
import com.twitter.apps.mytwitter.adapter.ItemClickSupport;
import com.twitter.apps.mytwitter.adapter.SmartFragmentStatePagerAdapter;
import com.twitter.apps.mytwitter.decoration.DividerItemDecoration;
import com.twitter.apps.mytwitter.decoration.SpacesItemDecoration;
import com.twitter.apps.mytwitter.dialogs.TweetDialog;
import com.twitter.apps.mytwitter.fragments.HomeTimelineFragment;
import com.twitter.apps.mytwitter.fragments.MentionsTimelineFragment;
import com.twitter.apps.mytwitter.fragments.Timeline;
import com.twitter.apps.mytwitter.fragments.TweetsListFragment;
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
public class TimelineActivity extends AppCompatActivity implements Timeline {

    private TwitterClient client;
    private SwipeRefreshLayout swipeContainer;
    final Activity activity = this;
    final Context context = this;
    MentionsTimelineFragment fragment;

    private User myDetails;
    ViewPager viewPager;
    TweetsPagerAdapter pagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        Toolbar toolbar = (Toolbar) findViewById(R.id.timeline_toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.twitter);
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        viewPager = (ViewPager)findViewById(R.id.viewpager);
        pagerAdapter = new TweetsPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);

        PagerSlidingTabStrip tabsStrip = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        // Attach the view pager to the tab strip
        tabsStrip.setViewPager(viewPager);

        //fragment = (MentionsTimelineFragment)getSupportFragmentManager().findFragmentById(R.id.fragment_tweets);

        attachSwipeRefresh();

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
                TweetsListFragment fragment = (TweetsListFragment)pagerAdapter.getRegisteredFragment(viewPager.getCurrentItem());
                fragment.refresh();
            }
        });

        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
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

    public void onProfileView(MenuItem view) {
        Intent intent = new Intent(this, ProfileActivity.class);
        startActivity(intent);
    }

    public void doneRefreshing() {
        swipeContainer.setRefreshing(false);
    }

    public class TweetsPagerAdapter extends SmartFragmentStatePagerAdapter {
        private String tabTitles[] = {"Home", "Mentions"};

        public TweetsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if(position == 0) {
                return new HomeTimelineFragment();
            } else {
                return new MentionsTimelineFragment();
            }
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabTitles[position];
        }

        @Override
        public int getCount() {
            return tabTitles.length;
        }
    }
}