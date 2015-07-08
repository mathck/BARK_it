package com.barkitapp.android.core.services;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.widget.Toast;

import com.barkitapp.android.Messages.InitialPostsReceivedEvent;
import com.barkitapp.android.core.utility.Constants;
import com.barkitapp.android.core.utility.SharedPrefKeys;
import com.barkitapp.android.parse.objects.Post;
import com.parse.ParseObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.greenrobot.event.EventBus;

public class MasterList {

    public static void StoreMasterList(Context context, HashMap<String, Object> result) {
        try {

            List<Post> postsList = new ArrayList<>();
            ArrayList<ParseObject> posts = (ArrayList<ParseObject>) result.get("posts");

            postsList.clear();

            for (ParseObject post : posts) {
                postsList.add(new Post(post.getString("objectId"),
                        post.getString("userId"),
                        post.getDate("time_created"),
                        post.getParseGeoPoint("location"),
                        post.getString("text"),
                        post.getParseFile("image_small"),
                        post.getString("media_content"),
                        post.getInt("media_type"),
                        post.getInt("vote_counter"),
                        post.getInt("reply_counter"),
                        post.getInt("badge")));
            }

            File file = new File(context.getDir("data", Context.MODE_PRIVATE), Constants.LOCAL_MASTER_LIST);
            boolean deleted = file.delete();

            ObjectOutputStream outputStream = null;

            outputStream = new ObjectOutputStream(new FileOutputStream(file));
            outputStream.writeObject(postsList);
            outputStream.flush();
            outputStream.close();

            EventBus.getDefault().post(new InitialPostsReceivedEvent());
            InternalAppData.Store(context, SharedPrefKeys.MASTER_LIST_UPDATED, true);

        } catch (Exception e) {
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    public static List<Post> GetMasterList(Context context, SwipeRefreshLayout refreshLayout) {

        List<Post> values = new ArrayList<>();

        int modePrivate = Context.MODE_PRIVATE;

        File file = new File(context.getDir("data", modePrivate), Constants.LOCAL_MASTER_LIST);
        ObjectInputStream inputStream = null;

        if(!InternalAppData.getBoolean(context, SharedPrefKeys.MASTER_LIST_UPDATED)) {
            refreshLayout.setRefreshing(true);
        }

        if(!file.exists())
        {
            return new ArrayList<>();
        }

        try {

            inputStream = new ObjectInputStream(new FileInputStream(file));
            List<Post> result = (ArrayList<Post>) inputStream.readObject();
            inputStream.close();

            for (Post post : result) {
                values.add(post);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return values;
    }
}
