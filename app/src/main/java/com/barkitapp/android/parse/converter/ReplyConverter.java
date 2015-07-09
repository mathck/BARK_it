package com.barkitapp.android.parse.converter;

import android.content.Context;
import android.widget.Toast;

import com.barkitapp.android.parse.objects.Reply;
import com.parse.ParseObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ReplyConverter {

    public static List<Reply> run(Context context, HashMap<String, Object> result) {
        try {

            List<Reply> replyList = new ArrayList<>();
            ArrayList<ParseObject> posts = (ArrayList<ParseObject>) result.get("replies");

            replyList.clear();

            for (ParseObject post : posts) {
                replyList.add(new Reply(
                        post.getString("objectId"),
                        post.getString("userId"),
                        post.getString("postId"),

                        post.getDate("time_created"),
                        post.getString("text"),
                        post.getInt("vote_counter"),
                        post.getInt("badge"),

                        post.getParseGeoPoint("location")));
            }

            return replyList;

        } catch (Exception e) {
            if(context != null)
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
        }

        return new ArrayList<>();
    }

}
