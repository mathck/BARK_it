package com.barkitapp.android.startup;

import android.app.Application;
import android.location.Location;
import android.widget.Toast;

import com.barkitapp.android._core.objects.Coordinates;
import com.barkitapp.android.events.MasterListUpdatedEvent;
import com.barkitapp.android.events.RequestUpdatePostsEvent;
import com.barkitapp.android._core.services.InternalAppData;
import com.barkitapp.android._core.services.LocationService;
import com.barkitapp.android._core.services.MasterList;
import com.barkitapp.android._core.utility.SharedPrefKeys;
import com.barkitapp.android.parse_backend.connection.Connection;
import com.barkitapp.android.parse_backend.enums.Order;
import com.barkitapp.android.parse_backend.functions.UpdatePosts;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.parse.ParseObject;

import java.util.ArrayList;
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
        InternalAppData.Store(this, SharedPrefKeys.RADIUS, 5000L);

        SmartLocation.with(this).location()
                .start(this);

        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
            .cacheInMemory(true)
            .cacheOnDisk(true)
            .displayer(new FadeInBitmapDisplayer(750))
            .build();

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
            .defaultDisplayImageOptions(defaultOptions)
            .build();

        ImageLoader.getInstance().init(config);
    }

    @Override
    public void onLocationUpdated(Location location) {
        LocationService.storeLocation(this, location);

        if(InternalAppData.getBoolean(this, SharedPrefKeys.WAITING_FOR_GPS)) {
            InternalAppData.Store(this, SharedPrefKeys.WAITING_FOR_GPS, false);

            EventBus.getDefault().post(new RequestUpdatePostsEvent());
        }

        Coordinates lastUpdateLocation = LocationService.getLocation(this);

        if(lastUpdateLocation != null) {
            Date lastUpdateDate = lastUpdateLocation.getDate();

            if(lastUpdateDate != null) {
                long diff = new Date().getTime() - lastUpdateDate.getTime();

                long minutes = TimeUnit.MILLISECONDS.toMinutes(diff);

                if(minutes > 5)
                {
                    Toast.makeText(this, "Reloading posts for new position", Toast.LENGTH_LONG).show();
                }
            }
        }


    }

    @Override
    public void onUpdatePostsCompleted(HashMap<String, ArrayList<ParseObject>> result) {
        MasterList.StoreMasterList(this, result, Order.TIME);
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
