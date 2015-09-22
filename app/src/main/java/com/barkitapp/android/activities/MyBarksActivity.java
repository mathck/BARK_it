package com.barkitapp.android.activities;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.View;

import com.barkitapp.android.R;
import com.barkitapp.android._main.MainActivity;
import com.barkitapp.android.my_stuff.MyBarksFragment;
import com.barkitapp.android.my_stuff.MyRepliesFragment;
import com.barkitapp.android.startup.Setup;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

public class MyBarksActivity extends AppCompatActivity {

    private Tracker mTracker;
    private Context mContext;
    private ActionBar mActionBar;


    @Override
    public View onCreateView(String name, @NonNull Context context, @NonNull AttributeSet attrs) {
        return super.onCreateView(name, mContext = context, attrs);
    }

    @Override
    public void onResume() {
        super.onResume();

        mTracker.setScreenName(MyBarksActivity.class.getSimpleName());
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Setup application = (Setup) getApplication();
        mTracker = application.getDefaultTracker();

        setContentView(R.layout.activity_my_stuff);

        //final BarkReplyListFragment listFragment = (BarkReplyListFragment) getSupportFragmentManager().findFragmentById(R.id.fragment);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        mActionBar = getSupportActionBar();

        if(mActionBar != null) {
            mActionBar.setDisplayHomeAsUpEnabled(true);
            mActionBar.setHomeButtonEnabled(true);
            mActionBar.setTitle(R.string.my_stuff);
        }

        final ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        if (viewPager != null) {
            setupViewPager(viewPager);
        }

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);

        if(viewPager != null) {
            tabLayout.setupWithViewPager(viewPager);

            TabLayout.Tab tab_new =  tabLayout.getTabAt(0);
            TabLayout.Tab tab_hot =  tabLayout.getTabAt(1);

            if(tab_new == null || tab_hot == null)
                return;

            tab_new.setIcon(R.drawable.ic_chat_white_24dp);
            tab_hot.setIcon(R.drawable.ic_forum_white_24dp);
        }
    }

    private void setupViewPager(ViewPager viewPager) {
        MainActivity.Adapter adapter = new MainActivity.Adapter(getSupportFragmentManager());
        adapter.addFragment(new MyBarksFragment(), "");
        adapter.addFragment(new MyRepliesFragment(), "");
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if(mActionBar != null && mActionBar.getTitle() != null) {
                    mActionBar.setTitle((position == 0 ? getString(R.string.my_barks) : getString(R.string.my_replies)));
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
}
