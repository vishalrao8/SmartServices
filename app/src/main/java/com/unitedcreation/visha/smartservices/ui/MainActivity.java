package com.unitedcreation.visha.smartservices.ui;

import android.app.ActivityOptions;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.transition.Slide;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.unitedcreation.visha.smartservices.R;
import com.unitedcreation.visha.smartservices.ui.technician.RegisterTechnicianActivity;
import com.unitedcreation.visha.smartservices.ui.technician.TechnicianMapsActivity;
import com.unitedcreation.visha.smartservices.ui.user.HomeActivity;
import com.unitedcreation.visha.smartservices.ui.user.RegisterUserActivity;

import java.util.Collections;
import java.util.List;

import static com.unitedcreation.visha.smartservices.utils.StringResourceProvider.CATEGORY;
import static com.unitedcreation.visha.smartservices.utils.StringResourceProvider.NAME;
import static com.unitedcreation.visha.smartservices.utils.StringResourceProvider.TECHNICIAN;
import static com.unitedcreation.visha.smartservices.utils.StringResourceProvider.TYPE;
import static com.unitedcreation.visha.smartservices.utils.StringResourceProvider.USER;
import static com.unitedcreation.visha.smartservices.utils.StringResourceProvider.USERS;

public class MainActivity extends AppCompatActivity {

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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){

            getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
            getWindow().setExitTransition(new Slide());

        }

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
                            buttonActive = true;

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
        intent.putExtra(TYPE, TECHNICIAN);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
        else
            startActivity(intent);

        finish();

    }

    public void intentToUserRegistration(){

        Intent intent = new Intent(this, RegisterUserActivity.class);
        intent.putExtra(TYPE,USER);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
        else
            startActivity(intent);

        finish();

    }

    public void intentToTechnicianUi(String name, String category){

        Intent intent=new Intent(this,TechnicianMapsActivity.class);
        intent.putExtra(CATEGORY,category);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
        else
            startActivity(intent);

        finish();

    }

    public void intentToUserUi(String name){

        Intent intent=new Intent(this,HomeActivity.class);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
        else
            startActivity(intent);

        //Toast.makeText(this, "Welcome back "+name+" :)", Toast.LENGTH_SHORT).show();
        finish();
    }
}
