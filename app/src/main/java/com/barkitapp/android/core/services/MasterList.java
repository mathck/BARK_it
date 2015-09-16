package com.barkitapp.android.core.services;

import android.content.Context;
import android.widget.Toast;

import com.barkitapp.android.Messages.InitialPostsReceivedEvent;
import com.barkitapp.android.core.utility.PostComperator;
import com.barkitapp.android.parse.enums.Order;
import com.barkitapp.android.parse.enums.VoteType;
import com.barkitapp.android.parse.objects.Post;
import com.parse.GetCallback;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.greenrobot.event.EventBus;

public class MasterList {

    public static void StoreMasterList(Context context, HashMap<String, Object> result, Order order) {
        try {

            ArrayList<ParseObject> posts = (ArrayList<ParseObject>) result.get("posts");
            ArrayList<ParseObject> votes = (ArrayList<ParseObject>) result.get("votes");

            //String myUserId = UserId.get(context);

            ParseObject.pinAllInBackground(order.toString(), posts);
            ParseObject.pinAllInBackground(order.toString(), votes);

//            for(ParseObject vote : votes) {
//                String userId = vote.getString("user_id");
//
//                if(myUserId.equals(userId)) {
//                    ParseObject post = GetPost(vote.getString("content_id"));
//                    post.get("asdasd") = "sad";
//                    post.setMy_Vote(vote.getInt("vote_type"));
//                    post.save();
//                }
//            }

            EventBus.getDefault().post(new InitialPostsReceivedEvent());

        } catch (Exception e) {
            if(context != null)
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    public static void clearMasterList(Order order) {
        try {
            ParseObject.unpinAllInBackground(order.toString(), GetMasterList(order));
            ParseObject.unpinAllInBackground(order.toString(), GetMasterListVotes());
        }
        catch(Exception e) {

        }
    }

    public static void clearMasterListAll() {
        clearMasterList(Order.TIME);
        clearMasterList(Order.UP_VOTES);
    }

    public static HashMap<String, VoteType> GetVotes() {
        try {
            ParseQuery<ParseObject> query = ParseQuery.getQuery("Post");
            query.fromLocalDatastore();
            List<ParseObject> objects = query.find();
            HashMap<String, VoteType> votes = new HashMap<>();

            for(ParseObject obj : objects) {
                votes.put(obj.getString("content_id"), VoteType.values()[obj.getInt("vote_type")]);
            }

            return votes;

        } catch (Exception e) {
            return new HashMap<>();
        }
    }

    public static List<Post> GetMasterListPost(Order order) {
        try {
            ParseQuery<ParseObject> query = ParseQuery.getQuery("Post");
            query.fromLocalDatastore().fromPin(order.toString());
            List<ParseObject> objects = query.find();
            ArrayList<Post> posts = new ArrayList<>();

            for(ParseObject obj : objects) {
                posts.add(new Post(obj));
            }

            return posts;
        }
        catch (Exception e) {
            return new ArrayList<Post>();
        }
    }

    public static List<ParseObject> GetMasterList(Order order) {
        try {
            ParseQuery<ParseObject> query = ParseQuery.getQuery("Post");
            query.fromLocalDatastore().fromPin(order.toString());
            return query.find();
        }
        catch (Exception e) {
            return new ArrayList<ParseObject>();
        }
    }

    public static List<ParseObject> GetMasterListVotes() {
        try {
            ParseQuery<ParseObject> query = ParseQuery.getQuery("Vote");
            query.fromLocalDatastore();
            return query.find();
        }
        catch (Exception e) {
            return new ArrayList<ParseObject>();
        }
    }

    public static ParseObject GetPost(String objectId) {
        try {
            ParseQuery<ParseObject> query = ParseQuery.getQuery("Post");
            query.fromLocalDatastore();
            return query.get(objectId); // do in background
        }
        catch (Exception e) {
            return null;
        }
    }

    public static Post GetPostPost(String objectId) {
        ParseObject post = GetPost(objectId);
        return new Post(post);
    }
}
