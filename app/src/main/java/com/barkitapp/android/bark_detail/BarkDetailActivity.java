package com.barkitapp.android.bark_detail;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.barkitapp.android.R;
import com.barkitapp.android.core.objects.Coordinates;
import com.barkitapp.android.core.services.LocationService;
import com.barkitapp.android.parse.enums.ContentType;
import com.barkitapp.android.parse.functions.Flag;
import com.parse.ParseGeoPoint;

public class BarkDetailActivity extends AppCompatActivity {

    public static final String EXTRA_NAME = "cheese_name";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bark_detail_activity);

        Intent intent = getIntent();
        final String bark_text = intent.getStringExtra(EXTRA_NAME);

        if(bark_text != null && !bark_text.equals("")) {
            ((TextView) findViewById(R.id.bark_text)).setText(bark_text);
        }

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();

        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("BARK");
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.share);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "BARKit");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, bark_text + "\n" + "https://play.google.com/store/apps/details?id=com.gmail.mathck.SpaceTrip");
                startActivity(Intent.createChooser(sharingIntent, getResources().getString(R.string.share_using)));
            }
        });
        fab.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.accent)));

        FloatingActionButton answer = (FloatingActionButton) findViewById(R.id.fab);
        answer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // send answer here
            }
        });
        answer.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.primary)));

        //CollapsingToolbarLayout collapsingToolbar =
        //        (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        //collapsingToolbar.setTitle("BARK");

        //loadBackdrop();
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
                                Flag.Run("kHoG2ihhvD",
                                        "objectId", // todo objectId for Flag
                                        ContentType.POST,
                                        new ParseGeoPoint(location.getLatitude(), location.getLongitude()));
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
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
