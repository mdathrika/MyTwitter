package com.twitter.apps.mytwitter;

import com.raizlabs.android.dbflow.config.FlowConfig;
import com.raizlabs.android.dbflow.config.FlowLog;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.twitter.apps.mytwitter.serviceclient.TwitterClient;

import android.app.Application;
import android.content.Context;

/*
 * This is the Android application itself and is used to configure various settings
 * including the image cache in memory and on disk. This also adds a singleton
 * for accessing the relevant rest client.
 *
 *     TwitterClient client = MyTwitterApplication.getRestClient();
 *     // use client to send requests to API
 *
 */
public class MyTwitterApplication extends Application {
	private static Context context;

	@Override
	public void onCreate() {
		super.onCreate();

		FlowManager.init(new FlowConfig.Builder(this).build());
		FlowLog.setMinimumLoggingLevel(FlowLog.Level.V);

		MyTwitterApplication.context = this;
	}

	public static TwitterClient getRestClient() {
		return (TwitterClient) TwitterClient.getInstance(TwitterClient.class, MyTwitterApplication.context);
	}
}