package com.twitter.apps.mytwitter.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.astuetz.PagerSlidingTabStrip;
import com.twitter.apps.mytwitter.MyTwitterApplication;
import com.twitter.apps.mytwitter.R;
import com.twitter.apps.mytwitter.adapter.SmartFragmentStatePagerAdapter;
import com.twitter.apps.mytwitter.dialogs.TweetDialog;
import com.twitter.apps.mytwitter.fragments.HomeTimelineFragment;
import com.twitter.apps.mytwitter.fragments.MentionsTimelineFragment;
import com.twitter.apps.mytwitter.fragments.Timeline;
import com.twitter.apps.mytwitter.fragments.TweetsListFragment;


/**
 * Created by mdathrika on 10/27/16.
 */
public class TimelineActivity extends BaseActivity implements Timeline {

    private SwipeRefreshLayout swipeContainer;
    final Activity activity = this;
    final Context context = this;
    MentionsTimelineFragment fragment;

    ViewPager viewPager;
    TweetsPagerAdapter pagerAdapter;

    public TimelineActivity() {
        client = MyTwitterApplication.getRestClient();
    }

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

        attachSwipeRefresh();

        Intent intent = getIntent();
        if(intent.getBooleanExtra("fromShare", false)) {
            String title = intent.getStringExtra("title");
            String urlOfPage = intent.getStringExtra("urlOfPage");

            showTweetDialog(title + " " + urlOfPage);
        }

        getMyDetails();

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
        intent.putExtra("screen_name", myDetails.getScreenName());
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