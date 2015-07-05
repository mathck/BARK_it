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

import com.barkitapp.android.R;
import com.barkitapp.android.main.MainActivity;
import com.barkitapp.android.core.Listener.UserLocationListener;
import com.barkitapp.android.core.services.LocationService;
import com.barkitapp.android.core.utility.Constants;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

public class SplashScreen extends Activity {
    // Splash screen timer
    private static int SPLASH_TIME_OUT = 1000;
    private ImageView mLogo;
    private TextView mSpeech;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_activity);

        mLogo = ((ImageView) findViewById(R.id.imgLogo));
        mSpeech = (TextView) findViewById(R.id.speech);

        // todo extract me to res/strings
        if(mSpeech != null) {
            switch ((int)(Math.random() * 6)) //0 - 5
            {
                case 0:  mSpeech.setText("cool");
                    break;
                case 1:  mSpeech.setText("oh hai!");
                    break;
                case 2:  mSpeech.setText("\u2665");
                    break;
                case 3:  mSpeech.setText("wow");
                    break;
                case 4:  mSpeech.setText("BARK");
                    break;
                case 5:  mSpeech.setText("?");
                    break;
                default: mSpeech.setText("??");
            }
        }

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        LocationListener locationListener = new UserLocationListener(this);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, Constants.GET_LOCATION_EVERY_MILLISECONDS, Constants.GET_LOCATION_EVERY_METERS, locationListener);

        Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        LocationService.storeLocation(this, lastKnownLocation);

        new Handler().postDelayed(new Runnable() {

            /*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo / company
             */

            @Override
            public void run() {
                // This method will be executed once the timer is over
                // Start your app main activity
                Intent i = new Intent(SplashScreen.this, MainActivity.class);
                startActivity(i);

                // close this activity
                finish();
            }
        }, SPLASH_TIME_OUT);
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

    /*
    String now_playing, earned;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_activity);


    new PrefetchData().execute();

}


private class PrefetchData extends AsyncTask<Void, Void, Void> {

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        // before making http calls

    }

    @Override
    protected Void doInBackground(Void... arg0) {
        JsonParser jsonParser = new JsonParser();
        String json = jsonParser
                .getJSONFromUrl("http://api.androidhive.info/game/game_stats.json");

        Log.e("Response: ", "> " + json);

        if (json != null) {
            try {
                JSONObject jObj = new JSONObject(json)
                        .getJSONObject("game_stat");
                now_playing = jObj.getString("now_playing");
                earned = jObj.getString("earned");

                Log.e("JSON", "> " + now_playing + earned);

            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }

        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);
        // After completing http call
        // will close this activity and lauch main activity
        Intent i = new Intent(SplashScreen.this, MainActivity.class);
        i.putExtra("now_playing", now_playing);
        i.putExtra("earned", earned);
        startActivity(i);

        // close this activity
        finish();
    }

}
     */
}
