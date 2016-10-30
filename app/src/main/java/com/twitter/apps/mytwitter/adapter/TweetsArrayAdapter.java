package com.twitter.apps.mytwitter.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.twitter.apps.mytwitter.MyTwitterApplication;
import com.twitter.apps.mytwitter.R;

import com.twitter.apps.mytwitter.models.Tweet;
import com.twitter.apps.mytwitter.serviceclient.TwitterClient;

import org.json.JSONObject;

import java.util.List;

import cz.msebera.android.httpclient.Header;
import jp.wasabeef.glide.transformations.CropCircleTransformation;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

/**
 * Created by mdathrika on 10/27/16.
 */
public class TweetsArrayAdapter extends RecyclerView.Adapter<TweetsArrayAdapter.ViewHolder> {

    private Context context;
    private List<Tweet> tweets;
    private TwitterClient twitterClient;


    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView ivProfileImage;
        public ImageView ivInlineImage;
        public ImageView retweet;
        public ImageView favourite;
        public ImageView reply;
        public ImageView share;

        public TextView tvUserName;
        public TextView tvBody;
        public TextView tvTimeAgo;
        public TextView tvScreenName;

        public TextView retweetCnt;
        public TextView favouriteCnt;

        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);

            ivProfileImage = (ImageView)itemView.findViewById(R.id.ivProfileImage);
            ivInlineImage = (ImageView)itemView.findViewById(R.id.ivInlineImage);
            share = (ImageView)itemView.findViewById(R.id.share);

            tvUserName = (TextView)itemView.findViewById(R.id.tvUserName);
            tvBody = (TextView)itemView.findViewById(R.id.tvBody);
            tvTimeAgo = (TextView)itemView.findViewById(R.id.tvTimeAgo);
            tvScreenName = (TextView)itemView.findViewById(R.id.tvScreenName);

            //retweet = (ImageView)itemView.findViewById(R.id.retweet);
            //favourite = (ImageView)itemView.findViewById(R.id.favourite);
            reply = (ImageView)itemView.findViewById(R.id.reply);

            retweetCnt = (TextView)itemView.findViewById(R.id.retweetCnt);
            favouriteCnt = (TextView)itemView.findViewById(R.id.favouriteCnt);



        }
    }

    public  TweetsArrayAdapter(Context context, List<Tweet> tweets) {
        this.tweets = tweets;
        this.context = context;
        twitterClient = MyTwitterApplication.getRestClient();
    }

    private Context getContext() {
        return context;
    }

    @Override
    public TweetsArrayAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View articleView = inflater.inflate(R.layout.item_tweet, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(articleView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final TweetsArrayAdapter.ViewHolder viewHolder, int position) {
        // Get the data model based on position
        final Tweet tweet = tweets.get(position);
        //Picasso.with(getContext()).load(tweet.getUser().getProfileImageUrl()).into(holder.ivProfileImage);

        viewHolder.tvBody.setText(tweet.getBody());
        viewHolder.tvUserName.setText(tweet.getUser().getName());

        viewHolder.tvTimeAgo.setText(tweet.getTimeAgo());
        viewHolder.tvScreenName.setText(tweet.getUser().getScreenName());

        if(tweet.isRetweeted())
            viewHolder.retweetCnt.setCompoundDrawablesWithIntrinsicBounds(R.drawable.retweet_clicked, 0,0,0);

        if(tweet.isFavorited())
            viewHolder.favouriteCnt.setCompoundDrawablesWithIntrinsicBounds(R.drawable.favorite_clicked, 0, 0, 0);


        viewHolder.retweetCnt.setText(""+tweet.getRetweetCount());
        viewHolder.favouriteCnt.setText(""+tweet.getFavouritesCount());

       // viewHolder.tvHeadline.setText(article.getHeadline());
        Glide.with(getContext()).load(tweet.getUser().getProfileImageUrl()).bitmapTransform(new CropCircleTransformation(context)).into(viewHolder.ivProfileImage);

        if(tweet.getMedia() != null && tweet.getMedia().getMediaUrl() != null) {
            Glide.with(getContext()).load(tweet.getMedia().getMediaUrl()).bitmapTransform(new RoundedCornersTransformation(context, 30, 0,
                    RoundedCornersTransformation.CornerType.ALL)).into(viewHolder.ivInlineImage);
            viewHolder.ivInlineImage.setVisibility(View.VISIBLE);
        } else {
            viewHolder.ivInlineImage.setVisibility(View.GONE);
        }

        viewHolder.reply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        viewHolder.favouriteCnt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!tweet.isFavorited()) {
                    createFavorite(Long.toString(tweet.getUid()));
                    tweet.setFavouritesCount(tweet.getFavouritesCount()+1);
                    viewHolder.favouriteCnt.setText(""+tweet.getFavouritesCount());
                    viewHolder.favouriteCnt.setCompoundDrawablesWithIntrinsicBounds(R.drawable.favorite_clicked, 0, 0, 0);
                }
            }
        });

        viewHolder.retweetCnt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!tweet.isRetweeted()) {
                    retweet(Long.toString(tweet.getUid()));
                    tweet.setRetweetCount(tweet.getRetweetCount()+1);
                    viewHolder.retweetCnt.setText(""+tweet.getRetweetCount());
                    viewHolder.retweetCnt.setCompoundDrawablesWithIntrinsicBounds(R.drawable.retweet_clicked, 0, 0, 0);
                }
            }
        });

        viewHolder.share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("plain/text");
                intent.putExtra(Intent.EXTRA_TEXT, tweet.getBody());

                if (intent.resolveActivity(context.getPackageManager()) != null) {
                    context.startActivity(intent);
                }

            }
        });

    }

    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        return tweets.size();
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
