package com.example.visha.smarttechnician;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.TypedValue;
import android.widget.Toast;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static com.example.visha.smarttechnician.FragmentOne.technicianLocationListener;

public class UsersMapActivity extends FragmentActivity implements OnMapReadyCallback {

    private String technicianUserId;
    private String category;
    private GeoFire geoFire;
    private LatLng latLng;
    private LatLngBounds bounds;

    private GoogleMap mMap;

    public static GeoQuery geoQuery;
    private GeoQueryEventListener geoQueryEventListener;

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private FirebaseAuth mFireBaseAuth;
    private String userId;
    private Boolean technicianArrived=false;
    private Boolean listenerAdded=false;

    public static Activity activity;

    public void backToServiceBoard(){

        Intent intent=new Intent(this,CategoryList.class);
        startActivity(intent);
        finish();

    }

    public void resetUserUI(){

        Toast.makeText(UsersMapActivity.this, "Technician has arrived :)", Toast.LENGTH_LONG).show();
        if(geoQuery!=null)
            geoQuery.removeAllListeners();
        mDatabaseReference.child("TechnicianAccepted").child(userId).removeEventListener(technicianLocationListener);
        mDatabaseReference.child("TechnicianAccepted").child(userId).removeValue();
        removeRequest();
        backToServiceBoard();

    }

    public float calculateDistanceLeft(GeoLocation techGeoLocation,LatLng userLatLng){

        Location techLocation=new Location(LOCATION_SERVICE);
        Location userLocation=new Location(LOCATION_SERVICE);
        techLocation.setLongitude(techGeoLocation.longitude);
        techLocation.setLatitude(techGeoLocation.latitude);
        userLocation.setLatitude(userLatLng.latitude);
        userLocation.setLongitude(userLatLng.longitude);
        float distance =  userLocation.distanceTo(techLocation);
        float distanceInDP = (Math.round(distance * 10)) / 10;
        return distanceInDP;

    }

    public void removeRequest(){

        mDatabaseReference.child("ActiveRequests").child(userId).removeValue(new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                if(databaseError==null){

                    geoFire=new GeoFire(mDatabaseReference.child("ULocations").child(category));
                    geoFire.removeLocation(userId);
                    FragmentOne.requestActive=false;
                    FragmentOne.removeActiveRequest();

                }
            }
        });

    }


    public void UpdateMap(GeoLocation techGeoLocation){

        mMap.clear();
        mMap.addMarker(new MarkerOptions().position(latLng).title("Requested position"));
        LatLng techLatLng=new LatLng(techGeoLocation.latitude,techGeoLocation.longitude);
        mMap.addMarker(new MarkerOptions().position(techLatLng).title("Technician").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
        if(calculateDistanceLeft(techGeoLocation,latLng)<10 && !technicianArrived){

            technicianArrived=true;
            resetUserUI();

        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_map);

        mFirebaseDatabase=FirebaseDatabase.getInstance();
        mDatabaseReference=mFirebaseDatabase.getReference();
        mFireBaseAuth=FirebaseAuth.getInstance();
        userId=mFireBaseAuth.getCurrentUser().getUid();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        geoQueryEventListener=new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                if(key.equals(technicianUserId)) {

                    LatLngBounds.Builder builder=new LatLngBounds.Builder();
                    builder.include(new LatLng(location.latitude,location.longitude));
                    builder.include(latLng);
                    bounds=builder.build();
                    mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds,100));
                    mMap.addMarker(new MarkerOptions().position(new LatLng(location.latitude,location.longitude)).title("Technician").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));

                }
            }

            @Override
            public void onKeyExited(String key) {

            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {
                if(key.equals(technicianUserId)) {

                    UpdateMap(location);

                }
            }

            @Override
            public void onGeoQueryReady() {

            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        };

        activity=this;

        Intent intent=getIntent();
        technicianUserId=intent.getStringExtra("technicianUserId");
        category=intent.getStringExtra("category");

        latLng=new LatLng(FragmentOne.requestedLocation.latitude,FragmentOne.requestedLocation.longitude);

        geoFire=new GeoFire(mDatabaseReference.child("TLocations").child(category));
        geoQuery=geoFire.queryAtLocation(FragmentOne.requestedLocation,5);
        geoQuery.addGeoQueryEventListener(geoQueryEventListener);
        listenerAdded=true;

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
        mMap.addMarker(new MarkerOptions().position(latLng).title("Requested position"));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,10));
    }

    @Override
    protected void onPause() {

        if(geoQuery!=null)
            geoQuery.removeAllListeners();
        super.onPause();
    }

    @Override
    protected void onResume() {

        if(!listenerAdded)
            geoQuery.addGeoQueryEventListener(geoQueryEventListener);

        super.onResume();
    }
}
