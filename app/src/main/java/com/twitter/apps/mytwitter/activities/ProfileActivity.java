package com.twitter.apps.mytwitter.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.astuetz.PagerSlidingTabStrip;
import com.bumptech.glide.Glide;
import com.twitter.apps.mytwitter.R;
import com.twitter.apps.mytwitter.adapter.SmartFragmentStatePagerAdapter;
import com.twitter.apps.mytwitter.fragments.MentionsTimelineFragment;
import com.twitter.apps.mytwitter.fragments.Profile;
import com.twitter.apps.mytwitter.fragments.Timeline;
import com.twitter.apps.mytwitter.fragments.TweetsListFragment;
import com.twitter.apps.mytwitter.fragments.UserFavoritesFragment;
import com.twitter.apps.mytwitter.fragments.UserImagesFragment;
import com.twitter.apps.mytwitter.fragments.UserTimelineFragment;
import com.twitter.apps.mytwitter.models.User;

import java.text.NumberFormat;
import java.util.Locale;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class ProfileActivity extends BaseActivity implements Profile, Timeline {

    private SwipeRefreshLayout swipeContainer;
    ViewPager viewPager;
    ProfilePagerAdapter pagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Toolbar toolbar = (Toolbar) findViewById(R.id.profile_toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.twitter);
        getSupportActionBar().setDisplayUseLogoEnabled(true);


        String screenName = getIntent().getStringExtra("screen_name");

        viewPager = (ViewPager)findViewById(R.id.profileViewPager);
        pagerAdapter = new ProfilePagerAdapter(getSupportFragmentManager(), screenName);
        viewPager.setAdapter(pagerAdapter);

        PagerSlidingTabStrip tabsStrip = (PagerSlidingTabStrip) findViewById(R.id.profileTabs);
        tabsStrip.setViewPager(viewPager);

        attachSwipeRefresh();




//        if(savedInstanceState == null) {
//            UserTimelineFragment userTimelineFragment = UserTimelineFragment.newInstance(screenName);
//            FragmentManager fragmentManager = getSupportFragmentManager();
//            fragmentManager.beginTransaction().replace(R.id.flContainer, userTimelineFragment).commit();
//        }

    }

    public void populateHeader(final User user) {

        getSupportActionBar().setTitle(user.getScreenName());
        ImageView profilePic = (ImageView)findViewById(R.id.profilePic);

        TextView name = (TextView)findViewById(R.id.name);
        TextView screen_name = (TextView)findViewById(R.id.screen_name);
        TextView description = (TextView)findViewById(R.id.description);
        TextView followers = (TextView)findViewById(R.id.followers);
        TextView following = (TextView)findViewById(R.id.following);

        name.setText(user.getName());
        screen_name.setText("@"+user.getScreenName());
        description.setText(user.getDescription());

        followers.setText(NumberFormat.getIntegerInstance(Locale.ENGLISH).format(user.getFollowersCount()) + " FOLLOWERS");
        following.setText(NumberFormat.getIntegerInstance(Locale.ENGLISH).format(user.getFriendsCount()) + " FOLLOWING");

        Glide.with(this).load(user.getProfileImageUrl()).bitmapTransform(new RoundedCornersTransformation(this, 10, 0,
                RoundedCornersTransformation.CornerType.ALL)).into(profilePic);

        followers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToUsersList("Followers",user.getScreenName());
            }
        });

        following.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToUsersList("Following", user.getScreenName());
            }
        });

    }

    public void attachSwipeRefresh() {

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

    public void doneRefreshing() {
        swipeContainer.setRefreshing(false);
    }

    public class ProfilePagerAdapter extends SmartFragmentStatePagerAdapter {
        private String tabTitles[] = {"Tweets", "Photos", "Favorites"};
        private String screenName;

        public ProfilePagerAdapter(FragmentManager fm, String screenName) {
            super(fm);
            this.screenName = screenName;
        }

        @Override
        public Fragment getItem(int position) {
            Bundle args = new Bundle();
            args.putString("screen_name", screenName);

            Fragment fragment;
            if(position == 0) {
                fragment = new UserTimelineFragment();
            } else if(position == 1){
                fragment = new UserImagesFragment();
            } else {
                fragment = new UserFavoritesFragment();
            }

            fragment.setArguments(args);
            return fragment;
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

    private void goToUsersList(String actionType, String screenName) {
        Intent intent = new Intent(this, UsersListActivity.class);
        intent.putExtra("screen_name", screenName);
        intent.putExtra("action_type", actionType);
        startActivity(intent);
    }

}
