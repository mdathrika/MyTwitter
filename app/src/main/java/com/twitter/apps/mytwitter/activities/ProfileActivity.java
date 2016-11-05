package com.twitter.apps.mytwitter.activities;

import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.twitter.apps.mytwitter.MyTwitterApplication;
import com.twitter.apps.mytwitter.R;
import com.twitter.apps.mytwitter.fragments.Profile;
import com.twitter.apps.mytwitter.fragments.UserTimelineFragment;
import com.twitter.apps.mytwitter.models.User;
import com.twitter.apps.mytwitter.serviceclient.TwitterClient;

import org.json.JSONObject;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

import cz.msebera.android.httpclient.Header;
import jp.wasabeef.glide.transformations.CropCircleTransformation;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class ProfileActivity extends AppCompatActivity implements Profile {

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

        if(savedInstanceState == null) {
            UserTimelineFragment userTimelineFragment = UserTimelineFragment.newInstance(screenName);
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.flContainer, userTimelineFragment).commit();
        }

    }

    public void populateHeader(User user) {

        getSupportActionBar().setTitle(user.getScreenName());
        ImageView profilePic = (ImageView)findViewById(R.id.profilePic);

        TextView name = (TextView)findViewById(R.id.name);
        TextView screen_name = (TextView)findViewById(R.id.screen_name);
        TextView description = (TextView)findViewById(R.id.description);
        TextView followers = (TextView)findViewById(R.id.followers);
        TextView following = (TextView)findViewById(R.id.following);

        name.setText(user.getName());
        screen_name.setText(user.getScreenName());
        description.setText(user.getDescription());

        followers.setText(NumberFormat.getIntegerInstance(Locale.ENGLISH).format(user.getFollowersCount()) + " FOLLOWERS");
        following.setText(NumberFormat.getIntegerInstance(Locale.ENGLISH).format(user.getFriendsCount()) + " FOLLOWING");

        Glide.with(this).load(user.getProfileImageUrl()).bitmapTransform(new RoundedCornersTransformation(this, 10, 0,
                RoundedCornersTransformation.CornerType.ALL)).into(profilePic);
    }



}
