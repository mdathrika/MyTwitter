package com.twitter.apps.mytwitter.serviceclient;

import org.scribe.builder.api.Api;
import org.scribe.builder.api.FlickrApi;
import org.scribe.builder.api.TwitterApi;

import android.content.Context;

import com.codepath.oauth.OAuthBaseClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/*
 * 
 * This is the object responsible for communicating with a REST API. 
 * Specify the constants below to change the API being communicated with.
 * See a full list of supported API classes: 
 *   https://github.com/fernandezpablo85/scribe-java/tree/master/src/main/java/org/scribe/builder/api
 * Key and Secret are provided by the developer site for the given API i.e dev.twitter.com
 * Add methods for each relevant endpoint in the API.
 * 
 * NOTE: You may want to rename this object based on the service i.e TwitterClient or FlickrClient
 * 
 */
public class TwitterClient extends OAuthBaseClient {
	public static final Class<? extends Api> REST_API_CLASS = TwitterApi.class; // Change this
	public static final String REST_URL = "https://api.twitter.com/1.1/"; // Change this, base API URL
	public static final String REST_CONSUMER_KEY = "cXmhMjYDJikV7Yro56R7wC4M4";       // Change this
	public static final String REST_CONSUMER_SECRET = "LWYg15E0G45Cjxf7YbocjHat5S5t78o2cpvnWwH2o8DjzGjsOx"; // Change this
	public static final String REST_CALLBACK_URL = "oauth://cpsimpletweets"; // Change this (here and in manifest)

	public TwitterClient(Context context) {
		super(context, REST_API_CLASS, REST_URL, REST_CONSUMER_KEY, REST_CONSUMER_SECRET, REST_CALLBACK_URL);
	}

	/* 1. Define the endpoint URL with getApiUrl and pass a relative path to the endpoint
	 * 	  i.e getApiUrl("statuses/home_timeline.json");
	 * 2. Define the parameters to pass to the request (query or body)
	 *    i.e RequestParams params = new RequestParams("foo", "bar");
	 * 3. Define the request method and make a call to the client
	 *    i.e client.get(apiUrl, params, handler);
	 *    i.e client.post(apiUrl, params, handler);
	 */

	public void getHomeTimeline(String max_id, AsyncHttpResponseHandler handler) {
		String apiUrl = getApiUrl("statuses/home_timeline.json");

		// Can specify query string params directly or through RequestParams.
		RequestParams params = new RequestParams();
		params.put("count", 25);
		if(max_id != null) {
			params.put("max_id", max_id);
		}

		params.put("format", "json");
		client.get(apiUrl, params, handler);
	}

	public void postTweet(String status, String replyTo, AsyncHttpResponseHandler handler) {
		String apiUrl = getApiUrl("statuses/update.json");
		// Can specify query string params directly or through RequestParams.
		RequestParams params = new RequestParams();
		params.put("status", status);
		if(replyTo != null) {
			params.put("in_reply_to_status_id", replyTo);
		}
		params.put("format", "json");
		client.post(apiUrl, params, handler);
	}

	public void getUserDetails(AsyncHttpResponseHandler handler) {
		String apiUrl = getApiUrl("account/verify_credentials.json");
		RequestParams params = new RequestParams();
		//params.put("screen_name", "dathrika_mahesh");
		//params.put("include_email", "false");
		params.put("format", "json");
		client.get(apiUrl, params, handler);
	}

	public void postRetweet(String tweetId, AsyncHttpResponseHandler handler) {
		String apiUrl = getApiUrl("statuses/retweet/"+tweetId+".json");
		RequestParams params = new RequestParams();
		params.put("format", "json");
		client.post(apiUrl, params, handler);
	}

	public void postFavorite(String tweetId, AsyncHttpResponseHandler handler) {
		String apiUrl = getApiUrl("favorites/create.json");
		RequestParams params = new RequestParams();
		params.put("id", tweetId);
		params.put("format", "json");
		client.post(apiUrl, params, handler);
	}

	public void getMentionsTimeline(String max_id, AsyncHttpResponseHandler handler) {
		String apiUrl = getApiUrl("statuses/mentions_timeline.json");

		RequestParams params = new RequestParams();
		params.put("count", 25);
		if(max_id != null) {
			params.put("max_id", max_id);
		}

		params.put("format", "json");
		client.get(apiUrl, params, handler);
	}

	public void getUserTimeline(String screeName, String max_id, AsyncHttpResponseHandler handler) {
		String apiUrl = getApiUrl("statuses/user_timeline.json");

		RequestParams params = new RequestParams();
		params.put("count", 25);
		if(max_id != null) {
			params.put("max_id", max_id);
		}
		if(screeName != null) {
			params.put("screen_name", screeName);
		}
		params.put("format", "json");
		client.get(apiUrl, params, handler);
	}

	public void findUser(String screeName, AsyncHttpResponseHandler handler) {
		String apiUrl = getApiUrl("users/lookup.json");

		RequestParams params = new RequestParams();
		params.put("count", 25);
		if(screeName != null) {
			params.put("screen_name", screeName);
		}
		params.put("format", "json");
		client.get(apiUrl, params, handler);
	}

	public void getFavoritesList(String screeName, String max_id, AsyncHttpResponseHandler handler) {
		String apiUrl = getApiUrl("favorites/list.json");

		RequestParams params = new RequestParams();
		params.put("count", 25);
		if(screeName != null) {
			params.put("screen_name", screeName);
		}
		if(max_id != null) {
			params.put("max_id", max_id);
		}
		params.put("format", "json");
		client.get(apiUrl, params, handler);
	}

	public void getFollowersList(String screeName, String cursor, AsyncHttpResponseHandler handler) {
		String apiUrl = getApiUrl("followers/list.json");

		RequestParams params = new RequestParams();
		params.put("count", 25);
		if(screeName != null) {
			params.put("screen_name", screeName);
		}
		if(cursor != null) {
			params.put("cursor", cursor);
		}
		params.put("format", "json");
		client.get(apiUrl, params, handler);
	}

	public void getFriendsList(String screeName, String cursor, AsyncHttpResponseHandler handler) {
		String apiUrl = getApiUrl("friends/list.json");

		RequestParams params = new RequestParams();
		params.put("count", 25);
		if(screeName != null) {
			params.put("screen_name", screeName);
		}
		if(cursor != null) {
			params.put("cursor", cursor);
		}
		params.put("format", "json");
		client.get(apiUrl, params, handler);
	}

}