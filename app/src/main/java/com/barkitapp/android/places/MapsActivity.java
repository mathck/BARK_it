package com.barkitapp.android.places;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.barkitapp.android.R;
import com.barkitapp.android.core.objects.Coordinates;
import com.barkitapp.android.core.services.LocationService;
import com.barkitapp.android.core.utility.Constants;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private Marker mMarker;
    private Circle mCircle;
    //private GooglePlaces mClient;
    //private ArrayAdapterSearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.places_change);

        //mClient = new GooglePlaces("AIzaSyCY9uZjMyBhr8ABjsy6NIurVoCu1A-nqbM");

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Change Place");

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        LocationService.storeLocation(this, lastKnownLocation);

        if(mMarker != null)
            mMarker.remove();

        if(mCircle != null)
            mCircle.remove();

        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // todo store location
            }
        });
        fab.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.primary)));

        Snackbar.make(findViewById(R.id.main_content), "Click on any place", Snackbar.LENGTH_SHORT).setAction("Action", null).show();
    }
/*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.maps_actions, menu);

        //MenuItem searchItem = menu.findItem(R.id.action_search);
        //ArrayAdapterSearchView searchView = (ArrayAdapterSearchView) searchItem.getActionView();
        searchView = (ArrayAdapterSearchView) menu.findItem(R.id.action_search).getActionView();

        searchView.setAdapter(new ArrayAdapter<String>(this, R.layout.places_auto_complete_list_item, new ArrayList<String>()));

        searchView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                searchView.setText(searchView.getAdapter().getItem(position).toString());
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                if(newText.length() < 2)
                    return false;

                new RetrieveFeedTask().execute(newText);

                return true;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    class RetrieveFeedTask extends AsyncTask<String, Void, ArrayList<String>> {

        private Exception exception;

        protected ArrayList<String> doInBackground(String... newText) {
            try {

                ArrayList<String> result = new ArrayList<>();

                List<se.walkercrou.places.Place> places = mClient.getPlacesByQuery(newText[0], 5);

                for (se.walkercrou.places.Place location : places) {
                    result.add(location.getAddress());
                }

                return result;
            } catch (Exception e) {
                this.exception = e;
                return null;
            }
        }

        protected void onPostExecute(ArrayList<String> places) {
            searchView.getAdapter().clear();

            if (places != null) {
                for (String place : places) {
                    searchView.getAdapter().insert(place, searchView.getAdapter().getCount());
                }
            }

            searchView.getAdapter().notifyDataSetChanged();
        }
    }
*/
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_search:
                // todo start search
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void scrollToCurrentPosition() {
        if(mMap != null)
        {
            Coordinates location = LocationService.getLocation(this);

            LatLng current_place = new LatLng(location.getLatitude(), location.getLongitude());

            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(current_place, 14.5f));

            //mMap.addMarker(new MarkerOptions()
            //        .title("Your Position")
            //        .position(current_place));
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMyLocationEnabled(true);

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng point) {
                if(mMarker != null)
                    mMarker.remove();

                if(mCircle != null)
                    mCircle.remove();

                mMarker = mMap.addMarker(new MarkerOptions()
                        .title("Selected Place")
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_location_on_white_48dp))
                        .position(point));

                mCircle = mMap.addCircle(new CircleOptions()
                        .center(point)
                        .radius(Constants.CHANGE_PLACE_RADIUS)
                        .strokeColor(getResources().getColor(R.color.primary_dark))
                        .fillColor(Color.argb(20, 244, 67, 54)));
            }
        });

        scrollToCurrentPosition();
    }
}
