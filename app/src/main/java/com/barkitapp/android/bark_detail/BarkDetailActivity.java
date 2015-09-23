package com.barkitapp.android.bark_detail;

import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.UiThread;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
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

import com.barkitapp.android.events.CollapseLayoutEvent;
import com.barkitapp.android.events.RecievedPostForNotification;
import com.barkitapp.android.events.RequestUpdateRepliesEvent;
import com.barkitapp.android.R;
import com.barkitapp.android.startup.Setup;
import com.barkitapp.android._core.objects.Coordinates;
import com.barkitapp.android._core.services.LocationService;
import com.barkitapp.android._core.services.MasterList;
import com.barkitapp.android._core.services.UserId;
import com.barkitapp.android._core.utility.Constants;
import com.barkitapp.android._core.utility.converter.DistanceConverter;
import com.barkitapp.android._core.utility.converter.TimeConverter;
import com.barkitapp.android.parse_backend.converter.NotificationConverter;
import com.barkitapp.android.parse_backend.enums.ContentType;
import com.barkitapp.android.parse_backend.functions.Flag;
import com.barkitapp.android.parse_backend.functions.GetPostById;
import com.barkitapp.android.parse_backend.functions.PostReply;
import com.barkitapp.android.parse_backend.objects.Post;
import com.barkitapp.android.pictures.FullscreenPictureActivity;
import com.barkitapp.android._main.MainActivity;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.parse.ParseGeoPoint;

import de.greenrobot.event.EventBus;

public class BarkDetailActivity extends AppCompatActivity {

    public static final String EXTRA_POST = "Post";
    public static final String EXTRA_POST_ID = "Origin.Reply.Notification";

    private Post mPost;
    public String mPostObjectId;
    public String mPostUserId;

    private long lastPostPerformed = 0;
    private boolean mCameFromNotification = false;

    private Tracker mTracker;
    private ImageLoader imageLoader;

    @UiThread
    private void collapseToolbar() {
        AppBarLayout appbarLayout = (AppBarLayout) findViewById(R.id.appbar);

        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) appbarLayout.getLayoutParams();
        AppBarLayout.Behavior behavior = (AppBarLayout.Behavior) params.getBehavior();
        if(behavior != null) {
            behavior.onNestedFling((CoordinatorLayout) findViewById(R.id.main_content), appbarLayout, null, 0, 10000, false);
        }
    }

    private void initView(Post post) {

        if(post != null)
            mPost = post;

        mPostUserId = mPost.getUserId();

        removeNotificationsForPost(mPost.getObjectId());

        ((TextView) findViewById(R.id.bark_text)).setText(mPost.getText());
        ((TextView) findViewById(R.id.comments_count)).setText(mPost.getReply_counter() + "");
        ((TextView) findViewById(R.id.hours)).setText(TimeConverter.getPostAge(mPost.getTime_created()));
        ((TextView) findViewById(R.id.distance)).setText(DistanceConverter.GetDistanceInKm(this, mPost.getLatitude(), mPost.getLongitude()));

        // share FAB was removed (see Bug 466)
        /*
        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.share);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Check out this BARK"); // todo replace link
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, mPost.getText() + "\n\n" + "Start barking http://barkitapp.com/");
                startActivity(Intent.createChooser(sharingIntent, getResources().getString(R.string.share_using)));
            }
        });
        fab.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.accent)));
        */

        final BarkReplyListFragment listFragment = (BarkReplyListFragment) getSupportFragmentManager().findFragmentById(R.id.fragment);

        final EditText chattext = (EditText) findViewById(R.id.chattext);
        chattext.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    performSend(chattext);
                    return true;
                }
                return false;
            }
        });

        ImageView answer = (ImageView) findViewById(R.id.fab);
        answer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                performSend(chattext);
            }
        });

        if(mPost != null && mPost.getImage_url() != null && !mPost.getImage_url().isEmpty())
        {
            ImageView backdrop = (ImageView) findViewById(R.id.image);
            View overlay = findViewById(R.id.overlay);

            overlay.setVisibility(View.VISIBLE);

            if(imageLoader == null)
                imageLoader = ImageLoader.getInstance();

            imageLoader.displayImage(mPost.getImage_url(), backdrop);

            backdrop.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), FullscreenPictureActivity.class);
                    intent.putExtra(FullscreenPictureActivity.EXTRA_IMAGE_URL, mPost.getImage_url());
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    getApplicationContext().startActivity(intent);
                }
            });

