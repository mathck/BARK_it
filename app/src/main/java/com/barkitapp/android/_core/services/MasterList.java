package com.barkitapp.android._core.services;

import android.content.Context;
import android.widget.Toast;

import com.barkitapp.android.events.MasterListUpdated;
import com.barkitapp.android.parse_backend.enums.Order;
import com.barkitapp.android.parse_backend.objects.Post;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.greenrobot.event.EventBus;

public class MasterList {

    public static void StoreMasterList(Context context, HashMap<String, ArrayList<ParseObject>> result, final Order order) {
        try {

            ArrayList<ParseObject> posts = result.get("posts");
            ArrayList<ParseObject> votes = result.get("votes");

            for(ParseObject post : posts) {
                ParseFile file = post.getParseFile("image_small");
                String image_url = "";

                if(file != null)
                    image_url = file.getUrl();

                post.put("image_url", image_url);
            }

            String myUserId = UserId.get(context);

            for(ParseObject vote : votes) {
                String userId = vote.getString("user_id");

                if(myUserId.equals(userId)) {
                    ParseObject post = posts.get(getIndexByProperty(posts, vote.getString("content_id")));
                    post.put("my_vote", vote.getInt("vote_type"));
                }
            }

            // store posts in database
            if(!posts.isEmpty())
            {
                ParseObject.pinAllInBackground(order.toString(), posts, new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        EventBus.getDefault().post(new MasterListUpdated(order));
                    }
                });
            }

        } catch (Exception e) {
            if(context != null)
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private static int getIndexByProperty(ArrayList<ParseObject> posts, String content_id) {
        for (int i = 0; i < posts.size(); i++) {
            if (posts.get(i) !=null && posts.get(i).getObjectId().equals(content_id)) {
                return i;
            }
        }

        return -1;
    }

    public static void clearMasterList(Order order) {
        try {
            ParseObject.unpinAllInBackground(order.toString(), GetMasterList(order));
        }
        catch(Exception ignored) {

        }
    }

    public static void clearMasterListSlow(Order order) {
        try {
            ParseObject.unpinAll(order.toString(), GetMasterList(order));
        }
        catch(Exception ignored) {

        }
    }

    public static void clearMasterListAll() {
        try {
            for (Order order : Order.values()) {
                clearMasterList(order);
            }
        }
        catch(Exception ignored) {

        }
    }

    public static void clearMasterListAllSlow() {
        try {
            for (Order order : Order.values()) {
                clearMasterListSlow(order);
            }
        }
        catch(Exception ignored) {

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
            return new ArrayList<>();
        }
    }

    public static List<ParseObject> GetMasterList(Order order) {
        try {
            ParseQuery<ParseObject> query = ParseQuery.getQuery("Post");
            query.fromLocalDatastore().fromPin(order.toString());
            return query.find();
        }
        catch (Exception e) {
            return new ArrayList<>();
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

        if(post == null)
            return null;

        return new Post(post);
    }
}
