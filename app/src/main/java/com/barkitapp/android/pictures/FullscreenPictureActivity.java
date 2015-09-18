package com.barkitapp.android.pictures;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.barkitapp.android.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class FullscreenPictureActivity extends AppCompatActivity {

    public static final String EXTRA_IMAGE_URL = "Image.Url";
    private ImageLoader imageLoader;

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
    }
}
