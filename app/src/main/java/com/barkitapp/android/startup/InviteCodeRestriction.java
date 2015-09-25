package com.barkitapp.android.startup;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.barkitapp.android.R;
import com.barkitapp.android._core.services.DeviceId;
import com.barkitapp.android.parse_backend.functions.CreateUserFirstTime;
import com.barkitapp.android.parse_backend.functions.ReferUser;
import com.parse.ParseObject;

import java.util.ArrayList;
import java.util.HashMap;

public class InviteCodeRestriction extends AppCompatActivity implements ReferUser.OnReferUserCompleted {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite_code_restriction);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_invite_code_restriction, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void registerUser() {
        String deviceId = DeviceId.get(this);
        if(deviceId != null && !deviceId.isEmpty()) {
            CreateUserFirstTime.run(this, deviceId);
        }
    }

    private void referUser() {
        String deviceId = DeviceId.get(this);
        if(deviceId != null && !deviceId.isEmpty()) {
            CreateUserFirstTime.run(this, deviceId);
        }
    }

    @Override
    public void onReferUserCompleted(HashMap<String, ArrayList<ParseObject>> result) {

    }

    @Override
    public void onReferUserFailed(String error) {

    }
}
