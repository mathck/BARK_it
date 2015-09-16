package com.barkitapp.android.base;

import android.app.Application;
import android.location.Location;
import android.widget.Toast;

import com.barkitapp.android.Messages.MasterListUpdatedEvent;
import com.barkitapp.android.Messages.RequestUpdatePostsEvent;
import com.barkitapp.android.core.services.InternalAppData;
import com.barkitapp.android.core.services.LocationService;
import com.barkitapp.android.core.utility.SharedPrefKeys;
import com.barkitapp.android.parse.Connection;
import com.barkitapp.android.parse.functions.UpdatePosts;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;

import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import de.greenrobot.event.EventBus;
import io.nlopez.smartlocation.OnLocationUpdatedListener;
import io.nlopez.smartlocation.SmartLocation;

public class Setup extends Application implements OnLocationUpdatedListener, UpdatePosts.OnUpdatePostsCompleted {
    @Override
    public void onCreate() {
        super.onCreate();

        // establish parse connection
        Connection.Connect(this);

        InternalAppData.Store(this, SharedPrefKeys.HAS_SET_MANUAL_LOCATION, false);
        InternalAppData.Store(this, SharedPrefKeys.MANUAL_TITLE, "");

        SmartLocation.with(this).location()
                .start(this);
    }

    @Override
    public void onLocationUpdated(Location location) {
        LocationService.storeLocation(this, location);

        if(InternalAppData.getBoolean(this, SharedPrefKeys.WAITING_FOR_GPS)) {
            InternalAppData.Store(this, SharedPrefKeys.WAITING_FOR_GPS, false);

            EventBus.getDefault().post(new RequestUpdatePostsEvent());
        }

        long diff = new Date().getTime() - LocationService.getLocation(this).getDate().getTime();

        long minutes = TimeUnit.MILLISECONDS.toMinutes(diff);

        if(minutes > 5)
        {
            Toast.makeText(this, "Reloading posts for new position", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onUpdatePostsCompleted(HashMap<String, Object> result) {
        EventBus.getDefault().post(new MasterListUpdatedEvent());
    }

    @Override
    public void onUpdatePostsFailed(String error) {

    }

    private Tracker mTracker;

    /**
     * Gets the default {@link Tracker} for this {@link Application}.
     * @return tracker
     */
    synchronized public Tracker getDefaultTracker() {
        if (mTracker == null) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            // To enable debug logging use: adb shell setprop log.tag.GAv4 DEBUG
            mTracker = analytics.newTracker("UA-66486624-1");
        }
        return mTracker;
    }
}
