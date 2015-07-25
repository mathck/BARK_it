package com.barkitapp.android.parse.functions;

import com.barkitapp.android.Messages.RecievedPostForNotification;
import com.barkitapp.android.parse.enums.VoteType;
import com.barkitapp.android.parse.objects.Post;
import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseObject;

import java.util.HashMap;

import de.greenrobot.event.EventBus;

public class GetPostById {

    public static void run(String user_id, String post_id) {
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("user_id", user_id);
        params.put("post_id", post_id);

        ParseCloud.callFunctionInBackground("GetPostById", params, new FunctionCallback<ParseObject>() {
            public void done(ParseObject post, ParseException e) {
                if (e == null) {

                    Post result = new Post(post.getObjectId(),
                                post.getString("user_id"),
                                post.getDate("time_created"),
                                post.getParseGeoPoint("location"),
                                post.getString("text"),
                                post.getString("media_content"),
                                post.getInt("media_type"),
                                post.getInt("vote_counter"),
                                post.getInt("reply_counter"),
                                post.getInt("badge"),
                                VoteType.NEUTRAL.ordinal());

                    EventBus.getDefault().post(new RecievedPostForNotification(result));
                }
            }
        });
    }
}

