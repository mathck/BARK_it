package com.barkitapp.android.bark_detail;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Build;
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
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.barkitapp.android.R;
import com.barkitapp.android.core.objects.Coordinates;
import com.barkitapp.android.core.services.LocationService;
import com.barkitapp.android.core.services.MasterList;
import com.barkitapp.android.core.services.UserId;
import com.barkitapp.android.core.utility.DistanceConverter;
import com.barkitapp.android.core.utility.TimeConverter;
import com.barkitapp.android.parse.enums.ContentType;
import com.barkitapp.android.parse.functions.Flag;
import com.barkitapp.android.parse.functions.PostReply;
import com.barkitapp.android.parse.objects.Post;
import com.parse.ParseGeoPoint;

public class BarkDetailActivity extends AppCompatActivity {

    public static final String EXTRA_POST = "Post";

    private Post mPost;
    public String ObjectId;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void setStatusBarColor() {
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(getResources().getColor(R.color.primary_dark));
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        ObjectId = intent.getStringExtra(EXTRA_POST);

        setContentView(R.layout.bark_detail_activity);

        setStatusBarColor();

        mPost = MasterList.GetPost(ObjectId);

        if(ObjectId == null || ObjectId.equals("") || mPost == null) {
            Toast.makeText(this, "Failed to load BARK", Toast.LENGTH_LONG).show();
            finish();
        }

        ((TextView) findViewById(R.id.bark_text)).setText(mPost.getText());
        ((TextView) findViewById(R.id.comments_count)).setText(mPost.getReply_counter() + "");
        ((TextView) findViewById(R.id.hours)).setText(TimeConverter.getPostAge(mPost.getTime_created()));
        ((TextView) findViewById(R.id.distance)).setText(DistanceConverter.GetDistanceInKm(this, mPost.getLatitude(), mPost.getLongitude()));

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();

        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            //actionBar.setTitle("BARK");
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.share);
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
        FloatingActionButton answer = (FloatingActionButton) findViewById(R.id.fab);
        answer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                performSend(chattext, listFragment);
            }
        });
        answer.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.primary)));

        //CollapsingToolbarLayout collapsingToolbar =
        //        (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        //collapsingToolbar.setTitle("BARK");

        //loadBackdrop();
    }

    private void performSend(EditText chattext, BarkReplyListFragment listFragment) {
        String textToPost = chattext.getText().toString();
        if(textToPost.isEmpty()) {
            return;
        }

        Coordinates location = LocationService.getLocation(getApplicationContext());

        PostReply.run(UserId.get(this),
                mPost.getObjectId(),
                new ParseGeoPoint(location.getLatitude(), location.getLongitude()),
                new ParseGeoPoint(location.getLatitude(), location.getLongitude()),
                textToPost,
                0);

        //listFragment.getRepliesFromParse();

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(chattext.getWindowToken(), 0);

        chattext.setText("");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_flag:
                final Coordinates location = LocationService.getLocation(this);
                new AlertDialog.Builder(this)
                        .setTitle("Inappropriate BARK")
                        .setMessage("Are you sure you want to flag this BARK?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Flag.run(UserId.get(getApplication()),
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
                        .setIcon(R.mipmap.ic_launcher)
                        .show();
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.bark_actions, menu);
        return true;
    }
}
