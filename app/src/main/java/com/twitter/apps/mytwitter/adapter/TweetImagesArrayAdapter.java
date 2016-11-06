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
import com.twitter.apps.mytwitter.activities.ProfileActivity;
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
public class TweetImagesArrayAdapter extends RecyclerView.Adapter<TweetImagesArrayAdapter.ViewHolder> {

    private Context context;
    private List<Tweet> tweets;


    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView ivImage;
        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);

            ivImage = (ImageView)itemView.findViewById(R.id.ivImage);
        }
    }

    public TweetImagesArrayAdapter(Context context, List<Tweet> tweets) {
        this.tweets = tweets;
        this.context = context;
    }

    private Context getContext() {
        return context;
    }

    @Override
    public TweetImagesArrayAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View articleView = inflater.inflate(R.layout.image_tweet, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(articleView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final TweetImagesArrayAdapter.ViewHolder viewHolder, int position) {
        // Get the data model based on position
        final Tweet tweet = tweets.get(position);


        if(tweet.getMedia() != null && tweet.getMedia().getMediaUrl() != null) {
            Glide.with(getContext()).load(tweet.getMedia().getMediaUrl()).bitmapTransform(new RoundedCornersTransformation(context, 30, 0,
                    RoundedCornersTransformation.CornerType.ALL)).into(viewHolder.ivImage);
            viewHolder.ivImage.setVisibility(View.VISIBLE);
        } else {
            viewHolder.ivImage.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return tweets.size();
    }


}
