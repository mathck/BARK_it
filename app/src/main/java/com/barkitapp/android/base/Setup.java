package com.barkitapp.android.base;

import android.location.Location;
import android.widget.Toast;

import com.barkitapp.android.Messages.MasterListUpdatedEvent;
import com.barkitapp.android.Messages.RequestUpdatePostsEvent;
import com.barkitapp.android.core.services.InternalAppData;
import com.barkitapp.android.core.services.LocationService;
import com.barkitapp.android.core.utility.SharedPrefKeys;
import com.barkitapp.android.parse.Connection;
import com.barkitapp.android.parse.functions.UpdatePosts;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.orm.SugarApp;

import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import de.greenrobot.event.EventBus;
import io.nlopez.smartlocation.OnLocationUpdatedListener;
import io.nlopez.smartlocation.SmartLocation;

public class Setup extends SugarApp implements OnLocationUpdatedListener, UpdatePosts.OnUpdatePostsCompleted {
    @Override
    public void onCreate() {
        super.onCreate();

        // establish parse connection
        Connection.Connect(this);

        SmartLocation.with(this).location()
                .start(this);

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this).build();
        ImageLoader.getInstance().init(config);
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
}
