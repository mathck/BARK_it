package com.barkitapp.android._core.utility;

import com.barkitapp.android.startup.Setup;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

public class GoogleAnalytics {

    public static void sendEvent(Tracker tracker, String categoryId, String actionId) {

        tracker.send(new HitBuilders.EventBuilder()
                .setCategory(categoryId)
                .setAction(actionId)
                .build());
    }

    public static final String CATEGORY_VOTING_FREQUENCY = "VotingFrequency";
    public static final String CATEGORY_VOTING_ACTION_UP = "Upvote";
    public static final String CATEGORY_VOTING_ACTION_DOWN = "Downvote";
}
