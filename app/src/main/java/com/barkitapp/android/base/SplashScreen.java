package com.barkitapp.android.base;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.barkitapp.android.Messages.UserIdRecievedEvent;
import com.barkitapp.android.R;
import com.barkitapp.android.core.objects.Coordinates;
import com.barkitapp.android.core.services.DeviceId;
import com.barkitapp.android.core.services.InternalAppData;
import com.barkitapp.android.core.services.LocationService;
import com.barkitapp.android.core.services.MasterList;
import com.barkitapp.android.core.services.UserId;
import com.barkitapp.android.core.utility.Connectivity;
import com.barkitapp.android.core.utility.Constants;
import com.barkitapp.android.core.utility.RandomBarkGenerator;
import com.barkitapp.android.core.utility.SharedPrefKeys;
import com.barkitapp.android.parse.enums.Order;
import com.barkitapp.android.parse.functions.CreateUser;
import com.barkitapp.android.parse.functions.UpdatePosts;
import com.barkitapp.android.parse.functions.UpdatePostsLat;
import com.barkitapp.android.prime.MainActivity;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.parse.ParseGeoPoint;
import com.parse.ParseInstallation;

import java.util.Date;
import java.util.HashMap;

import de.greenrobot.event.EventBus;
import io.nlopez.smartlocation.SmartLocation;

public class SplashScreen extends Activity implements UpdatePosts.OnUpdatePostsCompleted {

    private ImageView mLogo;
    private TextView mSpeech;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_activity);

        mLogo = ((ImageView) findViewById(R.id.imgLogo));
        mSpeech = (TextView) findViewById(R.id.speech);

        // random speech bubble for the dog
        if(mSpeech != null) {
            mSpeech.setText(RandomBarkGenerator.Run(this));
        }

        // if emulator TODO remove me
        // UserId.store(this, "qsct9gQBzc");

        String userId = UserId.get(this);
        if(userId != null && !userId.isEmpty())
        {
            AppStart();
        }
        else {
            Toast.makeText(this, "Initializing first app start", Toast.LENGTH_LONG).show();

            String deviceId = DeviceId.get(this);
            if(deviceId != null && !deviceId.isEmpty()) {
                CreateUser.run(this, deviceId);
            }
        }
    }

    private void AppStart() {

        MasterList.clearMasterList();

        // Notify about location info
        if(!SmartLocation.with(this).location().state().locationServicesEnabled())
            Toast.makeText(this, "Please enable Location Services on your device.", Toast.LENGTH_LONG).show();

        if(!SmartLocation.with(this).location().state().isGpsAvailable())
            Toast.makeText(this, "Please enable GPS on your device to improve your BARK it experience.", Toast.LENGTH_LONG).show();

        /*
        for EMULATOR
        Location targetLocation = new Location("");//provider name is unecessary
        targetLocation.setLatitude(48.20877d);//your coords of course
        targetLocation.setLongitude(16.37071d);
        LocationService.storeLocation(this, targetLocation);
        */

        // get last known location
        Coordinates lastKnownLocation = LocationService.getLocation(this);

        MasterList.clearMasterList();

        if(lastKnownLocation == null) {
            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            Location location = locationManager.getLastKnownLocation(locationManager.getBestProvider(new Criteria(), true));

            if (location != null)
                lastKnownLocation = new Coordinates(location.getLatitude(), location.getLongitude(), new Date());
        }

        if(!Connectivity.isOnline(this)) {
            Toast.makeText(this, "No internet connection", Toast.LENGTH_LONG).show();
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
                Toast.makeText(this, "Loading GPS data ...", Toast.LENGTH_LONG).show();
                InternalAppData.Store(this, SharedPrefKeys.WAITING_FOR_GPS, true);
            }
        }

        //startTime = System.currentTimeMillis();

        // close spalsh screen after some time
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i = new Intent(SplashScreen.this, MainActivity.class);
                startActivity(i);
                finish();
            }
        }, Constants.SPLASH_TIME_OUT);
    }

    public void onEvent(UserIdRecievedEvent event) {

        ParseInstallation installation = ParseInstallation.getCurrentInstallation();
        installation.put("user_id", UserId.get(this));
        installation.saveInBackground();

        AppStart();
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

        if(mLogo != null) {
            YoYo.with(Techniques.Bounce)
                    .duration(1000)
                    .playOn(mLogo);
        }

        if(mSpeech != null) {
            YoYo.with(Techniques.Tada)
                    .duration(1000)
                    .playOn(mSpeech);
        }
    }

    //long startTime;

    @Override
    public void onUpdatePostsCompleted(HashMap<String, Object> result) {
        //long difference = System.currentTimeMillis() - startTime;
        //Toast.makeText(this, difference + "ms", Toast.LENGTH_LONG).show();
        MasterList.StoreMasterList(this, result);
    }

    @Override
    public void onUpdatePostsFailed(String error) {
        
    }
}
