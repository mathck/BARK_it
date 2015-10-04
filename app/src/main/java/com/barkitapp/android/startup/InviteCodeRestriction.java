package com.barkitapp.android.startup;

import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.barkitapp.android.R;
import com.barkitapp.android._core.services.DeviceId;
import com.barkitapp.android._core.services.UserId;
import com.barkitapp.android._main.MainActivity;
import com.barkitapp.android.events.ForceOnResume;
import com.barkitapp.android.events.RequestUpdatePostsEvent;
import com.barkitapp.android.parse_backend.functions.CreateUser;
import com.dd.processbutton.iml.ActionProcessButton;
import com.parse.ParseObject;

import de.greenrobot.event.EventBus;

public class InviteCodeRestriction extends AppCompatActivity implements CreateUser.OnCreateUserCompleted {

    private ActionProcessButton btnVerify;
    private EditText code;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite_code_restriction);

        final InviteCodeRestriction context = this;

        code = (EditText) findViewById(R.id.code);
        btnVerify = (ActionProcessButton) findViewById(R.id.btnVerify);
        btnVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(code.getText().toString().isEmpty()) {
                    Toast.makeText(context, getResources().getText(R.string.enter_invite_code), Toast.LENGTH_LONG).show();
                    return;
                }

                String deviceId = DeviceId.get(context);
                if(deviceId != null && !deviceId.isEmpty()) {
                    btnVerify.setMode(ActionProcessButton.Mode.ENDLESS);
                    btnVerify.setProgress(50);
                    CreateUser.run(context, context, DeviceId.get(context), code.getText().toString());
                }
                btnVerify.setClickable(false);
            }
        });

        code.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    if(btnVerify.isClickable())
                        btnVerify.performClick();
                    return true;
                }
                return false;
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.red_700));
        }

        RelativeLayout help = (RelativeLayout) findViewById(R.id.help);
        help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(InviteCodeRestriction.this, InviteCodeHelp.class);
                startActivity(i);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_invite_code_restriction, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateUserCompleted(ParseObject result) {
        btnVerify.setCompleteText(getResources().getText(R.string.welcome));
        btnVerify.setProgress(100);
        btnVerify.setClickable(false);

        String userId = result.getObjectId();
        UserId.store(this, userId);

        Intent i = new Intent(InviteCodeRestriction.this, MainActivity.class);
        startActivity(i);
        finish();

//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//
//            }
//        }, 300);
    }

    @Override
    public void onCreateUserFailed(String error) {
        btnVerify.setErrorText(getResources().getText(R.string.wrong));
        btnVerify.setProgress(-1);
        btnVerify.setClickable(false);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                btnVerify.setProgress(0);
                btnVerify.setClickable(true);
            }
        }, 1500);
    }
}