//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//
//                if(Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
//                    int id = getResources().getIdentifier("config_enableTranslucentDecor", "bool", "android");
//                    if (id == 0) {
//                        // not on KitKat
//                    } else {
//                        boolean enabled = getResources().getBoolean(id);
//                        if(!enabled) {
//                            return;
//                        }
//                    }
//                }
//
//                Window w = getWindow();
//                w.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//            }
        }

        invalidateOptionsMenu();
    }

    public void onEvent(RecievedPostForNotification event) {
        initView(event.getPost());
        EventBus.getDefault().post(new RequestUpdateRepliesEvent());
    }

    public void onEvent(CollapseLayoutEvent event) {
        collapseToolbar();
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

    private void removeNotificationsForPost(String post_id) {
        // remove all active notifications for that id
        String ns = Context.NOTIFICATION_SERVICE;
        NotificationManager nMgr = (NotificationManager) getApplicationContext().getSystemService(ns);
        nMgr.cancel(NotificationConverter.getIdFromPostId(post_id));
        nMgr.cancel(NotificationConverter.getIdFromPostId(post_id) + Constants.UPVOTE_NOTIFICATION_VALUE);
    }

    @Override
    public void onResume() {
        super.onResume();

        mTracker.setScreenName(BarkDetailActivity.class.getSimpleName());
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Setup application = (Setup) getApplication();
        mTracker = application.getDefaultTracker();

        Intent intent = getIntent();
        mPostObjectId = intent.getStringExtra(EXTRA_POST_ID);
        setContentView(R.layout.activity_bark_detail);
        mPost = MasterList.GetPostPost(mPostObjectId);

        if(mPostObjectId == null || mPostObjectId.equals("") || mPost == null) {
            // coming from notification
            mPostObjectId = intent.getStringExtra(EXTRA_POST_ID);
            if(mPostObjectId == null || mPostObjectId.equals(""))
            {
                Toast.makeText(this, R.string.failed_to_load_bark, Toast.LENGTH_LONG).show();
                finish();
                return;
            }

            mCameFromNotification = true;
            GetPostById.run(this, UserId.get(this), mPostObjectId);

            removeNotificationsForPost(mPostObjectId);
        }
        else {
            initView(mPost);
            mCameFromNotification = false;
        }

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        // todo CAUTION this is a WORKAROUND
        // remove me when google fixes issue
        /*
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.LOLLIPOP) {
            int marginResult = 0;
            int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
            if (resourceId > 0) {
                marginResult = getResources().getDimensionPixelSize(resourceId)*2;
            }

            CollapsingToolbarLayout.LayoutParams params = (CollapsingToolbarLayout.LayoutParams) toolbar.getLayoutParams();
            params.topMargin -= marginResult;
            toolbar.setLayoutParams(params);
        }
        // end of workaround
        */

        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();

        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
            actionBar.setTitle("");
        }
    }

    private void performSend(EditText chattext) {
        String textToPost = chattext.getText().toString();
        if(textToPost.trim().isEmpty()) {
            return;
        }

        if(System.currentTimeMillis() - lastPostPerformed < Constants.REPLY_BLOCK)
        {
            Toast.makeText(this, R.string.please_wait_try_again, Toast.LENGTH_SHORT).show();
            return;
        }

        Coordinates location = LocationService.getLocation(getApplicationContext());

        if(location != null) {
            PostReply.run(this, UserId.get(this),
                    mPost.getObjectId(),
                    new ParseGeoPoint(location.getLatitude(), location.getLongitude()),
                    new ParseGeoPoint(location.getLatitude(), location.getLongitude()),
                    textToPost,
                    0);

            lastPostPerformed = System.currentTimeMillis();

            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(chattext.getWindowToken(), 0);

            chattext.setText("");
        }
        else {
            Toast.makeText(this, R.string.location_not_found, Toast.LENGTH_LONG);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if(mCameFromNotification)
                {
                    Intent i = new Intent(this, MainActivity.class);
                    this.startActivity(i);
                    return true;
                }

                finish();
                return true;

            case R.id.action_open:

                Intent intent = new Intent(getApplicationContext(), FullscreenPictureActivity.class);
                intent.putExtra(FullscreenPictureActivity.EXTRA_IMAGE_URL, mPost.getImage_url());
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getApplicationContext().startActivity(intent);

                return true;

            case R.id.action_share:
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, getString(R.string.check_out_bark)); // todo replace link
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, mPost.getText() + "\n\n" + getString(R.string.start_barking) + " http://barkitapp.com/");
                startActivity(Intent.createChooser(sharingIntent, getResources().getString(R.string.share_using)));
                return true;

            case R.id.action_flag:
                final Coordinates location = LocationService.getLocation(this);

                if(location == null) {
                    Toast.makeText(this, R.string.please_wait_try_again, Toast.LENGTH_LONG).show();
                    return true;
                }

                new AlertDialog.Builder(this)
                        .setTitle(getString(R.string.inappropriate_bark))
                        .setMessage(getString(R.string.are_you_sure_flag))
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Flag.run(getApplication(),
                                        UserId.get(getApplication()),
                                        mPost.getObjectId(),
                                        ContentType.POST,
                                        new ParseGeoPoint(location.getLatitude(), location.getLongitude()));
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .setIcon(R.drawable.ic_flag)
                        .show();
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if(mCameFromNotification)
        {
            Intent i = new Intent(this, MainActivity.class);
            this.startActivity(i);
            finish();
        }
        else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        if(mPost != null && !mPost.getImage_url().isEmpty())
        {
            getMenuInflater().inflate(R.menu.bark_withpic_actions, menu);
        }
        else {
            getMenuInflater().inflate(R.menu.bark_actions, menu);
        }

        return true;
    }
}