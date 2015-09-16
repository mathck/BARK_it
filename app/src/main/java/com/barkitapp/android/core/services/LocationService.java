package com.barkitapp.android.core.services;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;

import com.barkitapp.android.core.objects.Coordinates;
import com.barkitapp.android.core.utility.SharedPrefKeys;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class LocationService {

    public static Coordinates getLocation(Context context) {

        String latitude = InternalAppData.getString(context, InternalAppData.getBoolean(context, SharedPrefKeys.HAS_SET_MANUAL_LOCATION) ? SharedPrefKeys.LOCATION_LATITUDE_MANUAL : SharedPrefKeys.LOCATION_LATITUDE);
        String longitude = InternalAppData.getString(context, InternalAppData.getBoolean(context, SharedPrefKeys.HAS_SET_MANUAL_LOCATION) ? SharedPrefKeys.LOCATION_LONGITUDE_MANUAL : SharedPrefKeys.LOCATION_LONGITUDE);

        if(latitude == null || longitude == null || latitude.isEmpty() || longitude.isEmpty())
            return null;

        Date date = new Date(InternalAppData.getLongTime(context, SharedPrefKeys.LOCATION_DATE));

        return new Coordinates(Double.parseDouble(latitude), Double.parseDouble(longitude), date);
    }

    public static void storeLocation(Context context, Location location) {
        InternalAppData.Store(context, SharedPrefKeys.LOCATION_LATITUDE, Double.valueOf(location.getLatitude()).toString());
        InternalAppData.Store(context, SharedPrefKeys.LOCATION_LONGITUDE, Double.valueOf(location.getLongitude()).toString());

        // Store Date with location
        InternalAppData.Store(context, SharedPrefKeys.LOCATION_DATE, new Date().getTime());
    }

    /**
     * get city name from coordinates
     * @param context
     * @param loc
     * @return
     */
    public static String getLocationCity(Context context, Coordinates loc) {

        if(!InternalAppData.getString(context, SharedPrefKeys.MANUAL_TITLE).isEmpty()) {
            return InternalAppData.getString(context, SharedPrefKeys.MANUAL_TITLE);
        }

        String cityName = null;

        if(loc == null)
            return "";

        Geocoder gcd = new Geocoder(context, Locale.getDefault());
        List<Address> addresses;
        try {
            addresses = gcd.getFromLocation(loc.getLatitude(),
                    loc.getLongitude(), 1);
            if (addresses.size() > 0)
                System.out.println(addresses.get(0).getLocality());
            cityName = addresses.get(0).getLocality();
        }
        catch (Exception e) {
            return "BARK it";
        }

        return cityName;
    }

    /**
     * get state name from coordinates
     * @param context
     * @param loc
     * @return
     */
    public static String getLocationCountry(Context context, Coordinates loc) {
        String cityName = null;

        Geocoder gcd = new Geocoder(context, Locale.getDefault());
        List<Address> addresses;
        try {
            addresses = gcd.getFromLocation(loc.getLatitude(),
                    loc.getLongitude(), 1);
            if (addresses.size() > 0)
                System.out.println(addresses.get(0).getLocality());
            cityName = addresses.get(0).getCountryName();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        return cityName;
    }
}
