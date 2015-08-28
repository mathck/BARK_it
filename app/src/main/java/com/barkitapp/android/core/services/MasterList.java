package com.barkitapp.android.core.services;

import android.content.Context;
import android.widget.Toast;

import com.barkitapp.android.Messages.InitialPostsReceivedEvent;
import com.barkitapp.android.parse.enums.VoteType;
import com.barkitapp.android.parse.objects.Post;
import com.orm.query.Condition;
import com.orm.query.Select;
import com.parse.ParseFile;
import com.parse.ParseObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.greenrobot.event.EventBus;

public class MasterList {

    public static void StoreMasterList(Context context, HashMap<String, Object> result) {
        try {

            ArrayList<ParseObject> posts = (ArrayList<ParseObject>) result.get("posts");
            ArrayList<ParseObject> votes = (ArrayList<ParseObject>) result.get("votes");

            String myUserId = UserId.get(context);

            for (ParseObject post : posts) {

                String objectId = post.getObjectId();

                if(Select.from(Post.class).where(Condition.prop("object_Id").eq(objectId)).count() == 0) {
                    // INSERT NEW IF NOT EXIST

                    ParseFile file = post.getParseFile("image_small");
                    String image_url = "";

                    if(file != null)
                        image_url = file.getUrl();

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
                            image_url,
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

    public static void clearMasterList() {
        try {
            Post.deleteAll(Post.class);
        }
        catch (Exception e)
        {

        }
    }

    public static List<Post> GetMasterList() {
        return Post.listAll(Post.class);
    }

    public static Post GetPost(String objectId) {
        try {
            return Select.from(Post.class).where(Condition.prop("object_Id").eq(objectId)).first();
        }
        catch (Exception e) {
            return null;
        }
    }
}
