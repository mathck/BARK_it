package com.barkitapp.android.startup;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.barkitapp.android.R;
import com.barkitapp.android._core.services.DeviceId;
import com.barkitapp.android._core.services.UserId;
import com.barkitapp.android._main.MainActivity;
import com.barkitapp.android.parse_backend.functions.CreateUser;
import com.github.paolorotolo.appintro.AppIntro2;
import com.github.paolorotolo.appintro.AppIntroFragment;
import com.parse.ParseObject;

public class BarkitAppIntro extends AppIntro2 implements CreateUser.OnCreateUserCompleted {
    @Override
    public void init(Bundle savedInstanceState) {

        addSlide(AppIntroFragment.newInstance(getString(R.string.intro_1_title), getString(R.string.intro_1_description), R.mipmap.intro0, getResources().getColor(R.color.red_500)));
        addSlide(AppIntroFragment.newInstance(getString(R.string.intro_2_title), getString(R.string.intro_2_description), R.mipmap.intro1, getResources().getColor(R.color.blue_500)));
        addSlide(AppIntroFragment.newInstance(getString(R.string.intro_3_title), getString(R.string.intro_3_description), R.mipmap.intro2, getResources().getColor(R.color.teal_500)));

        setFlowAnimation();

        CreateUser.run(this, this, DeviceId.get(this), "");
    }

    private void loadMainActivity() {
        String userId = UserId.get(this);
        Intent intent = new Intent(this, (userId != null && !userId.isEmpty()) ? MainActivity.class : InviteCodeRestriction.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onDonePressed() {
        loadMainActivity();
    }

    public void getStarted(View v){
        loadMainActivity();
    }

    @Override
    public void onCreateUserCompleted(ParseObject result) {
        String userId = result.getObjectId();
        UserId.store(this, userId);
    }

    @Override
    public void onCreateUserFailed(String error) {

    }
}
