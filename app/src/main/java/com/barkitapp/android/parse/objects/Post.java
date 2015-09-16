package com.barkitapp.android.parse.objects;

import com.barkitapp.android.parse.enums.VoteType;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;

import java.util.Date;

/**
 * Post Class from Parse
 */
public class Post {

    String objectId;
    String userId;

    Date time_created;
    double latitude;
    double longitude;

    String text;
    //transient ParseFile image_small;
    String media_content;
    int media_type;

    int vote_counter;
    int reply_counter;
    int badge;

    public int getMy_Vote() {
        return my_Vote;
    }

    public void setMy_Vote(int my_Vote) {
        this.my_Vote = my_Vote;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    String image_url;

    int my_Vote;

    public Post() {
    }

    public Post(ParseObject parseObject) {
        this.objectId = parseObject.getObjectId();
        this.userId = parseObject.getString("user_id");
        this.time_created = parseObject.getDate("time_created");
        this.latitude = parseObject.getParseGeoPoint("location").getLatitude();
        this.longitude = parseObject.getParseGeoPoint("location").getLongitude();
        this.text = parseObject.getString("text");
        //this.image_small = image_small;
        this.media_content = parseObject.getString("media_content");
        this.media_type = parseObject.getInt("media_type");
        this.vote_counter = parseObject.getInt("vote_counter");
        this.reply_counter = parseObject.getInt("reply_counter");
        this.badge = parseObject.getInt("badge");
        this.my_Vote = parseObject.has("my_vote") ? parseObject.getInt("my_vote") : VoteType.NEUTRAL.ordinal();
    }
    
    public Post(String objectId, String userId, Date time_created, ParseGeoPoint location, String text, String media_content, int media_type, int vote_counter, int reply_counter, int badge, String image_url, int my_Vote) {
        this.objectId = objectId;
        this.userId = userId;
        this.time_created = time_created;
        this.latitude = location.getLatitude();
        this.longitude = location.getLongitude();
        this.text = text;
        //this.image_small = image_small;
        this.media_content = media_content;
        this.media_type = media_type;
        this.vote_counter = vote_counter;
        this.reply_counter = reply_counter;
        this.badge = badge;
        this.image_url = image_url;
        this.my_Vote = my_Vote;
    }

    @Override
    public boolean equals(Object obj) {
        return this.getObjectId().equals(((Post) obj).getObjectId());
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
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

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
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
