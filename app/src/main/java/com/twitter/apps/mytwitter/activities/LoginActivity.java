package com.twitter.apps.mytwitter.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.codepath.oauth.OAuthLoginActionBarActivity;
import com.twitter.apps.mytwitter.R;
import com.twitter.apps.mytwitter.serviceclient.TwitterClient;

public class LoginActivity extends OAuthLoginActionBarActivity<TwitterClient> {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		ImageView ivImg = (ImageView)findViewById(R.id.ivBg);
		Glide.with(this)
				.load("https://media.giphy.com/media/k4ZItrTKDPnSU/giphy.gif")
				.centerCrop()
				.into(ivImg);
	}


	// Inflate the menu; this adds items to the action bar if it is present.
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}

	// OAuth authenticated successfully, launch primary authenticated activity
	// i.e Display application "homepage"
	@Override
	public void onLoginSuccess() {
		Intent i = new Intent(this, TimelineActivity.class);

		i.putExtra("fromShare",false);

		//Share Intent
		Intent intent = getIntent();
		String action = intent.getAction();
		String type = intent.getType();


		if (Intent.ACTION_SEND.equals(action) && type != null) {
			if ("text/plain".equals(type)) {

				// Make sure to check whether returned data will be null.
				String titleOfPage = intent.getStringExtra(Intent.EXTRA_SUBJECT);
				String urlOfPage = intent.getStringExtra(Intent.EXTRA_TEXT);
				//Uri imageUriOfPage = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);
				i.putExtra("fromShare",true);
				i.putExtra("title",titleOfPage);
				i.putExtra("urlOfPage",urlOfPage);
			}
		}


		startActivity(i);
	}

	// OAuth authentication flow failed, handle the error
	// i.e Display an error dialog or toast
	@Override
	public void onLoginFailure(Exception e) {
		e.printStackTrace();
	}

	// Click handler method for the button used to start OAuth flow
	// Uses the client to initiate OAuth authorization
	// This should be tied to a button used to login
	public void loginToRest(View view) {
		getClient().connect();
	}


}
