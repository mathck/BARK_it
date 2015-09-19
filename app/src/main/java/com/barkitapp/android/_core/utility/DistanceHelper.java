package com.barkitapp.android._core.utility;

import com.parse.ParseGeoPoint;

public class DistanceHelper {

    public static ParseGeoPoint GetOffsetLocation(ParseGeoPoint location, double offset) {
        //Position, decimal degrees
        double lat = location.getLatitude();
        double lon = location.getLongitude();

        //Earth’s radius, sphere
        double R  = 6378137;

        //Coordinate offsets in radians
        double dLat = offset / R;
        double dLon = offset / (R * Math.cos(Math.PI * lat / 180));

        ParseGeoPoint result = new ParseGeoPoint();
        result.setLatitude(lat + dLat * 180 / Math.PI);
        result.setLongitude(lon + dLon * 180 / Math.PI);

        return result;
    }
}
