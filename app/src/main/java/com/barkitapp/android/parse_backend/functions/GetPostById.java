package com.barkitapp.android.parse_backend.functions;

import android.content.Context;
import android.widget.Toast;

import com.barkitapp.android.events.RecievedPostForNotification;
import com.barkitapp.android.parse_backend.objects.Post;
import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.SaveCallback;

import java.util.HashMap;

import de.greenrobot.event.EventBus;

public class GetPostById {

    public static void run(final Context context,String user_id, String post_id) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("user_id", user_id);
        params.put("post_id", post_id);

        ParseCloud.callFunctionInBackground("GetPostById", params, new FunctionCallback<ParseObject>() {
            public void done(final ParseObject post, ParseException e) {
                if (e == null) {

                    post.pinInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            EventBus.getDefault().post(new RecievedPostForNotification(new Post(post)));
                        }
                    });

//                    Post result = new Post(post.getObjectId(),
//                                post.getString("user_id"),
//                                post.getDate("time_created"),
//                                post.getParseGeoPoint("location"),
//                                post.getString("text"),
//                                post.getString("media_content"),
//                                post.getInt("media_type"),
//                                post.getInt("vote_counter"),
//                                post.getInt("reply_counter"),
//                                post.getInt("badge"),
//                                VoteType.NEUTRAL.ordinal());
//
//                    result.save();
                }
                else {
                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}

