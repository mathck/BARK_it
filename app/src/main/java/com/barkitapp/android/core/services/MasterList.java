package com.barkitapp.android.core.services;

import android.content.Context;
import android.widget.Toast;

import com.barkitapp.android.Messages.InitialPostsReceivedEvent;
import com.barkitapp.android.parse.objects.Post;
import com.orm.query.Condition;
import com.orm.query.Select;
import com.parse.ParseObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.greenrobot.event.EventBus;

public class MasterList {

    public static void StoreMasterList(Context context, HashMap<String, Object> result) {
        try {

            ArrayList<ParseObject> posts = (ArrayList<ParseObject>) result.get("posts");

            for (ParseObject post : posts) {

                String objectId = post.getObjectId();

                if(Select.from(Post.class).where(Condition.prop("object_Id").eq(objectId)).count() == 0) {
                    // INSERT NEW IF NOT EXIST
                    new Post(objectId,
                            post.getString("user_Id"),
                            post.getDate("time_created"),
                            post.getParseGeoPoint("location"),
                            post.getString("text"),
                            post.getString("media_content"),
                            post.getInt("media_type"),
                            post.getInt("vote_counter"),
                            post.getInt("reply_counter"),
                            post.getInt("badge")).save();
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

            EventBus.getDefault().post(new InitialPostsReceivedEvent());

        } catch (Exception e) {
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    public static List<Post> GetMasterList() {
        return Post.listAll(Post.class);
    }
}
