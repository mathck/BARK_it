package com.barkitapp.android;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class SplashScreen extends Activity {
    // Splash screen timer
    private static int SPLASH_TIME_OUT = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

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

    /*
    String now_playing, earned;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);


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
