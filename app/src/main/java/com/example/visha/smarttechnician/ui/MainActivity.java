package com.example.visha.smarttechnician.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.visha.smarttechnician.ui.technician.TechnicianMapsActivity;
import com.example.visha.smarttechnician.R;
import com.example.visha.smarttechnician.ui.technician.RegisterTechnicianActivity;
import com.example.visha.smarttechnician.ui.user.HomeActivity;
import com.example.visha.smarttechnician.ui.user.RegisterUserActivity;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String USERS = "Users";
    private static final String TYPE = "type";
    private static final String USER = "user";
    private static final String NAME = "name";
    private static final String CATEGORY = "category";
    private static final String TECHNICIAN = "technician";

    private static final int RC_SIGN_IN = 123;
    private List<AuthUI.IdpConfig> providers = Collections.singletonList(
            new AuthUI.IdpConfig.PhoneBuilder().build()
    );

    private Boolean buttonActive = true;
    private Boolean user;

    private ProgressDialog progressDialog;
    private Button requestButton;

    private FirebaseAuth mFireBaseAuth;
    private String userId;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setTitle(getString(R.string.main_title));

        progressDialog = new ProgressDialog(this);
        requestButton = findViewById(R.id.button_main_user);

        mFireBaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();

        //userEntry.animate().translationYBy(pixels()).setDuration(800);

        if(mFireBaseAuth.getCurrentUser() != null) {

            //linearLayout.setVisibility(View.INVISIBLE);
            progressDialog.setMessage(getString(R.string.main_progressbar));
            progressDialog.setCancelable(false);
            progressDialog.show();
            buttonActive = false;

            redirectAccording2user();

        }
    }

    public void userEntry(View view) {

        if (buttonActive) {
            user = true;
            initiateAuthentication();
        }
    }

    public void technicianEntry(View view){

        if (buttonActive) {
            user = false;
            initiateAuthentication();
        }
    }

    private void initiateAuthentication() {

        if(mFireBaseAuth.getCurrentUser() == null)
            startActivityForResult(AuthUI.getInstance()
                    .createSignInIntentBuilder()
                    .setAvailableProviders(providers)
                    .build(),
                    RC_SIGN_IN);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode == RC_SIGN_IN) {

            IdpResponse response=IdpResponse.fromResultIntent(data);

            if(resultCode == RESULT_OK) {

                buttonActive = false;
                progressDialog.setMessage(getString(R.string.main_progressbar));
                progressDialog.setCancelable(false);
                progressDialog.show();

                checkIfUserExist();

            } else {

                if (response == null) {
                    // User pressed back button
                    Toast.makeText(this, getString(R.string.main_signin_cancelled_info), Toast.LENGTH_SHORT).show();
                    return;
                }

                if (response.getError().getErrorCode() == ErrorCodes.NO_NETWORK) {
                    Toast.makeText(this, getString(R.string.main_no_network_warning), Toast.LENGTH_SHORT).show();
                    return;
                }

                if (response.getError().getErrorCode() == ErrorCodes.UNKNOWN_ERROR) {
                    Toast.makeText(this, getString(R.string.main_error), Toast.LENGTH_SHORT).show();
                    return;
                }

            }

        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    public void checkIfUserExist(){

        userId = mFireBaseAuth.getCurrentUser().getUid();

        databaseReference.child(USERS).child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){

                    if(dataSnapshot.child(TYPE).getValue().equals(USER)) {

                        if(!user) {

                            Toast.makeText(MainActivity.this, getString(R.string.main_technician_kickout_warning), Toast.LENGTH_SHORT).show();
                            AuthUI.getInstance().signOut(MainActivity.this);
                            buttonActive=true;

                        } else {

                            intentToUserUi((String) dataSnapshot.child(NAME).getValue());

                        }
                    } else {

                        if(user) {

                            Toast.makeText(MainActivity.this, getString(R.string.main_user_kickout_warning), Toast.LENGTH_SHORT).show();
                            AuthUI.getInstance().signOut(MainActivity.this);
                            buttonActive=true;

                        } else {

                            intentToTechnicianUi((String) dataSnapshot.child(NAME).getValue(), (String) dataSnapshot.child(CATEGORY).getValue());

                        }

                    }
                } else {

                    if (user)
                        intentToUserRegistration();
                    else
                        intentToTechnicianRegistration();

                }
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public void redirectAccording2user(){

        userId = mFireBaseAuth.getCurrentUser().getUid();

        databaseReference.child(USERS).child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){

                    if(dataSnapshot.child(TYPE).getValue().equals(USER)) {

                        user = true;
                        intentToUserUi((String) dataSnapshot.child(NAME).getValue());

                    } else {

                        user = false;
                        intentToTechnicianUi((String)dataSnapshot.child(NAME).getValue(),(String)dataSnapshot.child(CATEGORY).getValue());

                    }
                    progressDialog.dismiss();

                } else {

                    progressDialog.dismiss();
                    AuthUI.getInstance().signOut(MainActivity.this);
                    buttonActive=true;

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void intentToTechnicianRegistration() {

        Intent intent=new Intent(this,RegisterTechnicianActivity.class);
        intent.putExtra(TYPE,TECHNICIAN);
        startActivity(intent);
        finish();

    }

    public void intentToUserRegistration(){

        Intent intent = new Intent(this, RegisterUserActivity.class);
        intent.putExtra(TYPE,USER);
        startActivity(intent);
        finish();

    }

    public void intentToTechnicianUi(String name, String category){

        Intent intent=new Intent(this,TechnicianMapsActivity.class);
        intent.putExtra(CATEGORY,category);
        startActivity(intent);
        finish();

    }

    public void intentToUserUi(String name){

        Intent intent=new Intent(this,HomeActivity.class);
        startActivity(intent);
        //Toast.makeText(this, "Welcome back "+name+" :)", Toast.LENGTH_SHORT).show();
        finish();
    }
}
