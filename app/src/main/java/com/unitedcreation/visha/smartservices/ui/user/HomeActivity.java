package com.unitedcreation.visha.smartservices.ui.user;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.unitedcreation.visha.smartservices.R;
import com.unitedcreation.visha.smartservices.adapters.ViewPagerAdapter;
import com.unitedcreation.visha.smartservices.ui.MainActivity;
import com.unitedcreation.visha.smartservices.ui.fragments.ActiveRequestFragment;
import com.unitedcreation.visha.smartservices.ui.fragments.CategoryViewFragment;
import com.unitedcreation.visha.smartservices.ui.fragments.HistoryFragment;
import com.unitedcreation.visha.smartservices.utils.SharedPreferences;
import com.unitedcreation.visha.smartservices.utils.Utilities;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static com.unitedcreation.visha.smartservices.ui.fragments.CategoryViewFragment.buttonShowed;
import static com.unitedcreation.visha.smartservices.ui.fragments.CategoryViewFragment.category;
import static com.unitedcreation.visha.smartservices.ui.fragments.CategoryViewFragment.geoFire;
import static com.unitedcreation.visha.smartservices.ui.fragments.CategoryViewFragment.placePickerIntentBuilder;
import static com.unitedcreation.visha.smartservices.ui.fragments.CategoryViewFragment.requestActive;
import static com.unitedcreation.visha.smartservices.ui.fragments.CategoryViewFragment.requestedLocation;
import static com.unitedcreation.visha.smartservices.ui.fragments.CategoryViewFragment.technicianLocationListener;
import static com.unitedcreation.visha.smartservices.utils.StringResourceProvider.ACTIVE_REQUESTS;
import static com.unitedcreation.visha.smartservices.utils.StringResourceProvider.CATEGORY_POSITION;
import static com.unitedcreation.visha.smartservices.utils.StringResourceProvider.TECHNICIAN_ACCEPTED;
import static com.unitedcreation.visha.smartservices.utils.StringResourceProvider.USER_LOCATIONS;

public class HomeActivity extends AppCompatActivity {

    private Button cancelButton;
    private ViewPager viewPager;

    private FirebaseAuth mFireBaseAuth;
    private String userId;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;

    private List<String> categoryList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        SharedPreferences.userLoggedIn(this);

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();
        mFireBaseAuth = FirebaseAuth.getInstance();
        userId = mFireBaseAuth.getCurrentUser().getUid();

        cancelButton = findViewById(R.id.button_home_cancelrequest);

        viewPager = findViewById(R.id.viewpager_home_pager);
        setUpViewPager(viewPager);

        Toolbar toolbar= findViewById(R.id.toolbar_home_bar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.home_toolbar_title);

        TabLayout tabLayout= findViewById(R.id.tablayout_home_tabs);
        tabLayout.setupWithViewPager(viewPager);

        categoryList = Arrays.asList(getResources().getStringArray(R.array.services_name));

        Intent intentFromWidget = getIntent();

        if (intentFromWidget.hasExtra(CATEGORY_POSITION)) {

            if (!requestActive) {

                int position = intentFromWidget.getIntExtra(CATEGORY_POSITION, 0);
                category = categoryList.get(position);

                int PLACE_PICKER_REQUEST = 1;
                PlacePicker.IntentBuilder builder = placePickerIntentBuilder;

                try {
                    startActivityForResult(builder.build(this), PLACE_PICKER_REQUEST);
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
            } else {

                Toast.makeText(this, getString(R.string.home_extra_request_warning), Toast.LENGTH_SHORT).show();
                buttonShowed = false;
                showButton();

            }
        }
    }

    public void hideButton() {

        if(buttonShowed) {

            cancelButton.animate().translationYBy(Utilities.pixels(this)).setDuration(500);
            buttonShowed = false;

        }
    }

    public void cancelRequest(View view){

            mDatabaseReference.child(ACTIVE_REQUESTS).child(userId).removeValue(new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                if(databaseError == null) {

                    geoFire = new GeoFire(mDatabaseReference.child(USER_LOCATIONS).child(category));
                    geoFire.removeLocation(userId, new GeoFire.CompletionListener() {
                        @Override
                        public void onComplete(String key, DatabaseError error) {

                        }
                    });

                    Toast.makeText(HomeActivity.this, R.string.home_request_canceled_warning, Toast.LENGTH_SHORT).show();
                    hideButton();
                    CategoryViewFragment.requestActive=false;
                    viewPager.setCurrentItem(0);
                    CategoryViewFragment.removeActiveRequest();
                    mDatabaseReference.child(TECHNICIAN_ACCEPTED).child(userId).removeEventListener(technicianLocationListener);

                    if(UserMapActivity.geoQuery != null)

                        UserMapActivity.geoQuery.removeAllListeners();

                }
            }
        });
    }

    public void back2Home() {

        mDatabaseReference.child(TECHNICIAN_ACCEPTED).child(userId).removeEventListener(technicianLocationListener);
        SharedPreferences.userLoggedOut(this);

        if(UserMapActivity.geoQuery != null)
            UserMapActivity.geoQuery.removeAllListeners();

        Intent intent=new Intent(this,MainActivity.class);
        startActivity(intent);
        finish();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater=getMenuInflater();
        menuInflater.inflate(R.menu.category_list_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.signOut) {
            AuthUI.getInstance()
                    .signOut(this)
                    .addOnCompleteListener(task -> back2Home());
        }
        if (item.getItemId() == R.id.payment) {
            Intent intent = new Intent(this, PaymentActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 1) {

            if(resultCode == RESULT_OK) {

                Place place = PlacePicker.getPlace(data, this);
                raiseRequest (place);

            }
        }
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
                    Toast.makeText(getApplicationContext(), getString(R.string.home_request_raised_info), Toast.LENGTH_SHORT).show();
                    requestActive = true;
                    showButton();
                    viewPager.setCurrentItem(1,true);
                    showActiveRequests();
                    checkIfTechnicianIsComing();

                } else {

                    Toast.makeText(getApplicationContext(), getString(R.string.home_request_failed_info), Toast.LENGTH_SHORT).show();

                }
            }
        });
    }

    public void checkIfTechnicianIsComing() {

        mDatabaseReference.child(TECHNICIAN_ACCEPTED).child(userId).addValueEventListener(technicianLocationListener);

    }

    public void showActiveRequests(){

        ActiveRequestFragment.fragment2arrayList.add(categoryList.indexOf(category));
        ActiveRequestFragment.fragment2ArrayAdapter.notifyDataSetChanged();

    }

    public void showButton(){

        if(!buttonShowed) {

            cancelButton.animate().translationYBy(-Utilities.pixels(this)).setDuration(500);
            buttonShowed = true;

        }
    }

    public void setUpViewPager(ViewPager viewPager){

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPagerAdapter.addFragment(new CategoryViewFragment(),getString(R.string.home_tab_one_title));
        viewPagerAdapter.addFragment(new ActiveRequestFragment(),getString(R.string.home_tab_two_title));
        viewPagerAdapter.addFragment(new HistoryFragment(),getString(R.string.home_tab_three_title));

        viewPager.setAdapter(viewPagerAdapter);

    }
}
