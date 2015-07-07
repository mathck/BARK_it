package com.barkitapp.android.parse.objects;

import com.parse.ParseFile;
import com.parse.ParseGeoPoint;

import java.io.Serializable;
import java.util.Date;

/**
 * Post Class from Parse
 */
public class Post implements Serializable {

    String objectId;
    String userId;

    Date time_created;
    transient ParseGeoPoint location;

    String text;
    transient ParseFile image_small;
    String media_content;
    int media_type;

    int vote_counter;
    int reply_counter;
    int badge;

    public Post(String objectId, String userId, Date time_created, ParseGeoPoint location, String text, ParseFile image_small, String media_content, int media_type, int vote_counter, int reply_counter, int badge) {
        this.objectId = objectId;
        this.userId = userId;
        this.time_created = time_created;
        this.location = location;
        this.text = text;
        this.image_small = image_small;
        this.media_content = media_content;
        this.media_type = media_type;
        this.vote_counter = vote_counter;
        this.reply_counter = reply_counter;
        this.badge = badge;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Date getTime_created() {
        return time_created;
    }

    public void setTime_created(Date time_created) {
        this.time_created = time_created;
    }

    public ParseGeoPoint getLocation() {
        return location;
    }

    public void setLocation(ParseGeoPoint location) {
        this.location = location;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public ParseFile getImage_small() {
        return image_small;
    }

    public void setImage_small(ParseFile image_small) {
        this.image_small = image_small;
    }

    public String getMedia_content() {
        return media_content;
    }

    public void setMedia_content(String media_content) {
        this.media_content = media_content;
    }

    public int getMedia_type() {
        return media_type;
    }

    public void setMedia_type(int media_type) {
        this.media_type = media_type;
    }

    public int getVote_counter() {
        return vote_counter;
    }

    public void setVote_counter(int vote_counter) {
        this.vote_counter = vote_counter;
    }

    public int getReply_counter() {
        return reply_counter;
    }

    public void setReply_counter(int reply_counter) {
        this.reply_counter = reply_counter;
    }

    public int getBadge() {
        return badge;
    }

    public void setBadge(int badge) {
        this.badge = badge;
    }
}
