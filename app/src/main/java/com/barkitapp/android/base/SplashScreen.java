package com.barkitapp.android.base;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.barkitapp.android.R;
import com.barkitapp.android.core.Listener.UserLocationListener;
import com.barkitapp.android.core.services.LocationService;
import com.barkitapp.android.core.services.MasterList;
import com.barkitapp.android.core.utility.Constants;
import com.barkitapp.android.core.utility.RandomBarkGenerator;
import com.barkitapp.android.main.MainActivity;
import com.barkitapp.android.parse.Connection;
import com.barkitapp.android.parse.enums.Order;
import com.barkitapp.android.parse.functions.UpdatePosts;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.parse.ParseGeoPoint;

import java.util.HashMap;

public class SplashScreen extends Activity implements UpdatePosts.OnUpdatePostsCompleted {

    private ImageView mLogo;
    private TextView mSpeech;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_activity);

        mLogo = ((ImageView) findViewById(R.id.imgLogo));
        mSpeech = (TextView) findViewById(R.id.speech);

        if(mSpeech != null) {
            mSpeech.setText(RandomBarkGenerator.Run(this));
        }

        Connection.Connect(getApplication());

        // todo get user id

        // get location updates
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        LocationListener locationListener = new UserLocationListener(this);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, Constants.GET_LOCATION_EVERY_MILLISECONDS, Constants.GET_LOCATION_EVERY_METERS, locationListener);

        // get current location
        Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        LocationService.storeLocation(this, lastKnownLocation);

        // get Posts from Parse
        UpdatePosts.run(this,
                Constants.TEMP_USER_ID,
                new ParseGeoPoint(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude()),
                new ParseGeoPoint(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude()),
                Constants.DEFAULT_RADIUS,
                Constants.GET_POSTS_COUNT,
                Order.TIME,
                true);

        // close spalsh screen after some time
        new Handler().postDelayed(new Runnable() {

            /*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo / company
             */

            @Override
            public void run() {
                Intent i = new Intent(SplashScreen.this, MainActivity.class);
                startActivity(i);
                finish();
            }
        }, Constants.SPLASH_TIME_OUT);
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

    @Override
    public void onUpdatePostsCompleted(HashMap<String, Object> result) {
        MasterList.StoreMasterList(this, result);
    }

    @Override
    public void onUpdatePostsFailed(String error) {
        Toast.makeText(this, error, Toast.LENGTH_LONG).show();
    }
}
