package com.barkitapp.android.parse.objects;

import android.content.Context;

import com.barkitapp.android.core.services.InternalAppData;
import com.barkitapp.android.core.utility.SharedPrefKeys;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MasterListPosts {

    List<Post> mMasterList = new ArrayList<>();

    public MasterListPosts(Context context) {
        // todo get data from prefs JSON
        // mMasterList =
        String jsonData = InternalAppData.getString(context, SharedPrefKeys.MASTER_LIST_POSTS);

        try {

            JSONObject jObject = new JSONObject(jsonData);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void add(Post post) {
        mMasterList.add(post);
    }

    public void storeOnDevice() {
        // todo store data in JSON on prefs
    }

    public void clear() {

    }
}
