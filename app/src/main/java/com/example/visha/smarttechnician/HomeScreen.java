package com.example.visha.smarttechnician;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;
import java.util.List;

public class HomeScreen extends AppCompatActivity {

    private static final int RC_SIGN_IN = 123;
    private List<AuthUI.IdpConfig> providers = Arrays.asList(
            new AuthUI.IdpConfig.Builder(AuthUI.PHONE_VERIFICATION_PROVIDER).build()
    );

    private Boolean buttonActive=true;
    private Boolean user;

    private ProgressDialog progressDialog;
    private Button requestButton;

    private FirebaseAuth mFireBaseAuth;
    private String userId;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    public void redirectAccording2user(){


        userId=mFireBaseAuth.getCurrentUser().getUid();
        databaseReference.child("Users").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){

                    if(dataSnapshot.child("type").getValue().equals("user")) {

                        user=true;
                        comingBackUserUI((String) dataSnapshot.child("name").getValue());
                        finish();
                    }
                    else{

                        user=false;
                        comingBackTechnicianUI((String)dataSnapshot.child("name").getValue(),(String)dataSnapshot.child("category").getValue());
                        finish();

                    }
                    progressDialog.dismiss();
                }
                else{

                    progressDialog.dismiss();
                    AuthUI.getInstance().signOut(HomeScreen.this);
                    buttonActive=true;

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        /*databaseReference.child("Technicians").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){

                    progressDialog.dismiss();
                    comingBackTechnicianUI((String)dataSnapshot.child("name").getValue());
                    finish();

                }
                else{

                    progressDialog.dismiss();
                    AuthUI.getInstance().signOut(HomeScreen.this);
                    buttonActive=true;

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });*/

    }

    public void newTechnicianUI(){

        Intent intent=new Intent(this,RegisterTechnician.class);
        intent.putExtra("type","technician");
        startActivity(intent);
        finish();

    }

    public void comingBackTechnicianUI(String name,String category){

        Intent intent=new Intent(this,MapsActivity.class);
        intent.putExtra("category",category);
        startActivity(intent);
        finish();

    }

    public void newUserUI(){

        Intent intent = new Intent(this,RegisterName.class);
        intent.putExtra("type","user");
        startActivity(intent);
        finish();

    }

    public void comingBackUserUI(String name){

        Intent intent=new Intent(this,CategoryList.class);
        startActivity(intent);
        //Toast.makeText(this, "Welcome back "+name+" :)", Toast.LENGTH_SHORT).show();
        finish();

    }

    public void checkIfUserExist(){

        userId=mFireBaseAuth.getCurrentUser().getUid();
        databaseReference.child("Users").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    if(dataSnapshot.child("type").getValue().equals("user")) {

                        if(!user){

                            Toast.makeText(HomeScreen.this, "You can't continue with same mobile number", Toast.LENGTH_SHORT).show();
                            AuthUI.getInstance().signOut(HomeScreen.this);
                            buttonActive=true;

                        }
                        else {

                            comingBackUserUI((String) dataSnapshot.child("name").getValue());
                            finish();

                        }

                    }
                    else{

                        if(user) {

                            Toast.makeText(HomeScreen.this, "You can't continue with same mobile number", Toast.LENGTH_SHORT).show();
                            AuthUI.getInstance().signOut(HomeScreen.this);
                            buttonActive=true;

                        }
                        else{

                            comingBackTechnicianUI((String) dataSnapshot.child("name").getValue(), (String) dataSnapshot.child("category").getValue());
                            finish();

                        }

                    }
                }
                else{

                    if(user)
                        newUserUI();
                    else
                        newTechnicianUI();

                }
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public float pixels(){

        float px= TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,60,getResources().getDisplayMetrics());
        return px;

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode==RC_SIGN_IN){

            IdpResponse response=IdpResponse.fromResultIntent(data);
            if(resultCode==RESULT_OK){

                buttonActive=false;
                progressDialog.setMessage("Loading");
                progressDialog.setCancelable(false);
                progressDialog.show();
                checkIfUserExist();

            }
            else{

                if (response == null) {
                    // User pressed back button
                    Toast.makeText(this, "SignIn cancelled", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (response.getErrorCode() == ErrorCodes.NO_NETWORK) {
                    Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (response.getErrorCode() == ErrorCodes.UNKNOWN_ERROR) {
                    Toast.makeText(this, "Unknown error", Toast.LENGTH_SHORT).show();
                    return;
                }

            }

        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    public void requestButton(View view) {
        if (buttonActive) {

            user=true;
            if(mFireBaseAuth.getCurrentUser()==null)
                startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder().setAvailableProviders(providers).build(), RC_SIGN_IN);

        }
    }

    public void technicianButton(View view){

        if (buttonActive) {

            user=false;
            if(mFireBaseAuth.getCurrentUser()==null)
                startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder().setAvailableProviders(providers).build(), RC_SIGN_IN);

        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);
        getSupportActionBar().setTitle("Welcome");

        progressDialog=new ProgressDialog(this);
        requestButton= findViewById(R.id.requestButton);

        mFireBaseAuth=FirebaseAuth.getInstance();
        firebaseDatabase=FirebaseDatabase.getInstance();
        databaseReference=firebaseDatabase.getReference();

        requestButton.animate().translationYBy(pixels()).setDuration(800);

        if(mFireBaseAuth.getCurrentUser()!=null){

            //linearLayout.setVisibility(View.INVISIBLE);
            progressDialog.setMessage("Loading");
            progressDialog.setCancelable(false);
            progressDialog.show();
            buttonActive=false;
            redirectAccording2user();

        }
    }
}
