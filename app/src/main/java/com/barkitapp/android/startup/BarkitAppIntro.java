package com.barkitapp.android.startup;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.barkitapp.android.R;
import com.barkitapp.android._main.MainActivity;
import com.github.paolorotolo.appintro.AppIntro2;
import com.github.paolorotolo.appintro.AppIntroFragment;

public class BarkitAppIntro extends AppIntro2 {
    @Override
    public void init(Bundle savedInstanceState) {

        addSlide(AppIntroFragment.newInstance("Ein Bark ist ...", "Neuigkeiten und Interessantes in deiner Nähe", R.mipmap.intro0, getResources().getColor(R.color.red_500)));
        addSlide(AppIntroFragment.newInstance("Lokal", "Sieh was in deiner Umgebung passiert", R.mipmap.intro1, getResources().getColor(R.color.blue_500)));
        addSlide(AppIntroFragment.newInstance("Anonym", "Teile anonym deine Meinung", R.mipmap.intro2, getResources().getColor(R.color.teal_500)));

        setFlowAnimation();
    }

    private void loadMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    public void onDonePressed() {
        loadMainActivity();
    }

    public void getStarted(View v){
        loadMainActivity();
    }
}
