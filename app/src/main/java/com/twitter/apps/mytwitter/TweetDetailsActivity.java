package com.twitter.apps.mytwitter;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.twitter.apps.mytwitter.models.Tweet;
import com.twitter.apps.mytwitter.serviceclient.TwitterClient;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import jp.wasabeef.glide.transformations.CropCircleTransformation;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class TweetDetailsActivity extends AppCompatActivity {

    TwitterClient twitterClient = MyTwitterApplication.getRestClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tweet_details);

        Toolbar toolbar = (Toolbar) findViewById(R.id.tweet_details_toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        final Tweet tweet = intent.getParcelableExtra("tweet");

        ImageView ivProfileImage = (ImageView)findViewById(R.id.ivProfileImage);
        ImageView ivInlineImage = (ImageView)findViewById(R.id.ivInlineImage);
        ImageView share = (ImageView)findViewById(R.id.share);

        TextView tvUserName = (TextView)findViewById(R.id.tvUserName);
        TextView tvBody = (TextView)findViewById(R.id.tvBody);
        TextView tvTimeAgo = (TextView)findViewById(R.id.tvTimeAgo);
        TextView tvScreenName = (TextView)findViewById(R.id.tvScreenName);

        ImageView reply = (ImageView)findViewById(R.id.reply);

        final TextView retweetCnt = (TextView)findViewById(R.id.retweetCnt);
        final TextView favouriteCnt = (TextView)findViewById(R.id.favouriteCnt);
        final Button tweetBtn = (Button)findViewById(R.id.btnTweet);
        final TextView tweetText = (TextView)findViewById(R.id.txtTweet);

        tweetText.setHint(tweetText.getHint() + " " + tweet.getUser().getScreenName());
        //tweetText.setText("@"+tweet.getUser().getName());

        tvBody.setText(tweet.getBody());
        tvUserName.setText(tweet.getUser().getName());

        tvTimeAgo.setText(tweet.getTimeAgo());
        tvScreenName.setText(tweet.getUser().getScreenName());

        if(tweet.isRetweeted())
            retweetCnt.setCompoundDrawablesWithIntrinsicBounds(R.drawable.retweet_clicked, 0,0,0);

        if(tweet.isFavorited())
            favouriteCnt.setCompoundDrawablesWithIntrinsicBounds(R.drawable.favorite_clicked, 0, 0, 0);


        retweetCnt.setText(""+tweet.getRetweetCount());
        favouriteCnt.setText(""+tweet.getFavouritesCount());

        Glide.with(this).load(tweet.getUser().getProfileImageUrl()).bitmapTransform(new CropCircleTransformation(this)).into(ivProfileImage);

        if(tweet.getMedia() != null && tweet.getMedia().getMediaUrl() != null) {
            Glide.with(this).load(tweet.getMedia().getMediaUrl()).bitmapTransform(new RoundedCornersTransformation(this, 30, 0,
                    RoundedCornersTransformation.CornerType.ALL)).into(ivInlineImage);
            ivInlineImage.setVisibility(View.VISIBLE);
        } else {
            ivInlineImage.setVisibility(View.GONE);
        }

        favouriteCnt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!tweet.isFavorited()) {
                    createFavorite(Long.toString(tweet.getUid()));
                    tweet.setFavouritesCount(tweet.getFavouritesCount()+1);
                    favouriteCnt.setText(""+tweet.getFavouritesCount());
                    favouriteCnt.setCompoundDrawablesWithIntrinsicBounds(R.drawable.favorite_clicked, 0, 0, 0);
                }
            }
        });

        retweetCnt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!tweet.isRetweeted()) {
                    retweet(Long.toString(tweet.getUid()));
                    tweet.setRetweetCount(tweet.getRetweetCount()+1);
                    retweetCnt.setText(""+tweet.getRetweetCount());
                    retweetCnt.setCompoundDrawablesWithIntrinsicBounds(R.drawable.retweet_clicked, 0, 0, 0);
                }
            }
        });

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("plain/text");
                intent.putExtra(Intent.EXTRA_TEXT, tweet.getBody());

                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }

            }
        });

        tweetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               postTweet(tweet.getUser().getScreenName() + " " +tweetText.getText().toString());
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.empty, menu);
        return super.onCreateOptionsMenu(menu);

    }

    private void postTweet(String tweet) {
        twitterClient.postTweet(tweet, null, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {

            }
        });
    }

    private void retweet(String tweetId) {
        twitterClient.postRetweet(tweetId, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {

            }
        });
    }

    private void createFavorite(String tweetId) {
        twitterClient.postFavorite(tweetId, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {

            }
        });
    }
}
