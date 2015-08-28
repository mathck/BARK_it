package com.barkitapp.android.pictures;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.barkitapp.android.BuildConfig;
import com.barkitapp.android.R;
import com.barkitapp.android.core.objects.Coordinates;
import com.barkitapp.android.core.services.LocationService;
import com.barkitapp.android.core.services.UserId;
import com.barkitapp.android.core.utility.Constants;
import com.barkitapp.android.parse.enums.MediaType;
import com.barkitapp.android.parse.functions.PostPost;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PictureActivity extends Activity {

    private Context mContext;
    private String imagePath;

    @Override
    public View onCreateView(String name, @NonNull Context context, @NonNull AttributeSet attrs) {
        return super.onCreateView(name, mContext = context, attrs);
    }

    @Override
    public void onResume() {
        super.onResume();

        // FULLSCREEN VIEW

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.picture_activity);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            imagePath = extras.getString("path");

            //Bitmap myBitmap = BitmapFactory.decodeFile(value);
            ImageView myImage = (ImageView) findViewById(R.id.image);
            myImage.setImageURI(Uri.parse(imagePath));


        RelativeLayout cancelButton = (RelativeLayout) findViewById(R.id.cancel);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        ImageView fab = (ImageView) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new LongOperation().execute(imagePath);
                finish();
                // todo do me on background thread
            }
        });
        }
        else {
            finish();
        }
    }

    private class LongOperation extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {

            Bitmap bitmap = null;
            //Bitmap thumbImage = null;

            try {

                    /*
                    // load me in chunks like this
                    BitmapRegionDecoder decoder = BitmapRegionDecoder.newInstance(myStream, false);
                    Bitmap region = decoder.decodeRegion(new Rect(10, 10, 50, 50), null);
                     */

                bitmap = BitmapFactory.decodeFile(imagePath);
                //thumbImage = ThumbnailUtils.extractThumbnail(bitmap, 256, 256);

            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(mContext, "Image could not be read.", Toast.LENGTH_LONG).show();
                finish();
                return "Error";
            }

            File f = new File(imagePath);
            final String filename = f.getName();

            final ParseFile media = new ParseFile(filename, getBytesFromBitmap(bitmap));
            //final ParseFile small_image = new ParseFile("thumb_" + filename, getBytesFromBitmap(thumbImage));

            media.saveInBackground(new SaveCallback() {
                public void done(ParseException e) {
                    if (null == e) {
                        Coordinates location = LocationService.getLocation(getApplicationContext());
                        if (location == null) {
                            Toast.makeText(mContext, "No GPS data. Please enable GPS.", Toast.LENGTH_LONG).show();
                            return;
                        }

                        PostPost.run(mContext, UserId.get(mContext),
                                new ParseGeoPoint(location.getLatitude(), location.getLongitude()),
                                "", 0, media, MediaType.PICTURE, media);

                    }
                }
            });

            return "Executed";
        }

        @Override
        protected void onPostExecute(String result) {

        }

        @Override
        protected void onPreExecute() {}

        @Override
        protected void onProgressUpdate(Void... values) {}
    }

    public byte[] getBytesFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 76, stream);
        return stream.toByteArray();
    }
}
