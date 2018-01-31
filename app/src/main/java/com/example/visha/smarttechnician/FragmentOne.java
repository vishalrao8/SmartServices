package com.example.visha.smarttechnician;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.LocationCallback;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.app.Activity.RESULT_OK;

/**
 * Created by visha on 26 Jan 2018.
 */

public class FragmentOne extends ListFragment {

    public static GeoFire geoFire;
    public static String category;
    public static Boolean requestActive=false;
    private ProgressDialog progressDialog;
    private Button cancelButton;
    public static Boolean buttonShowed=false;
    public static GeoLocation requestedLocation;
    private ViewPager viewPager;
    public static Boolean technicianAccepted=false;
    public static String technicianUserId;

    private FirebaseAuth mFireBaseAuth;
    private String userId;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;

    public static ValueEventListener technicianLocationListener;

    private List<String> categoryList= new ArrayList<>();

    public static PlacePicker.IntentBuilder intentBuilder;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mFirebaseDatabase=FirebaseDatabase.getInstance();
        mDatabaseReference=mFirebaseDatabase.getReference();
        mFireBaseAuth=FirebaseAuth.getInstance();
        userId=mFireBaseAuth.getCurrentUser().getUid();

        categoryList.add(getResources().getString(R.string.electronics));
        categoryList.add(getResources().getString(R.string.plumber));
        categoryList.add(getResources().getString(R.string.carpenter));
        categoryList.add(getResources().getString(R.string.painter));
        categoryList.add(getResources().getString(R.string.portable_devices));

        cancelButton= getActivity().findViewById(R.id.cancelButton);
        intentBuilder=new PlacePicker.IntentBuilder();
        viewPager= getActivity().findViewById(R.id.viewPager);

        technicianLocationListener=new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){

                    //driver has accepted
                    if(!technicianAccepted) {

                        HashMap<String, String> hashMap = (HashMap) dataSnapshot.getValue();
                        for (String key : hashMap.keySet()) {

                            technicianUserId = key;

                        }
                        category = hashMap.get(technicianUserId);
                        technicianAccepted = true;
                        UserMapsUI(technicianUserId, category);
                    }

                }
                else{

                    //driver rejected the navigation
                    if(technicianAccepted){

                        Toast.makeText(getActivity(), "Technician rejected your request, waiting for another technician....", Toast.LENGTH_LONG).show();
                        technicianAccepted=false;
                        if(UsersMapActivity.geoQuery!=null)
                            UsersMapActivity.geoQuery.removeAllListeners();
                        UsersMapActivity.activity.finish();

                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        if(!requestActive)
        {
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Checking Requests");
            progressDialog.setCancelable(false);
            progressDialog.show();
            checkIfRequestIsActive();
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_one,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);

        ListView listView=getListView();
        mArrayAdapter arrayAdapter=new mArrayAdapter(getActivity(),R.layout.service_list_items,categoryList);
        listView.setAdapter(arrayAdapter);

    }

    public float pixels(){

        float px= TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,60,getResources().getDisplayMetrics());
        return px;

    }

    public void showButton(){

        if(!buttonShowed) {

            cancelButton.animate().translationYBy(-(pixels())).setDuration(500);
            buttonShowed=true;

        }
    }

    public void showActiveRequests(){

        FragmentTwo.fragment2arrayList.add(categoryList.indexOf(category));
        FragmentTwo.fragment2ArrayAdapter.notifyDataSetChanged();

    }

    public static void removeActiveRequest(){

        FragmentTwo.fragment2arrayList.clear();
        FragmentTwo.fragment2ArrayAdapter.notifyDataSetChanged();

    }

    public void checkIfRequestIsActive(){

        mDatabaseReference.child("ActiveRequests").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){

                    category=(String)dataSnapshot.getValue();
                    Log.i("category",category);
                    geoFire=new GeoFire(mDatabaseReference.child("ULocations").child(category));
                    geoFire.getLocation(userId, new LocationCallback() {
                        @Override
                        public void onLocationResult(String key, GeoLocation location) {
                            requestedLocation=location;
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                    requestActive=true;
                    showActiveRequests();
                    showButton();
                    checkIfTechnicianIsComing();

                }
                progressDialog.dismiss();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public void UserMapsUI(String id, String category){

        Intent intent=new Intent(getActivity(),UsersMapActivity.class);
        intent.putExtra("technicianUserId",id);
        intent.putExtra("category",category);
        startActivity(intent);

    }

    public void checkIfTechnicianIsComing(){

        mDatabaseReference.child("TechnicianAccepted").child(userId).addValueEventListener(technicianLocationListener);

    }

    public void raiseRequest(Place place) {

        requestedLocation=new GeoLocation(place.getLatLng().latitude,place.getLatLng().longitude);
        mDatabaseReference.child("ActiveRequests").child(userId).setValue(category, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if(databaseError==null){

                    geoFire=new GeoFire(mDatabaseReference.child("ULocations").child(category));
                    geoFire.setLocation(userId,requestedLocation);
                    Toast.makeText(getActivity(), "Requested Successfully", Toast.LENGTH_SHORT).show();
                    requestActive=true;
                    showButton();
                    viewPager.setCurrentItem(1,true);
                    showActiveRequests();
                    checkIfTechnicianIsComing();

                }
                else{

                    Toast.makeText(getActivity(), "Failed to request, please try again later", Toast.LENGTH_SHORT).show();

                }
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode==1){

            if(resultCode==RESULT_OK) {

                Place place = PlacePicker.getPlace(data, getActivity());
                raiseRequest(place);

            }

        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        if (!requestActive) {
            try {

                startActivityForResult(intentBuilder.build(getActivity()), 1);
                category = categoryList.get(position);

            } catch (GooglePlayServicesRepairableException e) {
                // ...
                e.printStackTrace();
            } catch (GooglePlayServicesNotAvailableException e) {
                // ...
                e.printStackTrace();
            }
        } else {

            Toast.makeText(getActivity(), "Can't raise more than one request as of now", Toast.LENGTH_SHORT).show();

        }
    }
}
