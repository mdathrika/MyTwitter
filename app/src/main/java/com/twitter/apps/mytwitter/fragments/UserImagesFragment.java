package com.twitter.apps.mytwitter.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.twitter.apps.mytwitter.MyTwitterApplication;
import com.twitter.apps.mytwitter.R;
import com.twitter.apps.mytwitter.adapter.TweetImagesArrayAdapter;
import com.twitter.apps.mytwitter.adapter.TweetsArrayAdapter;
import com.twitter.apps.mytwitter.decoration.DividerItemDecoration;
import com.twitter.apps.mytwitter.listeners.EndlessRecyclerViewScrollListener;
import com.twitter.apps.mytwitter.models.Tweet;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator;


/**
 * Created by mdathrika on 11/4/16.
 */

public class UserImagesFragment extends TweetsListFragment {

    private List<Tweet> tweets;
    Timeline timeline;
    private TweetImagesArrayAdapter adapter;
    private RecyclerView recyclerView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        tweets = new ArrayList<>();
        adapter = new TweetImagesArrayAdapter(getActivity(), tweets);

        client = MyTwitterApplication.getRestClient();


        timeline = (Timeline)getActivity();

    }

    @Override
    public void refresh() {
        getUserTimeline(null, false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_tweets_list, container,false);

        recyclerView =  (RecyclerView) view.findViewById(R.id.rvtweets);

        final UserImagesFragment me = this;
        StaggeredGridLayoutManager layoutManager =
                new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);

        //LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);

        recyclerView.setLayoutManager(layoutManager);

        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL_LIST));

        recyclerView.addOnScrollListener(new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                String maxId = Long.toString(tweets.get(tweets.size()-1).getUid());
                me.getUserTimeline(maxId, true);
            }
        });

        recyclerView.setItemAnimator(new SlideInUpAnimator(new OvershootInterpolator(1f)));
        recyclerView.setAdapter(adapter);

        getUserTimeline(null, false);

        return view;
    }


    private void getUserTimeline(String maxId, final boolean append) {
        String screenName = getArguments().getString("screen_name");
        final Profile profile = (Profile)getActivity();
        client.getUserTimeline(screenName, null, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                if(!append)
                    clearTweets();

                addAll(Tweet.fromJSONArray(response));

                timeline.doneRefreshing();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                timeline.doneRefreshing();
            }
        });
    }

    public void addAll(List<Tweet> tweets) {
        this.tweets.addAll(tweets);
        adapter.notifyDataSetChanged();
    }

}
