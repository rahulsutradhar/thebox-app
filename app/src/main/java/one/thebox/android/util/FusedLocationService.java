package one.thebox.android.util;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import one.thebox.android.app.TheBox;

/**
 * Created by robin on 11/10/16.
 */

public abstract class FusedLocationService implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private Context mContext;
    private GoogleApiClient mGoogleApiClient;
    private static FusedLocationService runningInstance;
    private Bundle bundle;
    private static LocationRequest mLocationRequest;

    public FusedLocationService(Activity mContext) {
        if (mLocationRequest == null) {
            mLocationRequest = new LocationRequest();
            mLocationRequest.setInterval(1000);
        }
        if (runningInstance != null) {
            this.mContext = runningInstance.mContext;
            this.mGoogleApiClient = runningInstance.mGoogleApiClient;
            Log.i("FusedLocationService", "Using running Instance");
            if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
                Log.i("FusedLocationService", "Already connected");
                startLocationUpdates();
                onConnected(bundle);
            } else {
                Log.i("FusedLocationService", "Connecting...");
                buildGoogleApiClient();
                runningInstance.mGoogleApiClient = this.mGoogleApiClient;
            }

        } else {
            Log.i("FusedLocationService", "Without Using running Instance");
            this.mContext = mContext;
            buildGoogleApiClient();
            runningInstance = this;
        }
    }

    public void stopLocationUpdates() {
        if (checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION))
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }

    public void startLocationUpdates() {
        if (checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION))
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    public void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(mContext)
                .addConnectionCallbacks(this).addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
        connect();
    }

    public void connect() {
        mGoogleApiClient.connect();
    }

    public void disconnect() {
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    public boolean checkPermission(String permission) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            if (mContext.checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (!checkPermission(Manifest.permission.ACCESS_FINE_LOCATION)) {
            onConnectionFailed(null);
            return;
        }
        startLocationUpdates();
        Location mLastKnownLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastKnownLocation != null) {
            Log.i("FusedLocationService", "fetched from gps");
            MyLocation myLocation = new MyLocation(mLastKnownLocation.getLongitude(), mLastKnownLocation.getLatitude());
            saveLastKnownLocation(myLocation);
            onSuccess(myLocation);
        } else {
            Log.i("FusedLocationService", "fetched from memory");
            onConnectionFailed(null);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        MyLocation myLocation = getSavedLocation();
        if (myLocation == null) {
            onFailed(connectionResult);
        } else {
            onSuccess(myLocation);
        }
    }

    public static MyLocation getLatestLocation() {
        String lat = PrefUtils.getString(TheBox.getInstance(), PrefUtils.KEY_LAT, null);
        String lng = PrefUtils.getString(TheBox.getInstance(), PrefUtils.KEY_LNG, null);
        if (TextUtils.isEmpty(lat) || TextUtils.isEmpty(lng)) {
            return null;
        } else {
            MyLocation location = new MyLocation(lat, lng);
            return location;
        }
    }

    private void saveLastKnownLocation(MyLocation mLastKnownLocation) {
        PrefUtils.putString(TheBox.getInstance(), PrefUtils.KEY_LAT, mLastKnownLocation.getLatitude() + "");
        PrefUtils.putString(TheBox.getInstance(), PrefUtils.KEY_LNG, mLastKnownLocation.getLongitude() + "");
    }

    private MyLocation getSavedLocation() {
        String latitude = PrefUtils.getString(TheBox.getInstance(), PrefUtils.KEY_LAT, null);
        String longitude = PrefUtils.getString(TheBox.getInstance(), PrefUtils.KEY_LNG, null);
        try {
            return new MyLocation(longitude, latitude);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location == null) return;
        MyLocation myLocation = new MyLocation(location.getLongitude(), location.getLatitude());
        saveLastKnownLocation(myLocation);
    }

    protected abstract void onSuccess(MyLocation mLastKnownLocation);

    protected abstract void onFailed(ConnectionResult connectionResult);

    public static class MyLocation {
        private Double longitude;
        private Double latitude;

        public MyLocation(String longitude, String latitude) {
            this.longitude = Double.parseDouble(longitude);
            this.latitude = Double.parseDouble(latitude);
        }

        public MyLocation(Double longitude, Double latitude) {
            this.longitude = longitude;
            this.latitude = latitude;
        }

        public Double getLatitude() {
            return latitude;
        }

        public void setLatitude(Double latitude) {
            this.latitude = latitude;
        }

        public Double getLongitude() {
            return longitude;
        }

        public void setLongitude(Double longitude) {
            this.longitude = longitude;
        }
    }

}
