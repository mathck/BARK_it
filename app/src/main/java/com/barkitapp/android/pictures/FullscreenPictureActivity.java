package com.barkitapp.android.pictures;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.barkitapp.android.R;
import com.barkitapp.android.startup.Setup;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class FullscreenPictureActivity extends AppCompatActivity {

    public static final String EXTRA_IMAGE_URL = "Image.Url";
    private ImageLoader imageLoader;
    private Tracker mTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        String url = intent.getStringExtra(EXTRA_IMAGE_URL);

        setContentView(R.layout.activity_fullscreen_picture);

        if(url != null && !url.isEmpty())
        {
            ImageView backdrop = (ImageView) findViewById(R.id.image);

            if(imageLoader == null)
                imageLoader = ImageLoader.getInstance();

            DisplayImageOptions options = new DisplayImageOptions.Builder()
                    .showImageOnLoading(android.R.color.black)
                    .showImageForEmptyUri(android.R.color.black)
                    .build();

            imageLoader.displayImage(url, backdrop, options);

            backdrop.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }
        else {
            finish();
        }

        Setup application = (Setup) getApplication();
        mTracker = application.getDefaultTracker();
    }

    @Override
    public void onResume() {
        super.onResume();

        mTracker.setScreenName(FullscreenPictureActivity.class.getSimpleName());
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }
}
