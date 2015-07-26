package com.barkitapp.android.prime;

import android.content.Context;
import android.content.Intent;
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
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.barkitapp.android.R;
import com.barkitapp.android.core.objects.Coordinates;
import com.barkitapp.android.core.services.LocationService;
import com.barkitapp.android.core.services.UserId;
import com.barkitapp.android.core.utility.Constants;
import com.barkitapp.android.parse.functions.PostPost;
import com.parse.ParseGeoPoint;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;
    private Context mContext;
    private ActionBar actionBar;

    private long lastPostPerformed = 0;

    @Override
    public View onCreateView(String name, @NonNull Context context, @NonNull AttributeSet attrs) {
        return super.onCreateView(name, mContext = context, attrs);
    }

    @Override
    protected void onResume() {
        super.onResume();

        String city = LocationService.getLocationCity(this, LocationService.getLocation(this));
        actionBar = getSupportActionBar();
        if(actionBar != null) {
            if(city != null && !city.isEmpty()) {
                actionBar.setTitle(city);
            }
            else {
                actionBar.setTitle(R.string.app_name);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        //AppBarLayout appbar = (AppBarLayout) findViewById(R.id.appbar);
        //appbar.ScrollingViewBehavior = new PatchedScrollingViewBehavior();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        actionBar = getSupportActionBar();

        if(actionBar != null) {

            String city = LocationService.getLocationCity(this, LocationService.getLocation(this));
            if(city != null && !city.isEmpty()) {
                getSupportActionBar().setTitle(city);
            }
            else {
                getSupportActionBar().setTitle(R.string.app_name);
            }

            getSupportActionBar().setTitle(R.string.app_name);

            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
            actionBar.setDisplayHomeAsUpEnabled(true);
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

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);

        if(viewPager != null) {
            tabLayout.setupWithViewPager(viewPager);

            if(tabLayout.getTabAt(0) == null || tabLayout.getTabAt(1) == null)
                return;

            tabLayout.getTabAt(0).setIcon(R.drawable.ic_new_releases_white_18dp);
            tabLayout.getTabAt(1).setIcon(R.drawable.ic_whatshot_white_18dp);
        }
    }

    private void performSend(EditText chattext, ViewPager viewPager) {
        String textToPost = chattext.getText().toString();
        if(textToPost.isEmpty()) {
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

        PostPost.run(UserId.get(this),
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
            //case R.id.action_places:
                //Intent intent = new Intent(mContext, PlacesActivity.class);
                //mContext.startActivity(intent);
                //return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupViewPager(ViewPager viewPager) {
        Adapter adapter = new Adapter(getSupportFragmentManager());
        adapter.addFragment(new NewFragment(), "  NEW");
        adapter.addFragment(new HotFragment(), "  HOT");
        viewPager.setAdapter(adapter);
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                mDrawerLayout.closeDrawers();

                switch (menuItem.getItemId()) {
                    case R.id.nav_home:
                        menuItem.setChecked(true);
                        return true;
                    case R.id.nav_places:
                        Toast.makeText(mContext, "Change location and featured places will be available on 27.07", Toast.LENGTH_LONG).show();
                        //Intent intent = new Intent(mContext, PlacesActivity.class);
                        //mContext.startActivity(intent);
                        return true;
                    case R.id.nav_feedback:
                        Intent i = new Intent(mContext, FeedbackActivity.class);
                        mContext.startActivity(i);
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
