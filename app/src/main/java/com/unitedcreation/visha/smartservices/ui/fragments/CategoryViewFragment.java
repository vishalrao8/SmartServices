package com.unitedcreation.visha.smartservices.ui.fragments;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.unitedcreation.visha.smartservices.R;
import com.unitedcreation.visha.smartservices.adapters.CategoryViewAdapter;
import com.unitedcreation.visha.smartservices.ui.user.UserMapActivity;
import com.unitedcreation.visha.smartservices.utils.Utilities;
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

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static android.app.Activity.RESULT_OK;
import static com.unitedcreation.visha.smartservices.utils.StringResourceProvider.ACTIVE_REQUESTS;
import static com.unitedcreation.visha.smartservices.utils.StringResourceProvider.CATEGORY;
import static com.unitedcreation.visha.smartservices.utils.StringResourceProvider.TECHNICIAN_ACCEPTED;
import static com.unitedcreation.visha.smartservices.utils.StringResourceProvider.TECHNICIAN_USER_ID;
import static com.unitedcreation.visha.smartservices.utils.StringResourceProvider.USER_LOCATIONS;

public class CategoryViewFragment extends Fragment implements CategoryViewAdapter.onCategoryClickedInterface {

    public static GeoFire geoFire;
    public static String category;
    public static Boolean requestActive = false;
    public static Boolean buttonShowed = false;
    public static GeoLocation requestedLocation;
    public static Boolean technicianAccepted = false;
    public static String technicianUserId;

    private ViewPager viewPager;
    public ProgressDialog progressDialog;
    private Button cancelButton;

    private FirebaseAuth mFireBaseAuth;
    private String userId;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;

    public static ValueEventListener technicianLocationListener;
    public static PlacePicker.IntentBuilder placePickerIntentBuilder;

    private List<String> categoryList;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();
        mFireBaseAuth = FirebaseAuth.getInstance();
        userId = mFireBaseAuth.getCurrentUser().getUid();

        categoryList = Arrays.asList(getResources().getStringArray(R.array.services_name));

        cancelButton = getActivity().findViewById(R.id.button_home_cancelrequest);
        placePickerIntentBuilder = new PlacePicker.IntentBuilder();
        viewPager = getActivity().findViewById(R.id.viewpager_home_pager);

        technicianLocationListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {

                    //technician has accepted.
                    if(!technicianAccepted) {

                        HashMap<String, String> hashMap = (HashMap) dataSnapshot.getValue();
                        for (String key : hashMap.keySet()) {

                            technicianUserId = key;

                        }
                        category = hashMap.get(technicianUserId);
                        technicianAccepted = true;
                        UserMapsUI(technicianUserId, category);
                    }

                } else {

                    //technician rejected the navigation.
                    if(technicianAccepted) {

                        Toast.makeText(getActivity(), getString(R.string.home_technician_rejected_info), Toast.LENGTH_LONG).show();
                        technicianAccepted=false;
                        if(UserMapActivity.geoQuery!=null)
                            UserMapActivity.geoQuery.removeAllListeners();
                        UserMapActivity.activity.finish();

                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        if(!requestActive) {

            new checkIfRequestActive().execute();

        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        RecyclerView recyclerView = (RecyclerView) inflater.inflate(R.layout.fragment_category_view, container,false);
        CategoryViewAdapter adapter = new CategoryViewAdapter(getActivity(), this);

        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(),2));

        return recyclerView;

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode == 1) {

            if(resultCode == RESULT_OK) {

                Place place = PlacePicker.getPlace(data, getActivity());
                raiseRequest (place);

            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onCategoryClicked(int position) {

        if (!requestActive) {

            category = categoryList.get(position);

            int PLACE_PICKER_REQUEST = 1;
            PlacePicker.IntentBuilder builder = placePickerIntentBuilder;

            try {
                startActivityForResult(builder.build(getActivity()), PLACE_PICKER_REQUEST);
            } catch (GooglePlayServicesRepairableException e) {
                e.printStackTrace();
            } catch (GooglePlayServicesNotAvailableException e) {
                e.printStackTrace();
            }

        } else {

            Toast.makeText(getActivity(), getString(R.string.home_extra_request_warning), Toast.LENGTH_SHORT).show();

        }
    }

    public void showButton(){

        if(!buttonShowed) {

            cancelButton.animate().translationYBy(-Utilities.pixels(getActivity())).setDuration(500);
            buttonShowed = true;

        }
    }

    public void showActiveRequests(){

        ActiveRequestFragment.fragment2arrayList.add(categoryList.indexOf(category));
        ActiveRequestFragment.fragment2ArrayAdapter.notifyDataSetChanged();

    }

    public static void removeActiveRequest(){

        ActiveRequestFragment.fragment2arrayList.clear();
        ActiveRequestFragment.fragment2ArrayAdapter.notifyDataSetChanged();

    }

    public void checkIfRequestIsActive(){

        mDatabaseReference.child(ACTIVE_REQUESTS).child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){

                    category = (String) dataSnapshot.getValue();
                    Log.i(CATEGORY, category);
                    geoFire = new GeoFire(mDatabaseReference.child(USER_LOCATIONS).child(category));
                    geoFire.getLocation(userId, new LocationCallback() {
                        @Override
                        public void onLocationResult(String key, GeoLocation location) {
                            requestedLocation = location;
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                    requestActive = true;
                    showActiveRequests();
                    showButton();
                    checkIfTechnicianIsComing();

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public void UserMapsUI(String id, String category) {

        Intent intent = new Intent(getActivity(),UserMapActivity.class);
        intent.putExtra(TECHNICIAN_USER_ID, id);
        intent.putExtra(CATEGORY, category);
        startActivity(intent);

    }

    public void checkIfTechnicianIsComing() {

        mDatabaseReference.child(TECHNICIAN_ACCEPTED).child(userId).addValueEventListener(technicianLocationListener);

    }

    public void raiseRequest(Place place) {

        requestedLocation = new GeoLocation(place.getLatLng().latitude,place.getLatLng().longitude);
        mDatabaseReference.child(ACTIVE_REQUESTS).child(userId).setValue(category, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {

                if(databaseError == null) {

                    geoFire = new GeoFire(mDatabaseReference.child(USER_LOCATIONS).child(category));
                    geoFire.setLocation(userId, requestedLocation, new GeoFire.CompletionListener() {
                        @Override
                        public void onComplete(String key, DatabaseError error) {

                        }
                    });
                    Toast.makeText(getActivity(), getString(R.string.home_request_raised_info), Toast.LENGTH_SHORT).show();
                    requestActive = true;
                    showButton();
                    viewPager.setCurrentItem(1,true);
                    showActiveRequests();
                    checkIfTechnicianIsComing();

                } else {

                    Toast.makeText(getActivity(), getString(R.string.home_request_failed_info), Toast.LENGTH_SHORT).show();

                }
            }
        });
    }

    @SuppressLint("StaticFieldLeak")
    public class checkIfRequestActive extends android.os.AsyncTask <Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage(getString(R.string.home_checking_active_requests_info));
            progressDialog.setCancelable(false);
            progressDialog.show();

        }

        @Override
        protected Void doInBackground(Void... voids) {

            checkIfRequestIsActive();
            return null;

        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressDialog.dismiss();
        }
    }
}

