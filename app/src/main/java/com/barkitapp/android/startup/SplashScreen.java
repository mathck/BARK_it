package com.barkitapp.android.startup;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import com.barkitapp.android.events.UserIdRecievedEvent;
import com.barkitapp.android.R;
import com.barkitapp.android._core.objects.Coordinates;
import com.barkitapp.android._core.services.InternalAppData;
import com.barkitapp.android._core.services.LocationService;
import com.barkitapp.android._core.services.MasterList;
import com.barkitapp.android._core.services.UserId;
import com.barkitapp.android._core.utility.Connectivity;
import com.barkitapp.android._core.utility.Constants;
import com.barkitapp.android._core.utility.SharedPrefKeys;
import com.barkitapp.android.parse_backend.enums.Order;
import com.barkitapp.android.parse_backend.functions.UpdatePosts;
import com.barkitapp.android.parse_backend.functions.UpdatePostsLat;
import com.barkitapp.android._main.MainActivity;
import com.parse.ParseGeoPoint;
import com.parse.ParseInstallation;
import com.parse.ParseObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import de.greenrobot.event.EventBus;
import io.nlopez.smartlocation.SmartLocation;

public class SplashScreen extends Activity implements UpdatePosts.OnUpdatePostsCompleted {

    //private TextView mSpeech;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        //ImageView logo = ((ImageView) findViewById(R.id.imgLogo));
        //mSpeech = (TextView) findViewById(R.id.speech);

        // random speech bubble for the dog
        //if(mSpeech != null) {
        //    mSpeech.setText(RandomBarkGenerator.Run(this));
        //}

        // if emulator TODO remove me
        // UserId.store(this, "qsct9gQBzc");

        String userId = UserId.get(this);
        if(userId != null && !userId.isEmpty())
        {
            AppStart(false);
        }
        else {
            // todo invite screen

            Intent i = new Intent(SplashScreen.this, InviteCodeRestriction.class);
            startActivity(i);
            finish();
        }
    }

    private void AppStart(final boolean firstStart) {

        MasterList.clearMasterListAllSlow();

        // Notify about location info
        if(!SmartLocation.with(this).location().state().locationServicesEnabled())
            Toast.makeText(this, R.string.enable_location_services, Toast.LENGTH_LONG).show();

        if(!SmartLocation.with(this).location().state().isGpsAvailable())
            Toast.makeText(this, R.string.enable_gps_better_barkit_experience, Toast.LENGTH_LONG).show();

        /*
        for EMULATOR
        Location targetLocation = new Location("");//provider name is unecessary
        targetLocation.setLatitude(48.20877d);//your coords of course
        targetLocation.setLongitude(16.37071d);
        LocationService.storeLocation(this, targetLocation);
        */

        // get last known location
        Coordinates lastKnownLocation = LocationService.getLocation(this);

        if(lastKnownLocation == null) {
            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            Location location = locationManager.getLastKnownLocation(locationManager.getBestProvider(new Criteria(), true));

            if (location != null)
                lastKnownLocation = new Coordinates(location.getLatitude(), location.getLongitude(), new Date());
        }

        if(!Connectivity.isOnline(this)) {
            Toast.makeText(this, R.string.no_internet_connection, Toast.LENGTH_LONG).show();
        }
        else {
            if(lastKnownLocation != null)
            {
                // get Posts from Parse
                UpdatePostsLat.run(this, this,
                        UserId.get(this),
                        new ParseGeoPoint(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude()),
                        new ParseGeoPoint(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude()),
                        Constants.GET_POSTS_COUNT,
                        Order.TIME,
                        true);
            }
            else {
                Toast.makeText(this, R.string.loading_gps_data, Toast.LENGTH_LONG).show();
                InternalAppData.Store(this, SharedPrefKeys.WAITING_FOR_GPS, true);
            }
        }

        // close spalsh screen after some time
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i = new Intent(SplashScreen.this, firstStart ? BarkitAppIntro.class : MainActivity.class);
                startActivity(i);
                finish();
            }
        }, Constants.SPLASH_TIME_OUT);
    }

    public void onEvent(UserIdRecievedEvent event) {

        ParseInstallation installation = ParseInstallation.getCurrentInstallation();
        installation.put("user_id", UserId.get(this));
        installation.saveInBackground();

        AppStart(true);
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();

        //if(mLogo != null) {
        //    YoYo.with(Techniques.Bounce)
        //            .duration(1000)
        //            .playOn(mLogo);
        //}

        //if(mSpeech != null) {
        //    YoYo.with(Techniques.Tada)
        //            .duration(1000)
        //            .playOn(mSpeech);
        //}
    }

    @Override
    public void onUpdatePostsCompleted(HashMap<String, ArrayList<ParseObject>> result) {
        MasterList.StoreMasterList(this, result, Order.TIME);
    }

    @Override
    public void onUpdatePostsFailed(String error) {
        
    }
}
