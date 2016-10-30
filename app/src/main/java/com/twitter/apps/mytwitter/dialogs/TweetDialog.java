package com.twitter.apps.mytwitter.dialogs;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.twitter.apps.mytwitter.MyTwitterApplication;
import com.twitter.apps.mytwitter.R;
import com.twitter.apps.mytwitter.models.User;
import com.twitter.apps.mytwitter.serviceclient.TwitterClient;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

/**
 * Created by mdathrika on 10/28/16.
 */
public class TweetDialog extends DialogFragment {
    private TextView screenName, name, charcount;
    private ImageView profileImage;
    private TwitterClient twitterClient;

    private User user;
    private DialogFragment dialog;

    public TweetDialog() {
        dialog = this;
        twitterClient = MyTwitterApplication.getRestClient();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_tweet, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Get field from view
        screenName = (TextView) view.findViewById(R.id.displayName);
        name = (TextView) view.findViewById(R.id.name);
        profileImage = (ImageView) view.findViewById(R.id.profilePic);
        final TextView charcount = (TextView) view.findViewById(R.id.charcount);



        final EditText tweetText = (EditText) view.findViewById(R.id.tweetText);

        final Button btnTweet = (Button) view.findViewById(R.id.btnTweet);
        btnTweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

                twitterClient.postTweet(tweetText.getText().toString(),null, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    }
                });
            }
        });

        //Close
        ImageView close = (ImageView) view.findViewById(R.id.close);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        tweetText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                int count = s.length();
                if(count >140) {
                    btnTweet.setEnabled(false);
                    return;
                }

                btnTweet.setEnabled(true);
                charcount.setText(""+(140-count));
            }
        });

        // Fetch arguments from bundle and set title
        User mydetails = getArguments().getParcelable("user");
        if(mydetails != null) {
            screenName.setText(mydetails.getScreenName());
            name.setText(mydetails.getName());
        }

        String tweetMsg = getArguments().getString("tweet");
        if(tweetMsg != null) {
            tweetText.setText(tweetMsg);
        }
        //getDialog().setTitle(title);
        // Show soft keyboard automatically and request focus to field
        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }

}
