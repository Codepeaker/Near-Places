package badhiyajobs.foursquareexample;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;


public class LauncherActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener,
        ResultCallback<LocationSettingsResult> {

    protected static final String TAG = "location-settings";


    protected static final int REQUEST_CHECK_SETTINGS = 0x1;


    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;


    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;


    protected final static String KEY_REQUESTING_LOCATION_UPDATES = "requesting-location-updates";
    protected final static String KEY_LOCATION = "location";
    protected final static String KEY_LAST_UPDATED_TIME_STRING = "last-updated-time-string";


    protected GoogleApiClient mGoogleApiClient;


    protected LocationRequest mLocationRequest;


    protected LocationSettingsRequest mLocationSettingsRequest;


    public Location mCurrentLocation;
    public String Longitude,Latitude;



    protected TextView mLatitudeTextView;
    protected TextView mLongitudeTextView;


    protected Boolean mRequestingLocationUpdates;
    public Location mLocation = null;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);

        mLatitudeTextView = (TextView)findViewById(R.id.latitude_text);
        mLongitudeTextView = (TextView)findViewById(R.id.longitude_text);





        mRequestingLocationUpdates = false;

        buildGoogleApiClient();
        createLocationRequest();
        buildLocationSettingsRequest();


    }




    public synchronized void buildGoogleApiClient() {
        Log.i(TAG, "Building GoogleApiClient");
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }


    public void createLocationRequest() {
        mLocationRequest = new LocationRequest();

        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);

        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }


    public void buildLocationSettingsRequest() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        mLocationSettingsRequest = builder.build();
    }


    public void onResult(LocationSettingsResult locationSettingsResult) {
        final Status status = locationSettingsResult.getStatus();
        switch (status.getStatusCode()) {
            case LocationSettingsStatusCodes.SUCCESS:
                Log.i(TAG, "All location settings are satisfied.");
                startLocationUpdates();
                break;
            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                Log.i(TAG, "Location settings are not satisfied. Show the user a dialog to" +
                        "upgrade location settings ");
                break;
            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                Log.i(TAG, "Location settings are inadequate, and cannot be fixed here. Dialog " +
                        "not created.");
                break;
        }
    }


    public void settingsrequest()
    {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        locationRequest.setInterval(30 * 1000);
        locationRequest.setFastestInterval(5 * 1000);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        builder.setAlwaysShow(true); //this is the key ingredient

        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                final LocationSettingsStates state = result.getLocationSettingsStates();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:

                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:

                        try {

                            status.startResolutionForResult(LauncherActivity.this, REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {

                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:

                        break;
                }
            }
        });
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {

            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        Log.i(TAG, "User agreed to make required location settings changes.");
                        startLocationUpdates();


                        break;
                    case Activity.RESULT_CANCELED:
                        Log.i(TAG, "User chose not to make required location settings changes.");
                        startLocationUpdates();
                        Toast.makeText(this,"Please change the Location setting to continue!! ",Toast.LENGTH_LONG).show();

                        break;
                }
                break;
        }
    }





    public void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient,
                mLocationRequest,
                this
        ).setResultCallback(new ResultCallback<Status>() {
            public void onResult(Status status) {
                mRequestingLocationUpdates = true;

            }
        });

    }




    public void updateLocationUI() {
        if (mCurrentLocation != null) {
            Intent i = new Intent(LauncherActivity.this,MainActivity.class);
            mLatitudeTextView.setText(String.valueOf(mCurrentLocation.getLatitude()));
            mLongitudeTextView.setText(String.valueOf(mCurrentLocation.getLongitude()));
           try {
                Latitude=String.valueOf(mCurrentLocation.getLatitude());
                Longitude = String.valueOf(mCurrentLocation.getLongitude());
                String location = Latitude+","+Longitude;
                i.putExtra("location",location);
               startActivity(i);



           }catch (NullPointerException e){
                e.printStackTrace();
           }

        }
    }


    public void stopLocationUpdates() {

        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient,
                this
        ).setResultCallback(new ResultCallback<Status>() {
            public void onResult(Status status) {
                mRequestingLocationUpdates = false;

            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
        settingsrequest();
    }

    @Override
    public void onResume() {
        super.onResume();

        if (mGoogleApiClient.isConnected() && mRequestingLocationUpdates) {
            startLocationUpdates();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mGoogleApiClient.isConnected()) {
            stopLocationUpdates();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
    }


    public void onConnected(Bundle connectionHint) {
        Log.i(TAG, "Connected to GoogleApiClient");

        startLocationUpdates(); //add this here


        if (mCurrentLocation == null) {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                return;
            }
            mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        updateLocationUI();
        }
        }


public void onLocationChanged(Location location) {
        mCurrentLocation = location;

        updateLocationUI();

        }

public void onConnectionSuspended(int cause) {
        Log.i(TAG, "Connection suspended");
        }

public void onConnectionFailed(ConnectionResult result) {

        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
        }

public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putBoolean(KEY_REQUESTING_LOCATION_UPDATES, mRequestingLocationUpdates);
        savedInstanceState.putParcelable(KEY_LOCATION, mCurrentLocation);

        super.onSaveInstanceState(savedInstanceState);
        }



}