package com.barkitapp.android.startup;

import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.barkitapp.android.R;
import com.barkitapp.android._core.services.DeviceId;
import com.barkitapp.android._main.MainActivity;
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

        com.dd.processbutton.iml.ActionProcessButton btnVerify = (com.dd.processbutton.iml.ActionProcessButton) findViewById(R.id.btnVerify);
        btnVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(InviteCodeRestriction.this, BarkitAppIntro.class);
                startActivity(i);
                finish();
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.blue_700));
        }
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
