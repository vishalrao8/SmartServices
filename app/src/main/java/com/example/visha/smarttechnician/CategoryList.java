package com.example.visha.smarttechnician;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.firebase.geofire.GeoFire;
import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static com.example.visha.smarttechnician.FragmentOne.buttonShowed;
import static com.example.visha.smarttechnician.FragmentOne.category;
import static com.example.visha.smarttechnician.FragmentOne.geoFire;
import static com.example.visha.smarttechnician.FragmentOne.technicianLocationListener;

public class CategoryList extends AppCompatActivity {

    private Button cancelButton;
    private ViewPager viewPager;

    private FirebaseAuth mFireBaseAuth;
    private String userId;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;

    public void hideButton(){

        if(buttonShowed) {

            cancelButton.animate().translationYBy(pixels()).setDuration(500);
            buttonShowed=false;

        }

    }

    public float pixels(){

        float px= TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,60,getResources().getDisplayMetrics());
        return px;

    }

    public void cancelRequest(View view){

            mDatabaseReference.child("ActiveRequests").child(userId).removeValue(new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                if(databaseError==null){

                    geoFire=new GeoFire(mDatabaseReference.child("ULocations").child(category));
                    geoFire.removeLocation(userId);
                    Toast.makeText(CategoryList.this, "Request canceled", Toast.LENGTH_SHORT).show();
                    hideButton();
                    FragmentOne.requestActive=false;
                    viewPager.setCurrentItem(0);
                    FragmentOne.removeActiveRequest();
                    mDatabaseReference.child("TechnicianAccepted").child(userId).removeEventListener(technicianLocationListener);
                    if(UsersMapActivity.geoQuery!=null)
                        UsersMapActivity.geoQuery.removeAllListeners();

                }
            }
        });

    }

    public void back2Home(){

        mDatabaseReference.child("TechnicianAccepted").child(userId).removeEventListener(technicianLocationListener);
        UsersMapActivity.geoQuery.removeAllListeners();
        Intent intent=new Intent(this,HomeScreen.class);
        startActivity(intent);
        finish();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater=getMenuInflater();
        menuInflater.inflate(R.menu.category_list_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){

            case R.id.signOut :
                AuthUI.getInstance().signOut(this);
                back2Home();


        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void setUpViewPager(ViewPager viewPager){

        ViewPagerAdapter viewPagerAdapter=new ViewPagerAdapter(getSupportFragmentManager());
        viewPagerAdapter.addFragment(new FragmentOne(),"Services");
        viewPagerAdapter.addFragment(new FragmentTwo(),"Active requests");
        viewPagerAdapter.addFragment(new FragmentThree(),"History");
        viewPager.setAdapter(viewPagerAdapter);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_list);

        mFirebaseDatabase=FirebaseDatabase.getInstance();
        mDatabaseReference=mFirebaseDatabase.getReference();
        mFireBaseAuth=FirebaseAuth.getInstance();
        userId=mFireBaseAuth.getCurrentUser().getUid();

        cancelButton= findViewById(R.id.cancelButton);

        viewPager= findViewById(R.id.viewPager);
        setUpViewPager(viewPager);

        android.support.v7.widget.Toolbar toolbar= findViewById(R.id.appBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("ServiceBoard");

        TabLayout tabLayout= findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager);

    }
}
