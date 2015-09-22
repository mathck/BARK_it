package com.barkitapp.android._main;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
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
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.barkitapp.android.BuildConfig;
import com.barkitapp.android.activities.FeedbackActivity;
import com.barkitapp.android.activities.ReportBugActivity;
import com.barkitapp.android.events.ForceOnResume;
import com.barkitapp.android.events.RequestUpdatePostsEvent;
import com.barkitapp.android.R;
import com.barkitapp.android.startup.Setup;
import com.barkitapp.android._core.objects.Coordinates;
import com.barkitapp.android._core.services.InternalAppData;
import com.barkitapp.android._core.services.LocationService;
import com.barkitapp.android._core.services.MasterList;
import com.barkitapp.android._core.services.UserId;
import com.barkitapp.android._core.utility.Constants;
import com.barkitapp.android._core.utility.SharedPrefKeys;
import com.barkitapp.android.parse_backend.functions.PostPost;
import com.barkitapp.android.places.PlacesActivity;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.barkitapp.android.pictures.BarkitCamera;
import com.parse.ParseGeoPoint;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;

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
            return " " + getString(R.string.in_for_barks_in_location) + " " + mCityName;
        }

        String city = LocationService.getLocationCity(this, LocationService.getLocation(this));
        if(city != null && !city.isEmpty() && !city.equals("null")) {
            mCityName = city;
            return " " + getString(R.string.in_for_barks_in_location) + " " + city;
        }
        else {
            return mCityName = " Barks";
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        RelativeLayout chatbox_ui = (RelativeLayout) findViewById(R.id.chatbox_ui);

        //--------------------------------------------------------------------
        // set Toolbar color depending on mode
        // chatbox visibility
        //--------------------------------------------------------------------
        mActionBar = getSupportActionBar();
        if(mActionBar != null)
        {
            boolean is_manual_location = InternalAppData.getBoolean(this, SharedPrefKeys.HAS_SET_MANUAL_LOCATION);

            if(is_manual_location) {
                mTracker.setScreenName("ManualLocationScreen");
                mTracker.send(new HitBuilders.ScreenViewBuilder().build());
            }

            mActionBar.setBackgroundDrawable(is_manual_location ? new ColorDrawable(getResources().getColor(R.color.orange_500)) : new ColorDrawable(getResources().getColor(R.color.primary)));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Window window = getWindow();
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(getResources().getColor(is_manual_location ? R.color.orange_700 : R.color.primary_dark));
            }

            TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
            if(tabLayout != null) {
                tabLayout.setBackgroundResource(is_manual_location ? R.color.orange_500 : R.color.primary);
            }

            if(chatbox_ui != null) {
                chatbox_ui.setVisibility(is_manual_location ? View.GONE : View.VISIBLE);
            }

            mActionBar.setTitle("Barks" + getCityName(true));
            //mActionBar.setSubtitle(is_manual_location ? "Featured Location" : "");
        }

        invalidateOptionsMenu();
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    public void onEvent(ForceOnResume event) {
        this.onResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

        ImageView takeAPicture = (ImageView) findViewById(R.id.picture);
        takeAPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //dispatchTakePictureIntent();

                Intent intent = new Intent(mContext, BarkitCamera.class);
                mContext.startActivity(intent);
            }
        });

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);

        if(viewPager != null) {
            tabLayout.setupWithViewPager(viewPager);

            TabLayout.Tab tab_new =  tabLayout.getTabAt(0);
            TabLayout.Tab tab_hot =  tabLayout.getTabAt(1);

            if(tab_new == null || tab_hot == null)
                return;

            tab_new.setIcon(R.drawable.ic_access_time_white_24dp);
            tab_hot.setIcon(R.drawable.ic_whatshot_white_24dp);
        }
    }

    private void performSend(EditText chattext, ViewPager viewPager) {
        String textToPost = chattext.getText().toString();
        if(textToPost.trim().isEmpty()) {
            return;
        }

        if(System.currentTimeMillis() - lastPostPerformed < Constants.POST_BLOCK)
        {
            Toast.makeText(this, R.string.please_wait_few_seconds, Toast.LENGTH_SHORT).show();
            return;
        }

        Coordinates location = LocationService.getLocation(getApplicationContext());
        if(location == null) {
            Toast.makeText(this, R.string.no_gps_plaease_enable_gps, Toast.LENGTH_LONG).show();
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
        if(InternalAppData.getBoolean(this, SharedPrefKeys.HAS_SET_MANUAL_LOCATION)) {
            getMenuInflater().inflate(R.menu.featured_main_actions, menu);
        }
        else {
            getMenuInflater().inflate(R.menu.main_actions, menu);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
            case R.id.action_places:

                if(InternalAppData.getBoolean(this, SharedPrefKeys.HAS_SET_MANUAL_LOCATION)) {
                    // todo resture UI + position + reload

                    InternalAppData.Store(mContext, SharedPrefKeys.LOCATION_LATITUDE_MANUAL, "");
                    InternalAppData.Store(mContext, SharedPrefKeys.LOCATION_LONGITUDE_MANUAL, "");
                    InternalAppData.Store(mContext, SharedPrefKeys.HAS_SET_MANUAL_LOCATION, false);
                    InternalAppData.Store(mContext, SharedPrefKeys.MANUAL_TITLE, "");
                    InternalAppData.Store(mContext, SharedPrefKeys.RADIUS, 5000L);

                    MasterList.clearMasterListAllSlow();

                    EventBus.getDefault().post(new RequestUpdatePostsEvent());

                    this.onResume();
                }
                else {
                    Intent intent = new Intent(mContext, PlacesActivity.class);
                    mContext.startActivity(intent);
                }

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
                    mActionBar.setTitle((position == 0 ? getString(R.string.new_) : getString(R.string.hot)) + getCityName(false));
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
        catch (Exception ignored)
        {

        }

        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                mDrawerLayout.closeDrawers();

                switch (menuItem.getItemId()) {
                    case R.id.nav_home:
                        //menuItem.setChecked(true);

                        if(InternalAppData.getBoolean(mContext, SharedPrefKeys.HAS_SET_MANUAL_LOCATION)) {
                            // todo resture UI + position + reload

                            InternalAppData.Store(mContext, SharedPrefKeys.LOCATION_LATITUDE_MANUAL, "");
                            InternalAppData.Store(mContext, SharedPrefKeys.LOCATION_LONGITUDE_MANUAL, "");
                            InternalAppData.Store(mContext, SharedPrefKeys.HAS_SET_MANUAL_LOCATION, false);
                            InternalAppData.Store(mContext, SharedPrefKeys.MANUAL_TITLE, "");
                            InternalAppData.Store(mContext, SharedPrefKeys.RADIUS, 5000L);

                            MasterList.clearMasterListAllSlow();

                            EventBus.getDefault().post(new RequestUpdatePostsEvent());

                            EventBus.getDefault().post(new ForceOnResume());
                        }

                        return true;
//                    case R.id.nav_mybarks:
//                        Intent intentMy = new Intent(mContext, MyBarksActivity.class);
//                        mContext.startActivity(intentMy);
//                        return true;
                    case R.id.nav_places:

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
                    case R.id.nav_settings:
                        Intent intent3 = new Intent(mContext, SettingsActivity.class);
                        mContext.startActivity(intent3);
                        return true;
                    case R.id.nav_bug:
                        Intent intent2 = new Intent(mContext, ReportBugActivity.class);
                        mContext.startActivity(intent2);
                        return true;
                    case R.id.nav_friends:
                        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                        sharingIntent.setType("text/plain");
                        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, getString(R.string.testing_barkit));
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
