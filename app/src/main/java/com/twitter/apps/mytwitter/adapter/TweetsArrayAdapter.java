package com.twitter.apps.mytwitter.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.twitter.apps.mytwitter.R;

import com.twitter.apps.mytwitter.models.Tweet;

import java.util.List;

import jp.wasabeef.glide.transformations.CropCircleTransformation;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

/**
 * Created by mdathrika on 10/27/16.
 */
public class TweetsArrayAdapter extends RecyclerView.Adapter<TweetsArrayAdapter.ViewHolder> {

    private Context context;
    private List<Tweet> tweets;

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView ivProfileImage;
        public ImageView ivInlineImage;
        public ImageView retweet;
        public ImageView favourite;
        public ImageView reply;

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

    /*@Override
    public TweetsArrayAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).
                    inflate(R.layout.item_tweet, parent, false);
            final ViewHolder holder = new ViewHolder();
            holder.ivProfileImage = (ImageView)convertView.findViewById(R.id.ivProfileImage);
            holder.tvUserName = (TextView)convertView.findViewById(R.id.tvUserName);
            holder.tvBody = (TextView)convertView.findViewById(R.id.tvBody);

            holder.tvTimeAgo = (TextView)convertView.findViewById(R.id.tvTimeAgo);
            holder.tvScreenName = (TextView)convertView.findViewById(R.id.tvScreenName);

            convertView.setTag(holder);
        }
        final Tweet tweet = getItem(position);
        final ViewHolder holder = (ViewHolder)convertView.getTag();

        Picasso.with(getContext()).load(tweet.getUser().getProfileImageUrl()).into(holder.ivProfileImage);
        holder.tvBody.setText(tweet.getBody());
        holder.tvUserName.setText(tweet.getUser().getName());

        holder.tvTimeAgo.setText(tweet.getTimeAgo());
        holder.tvScreenName.setText(tweet.getUser().getScreenName());

        return convertView;
    }*/

    @Override
    public void onBindViewHolder(TweetsArrayAdapter.ViewHolder viewHolder, int position) {
        // Get the data model based on position
        Tweet tweet = tweets.get(position);
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

    }

    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        return tweets.size();
    }
}
