package com.webrand.gpstracker;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.Settings;
import android.view.View;
import android.view.WindowManager;
import android.widget.Chronometer;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.TaskStackBuilder;
import androidx.core.content.ContextCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.webrand.gpstracker.db.DatabaseOperation;
import com.webrand.gpstracker.models.TrackerInfo;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

public class MapsActivity extends AppCompatActivity implements LocationListener, OnMapReadyCallback, View.OnClickListener {

    private static final String CHANNEL_ID = "MapsActivity";
    private String[] permissions = new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION};

    private static final float DEFAULT_ZOOM = 15f;
    private static final int DEFAULT_ORIENTATION_OF_THE_CAMERA = 90;
    private static final int DEFAULT_DEGREE_OF_THE_CAMERA = 30;
    public static final int LOCATION_PERMISSION_REQUEST_CODE = 100;

    private GoogleMap myMap;
    private ProgressDialog myProgress;

    private LatLng startLatLon;
    private LatLng lastLatLon;

    private Location lastLocation;

    private MaterialButton btn_start_tracking;
    private TextView textDistance;
    private MaterialCardView trackingLayout;

    private Chronometer mChronometer;

    private double distance = 0;
    private boolean isStarted = false;
    private boolean isInBackground = false;
    private int notificationId = 1;
    private NotificationManagerCompat notificationManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        showWaitDialog();

        FloatingActionButton btn = findViewById(R.id.FBtngetZoomToCurrentLoc);
        btn.setOnClickListener(this);

        btn_start_tracking = findViewById(R.id.btn_start_tracking);
        btn_start_tracking.setOnClickListener(this);

        mChronometer = findViewById(R.id.txt_chronometer);
        textDistance = findViewById(R.id.txt_distance);
        trackingLayout = findViewById(R.id.info_layout);

        Toast.makeText(this, "Create", Toast.LENGTH_SHORT).show();
    }

    private void showWaitDialog() {
        myProgress = new ProgressDialog(this);
        myProgress.setTitle("Map Loading ...");
        myProgress.setMessage("Please wait...");
        myProgress.setCancelable(true);
        myProgress.show();

        initMap();
    }

    private void initMap(){
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        Objects.requireNonNull(mapFragment).getMapAsync(MapsActivity.this);
    }



    private void askLocationPermission() {
        int accessCoarsePermission
                = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
        int accessFinePermission
                = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);

        if (accessCoarsePermission != PackageManager.PERMISSION_GRANTED
                || accessFinePermission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, permissions,
                    LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            myMap.setMyLocationEnabled(true);
            getCurrentLocation();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 1
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                showWaitDialog();
            } else {
                Toast.makeText(this, "Permission denied!", Toast.LENGTH_LONG).show();
            }
        }
    }

    private String getEnabledLocationProvider() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        String gpsProvider = Objects.requireNonNull(locationManager).getBestProvider(new Criteria(), true);

        boolean enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (!enabled) {
            Toast.makeText(this, "No location provider enabled!", Toast.LENGTH_LONG).show();
            showSettingsDialog();
            return null;
        }
        return gpsProvider;
    }

    private void getCurrentLocation() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        String locationProvider = getEnabledLocationProvider();

        if (locationProvider == null) {
            return;
        }

        final long MIN_TIME_BW_UPDATES_IN_MILLI_SECOND = 1000;

        final float MIN_DISTANCE_CHANGE_FOR_UPDATES_IN_METER = 1;

        Location myLocation;

        assert locationManager != null;

        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, permissions,
                    LOCATION_PERMISSION_REQUEST_CODE);
            return;
        }
        locationManager.requestLocationUpdates(
                locationProvider,
                MIN_TIME_BW_UPDATES_IN_MILLI_SECOND,
                MIN_DISTANCE_CHANGE_FOR_UPDATES_IN_METER, this);

            myLocation = locationManager
                    .getLastKnownLocation(locationProvider);


        if (myLocation != null) {

            startLatLon = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());

            createCameraPosition(startLatLon);

            btn_start_tracking.setVisibility(View.VISIBLE);
        } else {
            Toast.makeText(this, "Location not found!\n Please wait", Toast.LENGTH_LONG).show();
        }


    }

    private void createCameraPosition(LatLng latLng) {
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(latLng)
                .zoom(DEFAULT_ZOOM)
                .bearing(DEFAULT_ORIENTATION_OF_THE_CAMERA)
                .tilt(DEFAULT_DEGREE_OF_THE_CAMERA)
                .build();
        myMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    @Override
    public void onLocationChanged(Location location) {
        if (isStarted) {
            lastLatLon = new LatLng(location.getLatitude(), location.getLongitude());

            if (lastLocation==null) {startLatLon = new LatLng(location.getLatitude(),location.getLongitude());}

            Location startLocation = lastLocation == null ? location : lastLocation;

            lastLocation = location;
            calculateDistance(lastLocation, startLocation);
        }
    }

    private void calculateDistance(Location last,Location current) {
        double meter = current.distanceTo(last);
        distance +=meter;
        textDistance.setText(String.valueOf(Math.floor(distance)));
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) { }

    @Override
    public void onProviderEnabled(String s) {
        Toast.makeText(this, "Provider Enabled", Toast.LENGTH_SHORT).show();
        try {
            showWaitDialog();
        }catch (WindowManager.BadTokenException e){
            e.printStackTrace();
        }
    }

    @Override
    public void onProviderDisabled(String s) {
        Toast.makeText(this, "ProvideDisabled", Toast.LENGTH_SHORT).show();
        try {
            showSettingsDialog();
        }catch (WindowManager.BadTokenException e){
            e.printStackTrace();
        }
        btn_start_tracking.setVisibility(View.GONE);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Toast.makeText(this, "onMapReady", Toast.LENGTH_SHORT).show();
        myMap = googleMap;
        myProgress.dismiss();
        askLocationPermission();
    }

    private void showSettingsDialog(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

        alertDialog.setTitle("GPS is settings");

        alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");

        alertDialog.setPositiveButton("Settings", (dialog, which) -> {
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);
        });

        alertDialog.setNegativeButton("Cancel", (dialog, which) -> {
            dialog.cancel();
            Toast.makeText(MapsActivity.this, "GPS is disable", Toast.LENGTH_SHORT).show();
        });

        alertDialog.show();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn_start_tracking) {
            startTracking();
        }
        else if (view.getId()==R.id.FBtngetZoomToCurrentLoc){
            refresh();
        }
    }

    private void refresh() {
        if (startLatLon==null){
            showWaitDialog();
        }else {
            createCameraPosition(startLatLon);
        }
    }

    private void startTracking() {
        if (!isStarted) {
            startChronometer();
            isStarted = true;
            btn_start_tracking.setText(getResources().getString(R.string.btn_text_stop));
            trackingLayout.setVisibility(View.VISIBLE);
        }
        else {
            isStarted=false;
            btn_start_tracking.setText(getResources().getString(R.string.btn_text_start));
            saveToDB();
            cleanViews();
            trackingLayout.setVisibility(View.GONE);
            Toast.makeText(this, "Saved!", Toast.LENGTH_SHORT).show();
        }
    }

    private void cleanViews() {
        textDistance.setText("");
        mChronometer.setBase(SystemClock.elapsedRealtime());
    }

    private void saveToDB() {
        @SuppressLint("SimpleDateFormat") String sc =new SimpleDateFormat("hh:mm yyyy-MM-dd a").format(new Date());
        DatabaseOperation.get(this)
                .add(new TrackerInfo(Math.floor(startLatLon.latitude),Math.floor(startLatLon.longitude),
                        Math.floor(lastLatLon.latitude),Math.floor(lastLatLon.longitude),mChronometer.getText().toString(),
                        sc, textDistance.getText().toString()
                ));
    }

    private void startChronometer(){
        mChronometer.setBase(SystemClock.elapsedRealtime());
        mChronometer.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isStarted){ showNotification();}
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isStarted){notificationManager.cancel(notificationId); isInBackground=false; }
    }

    private void showNotification(){
        String CHANNEL_ID = "MapsActivity";

        Intent resultIntent = new Intent(this, MapsActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addNextIntentWithParentStack(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        notificationManager = NotificationManagerCompat.from(this);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID);
        builder.setContentTitle(getString(R.string.app_name))
                .setContentText("Tracking your activity")
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setOngoing(true);
        notificationManager.notify(notificationId, builder.build());

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isStarted){  notificationManager.cancel(notificationId);
        }
    }
}
