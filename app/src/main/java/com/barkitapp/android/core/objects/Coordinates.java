package com.barkitapp.android.core.objects;

public class Coordinates {

    private double mLatitude;
    private double mLongitude;

    public Coordinates(double latitude, double longitude) {

        mLatitude = latitude;
        mLongitude = longitude;
    }

    public double getLatitude() {
        return mLatitude;
    }

    public void setLatitude(double mLatitude) {
        this.mLatitude = mLatitude;
    }

    public double getLongitude() {
        return mLongitude;
    }

    public void setLongitude(double mLongitude) {
        this.mLongitude = mLongitude;
    }
}
