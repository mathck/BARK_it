package com.barkitapp.android.bark_detail;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
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

import com.barkitapp.android.Messages.RecievedPostForNotification;
import com.barkitapp.android.Messages.RequestUpdateRepliesEvent;
import com.barkitapp.android.R;
import com.barkitapp.android.core.objects.Coordinates;
import com.barkitapp.android.core.services.LocationService;
import com.barkitapp.android.core.services.MasterList;
import com.barkitapp.android.core.services.UserId;
import com.barkitapp.android.core.utility.Constants;
import com.barkitapp.android.core.utility.DistanceConverter;
import com.barkitapp.android.core.utility.TimeConverter;
import com.barkitapp.android.parse.enums.ContentType;
import com.barkitapp.android.parse.functions.Flag;
import com.barkitapp.android.parse.functions.GetPostById;
import com.barkitapp.android.parse.functions.PostReply;
import com.barkitapp.android.parse.objects.Post;
import com.barkitapp.android.prime.MainActivity;
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

    private void initView(Post post) {

        if(post != null)
            mPost = post;

        mPostUserId = mPost.getUserId();

        ((TextView) findViewById(R.id.bark_text)).setText(mPost.getText());
        ((TextView) findViewById(R.id.comments_count)).setText(mPost.getReply_counter() + "");
        ((TextView) findViewById(R.id.hours)).setText(TimeConverter.getPostAge(mPost.getTime_created()));
        ((TextView) findViewById(R.id.distance)).setText(DistanceConverter.GetDistanceInKm(this, mPost.getLatitude(), mPost.getLongitude()));

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

        final BarkReplyListFragment listFragment = (BarkReplyListFragment) getSupportFragmentManager().findFragmentById(R.id.fragment);

        final EditText chattext = (EditText) findViewById(R.id.chattext);
        chattext.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    performSend(chattext, listFragment);
                    return true;
                }
                return false;
            }
        });

        ImageView answer = (ImageView) findViewById(R.id.fab);
        answer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                performSend(chattext, listFragment);
            }
        });
    }

    public void onEvent(RecievedPostForNotification event) {
        initView(event.getPost());
        EventBus.getDefault().post(new RequestUpdateRepliesEvent());
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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        mPostObjectId = intent.getStringExtra(EXTRA_POST);

        setContentView(R.layout.bark_detail_activity);

        mPost = MasterList.GetPost(mPostObjectId);

        if(mPostObjectId == null || mPostObjectId.equals("") || mPost == null) {
            mPostObjectId = intent.getStringExtra(EXTRA_POST_ID);
            if(mPostObjectId == null || mPostObjectId.equals(""))
            {
                Toast.makeText(this, "Failed to load BARK", Toast.LENGTH_LONG).show();
                finish();
                return;
            }

            mCameFromNotification = true;
            GetPostById.run(UserId.get(this), mPostObjectId);
        }
        else {
            initView(mPost);
            mCameFromNotification = false;
        }

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        // todo CAUTION this is a WORKAROUND
        // remove me when google fixes issue
        //if (Build.VERSION.SDK_INT == Build.VERSION_CODES.LOLLIPOP) {
        //    int marginResult = 0;
        //    int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        //    if (resourceId > 0) {
        //        marginResult = getResources().getDimensionPixelSize(resourceId)*2;
        //    }
        //   CollapsingToolbarLayout.LayoutParams params = (CollapsingToolbarLayout.LayoutParams) toolbar.getLayoutParams();
        //   params.topMargin -= marginResult;
        //   toolbar.setLayoutParams(params);
        //}
        // end of workaround

        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();

        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
            //actionBar.setTitle("BARK");
        }

        //CollapsingToolbarLayout collapsingToolbar =
        //        (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        //collapsingToolbar.setTitle("BARK");
    }

    private void performSend(EditText chattext, BarkReplyListFragment listFragment) {
        String textToPost = chattext.getText().toString();
        if(textToPost.trim().isEmpty()) {
            return;
        }

        if(System.currentTimeMillis() - lastPostPerformed < Constants.REPLY_BLOCK)
        {
            Toast.makeText(this, "Please wait a few seconds to reply again", Toast.LENGTH_SHORT).show();
            return;
        }

        Coordinates location = LocationService.getLocation(getApplicationContext());

        PostReply.run(UserId.get(this),
                mPost.getObjectId(),
                new ParseGeoPoint(location.getLatitude(), location.getLongitude()),
                new ParseGeoPoint(location.getLatitude(), location.getLongitude()),
                textToPost,
                0);

        lastPostPerformed = System.currentTimeMillis();

        //listFragment.getRepliesFromParse();

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(chattext.getWindowToken(), 0);

        chattext.setText("");
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
            case R.id.action_flag:
                final Coordinates location = LocationService.getLocation(this);
                new AlertDialog.Builder(this)
                        .setTitle("Inappropriate BARK")
                        .setMessage("Are you sure you want to flag this BARK?")
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
        getMenuInflater().inflate(R.menu.bark_actions, menu);
        return true;
    }
}