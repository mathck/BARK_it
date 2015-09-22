package com.barkitapp.android.my_stuff;

import android.os.Bundle;

import com.barkitapp.android._core.utility.PostComperator;
import com.barkitapp.android._main.PostFragment;
import com.barkitapp.android.parse_backend.enums.Order;
import com.barkitapp.android.parse_backend.objects.Post;
import com.barkitapp.android.startup.Setup;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.util.Collections;
import java.util.List;

public class MyRepliesFragment extends PostFragment {

    private Tracker mTracker;

    @Override
    public void sort(List<Post> masterList) {
        Collections.sort(masterList, new PostComperator.Time());
    }

    @Override
    public Order getOrder() {
        return Order.MY_REPLIES;
    }

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        Setup application = (Setup) getActivity().getApplication();
        mTracker = application.getDefaultTracker();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            mTracker.setScreenName(MyRepliesFragment.class.getSimpleName());
            mTracker.send(new HitBuilders.ScreenViewBuilder().build());
        }
        else {  }
    }
}
