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
 * Created by mdathrika on 10/29/16.
 */

@Table(database = MyDatabase.class)
public class Media extends BaseModel implements Parcelable {

    @Column
    private String mediaUrl;
    @Column
    private String url;
    @Column
    private String type;

    @PrimaryKey
    @Column
    private long id;

    public String getMediaUrl() {
        return mediaUrl;
    }

    public void setMediaUrl(String mediaUrl) {
        this.mediaUrl = mediaUrl;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public static Media fromJSON(JSONObject jsonObject) {
        Media media = new Media();
        try {
            media.mediaUrl = jsonObject.getString("media_url");
            media.url = jsonObject.getString("url");
            media.id = jsonObject.getLong("id");
            media.type = jsonObject.getString("type");

        }catch(JSONException e) {
            e.printStackTrace();
        }
        return media;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mediaUrl);
        dest.writeString(this.url);
        dest.writeString(this.type);
        dest.writeLong(this.id);
    }

    public Media() {
    }

    protected Media(Parcel in) {
        this.mediaUrl = in.readString();
        this.url = in.readString();
        this.type = in.readString();
        this.id = in.readLong();
    }

    public static final Creator<Media> CREATOR = new Creator<Media>() {
        @Override
        public Media createFromParcel(Parcel source) {
            return new Media(source);
        }

        @Override
        public Media[] newArray(int size) {
            return new Media[size];
        }
    };
}
