package com.twitter.apps.mytwitter.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;
import com.twitter.apps.mytwitter.MyDatabase;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by mdathrika on 10/27/16.
 */
@Table(database = MyDatabase.class)
public class User extends BaseModel implements Parcelable{

    @Column
    private String name;
    @PrimaryKey
    @Column
    private long uid;
    @Column
    private String screenName;
    @Column
    private String profileImageUrl;

    private String profileBgImageUrl;

    private String description;

    private int followersCount;

    private int friendsCount;

    private int tweetsCount;

    public String getProfileBgImageUrl() {
        return profileBgImageUrl;
    }

    public void setProfileBgImageUrl(String profileBgImageUrl) {
        this.profileBgImageUrl = profileBgImageUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getFollowersCount() {
        return followersCount;
    }

    public void setFollowersCount(int followersCount) {
        this.followersCount = followersCount;
    }

    public int getFriendsCount() {
        return friendsCount;
    }

    public void setFriendsCount(int friendsCount) {
        this.friendsCount = friendsCount;
    }

    public int getTweetsCount() {
        return tweetsCount;
    }

    public void setTweetsCount(int tweetsCount) {
        this.tweetsCount = tweetsCount;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public String getScreenName() {
        return screenName;
    }

    public void setScreenName(String screenName) {
        this.screenName = screenName;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public static User fromJSON(JSONObject jsonObject) {
        User user = new User();
        try {
            user.screenName = "@"+jsonObject.getString("screen_name");
            user.name = jsonObject.getString("name");
            user.uid = jsonObject.getLong("id");
            user.profileImageUrl = jsonObject.getString("profile_image_url");

            user.description = jsonObject.getString("description");
            user.profileBgImageUrl = jsonObject.getString("profile_background_image_url");
            user.followersCount = jsonObject.getInt("followers_count");
            user.friendsCount = jsonObject.getInt("friends_count");
            user.tweetsCount = jsonObject.getInt("statuses_count");

        }catch(JSONException e) {
            e.printStackTrace();
        }
        return user;
    }


    public User() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeLong(this.uid);
        dest.writeString(this.screenName);
        dest.writeString(this.profileImageUrl);
        dest.writeString(this.profileBgImageUrl);
        dest.writeString(this.description);
        dest.writeInt(this.followersCount);
        dest.writeInt(this.friendsCount);
        dest.writeInt(this.tweetsCount);
    }

    protected User(Parcel in) {
        this.name = in.readString();
        this.uid = in.readLong();
        this.screenName = in.readString();
        this.profileImageUrl = in.readString();
        this.profileBgImageUrl = in.readString();
        this.description = in.readString();
        this.followersCount = in.readInt();
        this.friendsCount = in.readInt();
        this.tweetsCount = in.readInt();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel source) {
            return new User(source);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };
}
