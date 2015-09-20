package com.barkitapp.android.activities;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.barkitapp.android.R;
import com.barkitapp.android._core.services.UserId;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        final String friend_invite_code = UserId.get(this);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();

        if(actionBar != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("");
        }

        final EditText code = (EditText) findViewById(R.id.code);
        code.setText(friend_invite_code);

        final TextView friend_counter = (TextView) findViewById(R.id.friend_count);
        final TextView invitedFriendstext = (TextView) findViewById(R.id.invitedFriendstext);

        ParseQuery<ParseObject> query = ParseQuery.getQuery("BarkItUser");
        query.getInBackground(friend_invite_code, new GetCallback<ParseObject>() {
            public void done(ParseObject object, ParseException e) {
                if (e == null) {
                    invitedFriendstext.setText(R.string.invited_friends);
                    friend_counter.setText(object.getInt("referred_friend_counter") + "");
                } else {
                    invitedFriendstext.setText(R.string.try_again_later);
                    friend_counter.setText(":(");
                }
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.share);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, getString(R.string.using_barkit_check_it_out)); // todo replace link
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, getString(R.string.download_and_enter_my_invite_code) + friend_invite_code + "\n\n" + "http://barkitapp.com/");
                startActivity(Intent.createChooser(sharingIntent, getResources().getString(R.string.share_using)));
            }
        });
        fab.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.accent)));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
