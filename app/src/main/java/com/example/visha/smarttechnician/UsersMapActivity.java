package com.example.visha.smarttechnician;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
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
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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

    public static Activity activity;

    public void UpdateMap(GeoLocation techGeoLocation){

        mMap.clear();
        mMap.addMarker(new MarkerOptions().position(latLng).title("Requested position"));
        LatLng techLatLng=new LatLng(techGeoLocation.latitude,techGeoLocation.longitude);
        mMap.addMarker(new MarkerOptions().position(techLatLng).title("Technician").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_map);

        mFirebaseDatabase=FirebaseDatabase.getInstance();
        mDatabaseReference=mFirebaseDatabase.getReference();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        geoQueryEventListener=new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                if(key.equals(technicianUserId)) {

                    Toast.makeText(UsersMapActivity.this, "Technician found", Toast.LENGTH_SHORT).show();
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

                    Toast.makeText(UsersMapActivity.this, "Technician moved", Toast.LENGTH_SHORT).show();
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
        mMap.addMarker(new MarkerOptions().position(latLng).title("Requested position"));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,10));
    }
}
