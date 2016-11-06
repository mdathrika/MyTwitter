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
import com.twitter.apps.mytwitter.R;
import com.twitter.apps.mytwitter.activities.ProfileActivity;
import com.twitter.apps.mytwitter.models.Tweet;
import com.twitter.apps.mytwitter.models.User;

import java.util.List;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

/**
 * Created by mdathrika on 10/27/16.
 */
public class UsersArrayAdapter extends RecyclerView.Adapter<UsersArrayAdapter.ViewHolder> {

    private Context context;
    private List<User> users;


    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView ivProfileImage;
        public TextView tvUserName;
        public TextView tvScreenName;
        public TextView tvDescription;

        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);

            ivProfileImage = (ImageView)itemView.findViewById(R.id.ivProfileImage);
            tvUserName = (TextView)itemView.findViewById(R.id.tvUserName);
            tvScreenName = (TextView)itemView.findViewById(R.id.tvScreenName);
            tvDescription = (TextView)itemView.findViewById(R.id.tvDescription);
        }
    }

    public UsersArrayAdapter(Context context, List<User> users) {
        this.users = users;
        this.context = context;
    }

    private Context getContext() {
        return context;
    }

    @Override
    public UsersArrayAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View articleView = inflater.inflate(R.layout.item_users, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(articleView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final UsersArrayAdapter.ViewHolder viewHolder, int position) {
        // Get the data model based on position
        final User user = users.get(position);

        viewHolder.tvUserName.setText(user.getName());
        viewHolder.tvScreenName.setText("@"+user.getScreenName());
        viewHolder.tvDescription.setText(user.getDescription());

        if(user.getProfileImageUrl() != null) {
            Glide.with(getContext()).load(user.getProfileImageUrl()).bitmapTransform(new RoundedCornersTransformation(context, 30, 0,
                    RoundedCornersTransformation.CornerType.ALL)).into(viewHolder.ivProfileImage);
            viewHolder.ivProfileImage.setVisibility(View.VISIBLE);
        } else {
            viewHolder.ivProfileImage.setVisibility(View.GONE);
        }

        viewHolder.ivProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToUserProfile(user.getScreenName());
            }
        });
    }

    @Override
    public int getItemCount() {
        return users.size();
    }


    private void goToUserProfile(String screenName) {
        Intent intent = new Intent(getContext(), ProfileActivity.class);
        intent.putExtra("screen_name", screenName);
        getContext().startActivity(intent);
    }
}
