package com.barkitapp.android.prime;

import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.UiThread;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
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

import com.barkitapp.android.Messages.CollapseLayoutEvent;
import com.barkitapp.android.Messages.RecievedPostForNotification;
import com.barkitapp.android.Messages.RequestUpdateRepliesEvent;
import com.barkitapp.android.R;
import com.barkitapp.android.bark_detail.BarkReplyListFragment;
import com.barkitapp.android.base.Setup;
import com.barkitapp.android.core.objects.Coordinates;
import com.barkitapp.android.core.services.LocationService;
import com.barkitapp.android.core.services.MasterList;
import com.barkitapp.android.core.services.UserId;
import com.barkitapp.android.core.utility.Constants;
import com.barkitapp.android.core.utility.DistanceConverter;
import com.barkitapp.android.core.utility.TimeConverter;
import com.barkitapp.android.parse.converter.NotificationConverter;
import com.barkitapp.android.parse.enums.ContentType;
import com.barkitapp.android.parse.functions.Flag;
import com.barkitapp.android.parse.functions.GetPostById;
import com.barkitapp.android.parse.functions.PostReply;
import com.barkitapp.android.parse.objects.Post;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.parse.ParseGeoPoint;

import de.greenrobot.event.EventBus;

public class MyBarksActivity extends AppCompatActivity {

    private Tracker mTracker;

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

        setContentView(R.layout.activity_my_barks);

        //final BarkReplyListFragment listFragment = (BarkReplyListFragment) getSupportFragmentManager().findFragmentById(R.id.fragment);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();

        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
            actionBar.setTitle("My Barks");
        }
    }
}
