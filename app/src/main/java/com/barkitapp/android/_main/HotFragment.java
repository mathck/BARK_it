package com.barkitapp.android._main;

import android.os.Bundle;

import com.barkitapp.android.startup.Setup;
import com.barkitapp.android._core.utility.PostComperator;
import com.barkitapp.android.parse_backend.enums.Order;
import com.barkitapp.android.parse_backend.objects.Post;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.util.Collections;
import java.util.List;

public class HotFragment extends PostFragment {

    private Tracker mTracker;

    @Override
    public void sort(List<Post> masterList) {
        Collections.sort(masterList, new PostComperator.Vote());
    }

    @Override
    public Order getOrder() {
        return Order.UP_VOTES;
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
            mTracker.setScreenName(HotFragment.class.getSimpleName());
            mTracker.send(new HitBuilders.ScreenViewBuilder().build());
        }
        else {  }
    }
}
