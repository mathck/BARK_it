package com.barkitapp.android.base;

import android.location.Location;
import android.widget.Toast;

import com.barkitapp.android.core.services.LocationService;
import com.barkitapp.android.parse.Connection;
import com.orm.SugarApp;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import io.nlopez.smartlocation.OnLocationUpdatedListener;
import io.nlopez.smartlocation.SmartLocation;

public class Setup extends SugarApp implements OnLocationUpdatedListener {
    @Override
    public void onCreate() {
        super.onCreate();

        // establish parse connection
        Connection.Connect(this);

        // Notify about location info
        if(!SmartLocation.with(this).location().state().locationServicesEnabled())
            Toast.makeText(this, "Please enable Location Services on your device.", Toast.LENGTH_LONG).show();

        if(!SmartLocation.with(this).location().state().isGpsAvailable())
            Toast.makeText(this, "Please enable GPS on your device to improve your BARK it experience.", Toast.LENGTH_LONG).show();

        SmartLocation.with(this).location()
                .start(this);
    }

    @Override
    public void onLocationUpdated(Location location) {
        LocationService.storeLocation(this, location);

        long diff = new Date().getTime() - LocationService.getLocation(this).getDate().getTime();

        long minutes = TimeUnit.MILLISECONDS.toMinutes(diff);

        if(minutes > 5)
        {
            Toast.makeText(this, "Reloading posts for new position", Toast.LENGTH_LONG).show();
        }
    }
}
