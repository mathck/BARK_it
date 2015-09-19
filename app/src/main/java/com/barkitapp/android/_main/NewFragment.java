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

public class NewFragment extends PostFragment {

    private Tracker mTracker;

    @Override
    public void sort(List<Post> masterList) {
        Collections.sort(masterList, new PostComperator.Time());
    }

    @Override
    public Order getOrder() {
        return Order.TIME;
    }

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        Setup application = (Setup) getActivity().getApplication();
        mTracker = application.getDefaultTracker();
    }

    @Override
    public void onResume() {
        super.onResume();

        mTracker.setScreenName(NewFragment.class.getSimpleName());
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }
}
