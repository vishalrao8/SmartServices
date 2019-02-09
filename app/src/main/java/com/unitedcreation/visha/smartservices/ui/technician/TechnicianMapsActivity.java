package com.unitedcreation.visha.smartservices.ui.technician;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.unitedcreation.visha.smartservices.R;
import com.unitedcreation.visha.smartservices.ui.MainActivity;
import com.unitedcreation.visha.smartservices.utils.Utilities;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static com.unitedcreation.visha.smartservices.utils.StringResourceProvider.CATEGORY;
import static com.unitedcreation.visha.smartservices.utils.StringResourceProvider.MAPS_PACKAGE_ID;
import static com.unitedcreation.visha.smartservices.utils.StringResourceProvider.MAPS_STARTING_URL;
import static com.unitedcreation.visha.smartservices.utils.StringResourceProvider.TECHNICIAN_ACCEPTED;
import static com.unitedcreation.visha.smartservices.utils.StringResourceProvider.TECHNICIAN_LOCATIONS;
import static com.unitedcreation.visha.smartservices.utils.StringResourceProvider.USER_LOCATIONS;

public class TechnicianMapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private final int ZOOM = 15;

    private FirebaseAuth mFireBaseAuth;
    private String userId;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;

    private GoogleMap mMap;
    private Location lastKnownLocation;
    private GeoFire TgeoFire;
    private GeoFire UgeoFire;
    private String category;
    private Button navigateButton;
    private Boolean buttonShowed = false;
    private String userLongitude;
    private String userLatitude;
    private String requestedUserId;
    private Boolean destinationArrived = false;
    private Location userLocation;
    private Boolean technicianAccepted = false;
    private GeoQuery geoQuery;

    private GeoQueryEventListener geoQueryEventListener;

    private LocationRequest locationRequest;
    private LocationCallback locationCallback;

    private FusedLocationProviderClient mFusedLocationClient;

    public void showButton() {

        if (!buttonShowed) {

            navigateButton.animate().translationYBy(-(Utilities.pixels(this))).setDuration(500);
            buttonShowed = true;

        }
    }

    public void hideButton() {

        if (buttonShowed) {

            navigateButton.animate().translationYBy(Utilities.pixels(this)).setDuration(500);
            buttonShowed = false;

        }

    }

    public void back2Home() {

        if(geoQuery != null)
            geoQuery.removeAllListeners();

        mFusedLocationClient.removeLocationUpdates(locationCallback);

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.category_list_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.signOut:
                AuthUI.getInstance()
                        .signOut(this)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                back2Home();
                            }
                        });

        }

        return super.onOptionsItemSelected(item);
    }

    public void NavigationUI() {

        String technicianLatitude = String.valueOf(lastKnownLocation.getLatitude());
        String technicianLongitude = String.valueOf(lastKnownLocation.getLongitude());

        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(
                MAPS_STARTING_URL + technicianLatitude
                + ","
                + technicianLongitude
                + "&daddr="
                + userLatitude
                + ","
                + userLongitude));

        intent.setPackage(MAPS_PACKAGE_ID);
        startActivity(intent);

    }

    public void onNavigationClicked(View view) {

        NavigationUI();
        mDatabaseReference.child(TECHNICIAN_ACCEPTED).child(requestedUserId).child(userId).setValue(category);
        technicianAccepted=true;


    }

    public void updateMap(Location location) {

        if (location != null) {

            mMap.clear();

            if(geoQuery != null)
                geoQuery.removeAllListeners();

            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            mMap.addMarker(new MarkerOptions()
                    .position(latLng)
                    .title(getString(R.string.techmap_marker_header))
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));

            GeoLocation technicianGeoLocation = new GeoLocation(latLng.latitude, latLng.longitude);
            TgeoFire.setLocation(userId, technicianGeoLocation, new GeoFire.CompletionListener() {
                @Override
                public void onComplete(String key, DatabaseError error) {

                }
            });

            if (technicianAccepted) {

                if (calculateDistanceLeft() < 100) {

                    destinationArrived = true;

                }

            }
            else {

                geoQuery = UgeoFire.queryAtLocation(technicianGeoLocation, 5);
                geoQuery.addGeoQueryEventListener(geoQueryEventListener);

            }

            } else {

                Toast.makeText(this, getString(R.string.tech_cant_fetch_location_warning), Toast.LENGTH_SHORT).show();

            }

        }


    protected void createLocationRequest() {

        locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == 1) {

            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                    mFusedLocationClient.requestLocationUpdates(locationRequest,locationCallback,null);
                    mFusedLocationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {

                            lastKnownLocation=location;
                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), ZOOM));

                        }
                    });

                }

            }

        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_technician_map);
        getSupportActionBar().setTitle(R.string.techmap_toolbar_title);

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();
        mFireBaseAuth = FirebaseAuth.getInstance();
        userId = mFireBaseAuth.getCurrentUser().getUid();

        lastKnownLocation = new Location(LOCATION_SERVICE);

        geoQueryEventListener = new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                final GeoLocation newUserGeo = new GeoLocation(location.latitude, location.longitude);
                final String userKey = key;
                mDatabaseReference.child(TECHNICIAN_ACCEPTED).child(userKey).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if (!dataSnapshot.exists()) {

                            mMap.addMarker(new MarkerOptions().position(new LatLng(newUserGeo.latitude, newUserGeo.longitude)).title(userKey));

                        } else {

                            updateMap(lastKnownLocation);
                            hideButton();

                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }

            @Override
            public void onKeyExited(String key) {

                updateMap(lastKnownLocation);
                hideButton();

            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {

            }

            @Override
            public void onGeoQueryReady() {

            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        };

        locationCallback = new LocationCallback() {

            @Override
            public void onLocationResult(LocationResult locationResult) {
                for(Location location:locationResult.getLocations()){

                    lastKnownLocation = location;
                    updateMap(location);

                }
            }
        };

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        createLocationRequest();
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);

        SettingsClient client = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());

        task.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                if(e instanceof ResolvableApiException){

                    try{

                        ResolvableApiException resolve=(ResolvableApiException) e;
                        resolve.startResolutionForResult(TechnicianMapsActivity.this,1);

                    }
                    catch(Exception e1){

                        e.printStackTrace();

                    }

                }


            }
        });

        navigateButton =  findViewById(R.id.button_technicianmap_startnagivation);

        Intent intent = getIntent();
        category = intent.getStringExtra(CATEGORY);

        TgeoFire = new GeoFire(mDatabaseReference.child(TECHNICIAN_LOCATIONS).child(category));
        UgeoFire = new GeoFire(mDatabaseReference.child(USER_LOCATIONS).child(category));

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    public float calculateDistanceLeft(){

        float distance = lastKnownLocation.distanceTo(userLocation);
        return (float) ((Math.round(distance * 10)) / 10);

    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        mMap.getUiSettings().setMapToolbarEnabled(false);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){

            mFusedLocationClient.requestLocationUpdates(locationRequest,locationCallback,null);
            mFusedLocationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {

                    lastKnownLocation=location;
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), ZOOM));

                }
            });

        }

        else{

            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);

        }
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {

                if (!marker.getTitle().equals(getString(R.string.techmap_marker_header))) {

                    requestedUserId = marker.getTitle();

                    LatLng latLng = marker.getPosition();
                    userLocation = new Location(LOCATION_SERVICE);
                    userLocation.setLatitude(latLng.latitude);
                    userLocation.setLongitude(latLng.longitude);
                    float distanceInDP=calculateDistanceLeft();
                    marker.setTitle(String.valueOf(distanceInDP/1000) + getString(R.string.techmap_kilometers));

                    userLatitude = String.valueOf(latLng.latitude);
                    userLongitude = String.valueOf(latLng.longitude);

                    showButton();

                } else {

                    hideButton();

                }

                return false;
            }
        });

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                hideButton();
            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();

        if(technicianAccepted && !destinationArrived){

            //technician rejected in between the way
            mDatabaseReference.child(TECHNICIAN_ACCEPTED).child(requestedUserId).removeValue();
            technicianAccepted=false;
            updateMap(lastKnownLocation);

        }
        else if(mMap != null)
            updateMap(lastKnownLocation);

    }

    @Override
    protected void onPause() {
        super.onPause();
        if(geoQuery != null)
            geoQuery.removeAllListeners();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(technicianAccepted && !destinationArrived){

            //technician closed app in between the way
            mDatabaseReference.child(TECHNICIAN_ACCEPTED).child(requestedUserId).removeValue();

        }

    }
}


