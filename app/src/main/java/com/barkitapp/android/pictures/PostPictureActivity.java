package com.barkitapp.android.pictures;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.barkitapp.android.R;
import com.barkitapp.android._core.objects.Coordinates;
import com.barkitapp.android._core.services.LocationService;
import com.barkitapp.android._core.services.UserId;
import com.barkitapp.android.parse_backend.enums.MediaType;
import com.barkitapp.android.parse_backend.functions.PostPost;
import com.barkitapp.android.startup.Setup;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.File;

public class PostPictureActivity extends Activity {

    private Context mContext;
    private String imagePath;
    private Tracker mTracker;

    @Override
    public View onCreateView(String name, @NonNull Context context, @NonNull AttributeSet attrs) {
        return super.onCreateView(name, mContext = context, attrs);
    }

    @Override
    public void onResume() {
        super.onResume();

        mTracker.setScreenName(PostPictureActivity.class.getSimpleName());
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            imagePath = extras.getString("path");

            //Bitmap myBitmap = BitmapFactory.decodeFile(value);
            ImageView myImage = (ImageView) findViewById(R.id.image);
            myImage.setImageURI(Uri.parse(imagePath));


//            RelativeLayout cancelButton = (RelativeLayout) findViewById(R.id.cancel);
//            cancelButton.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    finish();
//                }
//            });


            ImageView fab = (ImageView) findViewById(R.id.fab);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    new LongOperation().execute(((EditText) findViewById(R.id.chattext)).getText().toString());
                    finish();
                    // todo do me on background thread
                }
            });
        }
        else {
            finish();
        }

        Setup application = (Setup) getApplication();
        mTracker = application.getDefaultTracker();
    }

    private class LongOperation extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(final String... params) {

            Bitmap bitmap;
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
                Toast.makeText(mContext, R.string.image_could_not_be_read, Toast.LENGTH_LONG).show();
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
                            Toast.makeText(mContext, mContext.getString(R.string.no_gps_plaease_enable_gps), Toast.LENGTH_LONG).show();
                            return;
                        }

                        PostPost.run(mContext, UserId.get(mContext),
                                new ParseGeoPoint(location.getLatitude(), location.getLongitude()),
                                params[0], 0, media, MediaType.PICTURE, media);

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
