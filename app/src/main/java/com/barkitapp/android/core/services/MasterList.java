package com.barkitapp.android.core.services;

import android.content.Context;
import android.widget.Toast;

import com.barkitapp.android.Messages.InitialPostsReceivedEvent;
import com.barkitapp.android.core.utility.PostComperator;
import com.barkitapp.android.parse.enums.Order;
import com.barkitapp.android.parse.enums.VoteType;
import com.barkitapp.android.parse.objects.Post;
import com.barkitapp.android.prime.HotFragment;
import com.parse.GetCallback;
import com.orm.query.Condition;
import com.orm.query.Select;
import com.parse.ParseFile;
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

//            for(ParseObject post : posts) {
//                for(ParseObject vote : votes) {
//                    updateVoteForPost(vote);
//                }
//            }

                if(Select.from(Post.class).where(Condition.prop("object_Id").eq(objectId)).count() == 0) {
                    // INSERT NEW IF NOT EXIST
                    new Post(objectId,
                            post.getString("user_id"),
                            post.getDate("time_created"),
                            post.getParseGeoPoint("location"),
                            post.getString("text"),
                            post.getString("media_content"),
                            post.getInt("media_type"),
                            post.getInt("vote_counter"),
                            post.getInt("reply_counter"),
                            post.getInt("badge"),
                            VoteType.NEUTRAL.ordinal()).save();
                }
                else {
                    // UPDATE IN DB
                    Post postInDb = Select.from(Post.class).where(Condition.prop("object_Id").eq(objectId)).first();

                    //postInDb.setUserId(post.getString("user_Id"));
                    //postInDb.setTime_created(post.getDate("time_created"));
                    //postInDb.setLatitude(post.getParseGeoPoint("location").getLatitude());
                    //postInDb.setLongitude(post.getParseGeoPoint("location").getLongitude());
                    //postInDb.setText(post.getString("text"));
                    //postInDb.setMedia_content(post.getString("media_content"));
                    //postInDb.setMedia_type(post.getInt("media_type"));
                    postInDb.setVote_counter(post.getInt("vote_counter"));
                    postInDb.setReply_counter(post.getInt("reply_counter"));
                    //postInDb.setBadge(post.getInt("badge"));

                    postInDb.save();
                }
            }

            for(ParseObject vote : votes) {
                String userId = vote.getString("user_id");

                if(myUserId.equals(userId)) {
                    Post post = GetPost(vote.getString("content_id"));
                    post.setMy_Vote(vote.getInt("vote_type"));
                    post.save();
                }
            }

            EventBus.getDefault().post(new InitialPostsReceivedEvent());

        } catch (Exception e) {
            if(context != null)
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    public static void clearMasterList(Order order) {
        try {
            ParseObject.unpinAllInBackground(order.toString(), GetMasterList(order));
            //ParseObject.unpinAllInBackground(order.toString(), GetMasterListVotes(order));
        }
        catch(Exception e) {

        }
    }

    public static void clearMasterListAll() {
        clearMasterList(Order.TIME);
        clearMasterList(Order.UP_VOTES);
    }

//    public static HashMap<String, VoteType> GetVotes(Order order) {
//        try {
//            List<ParseObject> objects = GetMasterListVotes(order);
//            HashMap<String, VoteType> votes = new HashMap<>();
//
//            for(ParseObject obj : objects) {
//                votes.put(obj.getString("content_id"), VoteType.values()[obj.getInt("vote_type")]);
//            }
//
//            return votes;
//
//        } catch (Exception e) {
//            return new HashMap<>();
//        }
//    }

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

//    public static List<ParseObject> GetMasterListVotes(Order order) {
//        try {
//            ParseQuery<ParseObject> query = ParseQuery.getQuery("Vote");
//            query.fromLocalDatastore().fromPin(order.toString());
//            return query.find();
//        }
//        catch (Exception e) {
//            return new ArrayList<ParseObject>();
//        }
//    }

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
