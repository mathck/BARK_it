package com.barkitapp.android.prime;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.barkitapp.android.BuildConfig;
import com.barkitapp.android.R;
import com.barkitapp.android.base.Setup;
import com.barkitapp.android.core.objects.Coordinates;
import com.barkitapp.android.core.services.LocationService;
import com.barkitapp.android.core.services.UserId;
import com.barkitapp.android.core.utility.Constants;
import com.barkitapp.android.parse.functions.PostPost;
import com.barkitapp.android.places.PlacesActivity;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.parse.ParseGeoPoint;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;
    private Context mContext;
    private ActionBar mActionBar;

    private long lastPostPerformed = 0;
    private Uri path;
    private Tracker mTracker;
    private String mCityName = "";

    @Override
    public View onCreateView(String name, @NonNull Context context, @NonNull AttributeSet attrs) {
        return super.onCreateView(name, mContext = context, attrs);
    }

    private String getCityName(boolean force) {

        if(!force && mCityName != null && !mCityName.isEmpty() && !mCityName.equals("null")) {
            return " in " + mCityName;
        }

        String city = LocationService.getLocationCity(this, LocationService.getLocation(this));
        if(city != null && !city.isEmpty() && !city.equals("null")) {
            mCityName = city;
            return " in " + city;
        }
        else {
            return mCityName = " Barks";
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        mTracker.setScreenName(MainActivity.class.getSimpleName());
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());

        mActionBar = getSupportActionBar();
        if(mActionBar != null)
            mActionBar.setTitle("Barks" + getCityName(true));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        // Obtain the shared Tracker instance.
        Setup application = (Setup) getApplication();
        mTracker = application.getDefaultTracker();

        //AppBarLayout appbar = (AppBarLayout) findViewById(R.id.appbar);
        //appbar.ScrollingViewBehavior = new PatchedScrollingViewBehavior();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mActionBar = getSupportActionBar();

        if(mActionBar != null) {

            String city = LocationService.getLocationCity(this, LocationService.getLocation(this));
            if(city != null && !city.isEmpty()) {
                getSupportActionBar().setTitle(city);
            }
            else {
                getSupportActionBar().setTitle(R.string.app_name);
            }

            getSupportActionBar().setTitle(R.string.app_name);

            mActionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
            mActionBar.setDisplayHomeAsUpEnabled(true);
        }

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        if (navigationView != null) {
            setupDrawerContent(navigationView);
        }

        final ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        if (viewPager != null) {
            setupViewPager(viewPager);
        }

        final EditText chattext = (EditText) findViewById(R.id.chattext);
        chattext.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    performSend(chattext, viewPager);
                    return true;
                }
                return false;
            }
        });

        ImageView fab = (ImageView) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                performSend(chattext, viewPager);
            }
        });
        //fab.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.primary)));

        /*
        ImageView takeAPicture = (ImageView) findViewById(R.id.picture);
        takeAPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dispatchTakePictureIntent();
            }
        });
        */

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);

        if(viewPager != null) {
            tabLayout.setupWithViewPager(viewPager);

            if(tabLayout.getTabAt(0) == null || tabLayout.getTabAt(1) == null)
                return;

            tabLayout.getTabAt(0).setIcon(R.drawable.ic_access_time_white_24dp);
            tabLayout.getTabAt(1).setIcon(R.drawable.ic_whatshot_white_24dp);
        }
    }

    static final int REQUEST_TAKE_PHOTO = 1;

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                Toast.makeText(this, "Please try again.", Toast.LENGTH_LONG).show();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        path = Uri.fromFile(photoFile));
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            //Bundle extras = data.getExtras();
            //Bitmap imageBitmap = (Bitmap) extras.get("data");
            Toast.makeText(this, "Yay i got the pic!", Toast.LENGTH_LONG).show();
            //mImageView.setImageBitmap(imageBitmap);

            Intent intent = new Intent(mContext, PictureActivity.class);
            intent.putExtra("path", path.toString());
            mContext.startActivity(intent);
        }
    }

    String mCurrentPhotoPath;

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        return image;
    }

    private void performSend(EditText chattext, ViewPager viewPager) {
        String textToPost = chattext.getText().toString();
        if(textToPost.trim().isEmpty()) {
            return;
        }

        if(System.currentTimeMillis() - lastPostPerformed < Constants.POST_BLOCK)
        {
            Toast.makeText(this, "Please wait a few seconds to bark again", Toast.LENGTH_SHORT).show();
            return;
        }

        Coordinates location = LocationService.getLocation(getApplicationContext());
        if(location == null) {
            Toast.makeText(this, "No GPS data. Please enable GPS.", Toast.LENGTH_LONG).show();
            return;
        }

        PostPost.run(this, UserId.get(this),
                new ParseGeoPoint(location.getLatitude(), location.getLongitude()),
                textToPost,
                0);

        lastPostPerformed = System.currentTimeMillis();

        //newFragment.addItem(new Post(Constants.UNKNOWN, Constants.TEMP_USER_ID, new Date(), new ParseGeoPoint(location.getLatitude(), location.getLongitude()),
        //        textToPost, "", 0, 0, 0, 0, VoteType.NEUTRAL.ordinal()));

        if(viewPager != null)
            viewPager.setCurrentItem(0);

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(chattext.getWindowToken(), 0);

        chattext.setText("");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_actions, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
            case R.id.action_places:
                Intent intent = new Intent(mContext, PlacesActivity.class);
                mContext.startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupViewPager(ViewPager viewPager) {
        Adapter adapter = new Adapter(getSupportFragmentManager());
        adapter.addFragment(new NewFragment(), "");
        adapter.addFragment(new HotFragment(), "");
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if(mActionBar != null && mActionBar.getTitle() != null) {
                    mActionBar.setTitle((position == 0 ? "New " : "Hot") + getCityName(false));
                }
            }

            @Override
            public void onPageSelected(int position) {
                //if(mActionBar != null && mActionBar.getTitle() != null) {
                //    mActionBar.setTitle((position == 0 ? "New in " : "Hot in ") + mCity);
                //}
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        viewPager.setAdapter(adapter);
    }

    private void setupDrawerContent(NavigationView navigationView) {

        TextView app_version = (TextView) navigationView.findViewById(R.id.app_version);
        try {
            String appVersionText = BuildConfig.VERSION_NAME + " " + BuildConfig.VERSION_CODE;
            if(app_version != null)
                app_version.setText(appVersionText);
        }
        catch (Exception e)
        {

        }

        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                mDrawerLayout.closeDrawers();

                switch (menuItem.getItemId()) {
                    case R.id.nav_home:
                        menuItem.setChecked(true);
                        return true;
//                    case R.id.nav_mybarks:
//                        Intent intentMy = new Intent(mContext, MyBarksActivity.class);
//                        mContext.startActivity(intentMy);
//                        return true;
                    case R.id.nav_places:
                        //Toast.makeText(mContext, "will be available soon", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(mContext, PlacesActivity.class);
                        mContext.startActivity(intent);
                        return true;
                    case R.id.nav_feedback:
                        Intent i = new Intent(mContext, FeedbackActivity.class);
                        mContext.startActivity(i);
                        return true;
                    case R.id.nav_facebook:
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/groups/barkitEA/")));
                        return true;
                    //case R.id.nav_settings:
                    //    Intent intent3 = new Intent(mContext, SettingsActivity.class);
                    //    mContext.startActivity(intent3);
                    //    return true;
                    case R.id.nav_bug:
                        Intent intent2 = new Intent(mContext, ReportBugActivity.class);
                        mContext.startActivity(intent2);
                        return true;
                    case R.id.nav_friends:
                        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                        sharingIntent.setType("text/plain");
                        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "I am testing Bark it, check it out");
                        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, "http://barkitapp.com/");
                        startActivity(Intent.createChooser(sharingIntent, getResources().getString(R.string.share_using)));
                        //Intent in = new Intent(mContext, ProfileActivity.class);
                        //mContext.startActivity(in);
                        return true;
                }

                return true;
            }
        });
    }

    static class Adapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragments = new ArrayList<>();
        private final List<String> mFragmentTitles = new ArrayList<>();

        public Adapter(FragmentManager fm) {
            super(fm);
        }

        public void addFragment(Fragment fragment, String title) {
            mFragments.add(fragment);
            mFragmentTitles.add(title);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitles.get(position);
        }
    }
}
