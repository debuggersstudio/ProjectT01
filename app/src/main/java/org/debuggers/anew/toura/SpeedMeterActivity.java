package org.debuggers.anew.toura;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.anew.toura.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

public class SpeedMeterActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    TextView latitudeText;
    TextView longitudeText;
    TextView altitudetxt;
    TextView speedtxt;
    TextView timetxt;
    TextView beartxt;
    TextView Accuracytxt;
    double myal ;
    double bearing ;
    float speed  ;
    double accuracy ;
    double time  ;
    private FusedLocationProviderApi locationProvider = LocationServices.FusedLocationApi;
    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    private Double myLatitude;
    private Double myLongitude;
    private static final int MY_PERMISSION_REQUEST_FINE_LOCATION = 101;
    private static final int MY_PERMISSION_REQUEST_COARSE_LOCATION = 102;
    private boolean permissionIsGranted = false;
    Button settingButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speed_meter);



        settingButton = (Button) findViewById(R.id.SettingBtn);
        speedtxt = (TextView)findViewById(R.id.SpeedText);


        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        locationRequest = new LocationRequest();
        locationRequest.setInterval(10 * 1000);
        locationRequest.setFastestInterval(15 * 1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        settingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSION_REQUEST_FINE_LOCATION);
                } else {
                    permissionIsGranted = true;
                }

                speedtxt.setText("00 km/h");
            }
        });


    }

    @Override
    public void onConnected(Bundle bundle) {
        requestLocationUpdates();
    }

    private void requestLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSION_REQUEST_FINE_LOCATION);
            } else {
                permissionIsGranted = true;
            }
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {

        speed = location.getSpeed();
        int kmh = (int) (speed*3.6);
        if(kmh<=90){
        speedtxt.setText( String.valueOf(kmh) +  " km/h");}
        else if (kmh>90){
            speedtxt.setText("95 km/h");
        }



    }

    @Override
    protected void onStart() {
        super.onStart();
        googleApiClient.connect();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (permissionIsGranted) {
            if (googleApiClient.isConnected()) {
                requestLocationUpdates();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (permissionIsGranted)
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (permissionIsGranted)
            googleApiClient.disconnect();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSION_REQUEST_FINE_LOCATION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission granted
                    permissionIsGranted = true;
                } else {
                    //permission denied
                    permissionIsGranted = false;
                    Toast.makeText(getApplicationContext(), "This app requires location permission to be granted", Toast.LENGTH_SHORT).show();
                   speedtxt.setText("Speed : permission not granted");
                    //longitudeText.setText("Longitude : permission not granted");
                }
                break;
            case MY_PERMISSION_REQUEST_COARSE_LOCATION:
                // do something for coarse location
                break;
        }
    }
}
