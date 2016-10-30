package com.twitter.apps.mytwitter.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.format.DateUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by mdathrika on 10/27/16.
 */
public class Tweet implements Parcelable {
    private String body;
    private long uid;

    private String createdAt;
    private String timeAgo;
    private int retweetCount;
    private boolean retweeted;
    private int favouritesCount;
    private boolean favorited;
    private String url;

    private User user;
    private Media media;

    public Media getMedia() {
        return media;
    }

    public void setMedia(Media media) {
        this.media = media;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getTimeAgo() {
        return timeAgo;
    }

    public void setTimeAgo(String timeAgo) {
        this.timeAgo = timeAgo;
    }

    public int getRetweetCount() {
        return retweetCount;
    }

    public void setRetweetCount(int retweetCount) {
        this.retweetCount = retweetCount;
    }

    public boolean isRetweeted() {
        return retweeted;
    }

    public void setRetweeted(boolean retweeted) {
        this.retweeted = retweeted;
    }

    public int getFavouritesCount() {
        return favouritesCount;
    }

    public void setFavouritesCount(int favouritesCount) {
        this.favouritesCount = favouritesCount;
    }

    public boolean isFavorited() {
        return favorited;
    }

    public void setFavorited(boolean favorited) {
        this.favorited = favorited;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public static Tweet fromJSON(JSONObject jsonObject) {
        System.out.println("******************jsonObject****************"+jsonObject);
        Tweet tweet = new Tweet();
        try {
            tweet.body = jsonObject.getString("text");
            tweet.uid = jsonObject.getLong("id");
            tweet.createdAt = jsonObject.getString("created_at");

            tweet.retweeted = jsonObject.getBoolean("retweeted");
            tweet.retweetCount = jsonObject.getInt("retweet_count");
            tweet.favorited = jsonObject.getBoolean("favorited");
            tweet.favouritesCount = jsonObject.getInt("favorite_count");

            tweet.user = User.fromJSON(jsonObject.getJSONObject("user"));
            tweet.timeAgo = getRelativeTimeAgo(tweet.createdAt);

            if(jsonObject.getJSONObject("entities") != null) {
                if (jsonObject.getJSONObject("entities").has("media") && jsonObject.getJSONObject("entities").getJSONArray("media").length() > 0)
                    tweet.media = Media.fromJSON((JSONObject) jsonObject.getJSONObject("entities").getJSONArray("media").get(0));

                if (jsonObject.getJSONObject("entities").has("urls") && jsonObject.getJSONObject("entities").getJSONArray("urls").length() > 0) {
                    JSONObject jsonObjectURL = (JSONObject) jsonObject.getJSONObject("entities").getJSONArray("urls").get(0);
                    tweet.url = jsonObjectURL.getString("url");
                }
            }

        }catch(JSONException e) {
            e.printStackTrace();
        }
        return tweet;
    }

    public static List<Tweet> fromJSONArray(JSONArray jsonArray) {
        List<Tweet> tweets = new ArrayList<>();
        for(int i=0; i< jsonArray.length(); i++) {
            try {
                tweets.add(Tweet.fromJSON(jsonArray.getJSONObject(i)));
            }catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return tweets;
    }

    private static String getRelativeTimeAgo(String rawJsonDate) {
        String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        sf.setLenient(true);

        String relativeDate = "";
        try {
            long dateMillis = sf.parse(rawJsonDate).getTime();
            relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis,
                    System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS).toString();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        relativeDate = relativeDate.replace(" ago","");
        relativeDate = relativeDate.replace(" seconds","s");
        relativeDate = relativeDate.replace(" second","s");
        relativeDate = relativeDate.replace(" minutes","m");
        relativeDate = relativeDate.replace(" minute","m");
        relativeDate = relativeDate.replace(" hours","h");
        relativeDate = relativeDate.replace(" hour","h");
        relativeDate = relativeDate.replace(" days","d");
        relativeDate = relativeDate.replace(" day","d");

        return relativeDate;
    }


    public Tweet() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.body);
        dest.writeLong(this.uid);
        dest.writeString(this.createdAt);
        dest.writeString(this.timeAgo);
        dest.writeInt(this.retweetCount);
        dest.writeByte(this.retweeted ? (byte) 1 : (byte) 0);
        dest.writeInt(this.favouritesCount);
        dest.writeByte(this.favorited ? (byte) 1 : (byte) 0);
        dest.writeString(this.url);
        dest.writeParcelable(this.user, flags);
        dest.writeParcelable(this.media, flags);
    }

    protected Tweet(Parcel in) {
        this.body = in.readString();
        this.uid = in.readLong();
        this.createdAt = in.readString();
        this.timeAgo = in.readString();
        this.retweetCount = in.readInt();
        this.retweeted = in.readByte() != 0;
        this.favouritesCount = in.readInt();
        this.favorited = in.readByte() != 0;
        this.url = in.readString();
        this.user = in.readParcelable(User.class.getClassLoader());
        this.media = in.readParcelable(Media.class.getClassLoader());
    }

    public static final Creator<Tweet> CREATOR = new Creator<Tweet>() {
        @Override
        public Tweet createFromParcel(Parcel source) {
            return new Tweet(source);
        }

        @Override
        public Tweet[] newArray(int size) {
            return new Tweet[size];
        }
    };
}

