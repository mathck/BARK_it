package com.barkitapp.android.parse_backend.objects;

import com.barkitapp.android._core.objects.Coordinates;
import com.parse.ParseGeoPoint;

import java.util.Date;

/**
 * Reply Class from Parse
 */
public class Reply {

    String objectId;
    String userId;
    String postId;

    Date time_created;
    String text;
    int vote_counter;
    int badge;

    int my_Vote;

    Coordinates location;

    public Reply(String objectId, String userId, String postId, Date time_created, String text, int vote_counter, int badge, ParseGeoPoint location, int my_Vote) {
        this.objectId = objectId;
        this.userId = userId;
        this.postId = postId;

        this.time_created = time_created;
        this.text = text;
        this.vote_counter = vote_counter;
        this.badge = badge;

        this.location = new Coordinates(location.getLatitude(), location.getLongitude(), new Date());

        this.my_Vote = my_Vote;
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

    public Coordinates getLocation() {
        return location;
    }

    public void setLocation(Coordinates location) {
        this.location = location;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getVote_counter() {
        return vote_counter;
    }

    public void setVote_counter(int vote_counter) {
        this.vote_counter = vote_counter;
    }

    public int getMy_Vote() {
        return my_Vote;
    }

    public void setMy_Vote(int my_Vote) {
        this.my_Vote = my_Vote;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public int getBadge() {
        return badge;
    }

    public void setBadge(int badge) {
        this.badge = badge;
    }
}
