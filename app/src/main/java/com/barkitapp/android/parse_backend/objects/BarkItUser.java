package com.barkitapp.android.parse_backend.objects;

import com.barkitapp.android.parse_backend.enums.VoteType;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;

import java.util.Date;

/**
 * BarkItUser Class from Parse
 */
public class BarkItUser {

    String objectId;
    Date time_created;
    int downvote_counter;
    int post_counter;
    int received_downvote_counter;
    int received_reply_counter;
    int received_upvote_counter;
    int recent_downvote_counter;
    int recent_post_counter;
    int recent_received_dowvote_counter;
    int recent_received_replay_counter;
    int recent_received_upvote_counter;
    int recent_referred_friend_counter;
    int recent_reply_counter;
    int recent_upvote_counter;
    int referred_friend_counter;
    int reply_counter;
    int upvote_counter;
    int recent_quarantine_counter;
    int quarantine_counter;

    public BarkItUser(ParseObject parseObject) {
        this.objectId = parseObject.getObjectId();
        this.time_created = parseObject.getDate("time_created");
        this.downvote_counter = parseObject.getInt("downvote_counter");
        this.post_counter = parseObject.getInt("post_counter");
        this.received_downvote_counter = parseObject.getInt("received_downvote_counter");
        this.received_reply_counter = parseObject.getInt("received_reply_counter");
        this.received_upvote_counter = parseObject.getInt("received_upvote_counter");
        this.recent_downvote_counter = parseObject.getInt("recent_downvote_counter");
        this.recent_post_counter = parseObject.getInt("recent_post_counter");
        this.recent_received_dowvote_counter = parseObject.getInt("recent_received_dowvote_counter");
        this.recent_received_replay_counter = parseObject.getInt("recent_received_replay_counter");
        this.recent_received_upvote_counter = parseObject.getInt("recent_received_upvote_counter");
        this.recent_referred_friend_counter = parseObject.getInt("recent_referred_friend_counter");
        this.recent_reply_counter = parseObject.getInt("recent_reply_counter");
        this.recent_upvote_counter = parseObject.getInt("recent_upvote_counter");
        this.referred_friend_counter = parseObject.getInt("referred_friend_counter");
        this.reply_counter = parseObject.getInt("reply_counter");
        this.upvote_counter = parseObject.getInt("upvote_counter");
        this.recent_quarantine_counter = parseObject.getInt("recent_quarantine_counter");
        this.quarantine_counter = parseObject.getInt("quarantine_counter");
    }

    @Override
    public boolean equals(Object obj) {
        return getClass() == obj.getClass() && this.getObjectId().equals(((BarkItUser) obj).getObjectId());
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public Date getTime_created() {
        return time_created;
    }

    public void setTime_created(Date time_created) {
        this.time_created = time_created;
    }

    public int getDownvote_counter() {
        return downvote_counter;
    }

    public void setDownvote_counter(int downvote_counter) {
        this.downvote_counter = downvote_counter;
    }

    public int getPost_counter() {
        return post_counter;
    }

    public void setPost_counter(int post_counter) {
        this.post_counter = post_counter;
    }

    public int getReceived_downvote_counter() {
        return received_downvote_counter;
    }

    public void setReceived_downvote_counter(int received_downvote_counter) {
        this.received_downvote_counter = received_downvote_counter;
    }

    public int getReceived_reply_counter() {
        return received_reply_counter;
    }

    public void setReceived_reply_counter(int received_reply_counter) {
        this.received_reply_counter = received_reply_counter;
    }

    public int getReceived_upvote_counter() {
        return received_upvote_counter;
    }

    public void setReceived_upvote_counter(int received_upvote_counter) {
        this.received_upvote_counter = received_upvote_counter;
    }

    public int getRecent_downvote_counter() {
        return recent_downvote_counter;
    }

    public void setRecent_downvote_counter(int recent_downvote_counter) {
        this.recent_downvote_counter = recent_downvote_counter;
    }

    public int getRecent_post_counter() {
        return recent_post_counter;
    }

    public void setRecent_post_counter(int recent_post_counter) {
        this.recent_post_counter = recent_post_counter;
    }

    public int getRecent_received_dowvote_counter() {
        return recent_received_dowvote_counter;
    }

    public void setRecent_received_dowvote_counter(int recent_received_dowvote_counter) {
        this.recent_received_dowvote_counter = recent_received_dowvote_counter;
    }

    public int getRecent_received_replay_counter() {
        return recent_received_replay_counter;
    }

    public void setRecent_received_replay_counter(int recent_received_replay_counter) {
        this.recent_received_replay_counter = recent_received_replay_counter;
    }

    public int getRecent_received_upvote_counter() {
        return recent_received_upvote_counter;
    }

    public void setRecent_received_upvote_counter(int recent_received_upvote_counter) {
        this.recent_received_upvote_counter = recent_received_upvote_counter;
    }

    public int getRecent_referred_friend_counter() {
        return recent_referred_friend_counter;
    }

    public void setRecent_referred_friend_counter(int recent_referred_friend_counter) {
        this.recent_referred_friend_counter = recent_referred_friend_counter;
    }

    public int getRecent_reply_counter() {
        return recent_reply_counter;
    }

    public void setRecent_reply_counter(int recent_reply_counter) {
        this.recent_reply_counter = recent_reply_counter;
    }

    public int getRecent_upvote_counter() {
        return recent_upvote_counter;
    }

    public void setRecent_upvote_counter(int recent_upvote_counter) {
        this.recent_upvote_counter = recent_upvote_counter;
    }

    public int getReferred_friend_counter() {
        return referred_friend_counter;
    }

    public void setReferred_friend_counter(int referred_friend_counter) {
        this.referred_friend_counter = referred_friend_counter;
    }

    public int getReply_counter() {
        return reply_counter;
    }

    public void setReply_counter(int reply_counter) {
        this.reply_counter = reply_counter;
    }

    public int getUpvote_counter() {
        return upvote_counter;
    }

    public void setUpvote_counter(int upvote_counter) {
        this.upvote_counter = upvote_counter;
    }

    public int getRecent_quarantine_counter() {
        return recent_quarantine_counter;
    }

    public void setRecent_quarantine_counter(int recent_quarantine_counter) {
        this.recent_quarantine_counter = recent_quarantine_counter;
    }

    public int getQuarantine_counter() {
        return quarantine_counter;
    }

    public void setQuarantine_counter(int quarantine_counter) {
        this.quarantine_counter = quarantine_counter;
    }

    public int getLevel() {
        return getPost_counter() / 20 + getReferred_friend_counter() + getUpvote_counter() / 120 + getReply_counter() / 60;
    }
}
