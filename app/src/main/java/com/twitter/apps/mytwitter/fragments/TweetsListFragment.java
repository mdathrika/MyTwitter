package com.twitter.apps.mytwitter.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.twitter.apps.mytwitter.MyTwitterApplication;
import com.twitter.apps.mytwitter.R;
import com.twitter.apps.mytwitter.TweetDetailsActivity;
import com.twitter.apps.mytwitter.adapter.ItemClickSupport;
import com.twitter.apps.mytwitter.adapter.TweetsArrayAdapter;
import com.twitter.apps.mytwitter.decoration.DividerItemDecoration;
import com.twitter.apps.mytwitter.listeners.EndlessRecyclerViewScrollListener;
import com.twitter.apps.mytwitter.models.Tweet;
import com.twitter.apps.mytwitter.models.User;
import com.twitter.apps.mytwitter.serviceclient.TwitterClient;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator;

/**
 * Created by mdathrika on 11/3/16.
 */
public class TweetsListFragment extends Fragment{

    private List<Tweet> tweets;
    private TweetsArrayAdapter adapter;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeContainer;

    TwitterClient client;
    User myDetails;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tweets_list, container,false);

        recyclerView =  (RecyclerView) view.findViewById(R.id.rvtweets);

        final TweetsListFragment me = this;
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);

        recyclerView.setLayoutManager(layoutManager);

        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL_LIST));

        recyclerView.addOnScrollListener(new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                String maxId = Long.toString(tweets.get(tweets.size()-1).getUid());
                me.populateTimeline(maxId, true);
            }
        });

        recyclerView.setItemAnimator(new SlideInUpAnimator(new OvershootInterpolator(1f)));
        recyclerView.setAdapter(adapter);

        ItemClickSupport.addTo(recyclerView).setOnItemClickListener(
                new ItemClickSupport.OnItemClickListener() {
                    @Override
                    public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                        Intent intent = new Intent(getContext(), TweetDetailsActivity.class);
                        Tweet tweet = tweets.get(position);
                        intent.putExtra("tweet", tweet);
                        startActivity(intent);
                    }
                }
        );

        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        tweets = new ArrayList<>();
        adapter = new TweetsArrayAdapter(getActivity(), tweets);

        client = MyTwitterApplication.getRestClient();
    }

    public void addAll(List<Tweet> tweets) {
        this.tweets.addAll(tweets);
        adapter.notifyDataSetChanged();
    }

    public void clearTweets() {
        tweets.clear();
    }

    public void refresh() {

    }

    public void populateTimeline(String maxId, final boolean append) {

    }

    protected void fetchUserDetails() {
        client.getUserDetails(new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                System.out.println("**********USER********"+response);
                myDetails = User.fromJSON(response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                throwable.printStackTrace();
            }
        });
    }

    public User getUserDetails() {
        return myDetails;
    }

    public TwitterClient getClient() {
        return client;
    }
}
